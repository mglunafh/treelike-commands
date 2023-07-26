package org.some.project.kotlin.countryinspector.v2.country

import org.some.project.kotlin.countryinspector.v2.command.CommandObject
import org.some.project.kotlin.countryinspector.v2.command.CommandAction
import org.some.project.kotlin.countryinspector.v2.command.ParseResult

sealed interface Hierarchy {

    val ancestor: Hierarchy

    fun parseCommandObject(args: List<String>): ParseResult<Hierarchy>

    fun createAction(commandObject: CommandObject<Hierarchy>): CommandAction

}
