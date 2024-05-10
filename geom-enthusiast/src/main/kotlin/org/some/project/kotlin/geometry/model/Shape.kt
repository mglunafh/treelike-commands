package org.some.project.kotlin.geometry.model

interface Shape {
    val type: ShapeType
    val id: Id

    fun show(): String

    companion object {
        fun create(type: ShapeType): Shape {
            return when (type) {
                ShapeType.POINT -> Point()
                ShapeType.SECTION -> Section(Point(), Point())
                ShapeType.TRIANGLE -> TODO()
                ShapeType.RECTANGLE -> TODO()
                ShapeType.RHOMBUS -> TODO()
                ShapeType.CUBE -> TODO()
                ShapeType.PYRAMID -> TODO()
            }
        }
    }
}