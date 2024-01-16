package org.some.project.kotlin.cmdparsing

data class ValueParseObject(val positionalArguments: List<String>, val options: Map<String, Any>) {

    val posArgCount: Int = positionalArguments.size

    /**
     * Retrieves a value from the ValueParseObject associated with the given parameter definition.
     * Suitable for the definitions having 'required' flag or 'default' value set, otherwise not recommended to use.
     * @throws NullPointerException if there was no value corresponding the parameter.
     */
    inline fun <reified T : Any> get(paramDefinition: ParameterDefinition<T>): T {
        if (!paramDefinition.required && paramDefinition.default == null) {
            println("WARNING: $paramDefinition is not required and does not have a default value, thus it might not have value")
        }
        return options[paramDefinition.name]!!.let { it as T }
    }

    /**
     * Retrieves a value from the ValueParseObject associated with the given parameter definition,
     * or null if there is none.
     */
    inline fun <reified T : Any> getNullable(paramDefinition: ParameterDefinition<T>): T? {
        return options[paramDefinition.name]?.let { it as T }
    }

    /**
     * Retrieves a value associated with the given parameter name.
     *
     * @throws NullPointerException if the container does not have an associated value.
     */
    inline fun <reified T> get(name: String): T {
        return options[name]!! as T
    }

    /**
     * Retrieves a value from the ValueParseObject associated with the given parameter name,
     * or default value if there is none.
     */
    inline fun <reified T> getOrDefault(name: String, default: T): T {
        return options[name]?.let { it as T } ?: default
    }

    /**
     * Retrieves a value from the ValueParseObject associated with the given parameter name,
     * or null if there is none.
     */
    inline fun <reified T> getOrNull(name: String): T? {
        return options[name]?.let { it as T }
    }

    /**
     * Retrieves a list of values associated with the given parameter name, or a default value if there is none.
     *
     * @throws IllegalArgumentException if a type of the found value was not a list.
     */
    inline fun <reified T : Any> getListOrDefault(name: String, default: List<T>): List<T> {
        val listValue = options[name] ?: default
        if (listValue !is List<*>) throw IllegalArgumentException("Parameter was not a list!")
        return listValue.map { it as T }
    }

    /**
     * Retrieves a list of values associated with the given parameter name, or null otherwise.
     *
     * @throws IllegalArgumentException if a type of the found value was not a list.
     */
    inline fun <reified T> getListOrNull(name: String): List<T>? {
        val listValue = options[name] ?: return null
        if (listValue !is List<*>) throw IllegalArgumentException("Parameter was not a list!")
        return listValue.map { it as T }
    }

    /**
     * Retrieves a list of values associated with the given parameter definition,
     * or a default value if there is none.
     *
     * @throws IllegalArgumentException if the parameter arity is 1 or 0,
     * if the parameter was not supplied with a delimiter,
     * or a type of the found value was not a list.
     */
    inline fun <reified T: Any> getListOrDefault(paramDefinition: ParameterDefinition<T>, default: List<T>): List<T> {
        if (paramDefinition.arity <= 1 && paramDefinition.withDelimiter == null) {
            throw IllegalArgumentException("Parameter $paramDefinition is not represented as a collection")
        }
        val listValue = options[paramDefinition.name] ?: return default
        if (listValue !is List<*>) throw IllegalArgumentException("Parameter was not a list!")
        return listValue.map { it as T }
    }

    /**
     * Retrieves a list of values associated with the given parameter definition, or null otherwise.
     *
     * @throws IllegalArgumentException if the parameter arity is 1 or 0,
     * if the parameter was not supplied with a delimiter,
     * or a type of the found value was not a list.
     */
    inline fun <reified T: Any> getListOrNull(paramDefinition: ParameterDefinition<T>): List<T>? {
        if (paramDefinition.arity <= 1 && paramDefinition.withDelimiter == null) {
            throw IllegalArgumentException("Parameter $paramDefinition is not represented as a collection")
        }
        val listValue = options[paramDefinition.name] ?: return null
        if (listValue !is List<*>) throw IllegalArgumentException("Parameter was not a list!")
        return listValue.map { it as T }
    }
}
