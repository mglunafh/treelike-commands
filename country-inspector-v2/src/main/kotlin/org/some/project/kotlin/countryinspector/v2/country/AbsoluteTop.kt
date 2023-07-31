package org.some.project.kotlin.countryinspector.v2.country

import org.some.project.kotlin.countryinspector.v2.command.CommandObject
import org.some.project.kotlin.countryinspector.v2.command.CommandAction
import org.some.project.kotlin.countryinspector.v2.command.ParseResult
import org.some.project.kotlin.countryinspector.v2.exception.IntegrityViolationException

data object AbsoluteTop: Hierarchy {
    override val ancestor: Hierarchy
        get() = throw IntegrityViolationException("AbsoluteTop: Ancestor not defined")

    override fun parseCommandObject(args: List<String>): ParseResult<Hierarchy> {
        throw IntegrityViolationException("AbsoluteTop: commands not defined")
    }

    override fun createAction(commandObject: CommandObject<Hierarchy>): CommandAction {
        throw IntegrityViolationException("AbsoluteTop: cannot create any actions")
    }
}
