package org.some.project.kotlin.geometry.command.point

import org.some.project.kotlin.cmdparsing.*
import org.some.project.kotlin.geometry.command.Tag

data class PointTagCommand(val show: Boolean?, val tagsToAdd: List<Tag>?, val tagsToRemove: List<Tag>?) {

    companion object : CommandObjectParser<PointTagCommand> {
        val defShow = BooleanSwitchDefinition("--show")
        val defAddTags = ParameterDefinition("--add", Tag::class, delimiter = ",")
        val defRemoveTags = ParameterDefinition("--rm", Tag::class, delimiter = ",")

        override val commandDefinition =
            CommandDefinition("tag", paramDefinitions = listOf(defShow, defAddTags, defRemoveTags))

        override fun parse(valueParseObject: ValueParseObject): ParseResult<PointTagCommand> {
            val presentOptions = mutableListOf<String>()
            val show = valueParseObject.getNullable(defShow)?.also { presentOptions.add("show") }
            val tagsToAdd = valueParseObject.getListOrNull(defAddTags)?.also { presentOptions.add("add") }
            val tagsToRemove = valueParseObject.getListOrNull(defRemoveTags)?.also { presentOptions.add("rm") }

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
