package org.some.project.kotlin.geometry.command

import org.some.project.kotlin.cmdparsing.*
import org.some.project.kotlin.geometry.SuccessfulParser
import org.some.project.kotlin.geometry.model.Id
import org.some.project.kotlin.geometry.model.ShapeType

sealed interface OverviewCommand: CommandObject {

    data class OverviewCreateCommand(val fig: ShapeType): OverviewCommand {

        companion object: CommandObjectParser<OverviewCreateCommand> {
            override val commandDefinition = CommandDefinition(
                "create",
                1,
                description = "Create a geometric shape"
            )

            override fun parse(arguments: ValueParseObject): ParseResult<OverviewCreateCommand> {
                val figure = arguments.positionalArguments[0]

                return ShapeType.toShapeOrNull(figure)?.let {
                    ParseResult.ParseSuccess(OverviewCreateCommand(it))
                } ?: ParseResult.ParseError(NotAFigure(figure))
            }
        }

        data class NotAFigure(val arg: String) : CustomValidationError() {
            override fun getMessage() = "Command 'create': argument '$arg' is not a shape or is not supported."
        }
    }

    data class OverviewInspectCommand(val id: Id): OverviewCommand {

        companion object : CommandObjectParser<OverviewInspectCommand> {
            override val commandDefinition = CommandDefinition(
                "inspect",
                1,
                description = "Inspect a figure with the given ID")

            override fun parse(arguments: ValueParseObject): ParseResult<OverviewInspectCommand> {
                val idString = arguments.positionalArguments[0]
                val id = idString.toIntOrNull() ?: return ParseResult.ParseError(CouldNotConvertId(idString))

                return Id[id]?.let {
                    ParseResult.ParseSuccess(OverviewInspectCommand(it))
                } ?: ParseResult.ParseError(FigureDoesNotExist(id))
            }
        }

        data class CouldNotConvertId(val arg: String) : CustomValidationError() {
            override fun getMessage() = "Inspect: could not convert ID from '$arg'."
        }

        data class FigureDoesNotExist(val id: Int) : CustomValidationError() {
            override fun getMessage() = "Inspect: Figure with ID '$id' does not exist."
        }
    }

    data object OverviewListCommand: OverviewCommand, SuccessfulParser<OverviewListCommand> {
        override val commandDefinition = CommandDefinition("list", description = "List available geometric figures")
        override val result = this
    }

    data class OverviewLoadCommand(val filename: String): OverviewCommand {

        companion object : CommandObjectParser<OverviewLoadCommand> {
            override val commandDefinition = CommandDefinition(
                "load",
                1,
                description = "Load geometric figures from a file")

            override fun parse(arguments: ValueParseObject): ParseResult<OverviewLoadCommand> {
                return ParseResult.ParseSuccess(OverviewLoadCommand(arguments.positionalArguments[0]))
            }
        }
    }

    data class OverviewSaveCommand(val filename: String): OverviewCommand {

        companion object : CommandObjectParser<OverviewSaveCommand> {
            override val commandDefinition = CommandDefinition(
                "save",
                1,
                "Save the current list of figures to a file"
            )

            override fun parse(arguments: ValueParseObject): ParseResult<OverviewSaveCommand> {
                return ParseResult.ParseSuccess(OverviewSaveCommand(arguments.positionalArguments[0]))
            }
        }
    }
}
