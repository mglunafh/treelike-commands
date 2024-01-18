package org.some.project.kotlin.cmdparsing.command

import org.some.project.kotlin.cmdparsing.*

data class FieldSetterCommandObject(
    val id: Int?,
    val name: String?,
    val color: Color,
    val tags: List<Tag>? = null,
    val readOnly: Boolean? = null,
    val person: Pair<String, String>? = null
) {
    companion object {
        private val idDefinition = IntFlagDefinition("--id")
        private val nameDefinition = StringFlagDefinition("--name")
        private val colorDefinition = FlagDefinition("--color", Color::class, default = Color.WHITE)
        private val tagDefinition = FlagDefinition("--tag", Tag::class, delimiter = ",")
        private val readOnlyDefinition = BooleanSwitchDefinition("--read-only")
        private val personDefinition = ParameterDefinition("--person", String::class, arity = 2, required = false)

        val fieldSetterCommandDefinition = CommandDefinition(
            "set", 0, 0, listOf(
                idDefinition,
                nameDefinition,
                colorDefinition,
                tagDefinition,
                readOnlyDefinition,
                personDefinition
            )
        )

        fun parse(values: ValueParseObject): FieldSetterCommandObject {
            val id = values.getOrNull<Int>("--id")
            val name = values.getOrNull<String>("--name")
            val color = values.get<Color>("--color")
            val readonly = values.getOrNull<Boolean>("--read-only")
            val tags = values.getListOrNull<Tag>("--tag")
            val person = values.getListOrNull<String>("--person")?.let { Pair(it[0], it[1]) }

            return FieldSetterCommandObject(id, name, color, tags, readonly, person)
        }
    }
}
