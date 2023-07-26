package org.some.project.kotlin.countryinspector.v2.command

import org.some.project.kotlin.countryinspector.v2.country.Hierarchy

interface Command<H: Hierarchy> {
    val commandName: String
    val description: String
}
