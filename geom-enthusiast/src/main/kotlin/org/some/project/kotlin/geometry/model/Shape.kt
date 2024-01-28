package org.some.project.kotlin.geometry.model

interface Shape {
    val type: ShapeType
    val id: Id

    companion object {
        fun create(type: ShapeType): Shape {
            return when (type) {
                ShapeType.POINT -> Point(Id.next())
                ShapeType.SECTION -> TODO()
                ShapeType.TRIANGLE -> TODO()
                ShapeType.RECTANGLE -> TODO()
                ShapeType.RHOMBUS -> TODO()
                ShapeType.CUBE -> TODO()
                ShapeType.PYRAMID -> TODO()
            }
        }
    }
}