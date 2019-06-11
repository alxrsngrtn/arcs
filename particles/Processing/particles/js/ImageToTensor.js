/**
 * @license
 * Copyright (c) 2019 Google Inc. All rights reserved.
 * This code may only be used under the BSD style license found at
 * http://polymer.github.io/LICENSE.txt
 * Code distributed by Google as part of this project is also
 * subject to an additional IP rights grant found at
 * http://polymer.github.io/PATENTS.txt
 */

defineParticle(({DomParticle, log}) => {

  const handleName = 'imageTensor';

  return class extends DomParticle {
    willReceiveProps({image}) {
      if (image) {
        this.apply(image);
      }
    }

    async apply(image) {
      log('Converting image URL to Tensor...');
      const imgReference = await this.service({call: 'tf.imageToTensor', imageUrl: image});
      log('Converted image URL to Tensor.');

      await this.clearHandle(handleName);
      this.updateSingleton(handleName, {ref: imgReference});
    }

  };
});
