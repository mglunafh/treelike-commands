package org.some.project.kotlin.cmdparsing

interface CommandObjectParser<T : Any> {

    val commandDefinition: CommandDefinition

    fun parse(arguments: ValueParseObject): ParseResult<T>

}
