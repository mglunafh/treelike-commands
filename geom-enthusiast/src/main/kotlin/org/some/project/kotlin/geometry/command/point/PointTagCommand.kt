package org.some.project.kotlin.geometry.command.point

import org.some.project.kotlin.cmdparsing.*
import org.some.project.kotlin.geometry.command.Tag

data class PointTagCommand(val show: Boolean?, val tagsToAdd: List<Tag>?, val tagsToRemove: List<Tag>?) {

    companion object : CommandObjectParser<PointTagCommand> {
        val defShow = BooleanSwitchDefinition("--show")
        val defAddTags = ParameterDefinition("--add", Tag::class, delimiter = ",")
        val defRemoveTags = ParameterDefinition("--rm", Tag::class, delimiter = ",")

        override val commandDefinition = CommandDefinition(
            "tag",
            listOf(defShow, defAddTags, defRemoveTags),
            description = "Perform one of show/add/remove operations on tags")

        override fun parse(arguments: ValueParseObject): ParseResult<PointTagCommand> {
            val presentOptions = mutableListOf<String>()
            val show = arguments.getNullable(defShow)?.also { presentOptions.add("show") }
            val tagsToAdd = arguments.getListOrNull(defAddTags)?.also { presentOptions.add("add") }
            val tagsToRemove = arguments.getListOrNull(defRemoveTags)?.also { presentOptions.add("rm") }

            return when {
                presentOptions.isEmpty() -> ParseResult.ParseError(NoOptions(commandDefinition.commandName))
                presentOptions.size > 1 -> ParseResult.ParseError(ExclusiveOptions(presentOptions))
                else -> ParseResult.ParseSuccess(PointTagCommand(show, tagsToAdd, tagsToRemove))
            }
        }
    }

    data class ExclusiveOptions(val options: List<String>): CustomValidationError() {

        override fun getMessage(): String {
            return "Options $options are mutually exclusive and cannot be used simultaneously"
        }
    }
}
