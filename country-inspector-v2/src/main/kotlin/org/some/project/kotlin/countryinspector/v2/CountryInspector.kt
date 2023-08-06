package org.some.project.kotlin.countryinspector.v2

import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinFeature
import com.fasterxml.jackson.module.kotlin.KotlinModule
import org.some.project.kotlin.countryinspector.v2.command.CommandAction
import org.some.project.kotlin.countryinspector.v2.command.ParseErrorType.Companion.UnrecognizedCommandYet
import org.some.project.kotlin.countryinspector.v2.command.ParseResult
import org.some.project.kotlin.countryinspector.v2.country.Country
import org.some.project.kotlin.countryinspector.v2.country.Hierarchy
import org.some.project.kotlin.countryinspector.v2.country.Overview
import org.some.project.kotlin.countryinspector.v2.exception.IntegrityViolationException
import org.some.project.kotlin.countryinspector.v2.l10n.MessageSource
import java.io.FileInputStream
import java.io.IOException

class CountryInspector(overview: Overview) {

    private var underInspection: Hierarchy = overview

    fun run() {
        print(MessageSource["greeting"])

        while (true) {
            val input = readlnOrNull() ?: continue
            val commandArgs = input.split(" ")

            when (val parseResult = getParseResult(commandArgs)) {
                is ParseResult.ParseError -> {
                    when (val err = parseResult.error) {
                        is UnrecognizedCommandYet -> throw IntegrityViolationException("Somehow 'UnrecognizedCommandYet' got returned...")
                        else -> println(MessageSource.localizeParseError(err))
                    }
                }
                is ParseResult.ParseSuccess -> {
                    when (val action = underInspection.createAction(parseResult.result)) {
                        is CommandAction.Exit ->  {
                            println(MessageSource.localizeCommandAction(action))
                            return
                        }
                        is CommandAction.OK,
                        is CommandAction.CityNotFound,
                        is CommandAction.IncorrectCountry -> {
                            println(MessageSource.localizeCommandAction(action))
                        }
                        is CommandAction.InspectCountry -> {
                            underInspection = action.country
                            println(MessageSource.localizeCommandAction(action))
                        }
                        is CommandAction.Back -> {
                            underInspection = action.ancestor
                            println(action.message)
                        }
                        is CommandAction.InspectCity -> {
                            underInspection = action.city
                            println(MessageSource.localizeCommandAction(action))
                        }
                        is CommandAction.LoadCountry -> {
                            try {
                                val newCountry = FileInputStream(action.filename).use {
                                    objectMapper.readValue(it, Country::class.java)
                                }
                                val overview = Overview(newCountry)
                                underInspection = overview
                                println(MessageSource.localizeCommandAction(action))
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
