package org.some.project.kotlin.geometry.command

import org.some.project.kotlin.cmdparsing.CommandObjectParser
import org.some.project.kotlin.geometry.model.Section

class SectionScene(val section: Section): Scene {

    override val name: String
        get() = name(section.type, section.id, section.name, section.color)

    override val commandParsers: List<CommandObjectParser<out SectionCommand>> = COMMAND_PARSERS

    companion object {
        val COMMAND_PARSERS: List<CommandObjectParser<out SectionCommand>> = listOf(
            SectionCommand.SectionIdCommandParser,
            SectionCommand.SectionNameCommandParser,
            SectionCommand.SectionColorCommandParser,
            SectionCommand.SectionTagCommand,
            SectionCommand.SectionSetCommand,
            SectionCommand.SectionShowCommand,
            SectionCommand.SectionInspectCommand,
            GenericBackCommand.SectionBackCommand
        )
    }
}
