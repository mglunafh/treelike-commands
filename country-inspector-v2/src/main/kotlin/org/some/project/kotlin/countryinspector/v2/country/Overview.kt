package org.some.project.kotlin.countryinspector.v2.country

import org.some.project.kotlin.countryinspector.v2.command.Command
import org.some.project.kotlin.countryinspector.v2.command.CommandObject
import org.some.project.kotlin.countryinspector.v2.command.CommandAction
import org.some.project.kotlin.countryinspector.v2.command.OverviewCommandObject
import org.some.project.kotlin.countryinspector.v2.command.OverviewCommandObject.Companion.ExitObject
import org.some.project.kotlin.countryinspector.v2.command.OverviewCommandObject.Companion.HelpObject
import org.some.project.kotlin.countryinspector.v2.command.OverviewCommandObject.Companion.InspectCountryObject
import org.some.project.kotlin.countryinspector.v2.command.OverviewCommandObject.Companion.ShowCountryObject
import org.some.project.kotlin.countryinspector.v2.command.ParseErrorType.Companion.ArgumentListEmpty
import org.some.project.kotlin.countryinspector.v2.command.ParseErrorType.Companion.MissingRequiredParameter
import org.some.project.kotlin.countryinspector.v2.command.ParseErrorType.Companion.UnrecognizedCommand
import org.some.project.kotlin.countryinspector.v2.command.ParseResult
import org.some.project.kotlin.countryinspector.v2.command.ParseResult.ParseError
import org.some.project.kotlin.countryinspector.v2.command.ParseResult.ParseSuccess
import java.lang.IllegalArgumentException


class Overview(val country: Country): Hierarchy {

    override val ancestor: Hierarchy = AbsoluteTop

    init {
        country.ancestor = this
    }

    override fun parseCommandObject(args: List<String>): ParseResult<Overview> {
        if (args.isEmpty()) return ParseError(ArgumentListEmpty)

        val command =
            OverviewCommand.values().firstOrNull { it.commandName == args[0] }
                ?: return ParseError(UnrecognizedCommand(args[0]))

        val firstArg = args.getOrNull(1)

        if (firstArg in listOf("-h", "--help"))
            return ParseSuccess(HelpObject(command = command))

        return when (command) {
            OverviewCommand.Help -> ParseSuccess(HelpObject(command = null))
            OverviewCommand.Exit -> ParseSuccess(ExitObject)
            OverviewCommand.ShowCountry -> ParseSuccess(ShowCountryObject)
            OverviewCommand.InspectCountry -> {
                firstArg?.let { ParseSuccess(InspectCountryObject(countryName = it)) }
                    ?: ParseError(MissingRequiredParameter(command.commandName))
            }
        }
    }

    override fun createAction(commandObject: CommandObject<Hierarchy>): CommandAction {
        if (commandObject !is OverviewCommandObject) {
            throw IllegalArgumentException("Somehow class hierarchy for Overview got hijacked...")
        }
        return when (commandObject) {
            ExitObject -> CommandAction.Exit("Exiting the Country Inspection application. Have a nice day!")
            ShowCountryObject -> CommandAction.OK(country.name)
            is HelpObject -> {
                val message = commandObject.command?.let { "   ${it.commandName} -- ${it.description}" }
                    ?: OverviewCommand.values().joinToString(separator = "\n") { "   ${it.commandName} -- ${it.description}" }
                CommandAction.OK(message)
            }
            is InspectCountryObject -> {
                if (commandObject.countryName == country.name)
                    CommandAction.InspectCountry(country)
                else
                    CommandAction.IncorrectCountry(commandObject.countryName)
            }
        }
    }

    companion object {

        enum class OverviewCommand(
            override val commandName: String,
            override val description: String
        ): Command<Overview> {

            Exit(commandName = "exit", description = "Closes Country Inspection application."),
            Help(commandName = "help", description = "Shows this help."),
            ShowCountry(commandName = "show", description = "Shows the country to observe."),
            InspectCountry(commandName = "inspect", description = "Sets the country for inspection.");
        }
    }
}
