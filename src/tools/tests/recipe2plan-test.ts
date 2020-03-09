/**
 * @license
 * Copyright 2020 Google LLC.
 * This code may only be used under the BSD style license found at
 * http://polymer.github.io/LICENSE.txt
 * Code distributed by Google as part of this project is also
 * subject to an additional IP rights grant found at
 * http://polymer.github.io/PATENTS.txt
 */
import {assert} from '../../platform/chai-web.js';
import {fs} from '../../platform/fs-web.js';
import {recipe2plan} from '../recipe2plan.js';

describe('recipe2plan', () => {
  it('generates plans from recipes in a manifest', async () => {
    assert.deepStrictEqual(
      await recipe2plan('java/arcs/core/data/testdata/WriterReaderExample.arcs', 'arcs.core.data.testdata'),
      fs.readFileSync('java/arcs/core/data/testdata/WriterReaderExample.kt', 'utf8')
    );
  });
});
