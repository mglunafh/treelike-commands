package org.some.project.kotlin.countryinspector.v2.command


sealed class ParseErrorType {

    companion object {

        data object ArgumentListEmpty: ParseErrorType()

        data class UnrecognizedCommand(val commandName: String): ParseErrorType()

        data class UnrecognizedCommandYet(val commandName: String): ParseErrorType()

        data class UnrecognizedParameter(val commandName: String, val parameterName: String): ParseErrorType()

        data class MissingRequiredParameter(val commandName: String): ParseErrorType()

    }
}
