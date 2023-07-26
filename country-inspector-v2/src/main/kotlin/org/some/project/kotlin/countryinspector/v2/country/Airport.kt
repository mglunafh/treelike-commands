package org.some.project.kotlin.countryinspector.v2.country

data class Airport(val name: String, val shortcut: String) {

    override fun toString(): String = "$name ($shortcut)"

    companion object {
        enum class AirportDisplayMode(val mode: String) {
            NAME("name"), CODE("code"), FULL("full")
        }
    }
}
