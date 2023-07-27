package org.some.project.kotlin.countryinspector.v2.command

import org.some.project.kotlin.countryinspector.v2.country.City
import org.some.project.kotlin.countryinspector.v2.country.Country
import org.some.project.kotlin.countryinspector.v2.country.Hierarchy
import org.some.project.kotlin.countryinspector.v2.country.Overview

sealed interface Command<H: Hierarchy> {
    val commandName: String
    val description: String
}

enum class OverviewCommand(
    override val commandName: String,
    override val description: String
): Command<Overview> {

    Exit(commandName = "exit", description = "Closes Country Inspection application."),
    Help(commandName = "help", description = "Shows this help."),
    ShowCountry(commandName = "show", description = "Shows the country to observe."),
    InspectCountry(commandName = "inspect", description = "Sets the country for inspection.");
}

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

enum class CityCommand(
    override val commandName: String,
    override val description: String
): Command<City> {

    CityName(commandName = "name", description = "Shows the name of the city."),
    CityPopulation(commandName = "population", description = " Shows the number of city residents."),
    Mayor(commandName = "mayor", description = "Tells the state head's name."),
    Sightseeings(commandName = "sightseeings", description = "Lists local tourist attractions."),
    Airports(commandName = "airports", description = "Shows the airports in this city (modes: full, name, code)."),
    BackToCountry(commandName = "back", description = "Enters back to the country level."),
    CityHelp(commandName = "help", description = "Shows this help")

}
