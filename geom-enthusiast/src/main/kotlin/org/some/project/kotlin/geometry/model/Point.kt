package org.some.project.kotlin.geometry.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import org.some.project.kotlin.geometry.model.Tag.Companion.appendTags

@Serializable
@SerialName("@point")
data class Point(
    @Transient override val id: Id = Id.next(),
    var name: Name? = null,
    var color: Color = Color.WHITE,
    var tags: List<Tag> = Tag.EMPTY_TAGS
) : Shape {

    override val type
        get() = ShapeType.POINT

    override fun show(): String {
        return show(false)
    }

    fun show(short: Boolean = false): String {
        val stringBuilder = StringBuilder()
        stringBuilder.append("${type.showName}[${id.id}]")
        name?.also { stringBuilder.append(" \"${it.name}\"") }
        if (!short) {
            stringBuilder.appendTags(tags)
        }
        if (color != Color.WHITE) {
            stringBuilder.insert(0, color.controlSequence())
            stringBuilder.append(Color.CONSOLE_RESET)
        }
        return stringBuilder.toString()
    }
}
