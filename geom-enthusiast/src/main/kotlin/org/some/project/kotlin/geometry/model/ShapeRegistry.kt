package org.some.project.kotlin.geometry.model

import java.util.concurrent.atomic.AtomicInteger

class ShapeRegistry {

    private val sequence = AtomicInteger(0)
    private val _shapes = mutableListOf<Shape>()

    val shapes: List<Shape>
        get() = _shapes

    private val points = mutableMapOf<Id, Point>()
    private val sections = mutableMapOf<Id, Section>()
    private val triangles = mutableMapOf<Id, Triangle>()

    private fun nextId(): Id = Id(sequence.incrementAndGet())

    fun create(type: ShapeType): Shape {
        return when (type) {
            ShapeType.POINT -> register(Point(nextId()))
            ShapeType.SECTION -> {
                val point1 = Point(nextId())
                val point2 = Point(nextId())
                register(Section(nextId(), point1, point2))
            }
            ShapeType.TRIANGLE -> {
                val edge1 = register(Point(nextId()))
                val edge2 = register(Point(nextId()))
                val edge3 = register(Point(nextId()))
                val side1 = Section(nextId(), edge1, edge2)
                val side2 = Section(nextId(), edge2, edge3)
                val side3 = Section(nextId(), edge3, edge1)
                Triangle(nextId(), side1, side2, side3)
            }
            ShapeType.RECTANGLE -> TODO()
            ShapeType.RHOMBUS -> TODO()
            ShapeType.CUBE -> TODO()
            ShapeType.PYRAMID -> TODO()
        }
    }

    operator fun get(id: Id): Shape? {
        return _shapes.firstOrNull { it.id == id }
    }

    fun upload(shapeList: List<Shape>) {
        shapeList.forEach {
            when (it) {
                is Point -> register(it)
                is Section -> register(it)
                is Triangle -> register(it)
            }
        }
    }

    fun register(point: Point): Point {
        val found = points[point.id]
        if (found != null) {
            require(found == point) {
                "Point with ${point.id} is already registered but their contents differ"
            }
            return found
        }
        points[point.id] = point
        _shapes.add(point)
        if (sequence.get() < point.id.id) {
            sequence.set(point.id.id)
        }
        return point
    }

    fun register(section: Section): Section {
        val found = sections[section.id]
        if (found != null) {
            require(found == section) {
                "Section with ${section.id} is already registered but their contents differ"
            }
            return found
        }

        val id = section.id
        val point1 = register(section.point1)
        val point2 = register(section.point2)
        require(point1 != point2) {
            "Line segment $id is invalid: its points must be distinct $point1, $point2"
        }

        val result = Section(id, point1, point2, name = section.name, color = section.color, tags = section.tags)
        sections[id] = result
        _shapes.add(result)
        if (sequence.get() < id.id) {
            sequence.set(id.id)
        }
        return result
    }

    fun register(triangle: Triangle): Triangle {
        val found = triangles[triangle.id]
        if (found != null) {
            require(found == triangle) {
                "Triangle with ${triangle.id} is already registered but their contents differ"
            }
            return found
        }

        val id = triangle.id
        val side1 = register(triangle.side1)
        val side2 = register(triangle.side2)
        val side3 = register(triangle.side3)

        require(side1 != side2 && side2 != side3 && side3 != side1) {
            "Triangle $id is invalid: some of its sides coincide."
         }
        require(setOf(side1.point1, side1.point2, side2.point1, side2.point2, side3.point1, side3.point2).size == 3) {
            "Triangle $id is invalid: some of its sides do not have the common points"
        }

        val result = Triangle(id, side1, side2, side3, name = triangle.name, color = triangle.color, tags = triangle.tags)
        triangles[id] = result
        _shapes.add(result)
        if (sequence.get() < id.id) {
            sequence.set(id.id)
        }
        return result
    }
}
