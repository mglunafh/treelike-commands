package org.some.project.kotlin.cmdparsing

sealed class ParseResult<T> {

    data class ParseSuccess<T>(val result: T) : ParseResult<T>()

    data class Help<T>(val helpMessage: String) : ParseResult<T>()

    data class ParseError<T>(val error: ErrorType) : ParseResult<T>()

    fun <R> map(transform: (T) -> R): ParseResult<R> {
        return when (this) {
            is ParseSuccess -> ParseSuccess(transform(this.result))
            is ParseError -> ParseError(this.error)
            is Help -> Help(this.helpMessage)
        }
    }

    fun <R> flatMap(transform: (T) -> ParseResult<R>): ParseResult<R> {
        return when (this) {
            is ParseSuccess -> transform(this.result)
            is ParseError -> ParseError(this.error)
            is Help -> Help(this.helpMessage)
        }
    }
}
