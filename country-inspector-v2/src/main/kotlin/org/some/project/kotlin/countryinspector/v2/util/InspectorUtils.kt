package org.some.project.kotlin.countryinspector.v2.util

import org.some.project.kotlin.countryinspector.v2.command.Command
import org.some.project.kotlin.countryinspector.v2.command.CommandAction
import org.some.project.kotlin.countryinspector.v2.country.Hierarchy

inline fun <H, reified E> createHelpAction(command: Command<H>?): CommandAction
        where H: Hierarchy, E: Enum<E>, E: Command<H> {

    val msg = command?.let(::helpFormatting)
            ?: enumValues<E>().joinToString(separator = "\n", transform = ::helpFormatting)
    return CommandAction.OK(msg)
}

fun <H: Hierarchy> helpFormatting(cmd: Command<H>) = "   ${cmd.commandName} -- ${cmd.description}"
