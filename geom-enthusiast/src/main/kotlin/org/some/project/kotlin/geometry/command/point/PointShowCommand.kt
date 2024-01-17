package org.some.project.kotlin.geometry.command.point

import org.some.project.kotlin.cmdparsing.*

data class PointShowCommand(val short: Boolean) {

    companion object: CommandObjectParser<PointShowCommand> {
        val defShort = BooleanSwitchDefinition("short", default = false)
        override val commandDefinition = CommandDefinition("show", paramDefinitions = listOf(defShort))

        override fun parse(valueParseObject: ValueParseObject): ParseResult<PointShowCommand> {
            val showShort = valueParseObject.get(defShort)
            return ParseResult.ParseSuccess(PointShowCommand(showShort))
        }
    }
}
