package org.some.project.kotlin.cmdparsing

import kotlin.reflect.KClass

class CommandDefinition(
    val commandName: String,
    val requiredPositionalArguments: Int,
    val positionalArguments: Int,
    paramDefinitions: List<ParameterDefinition<out Any>>,
    val description: String = ""
) {

    val paramDefinitions: List<ParameterDefinition<out Any>>

    constructor(commandName: String, description: String = "") : this(commandName, 0, 0, listOf(), description)

    constructor(commandName: String, requiredPositionalArguments: Int, description: String = "") :
            this(commandName, requiredPositionalArguments, requiredPositionalArguments, listOf(), description)

    constructor(commandName: String, paramDefinitions: List<ParameterDefinition<out Any>>, description: String = "") :
            this(commandName, 0, 0, paramDefinitions, description)

    init {
        this.paramDefinitions = List(paramDefinitions.size + 1) {
            if (it < paramDefinitions.size) paramDefinitions[it] else HELP_FLAG
        }
    }

    fun description(): String {
        val header = "$commandName: $description"
        val strRequiredPositionalArgs = if (requiredPositionalArguments > 0) {
            "  Required positional arguments: $requiredPositionalArguments"
        } else {
            null
        }
        val strPositionalArgs = if (positionalArguments > requiredPositionalArguments) {
            "  Positional arguments: $positionalArguments"
        } else {
            null
        }
        val strOptions = if (paramDefinitions.isNotEmpty()) {
            paramDefinitions.joinToString(separator = "\n", prefix = "  Options:\n") { "  ${it.name} -- ${it.description}" }
        } else {
            null
        }
        return listOfNotNull(header, strRequiredPositionalArgs, strPositionalArgs, strOptions).joinToString(separator = "\n")
    }

    companion object {
        val HELP_FLAG = BooleanSwitchDefinition("--help", default = false, description = "Show this help message")
    }
}

open class ParameterDefinition<T : Any>(
    open val name: String,
    open val type: KClass<T>,
    open val arity: Int = 1,
    open val required: Boolean = false,
    open val default: T? = null,
    open val delimiter: String? = null,
    open val description: String = ""
)

data class BooleanSwitchDefinition(
    override val name: String,
    override val default: Boolean? = null,
    override val description: String = ""
) : ParameterDefinition<Boolean>(name, Boolean::class, arity = 0, default = default, description = description)

data class FlagDefinition<T : Any>(
    override val name: String,
    override val type: KClass<T>,
    override val required: Boolean = false,
    override val default: T? = null,
    override val delimiter: String? = null,
    override val description: String = ""
) : ParameterDefinition<T>(name, type, required = required,default = default, delimiter = delimiter, description = description)

data class IntFlagDefinition(
    override val name: String,
    override val required: Boolean = false,
    override val default: Int? = null,
    override val delimiter: String? = null,
    override val description: String = ""
) : ParameterDefinition<Int>(name, Int::class, required = required, default = default, delimiter = delimiter, description = description)

data class DoubleFlagDefinition(
    override val name: String,
    override val required: Boolean = false,
    override val default: Double? = null,
    override val delimiter: String? = null,
    override val description: String = ""
) : ParameterDefinition<Double>(name, Double::class, required = required, default = default, delimiter = delimiter, description = description)

data class StringFlagDefinition(
    override val name: String,
    override val required: Boolean = false,
    override val default: String? = null,
    override val delimiter: String? = null,
    override val description: String = ""
) : ParameterDefinition<String>(name, String::class, required = required, default = default, delimiter = delimiter, description = description)
