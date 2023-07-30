package org.some.project.kotlin.countryinspector.v2.l10n

data class LocalizedCommand(val name: String, val description: String) {

    init {
        require(name.isNotBlank())
    }
}
