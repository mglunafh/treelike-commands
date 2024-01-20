package org.some.project.kotlin.geometry.command.point

object Point {

    val commands = listOf(
        PointIdCommand,
        PointNameCommand,
        PointSetCommand,
        PointShowCommand,
        PointTagCommand
    )
}
