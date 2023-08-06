package org.some.project.kotlin.countryinspector.v2.command

import org.some.project.kotlin.countryinspector.v2.country.City
import org.some.project.kotlin.countryinspector.v2.country.Country
import org.some.project.kotlin.countryinspector.v2.country.Hierarchy

sealed class CommandAction {

    data class OK(val message: String): CommandAction()
    data object Exit: CommandAction()
    data class IncorrectCountry(val countryName: String): CommandAction()
    data class LoadCountry(val filename: String): CommandAction()
    data class InspectCountry(val country: Country): CommandAction()
    data class InspectCity(val city: City): CommandAction()
    data class CityNotFound(val cityName: String): CommandAction()
    data class Back(val ancestor: Hierarchy, val message: String): CommandAction()
}
