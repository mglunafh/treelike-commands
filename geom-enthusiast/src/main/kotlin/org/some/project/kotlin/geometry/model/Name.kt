package org.some.project.kotlin.geometry.model

@JvmInline
value class Name private constructor(val name: String) {

    companion object {
        val NAME_REGEX = """^[a-zA-Z][a-zA-Z0-9\-_]*${'$'}""".toRegex()

        fun isName(arg: String): Boolean = arg.matches(NAME_REGEX)

        fun toNameOrNull(arg: String): Name? = if (arg.matches(NAME_REGEX)) Name(arg) else null
    }
}