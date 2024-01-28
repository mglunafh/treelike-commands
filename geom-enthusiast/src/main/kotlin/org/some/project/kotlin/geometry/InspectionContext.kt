package org.some.project.kotlin.geometry

import org.some.project.kotlin.geometry.command.*
import org.some.project.kotlin.geometry.command.OverviewCommand.*
import org.some.project.kotlin.geometry.model.Point
import org.some.project.kotlin.geometry.model.Shape
import org.some.project.kotlin.geometry.model.ShapeType

class InspectionContext {

    private val shapes: MutableList<Shape> = mutableListOf()

    private val inspectedScenes: MutableList<Scene> = mutableListOf()

    val scene: Scene
        get() = if (inspectedScenes.isNotEmpty()) inspectedScenes.last() else OverviewScene

    fun add(shape: Shape) {
        shapes.add(shape)
    }

    fun execute(command: CommandObject) {
        when (command) {
            is OverviewCommand -> overviewAction(command)
            is PointCommand -> pointAction(command)
            is SectionCommand -> println("Section command: $command")
        }
    }

    private fun overviewAction(command: OverviewCommand) {
        when (command) {
            is OverviewCreateCommand -> {
                val shape = Shape.create(command.fig)
                shapes.add(shape)
            }
            is OverviewInspectCommand -> {
                val shape = shapes.firstOrNull { it.id == command.id }
                shape?.also {
                    val scene = when (it.type) {
                        ShapeType.POINT -> PointScene(it as Point)
                        else -> TODO()
                    }
                    inspectedScenes.add(scene)
                }
            }
            OverviewListCommand -> {
                shapes.forEach { println(it) }
            }
            is OverviewLoadCommand -> TODO()
            is OverviewSaveCommand -> TODO()
        }
    }

    private fun pointAction(command: PointCommand) {
        val currentScene = this.scene
        require(currentScene is PointScene)
        val point = currentScene.point
        when (command) {
            GenericBackCommand.PointBackCommand -> {
                inspectedScenes.removeLast()
            }
            PointCommand.PointIdCommand -> {
                println(point.id)
            }
            PointCommand.PointNameCommand -> {
                println(point.name ?: "This point has no name")
            }
            is PointCommand.PointSetCommand -> {
                command.name?.also { point.name = it }
                command.color?.also { point.color = it }
                command.tags?.also { point.tags = it }
            }
            is PointCommand.PointShowCommand -> {
                println(point.show(command.short))
            }
            is PointCommand.PointTagCommand -> {
                val message = point.tags.let { if (it.isEmpty()) "" else it.joinToString(separator = ",") { t -> t.tag } }
                println(message)
            }
        }
    }
}
