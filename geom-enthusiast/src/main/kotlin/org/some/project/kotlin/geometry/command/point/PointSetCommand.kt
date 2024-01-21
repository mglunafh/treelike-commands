package org.some.project.kotlin.geometry.command.point

import org.some.project.kotlin.cmdparsing.*
import org.some.project.kotlin.geometry.command.Color
import org.some.project.kotlin.geometry.command.Name
import org.some.project.kotlin.geometry.command.Tag

data class PointSetCommand(val name: Name?, val color: Color?, val tags: List<Tag>?) {

    companion object : CommandObjectParser<PointSetCommand> {
        private val defName = ParameterDefinition("--name", Name::class, description = "Appropriate name to set")
        private val defColor = ParameterDefinition("--color", Color::class, description = "Color to set")
        private val defTags = ParameterDefinition(
            "--tag",
            Tag::class,
            delimiter = ",",
            description = "List of comma-separated tags to set"
        )

        override val commandDefinition = CommandDefinition(
            "set",
            listOf(defName, defColor, defTags),
            description = "Set point properties")

        override fun parse(arguments: ValueParseObject): ParseResult<PointSetCommand> {
            val presentOptions = mutableListOf<String>()
            val name = arguments.getNullable(defName)?.also { presentOptions.add("name") }
            val color = arguments.getNullable(defColor)?.also { presentOptions.add("color") }
            val tags = arguments.getListOrNull(defTags)?.also { presentOptions.add("tag") }

            return when {
                presentOptions.isEmpty() -> ParseResult.ParseError(NoOptions(commandDefinition.commandName))
                else -> ParseResult.ParseSuccess(PointSetCommand(name, color, tags))
            }
        }
    }
}
