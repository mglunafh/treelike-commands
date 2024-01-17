package org.some.project.kotlin.geometry.command

@JvmInline
value class Name(val name: String) {

    init {
        require(name.matches(NAME_REGEX)) { "Value '$name' does not satisfy the requirements of a valid name." }
    }

    companion object {

        val NAME_REGEX = """^[a-zA-Z][a-zA-Z0-9\-_]*${'$'}""".toRegex()
    }
}