package org.some.project.kotlin.countryinspector.v1.country

import org.some.project.kotlin.countryinspector.v1.command.Command

interface Ancestor<C: Command, T: Inspectable<C>> {

    var ancestor: T?

}
