package org.some.project.kotlin.geometry.model

enum class Color {
    WHITE, RED, GREEN, BLUE, YELLOW;

    fun controlSequence(): String {
        return when(this) {
            WHITE -> ""
            RED -> "\u001b[31m"
            GREEN -> "\u001b[32m"
            YELLOW -> "\u001b[33m"
            BLUE -> "\u001b[34m"
        }
    }

    companion object {
        fun toColorOrNull(value: String): Color? {
            return entries.firstOrNull { it.name.equals(value, ignoreCase = true) }
        }

        val CONSOLE_RESET = "\u001b[0m"
    }
}
