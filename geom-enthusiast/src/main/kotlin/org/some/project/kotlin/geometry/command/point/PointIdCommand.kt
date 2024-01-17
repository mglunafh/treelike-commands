package org.some.project.kotlin.geometry.command.point

import org.some.project.kotlin.cmdparsing.CommandDefinition
import org.some.project.kotlin.cmdparsing.CommandObjectParser
import org.some.project.kotlin.cmdparsing.ParseResult
import org.some.project.kotlin.cmdparsing.ValueParseObject

data object PointIdCommand : CommandObjectParser<PointIdCommand> {
    override val commandDefinition = CommandDefinition("id")

    override fun parse(valueParseObject: ValueParseObject): ParseResult<PointIdCommand> {
        return ParseResult.ParseSuccess(this)
    }
}
