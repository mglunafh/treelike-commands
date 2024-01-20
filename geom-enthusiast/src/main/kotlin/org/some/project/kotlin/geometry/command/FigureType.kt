package org.some.project.kotlin.geometry.command

enum class FigureType {
    POINT, SECTION, TRIANGLE, RECTANGLE, RHOMBUS, CUBE, PYRAMID;

    companion object {
        fun toFigureOrNull(value: String): FigureType? {
            return entries.firstOrNull { it.name.equals(value, ignoreCase = true) }
        }
    }
}
