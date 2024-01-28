package org.some.project.kotlin.geometry.model

enum class ShapeType {
    POINT, SECTION, TRIANGLE, RECTANGLE, RHOMBUS, CUBE, PYRAMID;

    companion object {
        fun toShapeOrNull(value: String): ShapeType? {
            return entries.firstOrNull { it.name.equals(value, ignoreCase = true) }
        }
    }
}
