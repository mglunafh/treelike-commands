package org.some.project.kotlin.geometry.command

enum class Color {
    WHITE, RED, GREEN, BLUE, YELLOW;

    companion object {
        fun toColorOrNull(value: String): Color? {
            return entries.firstOrNull { it.name.equals(value, ignoreCase = true) }
        }
    }
}
