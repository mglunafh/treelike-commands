package org.some.project.kotlin.cmdparsing

sealed interface OptionValue

@JvmInline
value class SwitchValue(val switch: Boolean = true): OptionValue

@JvmInline
value class StringValue(val str: String): OptionValue

@JvmInline
value class ListStringValue(val list: List<String>): OptionValue
