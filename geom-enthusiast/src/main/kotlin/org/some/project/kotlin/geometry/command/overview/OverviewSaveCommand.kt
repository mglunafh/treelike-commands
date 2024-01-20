package org.some.project.kotlin.geometry.command.overview

import org.some.project.kotlin.cmdparsing.CommandDefinition
import org.some.project.kotlin.cmdparsing.CommandObjectParser
import org.some.project.kotlin.cmdparsing.ParseResult
import org.some.project.kotlin.cmdparsing.ValueParseObject

data class OverviewSaveCommand(val filename: String) {

    companion object : CommandObjectParser<OverviewSaveCommand> {
        override val commandDefinition = CommandDefinition("save", 1)

        override fun parse(arguments: ValueParseObject): ParseResult<OverviewSaveCommand> {
            return ParseResult.ParseSuccess(OverviewSaveCommand(arguments.positionalArguments[0]))
        }
    }
}
