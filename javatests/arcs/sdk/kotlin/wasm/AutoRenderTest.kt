/*
 * Copyright 2019 Google LLC.
 *
 * This code may only be used under the BSD style license found at
 * http://polymer.github.io/LICENSE.txt
 *
 * Code distributed by Google as part of this project is also subject to an additional IP rights
 * grant found at
 * http://polymer.github.io/PATENTS.txt
 */

package arcs.sdk.kotlin.wasm

import arcs.sdk.kotlin.Handle
import arcs.sdk.kotlin.Particle
import arcs.sdk.kotlin.Singleton

class AutoRenderTest : Particle() {
    private val data = Singleton(this, "data") { AutoRenderTest_Data("") }

    override fun init() = renderOutput()
    override fun onHandleUpdate(handle: Handle) = renderOutput()
    override fun onHandleSync(handle: Handle, allSynced: Boolean) = renderOutput()
    override fun getTemplate(slotName: String): String = data.get()?.txt ?: "empty"
}
