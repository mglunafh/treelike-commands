package org.some.project.kotlin.cmdparsing

data class ValueParseObject(val positionalArguments: List<String>, val options: Map<String, Any>) {

    val posArgCount: Int = positionalArguments.size

    inline fun <reified T> get(name: String): T {
        return options[name]!! as T
    }

    inline fun <reified T : Any> get(paramDefinition: ParameterDefinition<T>): T {
        if (!paramDefinition.required && paramDefinition.default == null) {
            println("WARNING: $paramDefinition is not required and does not have a default value, thus it might not have value")
        }
        return options[paramDefinition.name]!!.let { it as T }
    }

    inline fun <reified T : Any> getNullable(paramDefinition: ParameterDefinition<T>): T? {
        return options[paramDefinition.name]?.let { it as T }
    }

    inline fun <reified T> getOrDefault(name: String, default: T): T {
        return options[name]?.let { it as T } ?: default
    }

    inline fun <reified T> getOrNull(name: String): T? {
        return options[name]?.let { it as T }
    }

    inline fun <reified T : Any> getCollectionOrDefault(name: String, default: List<T>): List<T> {
        val listValue = options[name] ?: default
        if (listValue !is List<*>) throw IllegalArgumentException("Parameter was not a list!")
        return listValue.map { it as T }
    }

    inline fun <reified T> getCollectionOrNull(name: String): List<T>? {
        val listValue = options[name] ?: return null
        if (listValue !is List<*>) throw IllegalArgumentException("Parameter was not a list!")
        return listValue.map { it as T }
    }

    inline fun <reified T: Any> getCollectionOrDefault(paramDefinition: ParameterDefinition<T>, default: List<T>): List<T> {
        if (paramDefinition.arity <= 1 && paramDefinition.withDelimiter == null) {
            throw IllegalArgumentException("Parameter $paramDefinition is not represented as a collection")
        }
        val listValue = options[paramDefinition.name] ?: return default
        if (listValue !is List<*>) throw IllegalArgumentException("Parameter was not a list!")
        return listValue.map { it as T }
    }

    inline fun <reified T: Any> getCollectionOrNull(paramDefinition: ParameterDefinition<T>): List<T>? {
        if (paramDefinition.arity <= 1 && paramDefinition.withDelimiter == null) {
            throw IllegalArgumentException("Parameter $paramDefinition is not represented as a collection")
        }
        val listValue = options[paramDefinition.name] ?: return null
        if (listValue !is List<*>) throw IllegalArgumentException("Parameter was not a list!")
        return listValue.map { it as T }
    }
}
