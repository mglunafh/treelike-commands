package org.some.project.kotlin.countryinspector.v2

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinFeature
import com.fasterxml.jackson.module.kotlin.KotlinModule
import org.some.project.kotlin.countryinspector.v2.country.Country
import org.some.project.kotlin.countryinspector.v2.country.Overview

fun main() {

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
