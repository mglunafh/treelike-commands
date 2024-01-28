package org.some.project.kotlin.geometry.command

object OverviewScene : Scene {

    override val commandParsers = listOf(
        OverviewCommand.OverviewListCommand,
        OverviewCommand.OverviewCreateCommand,
        OverviewCommand.OverviewInspectCommand,
        OverviewCommand.OverviewLoadCommand,
        OverviewCommand.OverviewSaveCommand
    )
}
