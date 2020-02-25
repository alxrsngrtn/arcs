package arcs.core.tools

import arcs.core.data.HandleProto
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.TypeSpec

class HandleGenerator(val handleProto: HandleProto) : Generator<TypeSpec.Builder> {
    private val handleClass = ClassName("arcs.core.data.Plan", "HandleConnection")
    override fun generate(builder: TypeSpec.Builder) {

        CodeBlock.builder()
            .addStatement("%T(", handleClass)
            .indent()
            .unindent()
            .addStatement(")")
    }


}

