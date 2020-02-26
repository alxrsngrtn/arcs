package arcs.core.tools

import arcs.core.data.ParticleProto
import arcs.core.data.RecipeEnvelopeProto
import arcs.core.data.RecipeProto
import com.google.protobuf.GeneratedMessageLite


interface Visitor {
    fun visit(proto: GeneratedMessageLite<*, *>)
}

fun GeneratedMessageLite<*, *>.accept(visitor: Visitor) = visitor.visit(this)

class PrintVisitor : Visitor {
    private val sb = StringBuilder()
    override fun visit(proto: GeneratedMessageLite<*, *>) {
        when (proto) {
            is RecipeEnvelopeProto -> handleEnvelope(proto)
            is RecipeProto -> handleRecipe(proto)
            is ParticleProto -> handleParticle(proto)
        }
    }

   fun build() = sb.toString()

    private fun handleEnvelope(proto: RecipeEnvelopeProto) {
        sb.append("RecipeEnvelopeProto(")
        proto.recipe.accept(this)
        sb.append(",particles=[")
        proto.particleSpecsList.forEach {
            it.accept(this)
            sb.append(",")
        }
        sb.append("])")
    }

    private fun handleRecipe(proto: RecipeProto) {
        sb.append("RecipeProto(")
            .append("name=")
            .append(proto.name)
            .append(",")
            .append("N_handles=")
            .append(proto.handlesList.size)
            .append(",")
            .append("N_Particles=")
            .append(proto.particlesList.size)
            .append(")")
    }

    private fun handleParticle(proto: ParticleProto) {
       sb.append("ParticleProto(specName=")
           .append(proto.specName)
           .append(",")
           .append("N_connections=")
           .append(proto.connectionsList.size)
           .append(")")
           .append(")")
    }

}


class ProtoTest(val recipeEnvelopeProto: RecipeEnvelopeProto) {
    fun run(): String {
        val visitor = PrintVisitor()
        recipeEnvelopeProto.accept(visitor)
        return visitor.build()
    }
}


