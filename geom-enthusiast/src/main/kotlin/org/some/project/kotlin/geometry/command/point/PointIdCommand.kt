package org.some.project.kotlin.geometry.command.point

import org.some.project.kotlin.cmdparsing.CommandDefinition
import org.some.project.kotlin.cmdparsing.CommandObjectParser
import org.some.project.kotlin.cmdparsing.ParseResult
import org.some.project.kotlin.cmdparsing.ValueParseObject

data object PointIdCommand : CommandObjectParser<PointIdCommand> {
    override val commandDefinition = CommandDefinition("id", description = "Show figure ID")

    override fun parse(arguments: ValueParseObject): ParseResult<PointIdCommand> {
        return ParseResult.ParseSuccess(this)
    }
}
