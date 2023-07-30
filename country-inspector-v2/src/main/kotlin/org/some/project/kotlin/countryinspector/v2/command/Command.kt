package org.some.project.kotlin.countryinspector.v2.command

import org.some.project.kotlin.countryinspector.v2.country.City
import org.some.project.kotlin.countryinspector.v2.country.Country
import org.some.project.kotlin.countryinspector.v2.country.Hierarchy
import org.some.project.kotlin.countryinspector.v2.country.Overview

sealed interface Command<out H: Hierarchy>

enum class OverviewCommand: Command<Overview> {
    Exit, LoadCountry, ShowCountry, InspectCountry, Help
}

enum class CountryCommand: Command<Country> {
    CountryName, CountryPopulation, HeadOfState, Cities, InspectCity, BackToOverview, CountryHelp
}

enum class CityCommand: Command<City> {
    CityName, CityPopulation, Mayor, Sightseeings, Airports, BackToCountry, CityHelp
}
