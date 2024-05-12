package org.some.project.kotlin.geometry.command

import org.some.project.kotlin.cmdparsing.CommandObjectParser
import org.some.project.kotlin.geometry.model.Triangle

class TriangleScene(val triangle: Triangle) : Scene {

    override val name: String
        get() = name(triangle.type, triangle.id, triangle.name, triangle.color)

    override val commandParsers: List<CommandObjectParser<out TriangleCommand>> = COMMAND_PARSERS

    companion object {
        val COMMAND_PARSERS: List<CommandObjectParser<out TriangleCommand>> = listOf(
            TriangleCommand.TriangleIdCommandParser,
            TriangleCommand.TriangleNameCommandParser,
            TriangleCommand.TriangleColorCommandParser,
            TriangleCommand.TriangleShowCommandParser,
            TriangleCommand.TriangleSetCommand,
            TriangleCommand.TriangleInspectCommand,
            GenericBackCommand.TriangleBackCommand
        )
    }
}
