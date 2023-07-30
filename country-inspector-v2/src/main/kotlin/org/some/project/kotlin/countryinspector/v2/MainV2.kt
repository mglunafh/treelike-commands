package org.some.project.kotlin.countryinspector.v2

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinFeature
import com.fasterxml.jackson.module.kotlin.KotlinModule
import org.some.project.kotlin.countryinspector.v2.country.Country
import org.some.project.kotlin.countryinspector.v2.country.Overview
import org.some.project.kotlin.countryinspector.v2.l10n.Lang
import org.some.project.kotlin.countryinspector.v2.l10n.LocalizationHolder

fun main(args: Array<String>) {

    val langArg = args.getOrNull(0)?.let { if (it in listOf("ru", "rus")) Lang.RUS else Lang.ENG } ?: Lang.ENG
    LocalizationHolder.init(langArg)

    val objectMapper = createObjectMapper()

    val resourceAsStream = object {}.javaClass.classLoader.getResourceAsStream("country.json")
    val country = objectMapper.readValue(resourceAsStream, Country::class.java)
    val overview = Overview(country)
    val countryInspector = CountryInspector(overview)

    countryInspector.run()
}

fun createObjectMapper(): ObjectMapper {
    val kotlinModule = KotlinModule.Builder()
        .configure(KotlinFeature.NullToEmptyCollection, true)
        .configure(KotlinFeature.NullToEmptyMap, true)
        .configure(KotlinFeature.StrictNullChecks, true)
        .build()
    return ObjectMapper().registerModule(kotlinModule)
}
