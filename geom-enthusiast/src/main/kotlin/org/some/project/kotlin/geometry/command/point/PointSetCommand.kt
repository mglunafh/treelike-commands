package org.some.project.kotlin.geometry.command.point

import org.some.project.kotlin.cmdparsing.*
import org.some.project.kotlin.geometry.command.Color
import org.some.project.kotlin.geometry.command.Name
import org.some.project.kotlin.geometry.command.Tag

data class PointSetCommand(val name: Name?, val color: Color?, val tags: List<Tag>?) {

    companion object : CommandObjectParser<PointSetCommand> {
        val defName = ParameterDefinition("--name", Name::class)
        val defColor = ParameterDefinition("--color", Color::class)
        val defTags = ParameterDefinition("--tag", Tag::class, delimiter = ",")

        override val commandDefinition =
            CommandDefinition("set", paramDefinitions = listOf(defName, defColor, defTags))

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
