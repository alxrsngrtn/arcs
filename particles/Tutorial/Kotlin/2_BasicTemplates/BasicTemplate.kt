package arcs

import kotlin.native.internal.ExportForCppRuntime

/**
 * Sample WASM Particle.
 */
class BasicTemplateParticle : Particle() {

  override fun populateModel(model: Map<String, String>): Map<String, String> {
    return model + mapOf(
      "name" to "Human"
    )
  } 

  override fun onHandleUpdate(handle: Handle) {
    renderSlot("root")
  }

  override fun onHandleSync(handle: Handle, allSynced: Boolean) {
    if(allSynced) {
      log("All handles synched\n")
      renderSlot("root")
    }
  }

    private fun console(s: String) {
      log(s)
    }

    override fun getTemplate(): String {
        return """<b>Hello, <span>{{name}}</span>!</b>"""
      }
}

@Retain
@ExportForCppRuntime("_newBasicTemplateParticle")
fun constructBasicTemplateParticle(): WasmAddress {
    log("_newBasicTemplateParticle called")
    return WasmParticle(BasicTemplateParticle()).toWasmAddress()
}
