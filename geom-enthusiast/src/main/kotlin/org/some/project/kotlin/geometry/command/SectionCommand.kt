package org.some.project.kotlin.geometry.command

import org.some.project.kotlin.cmdparsing.*
import org.some.project.kotlin.cmdparsing.ParseResult.ParseError
import org.some.project.kotlin.cmdparsing.ParseResult.ParseSuccess
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

    sealed class SectionTagCommand : SectionCommand {

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
                val show = arguments.getNullable(defShow)?.also { presentOptions.add("show") }
                val tagsToAdd = arguments.getListOrNull(defAddTags)?.also { presentOptions.add("add") }
                val tagsToRemove = arguments.getListOrNull(defRemoveTags)?.also { presentOptions.add("rm") }

                return when {
                    presentOptions.size > 1 -> ParseError(ExclusiveOptions(commandDefinition.commandName, presentOptions))
                    show != null -> ParseSuccess(SectionShowTagsCommand)
                    tagsToAdd != null -> ParseSuccess(SectionAddTagsCommand(tagsToAdd))
                    tagsToRemove != null -> ParseSuccess(SectionRemoveTagsCommand(tagsToRemove))
                    else -> ParseError(NoOptions(commandDefinition.commandName))
                }
            }
        }
    }

    data object SectionShowTagsCommand : SectionTagCommand()
    data class SectionAddTagsCommand(val tagsToAdd: List<Tag>) : SectionTagCommand()
    data class SectionRemoveTagsCommand(val tagsToRemove: List<Tag>) : SectionTagCommand()

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
                description = "Set section properties"
            )

            override fun parse(arguments: ValueParseObject): ParseResult<SectionSetCommand> {
                val presentOptions = mutableListOf<String>()
                val name = arguments.getNullable(defName)?.also { presentOptions.add("name") }
                val color = arguments.getNullable(defColor)?.also { presentOptions.add("color") }
                val tags = arguments.getListOrNull(defTags)?.also { presentOptions.add("tag") }

                return when {
                    presentOptions.isEmpty() -> ParseError(NoOptions(PointCommand.PointSetCommand.commandDefinition.commandName))
                    else -> ParseSuccess(SectionSetCommand(name, color, tags))
                }
            }
        }
    }

    sealed class SectionGeneralShowCommand : SectionCommand {

        companion object: CommandObjectParser<SectionGeneralShowCommand> {
            private val defShort = BooleanSwitchDefinition("--short", default = false, description = "Show info in concise form")
            private val defShowTags = BooleanSwitchDefinition("--with-tags", default = false, description = "Show tags of the objects")
            private val defPointId = IntFlagDefinition("--point", description = "Shows info about specific section point")
            override val commandDefinition = CommandDefinition("show", listOf(defShort, defShowTags, defPointId),
                description = "Show information about the section")

            override fun parse(arguments: ValueParseObject): ParseResult<out SectionGeneralShowCommand> {
                val short = arguments.get(defShort)
                val withTags = arguments.get(defShowTags)
                val pointId = arguments.getNullable(defPointId)?.let { Id[it] }

                val command = pointId?.let { SectionShowPointCommand(it, short) }
                    ?: SectionShowCommand(short, withTags)

                return ParseSuccess(command)
            }
        }
    }

    data class SectionShowCommand(val short: Boolean, val showTags: Boolean): SectionGeneralShowCommand()
    data class SectionShowPointCommand(val pointId: Id, val short: Boolean) : SectionGeneralShowCommand()

    data class SectionInspectCommand(val id: Id) : SectionCommand {

        companion object : CommandObjectParser<SectionInspectCommand> {
            override val commandDefinition = CommandDefinition(
                "inspect",
                1,
                "Inspects one of the end points of this line segment"
            )

            override fun parse(arguments: ValueParseObject): ParseResult<out SectionInspectCommand> {
                val pointIdStr = arguments.positionalArguments[0]
                val id = pointIdStr.toIntOrNull() ?: return ParseError(CouldNotConvertId(pointIdStr))

                return Id[id]?.let {
                    ParseSuccess(SectionInspectCommand(it))
                } ?: ParseError(PointDoesNotExist(id))
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
