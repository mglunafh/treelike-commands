package org.some.project.kotlin.geometry.command

import org.some.project.kotlin.cmdparsing.CommandDefinition
import org.some.project.kotlin.cmdparsing.CommandObjectParser
import org.some.project.kotlin.geometry.SuccessfulParser

sealed class GenericBackCommand<T : CommandObject> : CommandObjectParser<T>, SuccessfulParser<T> {

    override val commandDefinition = CommandDefinition("back", description = "Set the previous scene back")

    data object PointBackCommand : GenericBackCommand<PointBackCommand>(), PointCommand, SuccessfulParser<PointBackCommand> {
        override val result = this
    }

    data object SectionBackCommand : GenericBackCommand<SectionBackCommand>(), SectionCommand, SuccessfulParser<SectionBackCommand> {
        override val result = this
    }
}
