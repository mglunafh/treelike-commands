package org.some.project.kotlin.geometry.command.point

import org.some.project.kotlin.cmdparsing.*

data class PointShowCommand(val short: Boolean) {

    companion object: CommandObjectParser<PointShowCommand> {
        val defShort = BooleanSwitchDefinition("--short", default = false, description = "Show info in concise form")
        override val commandDefinition = CommandDefinition(
            "show",
            listOf(defShort),
            description = "Show information about the point")

        override fun parse(arguments: ValueParseObject): ParseResult<PointShowCommand> {
            val showShort = arguments.get(defShort)
            return ParseResult.ParseSuccess(PointShowCommand(showShort))
        }
    }
}
