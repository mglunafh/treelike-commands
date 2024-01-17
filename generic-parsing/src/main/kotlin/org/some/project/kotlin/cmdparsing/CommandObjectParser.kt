package org.some.project.kotlin.cmdparsing

interface CommandObjectParser<T : Any> {

    val commandDefinition: CommandDefinition

    fun parse(valueParseObject: ValueParseObject): ParseResult<T>

}