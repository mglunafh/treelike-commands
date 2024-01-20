package org.some.project.kotlin.cmdparsing

import kotlin.reflect.KClass

data class CommandDefinition(
    val commandName: String,
    val requiredPositionalArguments: Int,
    val positionalArguments: Int,
    val paramDefinitions: List<ParameterDefinition<out Any>> = listOf(),
    val description: String = ""
) {

    constructor(commandName: String, description: String = "") : this(commandName, 0, 0, listOf(), description)

    constructor(commandName: String, requiredPositionalArguments: Int, description: String = "") :
            this(commandName, requiredPositionalArguments, requiredPositionalArguments, listOf(), description)

    constructor(commandName: String, paramDefinitions: List<ParameterDefinition<out Any>>, description: String = "") :
            this(commandName, 0, 0, paramDefinitions, description)
}

open class ParameterDefinition<T : Any>(
    open val name: String,
    open val type: KClass<T>,
    open val arity: Int = 1,
    open val required: Boolean = false,
    open val default: T? = null,
    open val delimiter: String? = null
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
    override val delimiter: String? = null
) : ParameterDefinition<T>(name, type, required = required,default = default, delimiter = delimiter)

data class IntFlagDefinition(
    override val name: String,
    override val required: Boolean = false,
    override val default: Int? = null,
    override val delimiter: String? = null
) : ParameterDefinition<Int>(name, Int::class, required = required, default = default, delimiter = delimiter)

data class DoubleFlagDefinition(
    override val name: String,
    override val required: Boolean = false,
    override val default: Double? = null,
    override val delimiter: String? = null
) : ParameterDefinition<Double>(name, Double::class, required = required, default = default, delimiter = delimiter)

data class StringFlagDefinition(
    override val name: String,
    override val required: Boolean = false,
    override val default: String? = null,
    override val delimiter: String? = null
) : ParameterDefinition<String>(name, String::class, required = required, default = default, delimiter = delimiter)
