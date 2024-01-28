package org.some.project.kotlin.geometry.model

class Point(
    override val id: Id,
    var name: Name? = null,
    var color: Color = Color.WHITE,
    var tags: List<Tag> = listOf()
) : Shape {

    override val type
        get() = ShapeType.POINT

    fun show(short: Boolean = false): String {
        val stringBuilder = StringBuilder()
        stringBuilder.append("Point[${id.id}]")
        name?.also { stringBuilder.append(" \"${it.name}\"") }
        if (!short) {
            tags.also { tagList ->
                if (tagList.isNotEmpty()) {
                    stringBuilder.append(" ").append(tagList.joinToString(prefix = "(", separator = ",", postfix = ")") { it.tag })
                }
            }
        }
        if (color != Color.WHITE) {
            stringBuilder.insert(0, color.controlSequence())
            stringBuilder.append(Color.CONSOLE_RESET)
        }
        return stringBuilder.toString()
    }

    override fun toString(): String {
        val stringBuilder = StringBuilder("Point[${id.id}]")
        name?.also { stringBuilder.append(" \"${it.name}\"") }
        tags.also { tagList ->
            if (tagList.isNotEmpty()) {
                stringBuilder.append(" ").append(tagList.joinToString(prefix = "(", separator = ",", postfix = ")") { it.tag })
            }
        }
        return stringBuilder.toString()
    }
}
