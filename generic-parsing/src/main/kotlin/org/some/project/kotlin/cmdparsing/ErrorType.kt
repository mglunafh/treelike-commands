package org.some.project.kotlin.cmdparsing

import kotlin.reflect.KClass

sealed class ErrorType

data object ToBeImplemented : ErrorType()

data class CompositeError(val errors: List<ErrorType>) : ErrorType() {
    init {
        require(errors.isNotEmpty())
    }
}

sealed class GenericParseError : ErrorType()

data object EmptyArguments : GenericParseError()

data class WrongCommand(val expected: String, val actual: String) : GenericParseError()

data class TooManyArguments(val command: String, val argCount: Int, val excess: String) : GenericParseError()

data class TooFewRequiredArguments(val command: String, val requiredArgCount: Int, val actualArgCount: Int) :
    GenericParseError()

data class UnrecognizedFlag(val command: String, val flagName: String) : GenericParseError()

data class MissingParameterValue(val command: String, val paramName: String) : GenericParseError()

data class MissingParameters(val command: String, val paramName: String, val arity: Int) : GenericParseError()

sealed class GenericValidationError : ErrorType()

data class NoOptions(val command: String): GenericValidationError()

data class RequiredParameterNotSet(val command: String, val paramName: String) : GenericValidationError()

abstract class CustomValidationError: GenericValidationError() {

    abstract fun getMessage(): String

}

sealed class GenericConversionError : ErrorType()

data class SwitchValueExpected(val command: String, val option: String) : GenericConversionError()

data class StringValueExpected(val command: String, val option: String) : GenericConversionError()

data class ListValueExpected(val command: String, val option: String) : GenericConversionError()

data class ConverterNotFound(val command: String, val option: String, val type: KClass<out Any>) : GenericConversionError()

data class ValueConversionFailed(val command: String, val option: String, val value: String, val type: KClass<out Any>) :
    GenericConversionError()
