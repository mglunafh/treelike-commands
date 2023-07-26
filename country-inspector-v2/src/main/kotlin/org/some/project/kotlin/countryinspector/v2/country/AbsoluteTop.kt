package org.some.project.kotlin.countryinspector.v2.country

import org.some.project.kotlin.countryinspector.v2.command.CommandObject
import org.some.project.kotlin.countryinspector.v2.command.CommandAction
import org.some.project.kotlin.countryinspector.v2.command.ParseResult

data object AbsoluteTop: Hierarchy {
    override val ancestor: Hierarchy
        get() = throw IllegalAccessException("AbsoluteTop: Ancestor not defined")

    override fun parseCommandObject(args: List<String>): ParseResult<Hierarchy> {
        throw IllegalAccessException("AbsoluteTop: commands not defined")
    }

    override fun createAction(commandObject: CommandObject<Hierarchy>): CommandAction {
        throw IllegalAccessException("AbsoluteTop: cannot create any actions")
    }
}
