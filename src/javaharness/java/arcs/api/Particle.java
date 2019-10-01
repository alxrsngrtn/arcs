package arcs.api;

import java.util.Map;
import java.util.function.Consumer;

/** Interface that all built in particles must implement to create particles. */
public interface Particle {
  String getId();
  void setId(String id); // TODO: should be a ctor parameter instead?

  String getName();

  // TODO(alxr): not necessary
  void setSpec(ParticleSpec spec);

  // TODO(alxr): not necessary, already here through DI
  void setJsonParser(PortableJsonParser jsonParser);

  void setHandles(Map<String, Handle> handleByName);

  Handle getHandle(String id);

  default void onHandleSync(Handle handle, PortableJson model) {}

  default void onHandleUpdate(Handle handle, PortableJson update) {}

  default void onHandleDesync(Handle handle) {}

  // These APIs are copied from ui-particle.js
  // TODO: Consider adding a similar layer of abstraction, if needed.
  default String getTemplate(String slotName) { return ""; }

  default String getModel() { return ""; }

  void setOutput(Consumer<PortableJson> output);

  /**
   * Request to trigger rendering.
   */
  void output();

  // Particle doesn't know its spec until it is instantiated. This is a helper method
  // indicates to Arcs whether provided slot ID mapping needs to be created with the Renderer.
  default boolean providesSlot() { return false; }
}
