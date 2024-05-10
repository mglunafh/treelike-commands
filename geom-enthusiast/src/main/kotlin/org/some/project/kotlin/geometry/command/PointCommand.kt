package org.some.project.kotlin.geometry.command

import org.some.project.kotlin.cmdparsing.*
import org.some.project.kotlin.geometry.SuccessfulParser
import org.some.project.kotlin.geometry.model.Color
import org.some.project.kotlin.geometry.model.Name
import org.some.project.kotlin.geometry.model.Tag

sealed interface PointCommand: CommandObject {

    data object PointIdCommand : PointCommand, SuccessfulParser<PointIdCommand> {
        override val commandDefinition = CommandDefinition("id", description = "Show point ID")
        override val result = this
    }

    data object PointNameCommand: PointCommand, SuccessfulParser<PointNameCommand> {
        override val commandDefinition = CommandDefinition("name", description = "Show point name if it's set")
        override val result = this
    }

    data class PointSetCommand(val name: Name?, val color: Color?, val tags: List<Tag>?): PointCommand {

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

    data class PointShowCommand(val short: Boolean): PointCommand {

        companion object: CommandObjectParser<PointShowCommand> {
            val defShort = BooleanSwitchDefinition("--short", default = false, description = "Show info in concise form")
            override val commandDefinition = CommandDefinition(
                "show",
                listOf(defShort),
                description = "Show information about the point")

            override fun parse(arguments: ValueParseObject): ParseResult<PointShowCommand> {
                val showShort = arguments.get(defShort)
                return ParseResult.ParseSuccess(PointShowCommand(showShort))
            }
        }
    }

    data class PointTagCommand(val show: Boolean?, val tagsToAdd: List<Tag>?, val tagsToRemove: List<Tag>?): PointCommand {

        companion object : CommandObjectParser<PointTagCommand> {
            const val commandName = "tag"
            val defShow = BooleanSwitchDefinition("--show", description = "Show tags attached to the point")
            val defAddTags = ParameterDefinition(
                "--add",
                Tag::class,
                delimiter = ",",
                description = "Add a list of comma-separated tags to the point"
            )
            val defRemoveTags = ParameterDefinition(
                "--rm",
                Tag::class,
                delimiter = ",",
                description = "Remove a list of comma-separated tags from the point if possible"
            )

            override val commandDefinition = CommandDefinition(
                commandName,
                listOf(defShow, defAddTags, defRemoveTags),
                description = "Perform one of show/add/remove operations on point tags")

            override fun parse(arguments: ValueParseObject): ParseResult<PointTagCommand> {
                val presentOptions = mutableListOf<String>()
                val show = arguments.getNullable(defShow)?.also { presentOptions.add("show") }
                val tagsToAdd = arguments.getListOrNull(defAddTags)?.also { presentOptions.add("add") }
                val tagsToRemove = arguments.getListOrNull(defRemoveTags)?.also { presentOptions.add("rm") }

                return when {
                    presentOptions.isEmpty() -> ParseResult.ParseError(NoOptions(commandName))
                    presentOptions.size > 1 -> ParseResult.ParseError(ExclusiveOptions(commandName, presentOptions))
                    else -> ParseResult.ParseSuccess(PointTagCommand(show, tagsToAdd, tagsToRemove))
                }
            }
        }
    }
}
