package org.some.project.kotlin.countryinspector.v2.command

import org.some.project.kotlin.countryinspector.v2.country.City
import org.some.project.kotlin.countryinspector.v2.country.Country
import org.some.project.kotlin.countryinspector.v2.country.Hierarchy

sealed class CommandAction(open val message: String) {

    data class OK(override val message: String): CommandAction(message)
    data class Exit(override val message: String): CommandAction(message)
    data class IncorrectCountry(val countryName: String): CommandAction("Wrong country '$countryName', cannot inspect it.")
    data class LoadCountry(val filename: String): CommandAction("Loaded country data from file '$filename'.")
    data class InspectCountry(val country: Country): CommandAction("Country ${country.name} is set for inspection.")
    data class InspectCity(val city: City): CommandAction("City ${city.name} is set for inspection.")
    data class CityNotFound(val cityName: String): CommandAction("There is no such city as '$cityName'.")
    data class Back(val ancestor: Hierarchy, override val message: String): CommandAction(message)
}
