package org.some.project.kotlin.countryinspector.v2.country

import org.some.project.kotlin.countryinspector.v2.command.Command
import org.some.project.kotlin.countryinspector.v2.command.CommandAction
import org.some.project.kotlin.countryinspector.v2.command.CommandObject
import org.some.project.kotlin.countryinspector.v2.command.CountryCommandObject
import org.some.project.kotlin.countryinspector.v2.command.CountryCommandObject.Companion.BackToOverviewCommandObject
import org.some.project.kotlin.countryinspector.v2.command.CountryCommandObject.Companion.CountryValueCommandObject
import org.some.project.kotlin.countryinspector.v2.command.CountryCommandObject.Companion.HelpObject
import org.some.project.kotlin.countryinspector.v2.command.CountryCommandObject.Companion.InspectCityCommandObject
import org.some.project.kotlin.countryinspector.v2.command.ParseErrorType.Companion.ArgumentListEmpty
import org.some.project.kotlin.countryinspector.v2.command.ParseErrorType.Companion.MissingRequiredParameter
import org.some.project.kotlin.countryinspector.v2.command.ParseErrorType.Companion.UnrecognizedCommandYet
import org.some.project.kotlin.countryinspector.v2.command.ParseResult
import org.some.project.kotlin.countryinspector.v2.command.ParseResult.ParseError
import org.some.project.kotlin.countryinspector.v2.command.ParseResult.ParseSuccess

data class Country(val name: String, val population: Int, val headOfState: String, val cities: List<City>): Hierarchy {

    override lateinit var ancestor: Overview

    init {
        cities.forEach { it.ancestor = this }
    }

    override fun parseCommandObject(args: List<String>): ParseResult<Country> {
        if (args.isEmpty()) return ParseError(ArgumentListEmpty)

        val command = CountryCommand.values().firstOrNull { it.commandName == args[0] }
            ?: return ParseError(UnrecognizedCommandYet(args[0]))

        val firstArg = args.getOrNull(1)

        if (firstArg in listOf("-h", "--help"))
            return ParseSuccess(HelpObject(command = command))

        return when (command) {
            CountryCommand.CountryName -> ParseSuccess(CountryValueCommandObject(name))
            CountryCommand.CountryPopulation -> ParseSuccess(CountryValueCommandObject(population.toString()))
            CountryCommand.HeadOfState -> ParseSuccess(CountryValueCommandObject(headOfState))
            CountryCommand.Cities -> ParseSuccess(CountryValueCommandObject(cities.joinToString { it.name }))
            CountryCommand.BackToOverview -> ParseSuccess(BackToOverviewCommandObject)
            CountryCommand.CountryHelp -> ParseSuccess(HelpObject(command = null))
            CountryCommand.InspectCity -> {
                firstArg?.let {
                    ParseSuccess(InspectCityCommandObject(cityName = it))
                } ?: ParseError(MissingRequiredParameter(command.commandName))
            }
        }
    }

    override fun createAction(commandObject: CommandObject<Hierarchy>): CommandAction {
        if (commandObject !is CountryCommandObject) {
            return ancestor.createAction(commandObject)
        }

        return when (commandObject) {
            BackToOverviewCommandObject -> CommandAction.Back(ancestor, "Switched back to Overview mode.")
            is CountryValueCommandObject -> CommandAction.OK(commandObject.value)
            is HelpObject -> {
                val message = commandObject.command?.let { "   ${it.commandName} -- ${it.description}" }
                    ?: CountryCommand.values().joinToString(separator = "\n") { "   ${it.commandName} -- ${it.description}" }
                CommandAction.OK(message)
            }
            is InspectCityCommandObject -> {
                val cityName = commandObject.cityName
                cities.firstOrNull { it.name == cityName }
                    ?.let { city -> CommandAction.InspectCity(city) }
                    ?: CommandAction.CityNotFound(cityName)
            }
        }
    }

    companion object {

        enum class CountryCommand(
            override val commandName: String,
            override val description: String
        ): Command<Country> {

            CountryName(commandName = "name", description = "Shows the name of the country."),
            CountryPopulation(commandName = "population", description = " Shows the number of citizens."),
            HeadOfState(commandName = "head", description = "Tells the state head's name."),
            Cities(commandName = "cities", description = "Lists the names of the cities."),
            InspectCity(commandName = "inspect", description = "Sets the city for inspection."),
            BackToOverview(commandName = "back", description = "Enters back to the Overview mode."),
            CountryHelp(commandName = "help", description = "Shows this help.")

        }
    }
}
