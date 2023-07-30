package org.some.project.kotlin.countryinspector.v2.l10n

data class LocalizedCommand(val commandName: String, val description: String) {
    init {
        require(commandName.isNotBlank())
    }
}
