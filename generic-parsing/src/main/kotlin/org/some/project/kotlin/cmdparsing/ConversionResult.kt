package org.some.project.kotlin.cmdparsing

import java.lang.IllegalStateException

class ConversionResult<T> private constructor(private val resultVal: T?, private val errorVal: ConversionFailure?) {

    val isSuccessful: Boolean = resultVal != null

    val isFailure: Boolean = errorVal != null

    val result: T
        get() {
            return resultVal ?: throw IllegalStateException("There was no successful result recorded")
        }

    val error: ConversionFailure
        get() {
            return errorVal ?: throw IllegalStateException("There were no errors in the result")
        }

    companion object {

        fun <T> result(result: T) = ConversionResult(result, null)

        fun <T> failure(error: ConversionFailure) = ConversionResult(null as T?, error)
    }

    sealed interface ConversionFailure

    enum class ConversionFailureEnum : ConversionFailure {
        SWITCH_VALUE_EXPECTED, STRING_VALUE_EXPECTED, LIST_VALUE_EXPECTED, CONVERTER_NOT_FOUND
    }

    data class ValueConversionFailure(val value: String) : ConversionFailure

    data class ListEntryConversionFailure(val nonConvertibles: List<String>) : ConversionFailure
}
