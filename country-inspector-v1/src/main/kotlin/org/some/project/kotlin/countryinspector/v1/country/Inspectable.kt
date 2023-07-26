package org.some.project.kotlin.countryinspector.v1.country

import org.some.project.kotlin.countryinspector.v1.command.Command
import org.some.project.kotlin.countryinspector.v1.command.CommandObject
import org.some.project.kotlin.countryinspector.v1.command.CommandResult
import org.some.project.kotlin.countryinspector.v1.command.ParseErrorType
import org.some.project.kotlin.countryinspector.v1.command.ParseResult

interface Inspectable<out T: Command> {

    val commands: List<T>

    fun performCommand(args: List<String>): CommandResult

    fun help(): String = commands.joinToString(separator = "\n") { "   ${it.name} -- ${it.description}" }

    fun getCommand(commandName: String): T? {
        return commands.firstOrNull { it.name == commandName }
    }

    fun parseCommandObject(args: List<String>): ParseResult<T> {
        if (args.isEmpty()) return ParseResult.ParseError(ParseErrorType.ARGS_LIST_EMPTY)

        val command = commands.firstOrNull { it.name == args[0] }
            ?: return ParseResult.ParseError(ParseErrorType.UNRECOGNIZED_COMMAND)

        return when(args.getOrNull(1)) {
            "-h", "--help" -> ParseResult.Success(CommandObject(command = command, showHelp = true))
            else -> parseSpecificCommand(command, args)
        }
    }

    fun parseSpecificCommand(command: @UnsafeVariance T, args: List<String>): ParseResult<T> {
        return ParseResult.Success(CommandObject(command = command))
    }
}
