package org.some.project.kotlin.geometry.command.point

import org.some.project.kotlin.cmdparsing.CommandDefinition
import org.some.project.kotlin.cmdparsing.CommandObjectParser
import org.some.project.kotlin.cmdparsing.ParseResult
import org.some.project.kotlin.cmdparsing.ValueParseObject

data object PointNameCommand: CommandObjectParser<PointNameCommand> {
    override val commandDefinition = CommandDefinition("name")

    override fun parse(valueParseObject: ValueParseObject): ParseResult<PointNameCommand> {
        return ParseResult.ParseSuccess(this)
    }
}
