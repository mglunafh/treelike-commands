package org.some.project.kotlin.geometry.command

import org.some.project.kotlin.cmdparsing.*
import org.some.project.kotlin.cmdparsing.ParseResult.ParseError
import org.some.project.kotlin.cmdparsing.ParseResult.ParseSuccess
import org.some.project.kotlin.geometry.SuccessfulParser
import org.some.project.kotlin.geometry.model.Color
import org.some.project.kotlin.geometry.model.Id
import org.some.project.kotlin.geometry.model.Name
import org.some.project.kotlin.geometry.model.Tag

sealed interface TriangleCommand : CommandObject {

    data object TriangleIdCommand : TriangleCommand

    object TriangleIdCommandParser : SuccessfulParser<TriangleIdCommand> {
        override val commandDefinition = CommandDefinition("id", description = "Show triangle ID")
        override val result = TriangleIdCommand
    }

    data object TriangleNameCommand : TriangleCommand

    object TriangleNameCommandParser : SuccessfulParser<TriangleNameCommand> {
        override val commandDefinition = CommandDefinition("name", description = "Show triangle name if it's set")
        override val result = TriangleNameCommand
    }

    data object TriangleColorCommand : TriangleCommand

    object TriangleColorCommandParser : SuccessfulParser<TriangleColorCommand> {
        override val commandDefinition = CommandDefinition("color", description = "Show triangle color")
        override val result = TriangleColorCommand
    }

    sealed class TriangleGeneralShowCommand : TriangleCommand
    data class TriangleShowCommand(val short: Boolean, val showTags: Boolean) : TriangleGeneralShowCommand()
    data class TriangleShowSectionCommand(val sideId: Id, val short: Boolean, val showTags: Boolean) : TriangleGeneralShowCommand()
    data class TriangleShowPointCommand(val pointId: Id, val short: Boolean) : TriangleGeneralShowCommand()

    object TriangleShowCommandParser : CommandObjectParser<TriangleGeneralShowCommand> {
        private val defShort = BooleanSwitchDefinition("--short", default = false, description = "Show info in concise form")
        private val defShowTags = BooleanSwitchDefinition("--with-tags", default = false, description = "Show tags of the objects")
        private val defSectionId = IntFlagDefinition("--side", description = "Shows info about specific triangle side")
        private val defPointId = IntFlagDefinition("--point", description = "Shows info about specific triangle point")
        override val commandDefinition = CommandDefinition("show", listOf(defShort, defShowTags, defSectionId, defPointId),
            description = "Show information about the triangle")

        override fun parse(arguments: ValueParseObject): ParseResult<out TriangleGeneralShowCommand> {
            val short = arguments.get(defShort)
            val withTags = arguments.get(defShowTags)
            val sectionId = arguments.getNullable(defSectionId)?.let { Id[it] }
            val pointId = arguments.getNullable(defPointId)?.let { Id[it] }

            return when {
                sectionId != null && pointId != null -> ParseError(ExclusiveOptions(commandDefinition.commandName, listOf(defSectionId.name, defPointId.name)))
                sectionId != null -> ParseSuccess(TriangleShowSectionCommand(sectionId, short, withTags))
                pointId != null -> ParseSuccess(TriangleShowPointCommand(pointId, short))
                else -> ParseSuccess(TriangleShowCommand(short, withTags))
            }
        }
    }

    data class TriangleSetCommand(val name: Name?, val color: Color?, val tags: List<Tag>?) : TriangleCommand {

        companion object : CommandObjectParser<TriangleSetCommand> {
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
                description = "Set triangle properties"
            )

            override fun parse(arguments: ValueParseObject): ParseResult<out TriangleSetCommand> {
                val presentOptions = mutableListOf<String>()
                val name = arguments.getNullable(defName)?.also { presentOptions.add("name") }
                val color = arguments.getNullable(defColor)?.also { presentOptions.add("color") }
                val tags = arguments.getListOrNull(defTags)?.also { presentOptions.add("tag") }

                return when {
                    presentOptions.isEmpty() -> ParseError(NoOptions(commandDefinition.commandName))
                    else -> ParseSuccess(TriangleSetCommand(name, color, tags))
                }
            }
        }
    }

    data class TriangleInspectCommand(val id: Id) : TriangleCommand {

        companion object : CommandObjectParser<TriangleInspectCommand> {
            override val commandDefinition = CommandDefinition(
                "inspect",
                1,
                "Inspects one of the triangle sides"
            )

            override fun parse(arguments: ValueParseObject): ParseResult<out TriangleInspectCommand> {
                val pointIdStr = arguments.positionalArguments[0]
                val id = pointIdStr.toIntOrNull() ?: return ParseError(CouldNotConvertId(pointIdStr))

                return Id[id]?.let {
                    ParseSuccess(TriangleInspectCommand(it))
                } ?: ParseError(SectionDoesNotExist(id))
            }

            data class CouldNotConvertId(val arg: String) : CustomValidationError() {
                override fun getMessage() = "Inspect: could not convert ID from '$arg'."
            }

            data class SectionDoesNotExist(val id: Int) : CustomValidationError() {
                override fun getMessage() = "Inspect: Section with ID '$id' does not exist."
            }

        }
    }
}
