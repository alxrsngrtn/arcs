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


describe('tfjs-service', () => {
  const services = ['loadLayersModel', 'loadGraphModel', 'warmUp', 'predict', 'dispose', 'normalize', 'reshape',
                    'expandsDims', 'getTopKClasses', 'resizeBilinear', 'imageToTensor'];

  services.forEach((srvc) => {
    describe(srvc, async () => {

    });
  });

  describe('loadLayersModel', async () => {});
  describe('loadGraphModel', async () => {});
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


