package org.some.project.kotlin.countryinspector.v2.l10n

import org.some.project.kotlin.countryinspector.v2.command.CommandAction
import org.some.project.kotlin.countryinspector.v2.command.ParseErrorType
import org.some.project.kotlin.countryinspector.v2.exception.IntegrityViolationException
import java.io.InputStreamReader
import java.nio.charset.Charset
import java.util.Properties

object MessageSource {

    private val props: Properties = Properties()

    internal fun init(language: Lang) {
        val propertiesFileName = when (language) {
            Lang.ENG -> "messages-eng.properties"
            Lang.RUS -> "messages-rus.properties"
        }

        object {}.javaClass.classLoader.getResourceAsStream(propertiesFileName)
            ?.let { inputStream ->
                inputStream.use { InputStreamReader(it, Charset.forName("UTF-8")).use { utf8Stream -> props.load(utf8Stream) } } }
            ?: throw IllegalArgumentException("could not find file with translations: $propertiesFileName")
    }

    operator fun get(key: String): String {
        return props.getProperty(key)
    }

    fun localizeParseError(error: ParseErrorType): String {
        return when (error) {
            ParseErrorType.Companion.ArgumentListEmpty -> props.getProperty("argument.empty-list")
            is ParseErrorType.Companion.MissingCommandAfterPrefix -> {
                props.getProperty("argument.miss-command-after-prefix").format(error.prefixName)
            }
            is ParseErrorType.Companion.MissingRequiredParameter -> {
                props.getProperty("argument.miss-required-parameter").format(error.commandName)
            }
            is ParseErrorType.Companion.UnrecognizedCommand -> {
                props.getProperty("argument.unrecognized-command").format(error.commandName)
            }
            is ParseErrorType.Companion.UnrecognizedParameter -> {
                props.getProperty("argument.unrecognized-parameter").format(error.parameterName, error.commandName)
            }
            is ParseErrorType.Companion.UnrecognizedCommandYet -> {
                throw IntegrityViolationException("Should be unreachable, this value is for control flow")
            }
        }
    }

    fun localizeCommandAction(action: CommandAction): String {
        return when (action) {
            is CommandAction.OK -> action.message
            CommandAction.Exit -> props.getProperty("action.exit")
            is CommandAction.Back -> action.message
            is CommandAction.LoadCountry -> props.getProperty("action.load-country").format(action.filename)
            is CommandAction.IncorrectCountry -> props.getProperty("action.incorrect-country").format(action.countryName)
            is CommandAction.InspectCountry -> props.getProperty("action.inspect-country").format(action.country.name)
            is CommandAction.InspectCity -> props.getProperty("action.inspect-city").format(action.city.name)
            is CommandAction.CityNotFound -> props.getProperty("action.city-not-found").format(action.cityName)
        }
    }
}
