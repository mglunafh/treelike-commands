package org.some.project.kotlin.geometry

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import kotlinx.serialization.json.encodeToStream
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import kotlinx.serialization.modules.subclass
import org.some.project.kotlin.geometry.command.*
import org.some.project.kotlin.geometry.command.OverviewCommand.*
import org.some.project.kotlin.geometry.model.*
import java.io.FileInputStream
import java.io.FileOutputStream

class InspectionContext {

    private var shapes: MutableList<Shape> = mutableListOf()

    private val inspectedScenes: MutableList<Scene> = mutableListOf()
    private val formatter: Json

    init {
        val module = SerializersModule {
            polymorphic(Shape::class) {
                subclass(Point::class)
                subclass(Section::class)
                subclass(Triangle::class)
            }
        }
        formatter = Json { serializersModule = module }
    }

    val scene: Scene
        get() = if (inspectedScenes.isNotEmpty()) inspectedScenes.last() else OverviewScene

    fun execute(command: CommandObject) {
        when (command) {
            is OverviewCommand -> overviewAction(command)
            is PointCommand -> pointAction(command)
            is SectionCommand -> sectionAction(command)
            is TriangleCommand -> triangleAction(command)
        }
    }

    @OptIn(ExperimentalSerializationApi::class)
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
                        ShapeType.SECTION -> SectionScene(it as Section)
                        ShapeType.TRIANGLE -> TriangleScene(it as Triangle)
                        else -> TODO()
                    }
                    inspectedScenes.add(scene)
                }
            }
            OverviewListCommand -> {
                shapes.forEach { println(it.show()) }
            }
            is OverviewLoadCommand -> {
                shapes = FileInputStream(command.filename).use {
                    formatter.decodeFromStream<MutableList<Shape>>(it)
                }
            }
            is OverviewSaveCommand -> {
                FileOutputStream(command.filename).use {
                    formatter.encodeToStream(shapes, it)
                }
            }
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
                println(point.id.id)
            }
            PointCommand.PointNameCommand -> {
                println(point.name?.name ?: "This point has no name")
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
                executePointTagCommand(command, point)
            }
        }
    }

    private fun executePointTagCommand(command: PointCommand.PointTagCommand, point: Point) {
        when (command) {
            PointCommand.PointShowTagsCommand -> {
                val message = point.tags.let { it.joinToString(separator = ",") { t -> t.tag } }
                println(message)
            }
            is PointCommand.PointAddTagsCommand -> {
                val combinedTags = mutableSetOf<Tag>().apply {
                    addAll(point.tags)
                    addAll(command.tagsToAdd)
                }
                point.tags = combinedTags.toList()
            }
            is PointCommand.PointRemoveTagsCommand -> {
                val intersection = mutableSetOf<Tag>().apply {
                    addAll(point.tags)
                    removeAll(command.tagsToRemove)
                }
                if (point.tags.size > intersection.size) {
                    point.tags = intersection.toList()
                }
            }
        }
    }

    private fun sectionAction(command: SectionCommand) {
        val currentScene = this.scene
        require(currentScene is SectionScene)
        val section = currentScene.section

        when (command) {
            GenericBackCommand.SectionBackCommand -> {
                inspectedScenes.removeLast()
            }
            SectionCommand.SectionIdCommand -> {
                println(section.id.id)
            }
            SectionCommand.SectionNameCommand -> {
                println(section.name?.name ?: "This section has no name")
            }
            SectionCommand.SectionColorCommand -> {
                println(section.color)
            }
            is SectionCommand.SectionTagCommand -> {
                executeSectionTagCommand(command, section)
            }
            is SectionCommand.SectionSetCommand -> {
                command.name?.also { section.name = it }
                command.color?.also { section.color = it }
                command.tags?.also { section.tags = it }
            }
            is SectionCommand.SectionShowCommand -> {
                println(section.show(short = command.short, withTags = command.showTags))
            }
            is SectionCommand.SectionShowPointCommand -> {
                val point = listOf(section.point1, section.point2).firstOrNull { it.id == command.pointId }
                if (point != null) {
                    println(point.show(short = command.short))
                } else {
                    println("Section does not have a point with id ${command.pointId.id}")
                }
            }
            is SectionCommand.SectionInspectCommand -> {
                val point = listOf(section.point1, section.point2).firstOrNull { it.id == command.id }
                if (point != null) {
                    inspectedScenes.add(PointScene(point))
                } else {
                    println("Section does not have a point with id ${command.id.id}")
                }
            }
        }
    }

    private fun executeSectionTagCommand(command: SectionCommand.SectionTagCommand, section: Section) {
        when(command) {
            SectionCommand.SectionShowTagsCommand -> {
                val message = section.tags.let { it.joinToString(separator = ",") { t -> t.tag } }
                println(message)
            }
            is SectionCommand.SectionAddTagsCommand -> {
                val combinedTags = mutableSetOf<Tag>().apply {
                    addAll(section.tags)
                    addAll(command.tagsToAdd)
                }
                section.tags = combinedTags.toList()
            }
            is SectionCommand.SectionRemoveTagsCommand -> {
                val intersection = mutableSetOf<Tag>().apply {
                    addAll(section.tags)
                    removeAll(command.tagsToRemove)
                }
                if (section.tags.size > intersection.size) {
                    section.tags = intersection.toList()
                }
            }
        }
    }

    private fun triangleAction(command: TriangleCommand) {
        val currentScene = this.scene
        require(currentScene is TriangleScene)
        val triangle = currentScene.triangle

        when(command) {
            GenericBackCommand.TriangleBackCommand -> {
                inspectedScenes.removeLast()
            }
            TriangleCommand.TriangleIdCommand -> {
                println(triangle.id.id)
            }
            TriangleCommand.TriangleNameCommand -> {
                println(triangle.name?.name ?: "This triangle has no name")
            }
            TriangleCommand.TriangleColorCommand -> {
                println(triangle.color)
            }
            is TriangleCommand.TriangleGeneralShowCommand -> {
                executeTriangleShowCommand(command, triangle)
            }
            is TriangleCommand.TriangleSetCommand -> {
                command.name?.also { triangle.name = it }
                command.color?.also { triangle.color = it }
                command.tags?.also { triangle.tags = it }
            }
            is TriangleCommand.TriangleInspectCommand -> {
                val side = listOf(triangle.side1, triangle.side2, triangle.side3).firstOrNull { it.id == command.id }
                if (side != null) {
                    inspectedScenes.add(SectionScene(side))
                } else {
                    println("Triangle does not have a side with id ${command.id.id}")
                }
            }
        }
    }

    private fun executeTriangleShowCommand(command: TriangleCommand.TriangleGeneralShowCommand, triangle: Triangle) {
        when (command) {
            is TriangleCommand.TriangleShowCommand -> {
                println(triangle.show(short = command.short, withTags = command.showTags))
            }
            is TriangleCommand.TriangleShowSectionCommand -> {
                val side = listOf(triangle.side1, triangle.side2, triangle.side3).firstOrNull { it.id == command.sideId }
                if (side != null) {
                    val sideView = side.show(short = command.short, withTags = command.showTags)
                    println(sideView)
                } else {
                    println("Triangle does not have side with id ${command.sideId.id}")
                }
            }
            is TriangleCommand.TriangleShowPointCommand -> {
                val point = listOf(triangle.side1, triangle.side2, triangle.side3)
                    .flatMap { listOf(it.point1, it.point2) }
                    .firstOrNull { it.id == command.pointId }
                if (point != null) {
                    println(point.show(short = command.short))
                } else {
                    println("Triangle does not have a point with id ${command.pointId.id}")
                }
            }
        }
    }
}
