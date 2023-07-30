package org.some.project.kotlin.countryinspector.v2.util

import org.some.project.kotlin.countryinspector.v2.command.CommandAction
import org.some.project.kotlin.countryinspector.v2.country.City
import org.some.project.kotlin.countryinspector.v2.country.Country
import org.some.project.kotlin.countryinspector.v2.country.Hierarchy
import org.some.project.kotlin.countryinspector.v2.country.Overview
import org.some.project.kotlin.countryinspector.v2.l10n.LocalizationHolder
import org.some.project.kotlin.countryinspector.v2.l10n.LocalizedCommand
import java.lang.IllegalArgumentException
import kotlin.reflect.KClass


fun createHelpAction(cmd: LocalizedCommand): CommandAction {
    return CommandAction.OK(helpFormatting(cmd))
}

fun <H: Hierarchy> createHelpAction(clazz: KClass<H>): CommandAction {

    val commands = when(clazz) {
        Overview::class -> LocalizationHolder.overviewCommands.values.map { it.second }
        Country:: class -> LocalizationHolder.countryCommands.values.map { it.second }
        City::class -> LocalizationHolder.cityCommands.values.map { it.second }
        else -> throw IllegalArgumentException("$clazz does not have commands and cannot be localized")
    }
    val msg = commands.joinToString(separator = "\n", transform = ::helpFormatting)
    return CommandAction.OK(msg)
}

fun helpFormatting(cmd: LocalizedCommand) = "   ${cmd.name} -- ${cmd.description}"
