package org.some.project.kotlin.countryinspector.v2.country

import org.some.project.kotlin.countryinspector.v2.command.CommandAction
import org.some.project.kotlin.countryinspector.v2.command.CommandObject
import org.some.project.kotlin.countryinspector.v2.command.CountryCommand
import org.some.project.kotlin.countryinspector.v2.command.CountryCommandObject
import org.some.project.kotlin.countryinspector.v2.command.CountryCommandObject.Companion.BackToOverviewCommandObject
import org.some.project.kotlin.countryinspector.v2.command.CountryCommandObject.Companion.CountryValueCommandObject
import org.some.project.kotlin.countryinspector.v2.command.CountryCommandObject.Companion.HelpObject
import org.some.project.kotlin.countryinspector.v2.command.CountryCommandObject.Companion.InspectCityCommandObject
import org.some.project.kotlin.countryinspector.v2.command.ParseErrorType.Companion.ArgumentListEmpty
import org.some.project.kotlin.countryinspector.v2.command.ParseErrorType.Companion.MissingCommandAfterPrefix
import org.some.project.kotlin.countryinspector.v2.command.ParseErrorType.Companion.MissingRequiredParameter
import org.some.project.kotlin.countryinspector.v2.command.ParseErrorType.Companion.UnrecognizedCommandYet
import org.some.project.kotlin.countryinspector.v2.command.ParseResult
import org.some.project.kotlin.countryinspector.v2.command.ParseResult.ParseError
import org.some.project.kotlin.countryinspector.v2.command.ParseResult.ParseSuccess
import org.some.project.kotlin.countryinspector.v2.l10n.LocalizationHolder
import org.some.project.kotlin.countryinspector.v2.util.createHelpAction

data class Country(val name: String, val population: Int, val headOfState: String, val cities: List<City>): Hierarchy {

    override lateinit var ancestor: Overview

    init {
        cities.forEach { it.ancestor = this }
    }

    override fun parseCommandObject(args: List<String>): ParseResult<Country> {
        if (args.isEmpty()) return ParseError(ArgumentListEmpty)

        val commandPrefix = LocalizationHolder.countryPrefix
        val (commandString, firstArg) = when {
            args[0] != commandPrefix -> Pair(args[0], args.getOrNull(1))
            args[0] == commandPrefix && args.size == 1 -> return ParseError(MissingCommandAfterPrefix(commandPrefix))
            else -> Pair(args[1], args.getOrNull(2))
        }

        val (command, l10n) = LocalizationHolder.countryCommands[commandString]
            ?: return ParseError(UnrecognizedCommandYet(commandString))

        if (firstArg in listOf("-h", "--help"))
            return ParseSuccess(HelpObject(command = l10n))

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
                } ?: ParseError(MissingRequiredParameter(LocalizationHolder[command].name))
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
            is HelpObject -> commandObject.command?.let { createHelpAction(it) } ?: createHelpAction(Country::class)
            is InspectCityCommandObject -> {
                val cityName = commandObject.cityName
                cities.firstOrNull { it.name == cityName }
                    ?.let { city -> CommandAction.InspectCity(city) }
                    ?: CommandAction.CityNotFound(cityName)
            }
        }
    }
}
