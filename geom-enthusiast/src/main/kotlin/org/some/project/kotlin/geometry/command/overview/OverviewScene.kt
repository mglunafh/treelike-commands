package org.some.project.kotlin.geometry.command.overview

import org.some.project.kotlin.geometry.command.Scene

object OverviewScene : Scene {

    override val commandParsers = listOf(
        OverviewListCommand,
        OverviewCreateCommand,
        OverviewInspectCommand,
        OverviewLoadCommand,
        OverviewSaveCommand
    )
}
