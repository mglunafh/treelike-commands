package org.some.project.kotlin.cmdparsing.command

import org.some.project.kotlin.cmdparsing.*

data class PersonObject(
    val name: String,
    val surname: String,
    val age: Int,
    val middleName: String? = null,
    val hairColor: Color? = null,
    val kids: List<String> = listOf(),
    val coord: Pair<Double, Double>? = null
) {

    companion object {
        val defName = StringFlagDefinition("name", required = true)
        val defSurname = StringFlagDefinition("surname", required = true)
        val defAge = IntFlagDefinition("age", required = true)
        val defHairColor = ParameterDefinition("hair-color", Color::class)
        val defKids = StringFlagDefinition("kids", delimiter = ",")
        val defCoord = ParameterDefinition("coord", Double::class, arity = 2)

    val commandDefinition = CommandDefinition("person", 3, 2, listOf(defAge, defHairColor, defKids, defCoord))

        fun parse(values: ValueParseObject): PersonObject {
            val posArgs = values.positionalArguments
            val (name, middleName, surname ) = when {
                posArgs.size == 2 -> Triple(posArgs[0], null, posArgs[1])
                posArgs.size == 3 -> Triple(posArgs[0], posArgs[1], posArgs[2])
                else -> throw IllegalArgumentException("Positional args should 2 or 3 values, got these instead: $posArgs")
            }

            val age = values.get(defAge)
            val hairColor = values.getNullable(defHairColor)
            val kids = values.getListOrDefault(defKids, listOf())
            val coordinate = values.getListOrNull(defCoord) ?.let { Pair(it[0], it[1]) }

            return PersonObject(name, surname, age, middleName, hairColor, kids, coordinate)
        }
    }
}
