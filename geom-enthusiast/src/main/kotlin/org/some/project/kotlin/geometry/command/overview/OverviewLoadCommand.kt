package org.some.project.kotlin.geometry.command.overview

import org.some.project.kotlin.cmdparsing.CommandDefinition
import org.some.project.kotlin.cmdparsing.CommandObjectParser
import org.some.project.kotlin.cmdparsing.ParseResult
import org.some.project.kotlin.cmdparsing.ValueParseObject

data class OverviewLoadCommand(val filename: String) {

    companion object : CommandObjectParser<OverviewLoadCommand> {
        override val commandDefinition = CommandDefinition("load", 1, 1)

        override fun parse(arguments: ValueParseObject): ParseResult<OverviewLoadCommand> {
            return ParseResult.ParseSuccess(OverviewLoadCommand(arguments.positionalArguments[0]))
        }
    }
}
