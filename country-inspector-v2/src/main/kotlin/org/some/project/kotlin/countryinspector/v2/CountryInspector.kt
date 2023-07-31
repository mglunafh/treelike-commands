package org.some.project.kotlin.countryinspector.v2

import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinFeature
import com.fasterxml.jackson.module.kotlin.KotlinModule
import org.some.project.kotlin.countryinspector.v2.command.CommandAction
import org.some.project.kotlin.countryinspector.v2.command.ParseErrorType.Companion.ArgumentListEmpty
import org.some.project.kotlin.countryinspector.v2.command.ParseErrorType.Companion.MissingCommandAfterPrefix
import org.some.project.kotlin.countryinspector.v2.command.ParseErrorType.Companion.MissingRequiredParameter
import org.some.project.kotlin.countryinspector.v2.command.ParseErrorType.Companion.UnrecognizedCommand
import org.some.project.kotlin.countryinspector.v2.command.ParseErrorType.Companion.UnrecognizedCommandYet
import org.some.project.kotlin.countryinspector.v2.command.ParseErrorType.Companion.UnrecognizedParameter
import org.some.project.kotlin.countryinspector.v2.command.ParseResult
import org.some.project.kotlin.countryinspector.v2.country.Country
import org.some.project.kotlin.countryinspector.v2.country.Hierarchy
import org.some.project.kotlin.countryinspector.v2.country.Overview
import org.some.project.kotlin.countryinspector.v2.exception.IntegrityViolationException
import java.io.FileInputStream
import java.io.IOException

class CountryInspector(overview: Overview) {

    private var underInspection: Hierarchy = overview

    fun run() {
        println("You've entered Country Overview application.")
        print("Enter a command:> ")

        while (true) {
            val input = readlnOrNull() ?: continue
            val commandArgs = input.split(" ")

            when (val parseResult = getParseResult(commandArgs)) {
                is ParseResult.ParseError -> {
                    when (val err = parseResult.error) {
                        ArgumentListEmpty -> println("Argument list is absent.")
                        is MissingRequiredParameter -> println("Missing required argument for command '${err.commandName}'.")
                        is UnrecognizedParameter -> println("Unrecognized parameter '${err.parameterName}' for command '${err.commandName}'.")
                        is UnrecognizedCommand -> println("Unrecognized command '${err.commandName}'.")
                        is UnrecognizedCommandYet -> throw IntegrityViolationException("Somehow 'UnrecognizedCommandYet' got returned...")
                        is MissingCommandAfterPrefix -> println("Missing command for the level '${err.prefixName}'.")
                    }
                }
                is ParseResult.ParseSuccess -> {
                    when (val action = underInspection.createAction(parseResult.result)) {
                        is CommandAction.Exit ->  {
                            println(action.message)
                            return
                        }
                        is CommandAction.OK,
                        is CommandAction.CityNotFound,
                        is CommandAction.IncorrectCountry -> {
                            println(action.message)
                        }
                        is CommandAction.InspectCountry -> {
                            underInspection = action.country
                            println(action.message)
                        }

                        is CommandAction.Back -> {
                            underInspection = action.ancestor
                            println(action.message)
                        }
                        is CommandAction.InspectCity -> {
                            underInspection = action.city
                            println(action.message)
                        }
                        is CommandAction.LoadCountry -> {
                            try {
                                val newCountry = FileInputStream(action.filename).use {
                                    objectMapper.readValue(it, Country::class.java)
                                }
                                val overview = Overview(newCountry)
                                underInspection = overview
                                println(action.message)
                            } catch (ex: IOException) {
                                println("${ex.javaClass}: ${ex.message}")
                            }
                            catch (ex: JsonProcessingException) {
                                println("${ex.javaClass}: ${ex.message}")
                            }
                        }
                    }
                }
            }
            print(":> ")
        }
    }

    private fun getParseResult(commandArgs: List<String>): ParseResult<Hierarchy> {
        var tempCommandParser: Hierarchy = underInspection
        var parseResult = tempCommandParser.parseCommandObject(commandArgs)
        // looking for command up in the hierarchy
        while (parseResult is ParseResult.ParseError && parseResult.error is UnrecognizedCommandYet) {
            tempCommandParser = tempCommandParser.ancestor
            parseResult = tempCommandParser.parseCommandObject(commandArgs)
        }
        return parseResult
    }

    companion object {
        val objectMapper: ObjectMapper by lazy {
            val kotlinModule = KotlinModule.Builder()
                .configure(KotlinFeature.NullToEmptyCollection, true)
                .configure(KotlinFeature.NullToEmptyMap, true)
                .configure(KotlinFeature.StrictNullChecks, true)
                .build()
            ObjectMapper().registerModule(kotlinModule)
        }
    }
}
