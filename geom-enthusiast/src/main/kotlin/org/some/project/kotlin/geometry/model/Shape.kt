package org.some.project.kotlin.geometry.model

interface Shape {
    val type: ShapeType
    val id: Id

    fun show(): String

}