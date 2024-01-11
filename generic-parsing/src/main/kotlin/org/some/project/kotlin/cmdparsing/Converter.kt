package org.some.project.kotlin.cmdparsing

import kotlin.reflect.KClass
import kotlin.reflect.safeCast

object Converter {

    private val standardConversionList: MutableList<ConversionRecord<*>> = mutableListOf(
        ConversionRecord(String::class) { it },
        ConversionRecord(Int::class) { it.toIntOrNull() },
        ConversionRecord(Float::class) { it.toFloatOrNull() },
        ConversionRecord(Long::class) { it.toLongOrNull() },
        ConversionRecord(Double::class) { it.toDoubleOrNull() }
    )

    fun <T : Any> getConverter(type: KClass<T>): ConvertibleTo<T>? {
        return standardConversionList.firstOrNull { it.type == type }?.converter as ConvertibleTo<T>?
    }

    fun <T: Any> registerConverter(type: KClass<T>, converter: ConvertibleTo<T>) {
        if (standardConversionList.any { it.type == type }) return

        standardConversionList.add(ConversionRecord(type, converter))
    }

    fun <T: Any> castList(listValue: Any?, type: KClass<T>): List<T>? {
        if (listValue == null) return null
        if (listValue !is List<*>) {
            throw IllegalArgumentException("Parameter was not a list!")
        }
        return MutableList(listValue.size) {
            type.safeCast(listValue[it]) ?:
                throw IllegalArgumentException("List contains a value '${listValue[it]}' of type incompatible with $type")
        }
    }

    private data class ConversionRecord<T: Any>(val type: KClass<T>, val converter: ConvertibleTo<T>)

}
