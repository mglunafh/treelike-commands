package org.some.project.kotlin.cmdparsing.command

import org.some.project.kotlin.cmdparsing.*
import org.some.project.kotlin.cmdparsing.Converter.castList

data class FieldSetterCommandObject(
    val id: Int?,
    val name: String?,
    val color: Color,
    val tags: List<Tag>? = null,
    val readOnly: Boolean? = null,
    val person: Pair<String, String>? = null
) {
    companion object {
        private val idDefinition = IntFlagDefinition("id")
        private val nameDefinition = StringFlagDefinition("name")
        private val colorDefinition = FlagDefinition("color", Color::class, default = Color.WHITE)
        private val tagDefinition = FlagDefinition("tag", Tag::class, withDelimiter = ",")
        private val readOnlyDefinition = BooleanSwitchDefinition("read-only")
        private val personDefinition = ParameterDefinition("person", String::class, arity = 2, required = false)

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

        fun parse(dictionary: Map<String, Any>): FieldSetterCommandObject {
            val id = dictionary["id"] as Int?
            val name = dictionary["name"] as String?
            val color = dictionary["color"] as Color
            val readonly = dictionary["read-only"] as Boolean?
            val tags = castList(dictionary["tag"], Tag::class)
            val personList = castList(dictionary["person"], String::class)
            val person = personList ?.let { Pair(it[0], it[1]) }

            return FieldSetterCommandObject(id, name, color, tags, readonly, person)
        }
    }
}
