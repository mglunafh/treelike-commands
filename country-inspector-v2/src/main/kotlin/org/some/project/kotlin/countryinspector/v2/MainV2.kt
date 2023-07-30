package org.some.project.kotlin.countryinspector.v2

import org.some.project.kotlin.countryinspector.v2.country.Country
import org.some.project.kotlin.countryinspector.v2.country.Overview
import org.some.project.kotlin.countryinspector.v2.l10n.Lang
import org.some.project.kotlin.countryinspector.v2.l10n.LocalizationHolder

fun main(args: Array<String>) {

    val langArg = args.getOrNull(0)?.let { if (it in listOf("ru", "rus")) Lang.RUS else Lang.ENG } ?: Lang.ENG
    LocalizationHolder.init(langArg)

    val resourceAsStream = object {}.javaClass.classLoader.getResourceAsStream("country.json")
    val country = CountryInspector.objectMapper.readValue(resourceAsStream, Country::class.java)
    val overview = Overview(country)
    val countryInspector = CountryInspector(overview)

    countryInspector.run()
}
