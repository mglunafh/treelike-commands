package org.some.project.kotlin.cmdparsing

import kotlin.reflect.KClass

object Converter {

    private val standardConversionList: MutableList<ConversionRecord<out Any>> = mutableListOf(
        ConversionRecord(String::class) { it },
        ConversionRecord(Int::class) { it.toIntOrNull() },
        ConversionRecord(Float::class) { it.toFloatOrNull() },
        ConversionRecord(Long::class) { it.toLongOrNull() },
        ConversionRecord(Double::class) { it.toDoubleOrNull() }
    )

    fun <T : Any> getConverter(type: KClass<T>): ConvertibleTo<T>? {
        return standardConversionList
            .firstOrNull { it.type == type }?.let {
                @Suppress("UNCHECKED_CAST") (it as ConversionRecord<T>)
            }?.converter
    }

    fun <T: Any> registerConverter(type: KClass<T>, converter: ConvertibleTo<T>) {
        if (standardConversionList.any { it.type == type }) return

        standardConversionList.add(ConversionRecord(type, converter))
    }

    private data class ConversionRecord<T: Any>(val type: KClass<T>, val converter: ConvertibleTo<T>)

}
