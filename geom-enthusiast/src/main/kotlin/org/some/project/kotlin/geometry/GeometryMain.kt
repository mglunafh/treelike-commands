package org.some.project.kotlin.geometry

import org.some.project.kotlin.cmdparsing.*
import org.some.project.kotlin.geometry.command.Color
import org.some.project.kotlin.geometry.command.Id
import org.some.project.kotlin.geometry.command.Name
import org.some.project.kotlin.geometry.command.Tag
import org.some.project.kotlin.geometry.command.overview.*
import org.some.project.kotlin.geometry.command.point.*

fun main(args: Array<String>) {
    print("Hello, Geometry enthusiast! ")
    Converter.registerConverter(Tag::class) { Tag.toTagOrNull(it) }
    Converter.registerConverter(Color::class) { Color.toColorOrNull(it) }
    Converter.registerConverter(Name::class) { Name.toNameOrNull(it) }

    val figureIds = List(3) { Id.next() }

    while (true) {
        print(":> ")
        val input = readlnOrNull() ?: continue
        val cmdArgs = Tokenizer.tokenize(input, "\\s+".toRegex())

        if (cmdArgs.isEmpty()) continue

        if (cmdArgs[0] in listOf("q", "quit", "exit")) {
            println("Have a nice day!")
            break
        }
        if (cmdArgs[0] == "help") {
            // Point and Overview should implement some interface since they definitely act the same
            val helpMessage = Point.commands
                .map { it.commandDefinition }
                .joinToString(separator = "\n") { "${it.commandName} -- ${it.description}" }
            println(helpMessage)
            continue
        }

        val commandParser = determinePointCommand(cmdArgs[0])
        if (commandParser == null) {
            println("Could not understand the command")
            continue
        }

        val commandObject = CmdParsingFacade.parse(commandParser, cmdArgs)

        when (commandObject) {
            is ParseResult.ParseError -> println(displayParseError(commandObject.error))
            is ParseResult.ParseSuccess -> println(commandObject.result)
            is ParseResult.Help -> println(commandObject.helpMessage)
        }
    }
}

fun determineOverviewCommand(commandName: String): CommandObjectParser<*>? {
    return when (commandName) {
        "list"      -> OverviewListCommand
        "create"    -> OverviewCreateCommand
        "save"      -> OverviewSaveCommand
        "load"      -> OverviewLoadCommand
        "inspect"   -> OverviewInspectCommand
        else        -> null
    }
}

fun determinePointCommand(commandName: String): CommandObjectParser<*>? {
    return when (commandName) {
        "id"    -> PointIdCommand
        "name"  -> PointNameCommand
        "tag"   -> PointTagCommand
        "set"   -> PointSetCommand
        "show"  -> PointShowCommand
        else    -> null
    }
}

fun displayParseError(error: ErrorType): String {
    val message: String = when (error) {
        EmptyArguments -> "No arguments have been passed"
        is CompositeError -> error.errors.joinToString(separator = "\n") { displayParseError(it) }

        is TooFewRequiredArguments ->
            "Command '${error.command}': Too few arguments (${error.actualArgCount}) have been passed" +
                    " to the command, it requires at least ${error.requiredArgCount}."
        is TooManyArguments -> "Command '${error.command}: Too many arguments, expected ${error.argCount} at most, excess: '${error.excess}'."
        is MissingParameterValue -> "Command '${error.command}': Missing a parameter for flag '${error.paramName}'"
        is MissingParameters -> "Command '${error.command}': Missing one or more arguments for flag '${error.paramName}'"
        ToBeImplemented -> "Is not implemented yet"
        is UnrecognizedFlag -> "Command '${error.command}': Unrecognized flag '${error.flagName}'"
        is WrongCommand -> "Somehow parser for command '${error.expected}' was called for a different command '${error.actual}'"

        is NoOptions -> "Command '${error.command}': No options have been passed"
        is RequiredParameterNotSet -> "Command '${error.command}': Option '${error.paramName}' was not provided with value"

        is SwitchValueExpected -> "Command '${error.command}': Could not parse option '${error.option}' since it was not a switch"
        is StringValueExpected -> "Command '${error.command}': Could not parse option '${error.option}' since it was not a string"
        is ListValueExpected -> "Command '${error.command}': Could not parse option '${error.option}' since it was not a list"
        is ConverterNotFound -> "Command '${error.command}': Could not parse option '${error.option}' since the converter for type '${error.type}' could not be found"
        is ValueConversionFailed -> "Command '${error.command}': Could not convert '${error.option}' from '${error.value}'"
        is CustomValidationError -> error.getMessage()
    }
    val errorClass: String = when (error) {
        is GenericParseError -> "[parse-error] "
        is GenericValidationError -> "[validation-error] "
        is GenericConversionError -> "[conversion-error] "
        is CompositeError -> "Errors:\n"
        else -> "[error] "
    }

    return "$errorClass$message"
}
