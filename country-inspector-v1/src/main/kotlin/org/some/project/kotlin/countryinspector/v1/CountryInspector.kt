package org.some.project.kotlin.countryinspector.v1

import org.some.project.kotlin.countryinspector.v1.CountryInspector.Companion.InspectorCommand
import org.some.project.kotlin.countryinspector.v1.command.Command
import org.some.project.kotlin.countryinspector.v1.command.CommandObject
import org.some.project.kotlin.countryinspector.v1.command.CommandResult
import org.some.project.kotlin.countryinspector.v1.command.ParseResult
import org.some.project.kotlin.countryinspector.v1.country.Ancestor
import org.some.project.kotlin.countryinspector.v1.country.Country
import org.some.project.kotlin.countryinspector.v1.country.Inspectable

class CountryInspector(private val country: Country): Inspectable<InspectorCommand> {

    private var underInspection: Inspectable<Command> = this

    fun run() {
        println("You've entered Country inspection mode.")
        print("Enter a command:> ")

        while (true) {
            val input = readlnOrNull() ?: continue
            val commandArgs = input.split(" ")

            var currentInspectable: Inspectable<*>? = underInspection

            while (currentInspectable != null) {
                when (val result = currentInspectable.performCommand(commandArgs)) {
                    is CommandResult.Exit -> {
                        println(result.message)
                        return
                    }

                    is CommandResult.CommandNotFoundYet -> {
                        if (currentInspectable is Ancestor<*, *>) {
                            currentInspectable = currentInspectable.ancestor ?: throw IllegalStateException("The entity under did not have an ancestor")
                            continue
                        }
                    }

                    is CommandResult.OK,
                    is CommandResult.CommandNotFound,
                    is CommandResult.NotImplementedYet,
                    is CommandResult.NotEnoughParameters,
                    is CommandResult.IncorrectCountry,
                    is CommandResult.CityNotFound -> {
                        println(result.message)
                    }

                    is CommandResult.AddCountry -> {
                        country.ancestor = this
                        underInspection = country
                        println(result.message)
                    }

                    is CommandResult.AddCity -> {
                        result.city.ancestor = result.country
                        underInspection = result.city
                        println(result.message)
                    }

                    is CommandResult.Back -> {
                        if (currentInspectable is CountryInspector) {
                            throw IllegalStateException("Command 'back' got somehow invoked from top-level of Inspector")
                        }
                        if (currentInspectable is Ancestor<*, *>) {
                            underInspection = currentInspectable.ancestor
                                ?: throw IllegalStateException("Command 'back' got somehow invoked for entity without ancestor")
                            println(result.message)
                        }
                    }
                }
                print(":> ")
                break
            }
        }
    }

    override val commands: List<InspectorCommand> = listOf(Exit, ShowCountry, InspectCountry, Help)

    override fun performCommand(args: List<String>): CommandResult {
        val commandName = args[0]
        return when (val command = getCommand(commandName)) {
            null -> CommandResult.CommandNotFound("Could not understand the command '$commandName' for Country Inspector, please, try again.")
            Help -> CommandResult.OK(help())
            Exit -> CommandResult.Exit("Exiting the Country inspection mode.")
            ShowCountry -> CommandResult.OK(country.name)
            is InspectCountry -> {
                when (val value = args.getOrNull(1)) {
                    null -> CommandResult.NotEnoughParameters("Command '${command.name}' needs a country.")
                    country.name -> CommandResult.AddCountry(country, "Country ${country.name} is set for inspection.")
                    else -> CommandResult.IncorrectCountry(value)
                }
            }
        }
    }

    override fun parseSpecificCommand(command: InspectorCommand, args: List<String>): ParseResult<InspectorCommand> {
        if (command is InspectCountry) {
            return when(val value = args[1]) {
                "-h", "--help" -> ParseResult.Success(CommandObject(command = command, showHelp = true))
                else -> ParseResult.Success(InspectCountryCommandObject(country = value))
            }
        }
        return super.parseSpecificCommand(command, args)
    }

    companion object {

        sealed class InspectorCommand(name: String, description: String = ""): Command(name, description)

        data object Exit: InspectorCommand(name = "exit", description = "Exits from from the inspection mode.")

        data object Help: InspectorCommand(name = "help", description = "Shows this help.")

        data object ShowCountry: InspectorCommand(name = "show", description = "Shows the country to observe.")

        data object InspectCountry: InspectorCommand(name = "inspect", description = "Sets the country for inspection.")

        data class InspectCountryCommandObject(val country: String, override val showHelp: Boolean = false):
            CommandObject<InspectCountry>(command = InspectCountry, showHelp = showHelp)
    }
}
