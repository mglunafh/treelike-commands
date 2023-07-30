package org.some.project.kotlin.countryinspector.v2.l10n

import org.some.project.kotlin.countryinspector.v2.command.CityCommand
import org.some.project.kotlin.countryinspector.v2.command.CountryCommand
import org.some.project.kotlin.countryinspector.v2.command.OverviewCommand
import org.some.project.kotlin.countryinspector.v2.localized
import java.lang.IllegalStateException
import java.util.Properties

object LocalizationHolder {

    lateinit var overviewPrefix: String
    lateinit var countryPrefix: String
    lateinit var cityPrefix: String

    val overviewCommands: MutableMap<String, Pair<OverviewCommand, LocalizedCommand>> = mutableMapOf()

    val countryCommands: MutableMap<String, Pair<CountryCommand, LocalizedCommand>> = mutableMapOf()

    val cityCommands: MutableMap<String, Pair<CityCommand, LocalizedCommand>> = mutableMapOf()

    internal fun init(language: Lang) {
        println("Language: '$language'")

        val propertiesFileName = "commands-eng.properties"
        val props = Properties()

        object {}.javaClass.classLoader.getResourceAsStream(propertiesFileName)
            ?.let { inputStream -> inputStream.use { props.load(it) } }
            ?: throw IllegalArgumentException("could not find file with translations: $propertiesFileName")

        initOverviewCommands(props)
        initCountryCommands(props)
        initCityCommands(props)
    }

    private fun initOverviewCommands(props: Properties) {
        overviewPrefix = props.getProperty("overview.prefix")

        OverviewCommand.values().forEach {
            val cmd = when (it) {
                OverviewCommand.Exit -> localized(props, "overview.exit", "overview.exit.desc")
                OverviewCommand.Help -> localized(props, "overview.help", "overview.help.desc")
                OverviewCommand.ShowCountry -> localized(props, "overview.show-country", "overview.show-country.desc")
                OverviewCommand.InspectCountry -> localized(props, "overview.inspect", "overview.inspect.desc")
            }

            val alreadyPut = overviewCommands.put(cmd.commandName, Pair(it, cmd))
            alreadyPut?.let { (prevCmd, _) ->
                val msg = "Overview group of commands: $prevCmd and $it have the same command '${cmd.commandName}'."
                throw IllegalStateException(msg)
            }
        }
    }

    private fun initCountryCommands(props: Properties) {
        countryPrefix = props.getProperty("country.prefix")

        CountryCommand.values().forEach {
            val cmd = when (it) {
                CountryCommand.CountryName -> localized(props, "country.name", "country.name.desc")
                CountryCommand.CountryPopulation -> localized(props, "country.population", "country.population.desc")
                CountryCommand.HeadOfState -> localized(props, "country.head-of-state", "country.head-of-state.desc")
                CountryCommand.Cities -> localized(props, "country.cities", "country.cities.desc")
                CountryCommand.InspectCity -> localized(props, "country.inspect", "country.inspect.desc")
                CountryCommand.BackToOverview -> localized(props, "country.back", "country.back.desc")
                CountryCommand.CountryHelp -> localized(props, "country.help", "country.help.desc")
            }

            val alreadyPut = countryCommands.put(cmd.commandName, Pair(it, cmd))
            alreadyPut?.let { (prevCmd, _) ->
                val msg = "Country group of commands: $prevCmd and $it have the same command '${cmd.commandName}'."
                throw IllegalStateException(msg)
            }
        }

    }

    private fun initCityCommands(props: Properties) {
        cityPrefix = props.getProperty("city.prefix")

        CityCommand.values().forEach {
            val cmd = when (it) {
                CityCommand.CityName -> localized(props, "city.name", "city.name.desc")
                CityCommand.CityPopulation -> localized(props, "city.population", "city.population.desc")
                CityCommand.Mayor -> localized(props, "city.mayor", "city.mayor.desc")
                CityCommand.Sightseeings -> localized(props, "city.sightseeings", "city.sightseeings.desc")
                CityCommand.Airports -> localized(props, "city.airports", "city.airports.desc")
                CityCommand.BackToCountry -> localized(props, "city.back", "city.back.desc")
                CityCommand.CityHelp -> localized(props, "city.help", "city.help.desc")
            }

            val alreadyPut = cityCommands.put(cmd.commandName, Pair(it, cmd))
            alreadyPut?.let { (prevCmd, _) ->
                val msg = "City group of commands: $prevCmd and $it have the same command '${cmd.commandName}'."
                throw IllegalStateException(msg)
            }
        }
    }
}
