/**
 * Copyright (c) 2019 Google Inc. All rights reserved.
 * This code may only be used under the BSD style license found at
 * http://polymer.github.io/LICENSE.txt
 * Code distributed by Google as part of this project is also
 * subject to an additional IP rights grant found at
 * http://polymer.github.io/PATENTS.txt
 */

'use strict';

defineParticle(({DomParticle, html, log}) => {

  const tmpl = html`
  <div style="padding: 16px">
    <img src="{{newImg}}">
  </div>
  `;

  return class extends DomParticle {
    get template() {
      return tmpl;
    }

    render({image}, state) {
      return {
        newImg: image && image.url ? image.url : state.newImg
      };
    }

  };
});
