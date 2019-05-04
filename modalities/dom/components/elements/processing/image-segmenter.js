/*
 * Copyright (c) 2019 The Polymer Project Authors. All rights reserved.
 * This code may only be used under the BSD style license found at http://polymer.github.io/LICENSE.txt
 * The complete set of authors may be found at http://polymer.github.io/AUTHORS.txt
 * The complete set of contributors may be found at http://polymer.github.io/CONTRIBUTORS.txt
 * Code distributed by Google as part of the polymer project is also
 * subject to an additional IP rights grant found at http://polymer.github.io/PATENTS.txt
 */

import {Xen} from '../../xen/xen-async.js';

import * as bodyPix from 'https://unpkg.com/@tensorflow-models/body-pix@1.0.0/dist/body-pix.esm.js?module';

const log = Xen.logFactory('ImageSegmenter', 'green');

const template = Xen.html`
<canvas id='canvas' style='display: none'></canvas>
`;


/**
 * Apply a style-transfer model to an input image.
 * Passes a new image source to the `on-results` event handler.
 */
class ImageSegmenter extends Xen.Async {
  static get observedAttributes() {
    return ['imgurl'];
  }
  get template() {
    return template;
  }
  _didMount() {
    this.canvas = this.host.getElementById('canvas');
    this.ctx = this.canvas.getContext('2d');
  }

  update({imgurl}, state) {
    log('args: ', imgurl);
    if (!state.status) {
      state.status = 'idle';
    }
    if (state.imgurl !== imgurl) {
      state.imgurl = imgurl;
      this.updateUrl(imgurl);
    }

    if (state.img) {
      const img = state.img;
      const styler = state.styler;
      this.segment(img);
    }
  }

  async updateUrl(url) {
    if (url) {
      const img = await this.getImage(url);
      this._setState({img});
    } else {
      this._setState({img: null});
    }
  }

  async getImage(url) {
    return new Promise((resolve, reject) => {
      const img = new Image();
      img.onload = () => resolve(img);
      img.src = url;
    });
  }
  render(props, state) {
    return state;
  }
  async segment(img) {
    log('Segmenting...');
    const net = await bodyPix.load();
    const segmentation = await net.estimatePersonSegmentation(img);
    const maskBackground = true;
// Convert the personSegmentation into a mask to darken the background.
    const backgroundDarkeningMask = bodyPix.toMaskImageData(segmentation, maskBackground);

    const opacity = 0.7;

// draw the mask onto the image on a canvas.  With opacity set to 0.7 this will darken the background.
    bodyPix.drawMask(
      this.canvas, img, backgroundDarkeningMask, opacity);
    this.value = {url: this.canvas.toDataURL("image/png")};
    this.fire('results');
  }
}


customElements.define('image-segmenter', ImageSegmenter);
