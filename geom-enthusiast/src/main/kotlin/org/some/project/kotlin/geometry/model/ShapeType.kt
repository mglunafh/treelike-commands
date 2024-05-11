package org.some.project.kotlin.geometry.model

enum class ShapeType(val showName: String) {
    POINT("Point"),
    SECTION("Section"),
    TRIANGLE("Triangle"),
    RECTANGLE("Rectangle"),
    RHOMBUS("Rhombus"),
    CUBE("Cube"),
    PYRAMID("Pyramid");

    companion object {
        fun toShapeOrNull(value: String): ShapeType? {
            return entries.firstOrNull { it.name.equals(value, ignoreCase = true) }
        }
    }
}
