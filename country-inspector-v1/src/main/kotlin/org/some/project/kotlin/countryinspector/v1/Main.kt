package org.some.project.kotlin.countryinspector.v1

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinFeature
import com.fasterxml.jackson.module.kotlin.KotlinModule
import org.some.project.kotlin.countryinspector.v1.country.Country

fun main() {

    val objectMapper = createObjectMapper()

    val resourceAsStream = object {}.javaClass.classLoader.getResourceAsStream("country.json")
    val country = objectMapper.readValue(resourceAsStream, Country::class.java)

    val countryInspector = CountryInspector(country)
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
