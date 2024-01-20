package org.some.project.kotlin.geometry.command.overview

import org.some.project.kotlin.cmdparsing.CommandDefinition
import org.some.project.kotlin.cmdparsing.CommandObjectParser
import org.some.project.kotlin.cmdparsing.ParseResult
import org.some.project.kotlin.cmdparsing.ValueParseObject

data object OverviewListCommand: CommandObjectParser<OverviewListCommand> {
    override val commandDefinition = CommandDefinition("list")

    override fun parse(arguments: ValueParseObject): ParseResult<OverviewListCommand> {
        return ParseResult.ParseSuccess(this)
    }
}
