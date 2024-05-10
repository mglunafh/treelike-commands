package org.some.project.kotlin.geometry.command

import org.some.project.kotlin.cmdparsing.*
import org.some.project.kotlin.geometry.SuccessfulParser
import org.some.project.kotlin.geometry.model.*

sealed interface SectionCommand : CommandObject {

    data object SectionIdCommand : SectionCommand

    data object SectionIdCommandParser : SuccessfulParser<SectionIdCommand> {
        override val commandDefinition = CommandDefinition("id", description = "Show section ID")
        override val result = SectionIdCommand
    }

    data object SectionNameCommand : SectionCommand

    data object SectionNameCommandParser : SuccessfulParser<SectionNameCommand> {
        override val commandDefinition = CommandDefinition("name", description = "Show section name if it's set")
        override val result = SectionNameCommand
    }

    data object SectionColorCommand : SectionCommand

    data object SectionColorCommandParser : SuccessfulParser<SectionColorCommand> {
        override val commandDefinition = CommandDefinition("color", description = "Show section color")
        override val result = SectionColorCommand
    }

    data class SectionTagCommand(val show: Boolean?, val tagsToAdd: List<Tag>?, val tagsToRemove: List<Tag>?) : SectionCommand {

        companion object : CommandObjectParser<SectionTagCommand> {
            private val defShow = BooleanSwitchDefinition("--show", description = "Show tags attached to the point")
            private val defAddTags = ParameterDefinition(
                "--add",
                Tag::class,
                delimiter = ",",
                description = "Add a list of comma-separated tags to the section"
            )
            private val defRemoveTags = ParameterDefinition(
                "--rm",
                Tag::class,
                delimiter = ",",
                description = "Remove a list of comma-separated tags from the point if possible"
            )

            override val commandDefinition = CommandDefinition(
                "tag",
                listOf(defShow, defAddTags, defRemoveTags),
                description = "Perform one of show/add/remove operations on section tags"
            )

            override fun parse(arguments: ValueParseObject): ParseResult<out SectionTagCommand> {
                val presentOptions = mutableListOf<String>()
                val show = arguments.getNullable(PointCommand.PointTagCommand.defShow)?.also { presentOptions.add("show") }
                val tagsToAdd = arguments.getListOrNull(PointCommand.PointTagCommand.defAddTags)?.also { presentOptions.add("add") }
                val tagsToRemove = arguments.getListOrNull(PointCommand.PointTagCommand.defRemoveTags)?.also { presentOptions.add("rm") }

                return when {
                    presentOptions.isEmpty() -> ParseResult.ParseError(NoOptions(PointCommand.PointTagCommand.commandDefinition.commandName))
                    presentOptions.size > 1 -> ParseResult.ParseError(ExclusiveOptions(commandDefinition.commandName, presentOptions))
                    else -> ParseResult.ParseSuccess(SectionTagCommand(show, tagsToAdd, tagsToRemove))
                }
            }
        }
    }

    data class SectionSetCommand(val name: Name?, val color: Color?, val tags: List<Tag>?) : SectionCommand {

        companion object : CommandObjectParser<SectionSetCommand> {
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
                description = "Set point properties"
            )

            override fun parse(arguments: ValueParseObject): ParseResult<SectionSetCommand> {
                val presentOptions = mutableListOf<String>()
                val name = arguments.getNullable(defName)?.also { presentOptions.add("name") }
                val color = arguments.getNullable(defColor)?.also { presentOptions.add("color") }
                val tags = arguments.getListOrNull(defTags)?.also { presentOptions.add("tag") }

                return when {
                    presentOptions.isEmpty() -> ParseResult.ParseError(NoOptions(PointCommand.PointSetCommand.commandDefinition.commandName))
                    else -> ParseResult.ParseSuccess(SectionSetCommand(name, color, tags))
                }
            }
        }
    }

    data class SectionShowCommand(val short: Boolean, val showTags: Boolean) : SectionCommand {

        companion object: CommandObjectParser<SectionShowCommand> {
            val defShort = BooleanSwitchDefinition("--short", default = false, description = "Show info in concise form")
            val defShowTags = BooleanSwitchDefinition("--with-tags", default = false, description = "Show tags of the objects")
            override val commandDefinition = CommandDefinition("show", listOf(defShort, defShowTags),
                description = "Show information about the section")

            override fun parse(arguments: ValueParseObject): ParseResult<out SectionShowCommand> {
                val short = arguments.get(defShort)
                val withTags = arguments.get(defShowTags)
                return ParseResult.ParseSuccess(SectionShowCommand(short, withTags))
            }
        }
    }

    data class SectionInspectCommand(val id: Id) : SectionCommand {

        companion object : CommandObjectParser<SectionInspectCommand> {
            override val commandDefinition = CommandDefinition(
                "inspect",
                1,
                "Inspects one of the end points of this line segment"
            )

            override fun parse(arguments: ValueParseObject): ParseResult<out SectionInspectCommand> {
                val pointIdStr = arguments.positionalArguments[0]
                val id = pointIdStr.toIntOrNull() ?: return ParseResult.ParseError(CouldNotConvertId(pointIdStr))

                return Id[id]?.let {
                    ParseResult.ParseSuccess(SectionInspectCommand(it))
                } ?: ParseResult.ParseError(PointDoesNotExist(id))
            }
        }

        data class CouldNotConvertId(val arg: String) : CustomValidationError() {
            override fun getMessage() = "Inspect: could not convert ID from '$arg'."
        }

        data class PointDoesNotExist(val id: Int) : CustomValidationError() {
            override fun getMessage() = "Inspect: Point with ID '$id' does not exist."
        }
    }
}
