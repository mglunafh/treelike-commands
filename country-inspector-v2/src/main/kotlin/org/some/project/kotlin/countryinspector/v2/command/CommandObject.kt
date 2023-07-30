package org.some.project.kotlin.countryinspector.v2.command

import org.some.project.kotlin.countryinspector.v2.country.Airport.Companion.AirportDisplayMode
import org.some.project.kotlin.countryinspector.v2.country.City
import org.some.project.kotlin.countryinspector.v2.country.Country
import org.some.project.kotlin.countryinspector.v2.country.Hierarchy
import org.some.project.kotlin.countryinspector.v2.country.Overview
import org.some.project.kotlin.countryinspector.v2.l10n.LocalizedCommand

sealed interface CommandObject<out H: Hierarchy>

sealed class OverviewCommandObject: CommandObject<Overview> {

    companion object {
        data class HelpObject(val command: LocalizedCommand?): OverviewCommandObject()
        data class InspectCountryObject(val countryName: String): OverviewCommandObject()
        data object ExitObject: OverviewCommandObject()
        data object ShowCountryObject: OverviewCommandObject()
    }
}

sealed class CountryCommandObject: CommandObject<Country> {

    companion object {
        data class HelpObject(val command: LocalizedCommand?): CountryCommandObject()
        data class CountryValueCommandObject(val value: String): CountryCommandObject()
        data object BackToOverviewCommandObject: CountryCommandObject()
        data class InspectCityCommandObject(val cityName: String): CountryCommandObject()
    }
}

sealed class CityCommandObject: CommandObject<City> {

    companion object {
        data class HelpObject(val command: LocalizedCommand?): CityCommandObject()
        data class CityValueCommandObject(val value: String): CityCommandObject()
        data class AirportsCommandObject(val displayMode: AirportDisplayMode): CityCommandObject()
        data object NotImplementedYet: CityCommandObject()
        data object BackToCountryCommandObject: CityCommandObject()
    }
}
