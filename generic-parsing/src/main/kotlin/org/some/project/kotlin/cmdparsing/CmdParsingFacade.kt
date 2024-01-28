package org.some.project.kotlin.cmdparsing

object CmdParsingFacade {

    fun <T: Any> parse(commandObjectParser: CommandObjectParser<T>, arguments: List<String>): ParseResult<out T> {
        val commandDefinition = commandObjectParser.commandDefinition
        return CommandLineArgumentParser.parse(commandDefinition, arguments)
            .flatMap { CommandLineArgumentParser.convertParseResults(commandDefinition, it) }
            .flatMap { commandObjectParser.parse(it) }
    }
}
