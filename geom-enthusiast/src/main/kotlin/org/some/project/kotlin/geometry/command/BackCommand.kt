package org.some.project.kotlin.geometry.command

import org.some.project.kotlin.cmdparsing.CommandDefinition
import org.some.project.kotlin.cmdparsing.CommandObjectParser
import org.some.project.kotlin.cmdparsing.ParseResult
import org.some.project.kotlin.cmdparsing.ValueParseObject

data object BackCommand : CommandObjectParser<BackCommand> {

    override val commandDefinition = CommandDefinition("back", description = "Set the previous scene back")
    override fun parse(arguments: ValueParseObject) = ParseResult.ParseSuccess(BackCommand)

}
