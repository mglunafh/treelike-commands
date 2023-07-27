package org.some.project.kotlin.countryinspector.v2.country

import org.some.project.kotlin.countryinspector.v2.command.CityCommand
import org.some.project.kotlin.countryinspector.v2.command.CityCommandObject
import org.some.project.kotlin.countryinspector.v2.command.CityCommandObject.Companion.AirportsCommandObject
import org.some.project.kotlin.countryinspector.v2.command.CityCommandObject.Companion.BackToCountryCommandObject
import org.some.project.kotlin.countryinspector.v2.command.CityCommandObject.Companion.CityValueCommandObject
import org.some.project.kotlin.countryinspector.v2.command.CityCommandObject.Companion.HelpObject
import org.some.project.kotlin.countryinspector.v2.command.CityCommandObject.Companion.NotImplementedYet
import org.some.project.kotlin.countryinspector.v2.command.CommandObject
import org.some.project.kotlin.countryinspector.v2.command.CommandAction
import org.some.project.kotlin.countryinspector.v2.command.ParseErrorType.Companion.ArgumentListEmpty
import org.some.project.kotlin.countryinspector.v2.command.ParseErrorType.Companion.UnrecognizedCommandYet
import org.some.project.kotlin.countryinspector.v2.command.ParseErrorType.Companion.UnrecognizedParameter
import org.some.project.kotlin.countryinspector.v2.command.ParseResult
import org.some.project.kotlin.countryinspector.v2.command.ParseResult.ParseError
import org.some.project.kotlin.countryinspector.v2.command.ParseResult.ParseSuccess
import org.some.project.kotlin.countryinspector.v2.country.Airport.Companion.AirportDisplayMode
import org.some.project.kotlin.countryinspector.v2.util.createHelpAction

data class City(
    val name: String,
    val population: Int,
    val mayor: String,
    val airports: List<Airport>
): Hierarchy {

    override lateinit var ancestor: Country

    override fun parseCommandObject(args: List<String>): ParseResult<Hierarchy> {
        if (args.isEmpty()) return ParseError(ArgumentListEmpty)

        val command = CityCommand.values().firstOrNull { it.commandName == args[0] }
            ?: return ParseError(UnrecognizedCommandYet(args[0]))

        if (args.getOrNull(1) in listOf("-h", "--help"))
            return ParseSuccess(HelpObject(command = command))

        return when (command) {
            CityCommand.CityName -> ParseSuccess(CityValueCommandObject(value = name))
            CityCommand.CityPopulation -> ParseSuccess(CityValueCommandObject(value = population.toString()))
            CityCommand.Mayor -> ParseSuccess(CityValueCommandObject(value = mayor))
            CityCommand.Sightseeings -> ParseSuccess(NotImplementedYet)
            CityCommand.Airports -> getAirportArgs(args)
            CityCommand.BackToCountry -> ParseSuccess(BackToCountryCommandObject)
            CityCommand.CityHelp -> ParseSuccess(HelpObject(command = null))
        }
    }

    private fun getAirportArgs(args: List<String>): ParseResult<City> {
        val firstArg = args.getOrNull(1)
            ?: return ParseSuccess(AirportsCommandObject(displayMode = AirportDisplayMode.FULL))

        return AirportDisplayMode.values().firstOrNull { it.mode == firstArg }
            ?.let { displayMode -> ParseSuccess(AirportsCommandObject(displayMode = displayMode)) }
            ?: ParseError(UnrecognizedParameter(CityCommand.Airports.commandName, firstArg))
    }

    override fun createAction(commandObject: CommandObject<Hierarchy>): CommandAction {
        if (commandObject !is CityCommandObject) {
            return ancestor.createAction(commandObject)
        }

        return when (commandObject) {
            BackToCountryCommandObject -> CommandAction.Back(ancestor, "Switched back to Country mode.")
            is CityValueCommandObject -> CommandAction.OK(commandObject.value)
            is HelpObject -> createHelpAction<City, CityCommand>(commandObject.command)
            is AirportsCommandObject -> airportsAction(commandObject)
            NotImplementedYet -> CommandAction.OK("Not implemented yet")
        }
    }

    private fun airportsAction(commandObject: AirportsCommandObject): CommandAction {
        if (airports.isEmpty()) return CommandAction.OK("There are no airports in $name")

        val airportList = when (commandObject.displayMode) {
            AirportDisplayMode.NAME -> airports.joinToString(postfix = ".") { it.name }
            AirportDisplayMode.CODE -> airports.joinToString(postfix = ".") { it.shortcut }
            AirportDisplayMode.FULL -> airports.joinToString(postfix = ".")
        }
        return CommandAction.OK(airportList)
    }
}
