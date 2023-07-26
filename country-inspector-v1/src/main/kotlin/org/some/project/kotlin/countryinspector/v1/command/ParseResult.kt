package org.some.project.kotlin.countryinspector.v1.command

sealed class ParseResult<out T: Command> {

    abstract val isSuccess: Boolean
    abstract val isFailure: Boolean

    data class Success<out T: Command>(val result: CommandObject<T>): ParseResult<T>() {
        override val isFailure = false
        override val isSuccess = true
    }

    data class ParseError<out T: Command> (val error: ParseErrorType): ParseResult<T>() {
        override val isFailure = true
        override val isSuccess = false
    }
}

enum class ParseErrorType {
    ARGS_LIST_EMPTY, UNRECOGNIZED_COMMAND
}
