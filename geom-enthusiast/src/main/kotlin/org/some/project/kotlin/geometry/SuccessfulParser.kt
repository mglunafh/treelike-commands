package org.some.project.kotlin.geometry

import org.some.project.kotlin.cmdparsing.CommandObjectParser
import org.some.project.kotlin.cmdparsing.ParseResult
import org.some.project.kotlin.cmdparsing.ValueParseObject


interface SuccessfulParser<T : Any> : CommandObjectParser<T> {

    val result: T

    override fun parse(arguments: ValueParseObject): ParseResult<T> {
        return ParseResult.ParseSuccess(result)
    }
}
