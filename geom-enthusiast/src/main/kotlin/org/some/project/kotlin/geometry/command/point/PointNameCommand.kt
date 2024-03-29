package org.some.project.kotlin.geometry.command.point

import org.some.project.kotlin.cmdparsing.CommandDefinition
import org.some.project.kotlin.cmdparsing.CommandObjectParser
import org.some.project.kotlin.cmdparsing.ParseResult
import org.some.project.kotlin.cmdparsing.ValueParseObject

data object PointNameCommand: CommandObjectParser<PointNameCommand> {
    override val commandDefinition = CommandDefinition("name", description = "Show point name if it's set")

    override fun parse(arguments: ValueParseObject): ParseResult<PointNameCommand> {
        return ParseResult.ParseSuccess(this)
    }
}
