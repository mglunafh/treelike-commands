package org.some.project.kotlin.countryinspector.v1.country

import org.some.project.kotlin.countryinspector.v1.CountryInspector
import org.some.project.kotlin.countryinspector.v1.CountryInspector.Companion.InspectorCommand
import org.some.project.kotlin.countryinspector.v1.command.Command
import org.some.project.kotlin.countryinspector.v1.command.CommandResult
import org.some.project.kotlin.countryinspector.v1.country.Country.Companion.CountryCommand

data class Country(val name: String, val population: Int, val headOfState: String, val cities: List<City>):
    Inspectable<CountryCommand>,
    Ancestor<InspectorCommand, CountryInspector> {

    override val commands =
        listOf(CountryName, CountryPopulation, HeadOfState, Cities, InspectCity, BackToInspector, CountryHelp)

    override var ancestor: CountryInspector? = null

    override fun performCommand(args: List<String>): CommandResult {
        val commandName = args[0]
        return when (val command = getCommand(commandName)) {
            null -> CommandResult.CommandNotFoundYet
            CountryHelp -> CommandResult.OK(help())
            Cities -> CommandResult.OK(cities.joinToString(", ") { it.name })
            CountryName -> CommandResult.OK(name)
            CountryPopulation -> CommandResult.OK(population.toString())
            HeadOfState -> CommandResult.OK(headOfState)
            InspectCity -> {
                args.getOrNull(1)?.let { cityName ->
                    cities.find { it.name == cityName }?.let { city ->
                        CommandResult.AddCity(this, city, "City ${city.name} is set for inspection.")
                    } ?: CommandResult.CityNotFound(cityName)
                } ?: CommandResult.NotEnoughParameters("Command '${command.name}' needs a city.")
            }

            BackToInspector -> CommandResult.Back("Country inspection mode is set.")
        }
    }

    companion object {

        sealed class CountryCommand(name: String, description: String): Command(name, description)

        data object CountryName: CountryCommand(name = "name", description = "Shows the name of the country.")

        data object CountryPopulation:
            CountryCommand(name = "population", description = " Shows the number of citizens.")

        data object HeadOfState: CountryCommand(name = "head", description = "Tells the state head's name.")

        data object Cities: CountryCommand(name = "cities", description = "Lists the names of the cities.")

        data object InspectCity: CountryCommand(name = "inspect", description = "Sets the city for inspection.")

        data object BackToInspector: CountryCommand(name = "back", description = "Enters back to the Country inspection mode.")

        data object CountryHelp: CountryCommand(name = "help", description = "Shows this help.")

    }
}
