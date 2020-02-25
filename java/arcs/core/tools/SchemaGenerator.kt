package arcs.core.tools

import arcs.core.data.Schema
import arcs.core.data.SchemaProto
import arcs.core.data.TypeProto
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.TypeSpec

class SchemaGenerator(val schema: SchemaProto) : Generator<TypeSpec.Builder> {
    private var anons = 0
    private val schemaClass = ClassName("arcs.core.data", "Schema")
    private val schemaNameClass = ClassName("arcs.core.data", "SchemaName")
    private val schemaFieldsClass = ClassName("arcs.core.data", "SchemaFields")
    val names = schema.namesList
    val schemaName = schema.namesList.firstOrNull() ?: "anon${++anons}"
    val generatedName = "${formatName(schemaName)}Schema"

    private fun formatName(name: String): String {
        return name[0].toLowerCase() + name.substring(1)
    }

    override fun generate(builder: TypeSpec.Builder) {

        val entities = schema.fields.filter { it.value.dataCase.number == TypeProto.ENTITY_FIELD_NUMBER }

        val blockBuilder = CodeBlock.builder()
            .addStatement("%T(", schemaClass)

        schemaNames(blockBuilder)

//         blockBuilder
//            .indent()
//            .addStatement("%T(", schemaFieldsClass)
//            .indent()
//            .addStatement("singletons = mapOf(")
//            .indent()
//            .apply {
//                val entries = schema.fields.singletons.entries
//                entries.forEachIndexed { index, entry ->
//                    when (entry.value.tag) {
//                        FieldType.Tag.EntityRef -> add(
//                            "%S to %T(%S)",
//                            entry.key,
//                            FieldType.EntityRef::class,
//                            (entry.value as FieldType.EntityRef).schemaHash
//                        )
//                        FieldType.Tag.Primitive -> add(
//                            "%S to %T.%L",
//                            entry.key,
//                            FieldType::class,
//                            (entry.value as FieldType.Primitive).primitiveType
//                        )
//                    }
//                    if (index != entries.size - 1) add(",")
//                    add("\n")
//                }
//            }
//            .unindent()
//            .addStatement("),")
//            .addStatement("collections = mapOf(")
//            .indent()
//            .apply {
//                val entries = schema.fields.collections.entries
//                entries.forEachIndexed { index, entry ->
//                    when (entry.value.tag) {
//                        FieldType.Tag.EntityRef -> add(
//                            "%S to %T(%S)",
//                            entry.key,
//                            FieldType.EntityRef::class,
//                            (entry.value as FieldType.EntityRef).schemaHash
//                        )
//                        FieldType.Tag.Primitive -> add(
//                            "%S to %T.%L",
//                            entry.key,
//                            FieldType::class,
//                            (entry.value as FieldType.Primitive).primitiveType
//                        )
//                    }
//                    if (index != entries.size - 1) add(",")
//                    add("\n")
//                }
//            }
//            .unindent()
//            .addStatement(")")
//            .unindent()
//            .addStatement("),")
//            .addStatement("%S", schema.hash)
//            .addStatement(")")

        PropertySpec.builder(generatedName, Schema::class)
            .initializer(blockBuilder.build())
            .build()

    }

    private fun schemaNames(builder: CodeBlock.Builder) {
        when (names.size) {
            0 -> builder.addStatement("listOf()")
            1 -> builder.addStatement("listOf(%T(%S))", schemaNameClass, schemaName)
            else ->
                builder
                    .addStatement("listOf(")
                    .indent()
                    .apply {
                        names.forEachIndexed { index, name ->
                            if (index > 0) addStatement(",%T(%S)", schemaNameClass, schemaName)
                            else addStatement("%T(%S)", schemaNameClass, schemaName)
                        }
                    }
                    .unindent()
                    .addStatement(")")
        }
    }
}

