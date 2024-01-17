package org.some.project.kotlin.geometry.command.point

import org.some.project.kotlin.geometry.command.Color
import org.some.project.kotlin.geometry.command.Tag

data class PointSetCommand(val name: String?, val color: Color?, val tags: List<Tag>?) {

    companion object{

    }
}