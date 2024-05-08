package org.some.project.kotlin.geometry.model

import kotlinx.serialization.Serializable

@Serializable
@JvmInline
value class Name private constructor(val name: String) {

    init {
        require(name.matches(NAME_REGEX)) {
            "Name should satisfy regex $NAME_REGEX, got '$name'"
        }
    }
    companion object {
        private val NAME_REGEX = """^[a-zA-Z][a-zA-Z0-9\-_]*${'$'}""".toRegex()

        fun isName(arg: String): Boolean = arg.matches(NAME_REGEX)

        fun toNameOrNull(arg: String): Name? = if (arg.matches(NAME_REGEX)) Name(arg) else null
    }
}