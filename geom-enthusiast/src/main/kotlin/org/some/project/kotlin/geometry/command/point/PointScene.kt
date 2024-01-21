package org.some.project.kotlin.geometry.command.point

import org.some.project.kotlin.geometry.command.BackCommand
import org.some.project.kotlin.geometry.command.Scene

object PointScene : Scene {

    override val commandParsers = listOf(
        PointIdCommand,
        PointNameCommand,
        PointSetCommand,
        PointShowCommand,
        PointTagCommand,
        BackCommand
    )
}
