package org.some.project.kotlin.geometry.command

import org.some.project.kotlin.cmdparsing.CommandObjectParser

interface Scene {

    val commandParsers : List<CommandObjectParser<out CommandObject>>

    fun determineCommandParser(commandName: String): CommandObjectParser<out CommandObject>? {
        return commandParsers.firstOrNull { it.commandDefinition.commandName == commandName }
    }
}
