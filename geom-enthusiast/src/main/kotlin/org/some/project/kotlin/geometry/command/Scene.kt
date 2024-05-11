package org.some.project.kotlin.geometry.command

import org.some.project.kotlin.cmdparsing.CommandObjectParser
import org.some.project.kotlin.geometry.model.Color
import org.some.project.kotlin.geometry.model.Id
import org.some.project.kotlin.geometry.model.Name
import org.some.project.kotlin.geometry.model.ShapeType
import java.lang.StringBuilder

interface Scene {

    val name: String
    val commandParsers : List<CommandObjectParser<out CommandObject>>

    fun determineCommandParser(commandName: String): CommandObjectParser<out CommandObject>? {
        return commandParsers.firstOrNull { it.commandDefinition.commandName == commandName }
    }

    fun name(shapeType: ShapeType, id: Id, name: Name?, color: Color): String {
        val nameString = name?.name ?: "${shapeType.showName}[${id.id}]"
        val sb = StringBuilder(nameString)
        if (color != Color.WHITE) {
            sb.insert(0, color.controlSequence()).append(Color.CONSOLE_RESET)
        }
        return sb.toString()
    }
}
