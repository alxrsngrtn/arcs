/**
 * @license
 * Copyright 2020 Google LLC.
 * This code may only be used under the BSD style license found at
 * http://polymer.github.io/LICENSE.txt
 * Code distributed by Google as part of this project is also
 * subject to an additional IP rights grant found at
 * http://polymer.github.io/PATENTS.txt
 */
import {Type} from '../type.js';
import {Manifest} from '../manifest.js';
import {ToStore} from './storage.js';
import {AbstractStore} from './abstract-store.js';

// TODO(alxr) name bike-shedding: `StoreClosure`? `StoreContext`? `StoreHolder` Other ideas?
export interface SearchableStore {
  context: Manifest;
  findStoresByType<T extends Type>(type: T, options?: { tags: string[] }): ToStore<T>[];
  findStoreById(id: String): AbstractStore;
}
