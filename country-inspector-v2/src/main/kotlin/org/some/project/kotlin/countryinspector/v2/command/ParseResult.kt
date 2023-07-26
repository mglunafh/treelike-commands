package org.some.project.kotlin.countryinspector.v2.command

import org.some.project.kotlin.countryinspector.v2.country.Hierarchy

sealed class ParseResult<out H: Hierarchy> {

    abstract val isSuccess: Boolean
    abstract val isFailure: Boolean

    data class ParseSuccess<H: Hierarchy>(val result: CommandObject<H>): ParseResult<H>() {
        override val isFailure = false
        override val isSuccess = true
    }

    data class ParseError<H: Hierarchy> (val error: ParseErrorType): ParseResult<H>() {
        override val isFailure = true
        override val isSuccess = false
    }
}
