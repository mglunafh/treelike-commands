package org.some.project.kotlin.cmdparsing

import kotlin.reflect.KClass

data class CommandDefinition(
    val commandName: String,
    val positionalArguments: Int,
    val requiredPositionalArguments: Int,
    val paramDefinitions: List<ParameterDefinition<out Any>>
)

open class ParameterDefinition<T : Any>(
    open val name: String,
    open val type: KClass<T>,
    open val arity: Int = 1,
    open val required: Boolean = false,
    open val default: T? = null,
    open val withDelimiter: String? = null
)

data class BooleanSwitchDefinition(
    override val name: String,
    override val default: Boolean? = null
) : ParameterDefinition<Boolean>(name, Boolean::class, arity = 0, default = default)

data class FlagDefinition<T : Any>(
    override val name: String,
    override val type: KClass<T>,
    override val required: Boolean = false,
    override val default: T? = null,
    override val withDelimiter: String? = null
) : ParameterDefinition<T>(name, type, required = required,default = default, withDelimiter = withDelimiter)

data class IntFlagDefinition(
    override val name: String,
    override val required: Boolean = false,
    override val default: Int? = null,
    override val withDelimiter: String? = null
) : ParameterDefinition<Int>(name, Int::class, required = required, default = default, withDelimiter = withDelimiter)

data class StringFlagDefinition(
    override val name: String,
    override val required: Boolean = false,
    override val default: String? = null,
    override val withDelimiter: String? = null
) : ParameterDefinition<String>(name, String::class, required = required, default = default, withDelimiter = withDelimiter)
