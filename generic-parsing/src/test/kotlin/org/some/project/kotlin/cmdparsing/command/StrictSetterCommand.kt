package org.some.project.kotlin.cmdparsing.command

import org.some.project.kotlin.cmdparsing.*

data class StrictSetterCommand(
    val id: Int,
    val name: String,
    val age: Int,
    val surname: String? = null,
    val height: Double? = null,
    val color: Color? = null,
    val readOnly: Boolean? = null,
    val tags: List<Tag>? = null,
    val person: List<String>? = null
) {

    companion object {
        private val idDefinition = IntFlagDefinition("id", required = true)
        private val nameDefinition = StringFlagDefinition("name", required = true)
        private val surnameDefinition = StringFlagDefinition("surname")
        private val ageDefinition = IntFlagDefinition("age", default = 18)
        private val heightDefinition = ParameterDefinition("height", Double::class)
        private val colorDefinition = FlagDefinition("color", Color::class)
        private val readOnlyDefinition = BooleanSwitchDefinition("read-only")
        private val tagDefinition = FlagDefinition("tag", Tag::class, delimiter = ",")
        private val personDefinition = ParameterDefinition("person", String::class, arity = 2)

        val commandDefinition = CommandDefinition(
            "set", 0, 0, listOf(
                idDefinition,
                nameDefinition,
                ageDefinition,
                surnameDefinition,
                heightDefinition,
                colorDefinition,
                tagDefinition,
                readOnlyDefinition,
                personDefinition
            )
        )

        fun parse(values: ValueParseObject): StrictSetterCommand {
            val id = values.get(idDefinition)
            val name = values.get(nameDefinition)
            val surname = values.getNullable(surnameDefinition)
            val age = values.get(ageDefinition)
            val height = values.getNullable(heightDefinition)
            val color = values.getNullable(colorDefinition)
            val readonly = values.getNullable(readOnlyDefinition)
            val tags = values.getListOrNull(tagDefinition)
            val personList = values.getListOrNull(personDefinition)

            return StrictSetterCommand(
                id,
                name,
                age,
                surname = surname,
                height = height,
                color = color,
                readOnly = readonly,
                tags = tags,
                person = personList
            )
        }
    }
}