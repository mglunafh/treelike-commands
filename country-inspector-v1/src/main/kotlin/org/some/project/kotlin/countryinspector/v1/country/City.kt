package org.some.project.kotlin.countryinspector.v1.country

import org.some.project.kotlin.countryinspector.v1.command.Command
import org.some.project.kotlin.countryinspector.v1.command.CommandResult
import org.some.project.kotlin.countryinspector.v1.country.City.Companion.CityCommand
import org.some.project.kotlin.countryinspector.v1.country.Country.Companion.CountryCommand

data class City(val name: String, val population: Int, val mayor: String):
    Inspectable<CityCommand>,
    Ancestor<CountryCommand, Country>
{

    override val commands = listOf(CityHelp, CityName, CityPopulation, Mayor, Sightseeings, BackToCountry, Airports)

    override var ancestor: Country? = null

    override fun performCommand(args: List<String>): CommandResult {
        val commandName = args[0]
        return when (getCommand(commandName)) {
            null -> CommandResult.CommandNotFoundYet
            CityHelp -> CommandResult.OK(help())
            CityName -> CommandResult.OK(name)
            CityPopulation -> CommandResult.OK(population.toString())
            Mayor -> CommandResult.OK(mayor)
            Sightseeings -> CommandResult.NotImplementedYet("Sightseeings are not available now.")
            Airports -> CommandResult.NotImplementedYet("Airports are not available now.")
            BackToCountry -> CommandResult.Back("Country is set for inspection again.")
        }
    }


    companion object {

        sealed class CityCommand(name: String, description: String = ""): Command(name, description)

        object CityName: CityCommand(name = "name", description = "Shows the name of the city.")

        object CityPopulation: CityCommand(name = "population", description = " Shows the number of city residents.")

        object Mayor: CityCommand(name = "mayor", description = "Tells the state head's name.")

        object Sightseeings: CityCommand(name = "sightseeings", description = "Lists local tourist attractions.")

        object Airports: CityCommand(name = "airports", description = "Shows the airports in this city.")

        data object BackToCountry: CityCommand(name = "back", description = "Enters back to the country level.")

        data object CityHelp: CityCommand(name = "help", description = "Shows this help")
    }
}
