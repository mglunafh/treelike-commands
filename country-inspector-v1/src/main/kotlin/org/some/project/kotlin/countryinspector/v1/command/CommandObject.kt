package org.some.project.kotlin.countryinspector.v1.command

open class CommandObject<out T: Command>(val command: T, open val showHelp: Boolean = false)
