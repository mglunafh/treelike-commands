package org.some.project.kotlin.geometry.command.overview

import org.some.project.kotlin.cmdparsing.*
import org.some.project.kotlin.geometry.command.FigureType

data class OverviewCreateCommand(val fig: FigureType) {

    companion object: CommandObjectParser<OverviewCreateCommand> {
        override val commandDefinition = CommandDefinition("create", 1)

        override fun parse(arguments: ValueParseObject): ParseResult<OverviewCreateCommand> {
            val figure = arguments.positionalArguments[0]

            return FigureType.toFigureOrNull(figure)?.let {
                ParseResult.ParseSuccess(OverviewCreateCommand(it))
            } ?: ParseResult.ParseError(NotAFigure(figure))
        }
    }

    data class NotAFigure(val arg: String) : CustomValidationError() {
        override fun getMessage(): String {
            return "Command 'create': argument '$arg' is not a figure or is not supported."
        }
    }
}
