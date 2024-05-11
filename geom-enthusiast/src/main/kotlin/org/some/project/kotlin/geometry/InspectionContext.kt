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
                require(command.show != null || command.tagsToAdd != null || command.tagsToRemove != null) {
                    "command 'tag' for points should have at least one of its show/add/rm options set"
                }
                when {
                    command.show != null -> {
                        val message = point.tags.let { if (it.isEmpty()) "" else it.joinToString(separator = ",") { t -> t.tag } }
                        println(message)
                    }
                    command.tagsToAdd != null -> {
                        val combinedTags = mutableSetOf<Tag>().apply {
                            addAll(point.tags)
                            addAll(command.tagsToAdd)
                        }
                        point.tags = combinedTags.toList()
                    }
                    command.tagsToRemove != null -> {
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
                require(command.show != null || command.tagsToAdd != null || command.tagsToRemove != null) {
                    "command 'tag' for sections should have at least one of its show/add/rm options set"
                }
                when {
                    command.show != null -> {
                        val message = section.tags.let { if (it.isEmpty()) "" else it.joinToString(separator = ",") { t -> t.tag } }
                        println(message)
                    }
                    command.tagsToAdd != null -> {
                        val combinedTags = mutableSetOf<Tag>().apply {
                            addAll(section.tags)
                            addAll(command.tagsToAdd)
                        }
                        section.tags = combinedTags.toList()
                    }
                    command.tagsToRemove != null -> {
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
            is SectionCommand.SectionSetCommand -> {
                command.name?.also { section.name = it }
                command.color?.also { section.color = it }
                command.tags?.also { section.tags = it }
            }
            is SectionCommand.SectionShowCommand -> {
                println(section.show(short = command.short, withTags = command.showTags))
            }
            is SectionCommand.SectionInspectCommand -> {
                // TODO possible errors
                listOf(section.point1, section.point2)
                    .firstOrNull { it.id == command.id }
                    ?.also { inspectedScenes.add(PointScene(it)) }
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
            is TriangleCommand.TriangleShowCommand -> {
                if (command.sectionId != null) {
                    //TODO possible errors
                    listOf(triangle.side1, triangle.side2, triangle.side3)
                        .firstOrNull { it.id == command.sectionId }
                        ?.show(short = command.short, withTags = command.showTags)
                        ?.also { sideView -> println(sideView) }
                } else {
                    println(triangle.show(short = command.short, withTags = command.showTags))
                }
            }
            is TriangleCommand.TriangleSetCommand -> {
                command.name?.also { triangle.name = it }
                command.color?.also { triangle.color = it }
                command.tags?.also { triangle.tags = it }
            }
            is TriangleCommand.TriangleInspectCommand -> {
                // TODO possible errors
                listOf(triangle.side1, triangle.side2, triangle.side3)
                    .firstOrNull { it.id == command.id }
                    ?.also { inspectedScenes.add(SectionScene(it)) }
            }
        }
    }
}
