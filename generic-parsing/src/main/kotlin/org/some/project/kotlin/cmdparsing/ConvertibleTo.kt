package org.some.project.kotlin.cmdparsing

fun interface ConvertibleTo<T> {
    fun convert(value: String): T?
}
