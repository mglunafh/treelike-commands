package org.some.project.kotlin.geometry.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.some.project.kotlin.geometry.model.Tag.Companion.appendTags

@Serializable
@SerialName("@section")
data class Section(
    override val id: Id,
    val point1: Point,
    val point2: Point,
    var name: Name? = null,
    var color: Color = Color.WHITE,
    var tags: List<Tag> = Tag.EMPTY_TAGS
) : Shape {
    override val type: ShapeType
        get() = ShapeType.SECTION

    override fun show(): String {
        return show(short = true, withTags = false)
    }

    fun show(short: Boolean = false, withTags: Boolean = false): String {
        val stringBuilder = StringBuilder()
        stringBuilder.append("${type.showName}[${id.id}]")
        name?.also { stringBuilder.append(" \"${it.name}\"") }

        if (withTags) {
            stringBuilder.appendTags(tags)
        }
        if (color != Color.WHITE) {
            stringBuilder.insert(0, color.controlSequence())
            stringBuilder.append(Color.CONSOLE_RESET)
        }
        if (!short) {
            val showPointTags = !withTags
            val point1Info = point1.show(short = showPointTags)
            val point2Info = point2.show(short = showPointTags)
            stringBuilder.append(" {").append(point1Info).append(", ").append(point2Info).append("}")
        }

        return stringBuilder.toString()
    }
}
