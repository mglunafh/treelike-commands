package org.some.project.kotlin.geometry.command

import org.some.project.kotlin.cmdparsing.CommandObjectParser
import org.some.project.kotlin.geometry.model.Point

class PointScene(val point: Point) : Scene {

    override val name: String
        get() = name(point.type, point.id, point.name, point.color)

    override val commandParsers: List<CommandObjectParser<out PointCommand>> = COMMAND_PARSERS

    companion object {
        val COMMAND_PARSERS: List<CommandObjectParser<out PointCommand>> = listOf(
            PointCommand.PointIdCommand,
            PointCommand.PointNameCommand,
            PointCommand.PointSetCommand,
            PointCommand.PointShowCommand,
            PointCommand.PointTagCommand,
            GenericBackCommand.PointBackCommand
        )
    }
}
