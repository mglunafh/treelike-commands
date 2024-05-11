package org.some.project.kotlin.geometry.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import org.some.project.kotlin.geometry.model.Tag.Companion.appendTags
import java.lang.StringBuilder


@Serializable
@SerialName("@triangle")
data class Triangle(
    val side1: Section,
    val side2: Section,
    val side3: Section,
    @Transient override val id: Id = Id.next(),
    var name: Name? = null,
    var color: Color = Color.WHITE,
    var tags: List<Tag> = Tag.EMPTY_TAGS
) : Shape {

    override val type: ShapeType
        get() = ShapeType.TRIANGLE

    override fun show(): String {
        return show(short = true, withTags = false)
    }

    fun show(short: Boolean = false, withTags: Boolean = false): String {
        val sb = StringBuilder()
        sb.append("${type.showName}[${id.id}]")
        name?.also { sb.append(" \"${it.name}\"") }

        if (withTags) {
            sb.appendTags(tags)
        }
        if (color != Color.WHITE) {
            sb.insert(0, color.controlSequence()).append(Color.CONSOLE_RESET)
        }
        if (!short) {
            val side1Info = side1.show(short = short, withTags = withTags)
            val side2Info = side2.show(short = short, withTags = withTags)
            val side3Info = side3.show(short = short, withTags = withTags)
            sb.append(" {").append(side1Info).append(", ").append(side2Info).append(", ").append(side3Info).append("}")
        }

        return sb.toString()
    }
}
