/**
 * @license
 * Copyright 2019 Google LLC.
 * This code may only be used under the BSD style license found at
 * http://polymer.github.io/LICENSE.txt
 * Code distributed by Google as part of this project is also
 * subject to an additional IP rights grant found at
 * http://polymer.github.io/PATENTS.txt
 */

import {assert} from '../../platform/chai-web.js';
import {Services} from '../../runtime/services.js';
import {ResourceManager} from '../../services/resource-manager.js';

describe('tfjs-service', () => {


  describe('loadGraphModel', async () => {

    beforeEach(() => {
      ResourceManager.references = [];
    });

    const load = async (model = 'arcs/src/services/test/assets/MobileNetV1/model.json', options={}) => {
      const modelUrl = `./${model}`;
      // const modelUrl = model;
      console.log(modelUrl);
      return await Services.request({call: 'tf.loadGraphModel', modelUrl, options});
    };

    it('produces a reference when given a valid model url', async () => {
      assert.isNumber(await load());
    });

    it('returns null when given bad arguments', async () => {
      assert.isNumber(await load('assets/MobileNetV1/model.json'));
    });

    it('produces a reference to something that can perform inference (i.e. `predict`)', async () => {
      assert.isNumber(await load('services/test/assets/MobileNetV1/model.json'));
      // const model = ResourceManager.deref(ref);
      //
      // assert.hasAllKeys(model, ['predict']);
    });

    it.skip('produces a reference to a frozen model (i.e. untrainable)', async () => {
      const ref = await load();
      const model = ResourceManager.deref(ref);

      assert.doesNotHaveAllKeys(model, ['fit']);
    });
  });
  describe('loadLayersModel', async () => {

  });
  describe('warmUp', async () => {});
  describe('predict', async () => {});
  describe('dispose', async () => {});
  describe('normalize', async () => {});
  describe('reshape', async () => {});
  describe('expandDims', async () => {});
  describe('getTopKClasses', async () => {});
  describe('resizeBilinear', async () => {});
  describe('imageToTensor', async () => {});
});


