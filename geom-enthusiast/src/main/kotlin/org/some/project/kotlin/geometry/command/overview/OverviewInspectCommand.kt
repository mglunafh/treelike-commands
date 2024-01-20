package org.some.project.kotlin.geometry.command.overview

import org.some.project.kotlin.cmdparsing.*
import org.some.project.kotlin.geometry.command.Id

data class OverviewInspectCommand(val id: Id) {

    companion object : CommandObjectParser<OverviewInspectCommand> {
        override val commandDefinition = CommandDefinition("inspect", 1)

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
