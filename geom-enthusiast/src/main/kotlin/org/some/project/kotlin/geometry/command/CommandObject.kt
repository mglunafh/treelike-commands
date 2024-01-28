package org.some.project.kotlin.geometry.command

import org.some.project.kotlin.geometry.l10n.CommandMessageSource

sealed interface CommandObject

enum class PointCommandEnum {
    SELF, SHOW, ID, NAME, COLOR, TAG, SET, BACK, HIERARCHY
}

object PointHelpFactory {

    fun getDescription(command: PointCommandEnum, descriptionOnly: Boolean): String {

        return when (command) {
            PointCommandEnum.SELF -> showHelpList()
            PointCommandEnum.SHOW -> if (descriptionOnly) CommandMessageSource["point.show"] else detailedShow()
            PointCommandEnum.ID -> CommandMessageSource["point.id"]
            PointCommandEnum.NAME -> CommandMessageSource["point.name"]
            PointCommandEnum.COLOR -> CommandMessageSource["point.color"]
            PointCommandEnum.TAG -> if (descriptionOnly) CommandMessageSource["point.tag"] else detailedTags()
            PointCommandEnum.SET -> if (descriptionOnly) CommandMessageSource["point.set"] else detailedSet()
            PointCommandEnum.BACK -> CommandMessageSource["point.back"]
            PointCommandEnum.HIERARCHY -> CommandMessageSource["point.hierarchy"]
        }
    }

    private fun showHelpList(): String = TODO()

    private fun detailedShow(): String = TODO()

    private fun detailedTags(): String = TODO()

    private fun detailedSet(): String = TODO()

}
