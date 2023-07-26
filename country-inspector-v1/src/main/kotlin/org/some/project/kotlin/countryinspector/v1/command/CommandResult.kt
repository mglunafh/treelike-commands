package org.some.project.kotlin.countryinspector.v1.command

import org.some.project.kotlin.countryinspector.v1.country.City
import org.some.project.kotlin.countryinspector.v1.country.Country

sealed class CommandResult(open val message: String) {

    data object CommandNotFoundYet: CommandResult("Command is not recognized.")
    data class CommandNotFound(override val message: String): CommandResult(message)
    data class OK(override val message: String): CommandResult(message)
    data class Exit(override val message: String): CommandResult(message)
    data class NotImplementedYet(override val message: String): CommandResult(message)
    data class NotEnoughParameters(override val message: String): CommandResult(message)
    data class IncorrectCountry(val countryName: String): CommandResult("Wrong country '$countryName', cannot inspect it.")
    data class AddCountry(val country: Country, override val message: String): CommandResult(message)
    data class AddCity(val country: Country, val city: City, override val message: String): CommandResult(message)
    data class CityNotFound(val cityName: String): CommandResult("There is no such city as $cityName.")
    data class Back(override val message: String): CommandResult(message)

}
