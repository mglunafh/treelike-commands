package org.some.project.kotlin.cmdparsing

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import org.some.project.kotlin.cmdparsing.command.Color
import org.some.project.kotlin.cmdparsing.command.StrictSetterCommand
import org.some.project.kotlin.cmdparsing.command.Tag

class StrictFiledSetterParsingTest {

    @ParameterizedTest
    @MethodSource("positiveCases")
    fun `Test positive cases`(pair: Pair<String, StrictSetterCommand>) {
        val line = pair.first
        val expected = pair.second

        val parseResult = CommandLineArgumentParser.parse(DEFINITION, Tokenizer.tokenize(line))
        require(parseResult is ParseResult.ParseSuccess) {
            "Instead of success, got ${(parseResult as ParseResult.ParseError).error}."
        }
        val validationResult =
            CommandLineArgumentParser.convertParseResults(DEFINITION, parseResult.result)
        require(validationResult is ParseResult.ParseSuccess) {
            "Instead of successful conversion, got ${(validationResult as ParseResult.ParseError).error}."
        }

        val result = StrictSetterCommand.parse(validationResult.result)
        assertEquals(expected, result)
    }

    @ParameterizedTest
    @MethodSource("parsingErrors")
    fun `Test parsing errors`(pair: Pair<String, ErrorType>) {
        val line = pair.first
        val expectedError = pair.second

        val parseResult = CommandLineArgumentParser.parse(DEFINITION, Tokenizer.tokenize(line))
        require(parseResult is ParseResult.ParseError)
        assertEquals(expectedError, parseResult.error)
    }

    @ParameterizedTest
    @MethodSource("conversionErrors")
    fun `Test conversion and validation errors`(pair: Pair<String, ErrorType>) {
        val line = pair.first
        val expectedError = pair.second

        val parseResult = CommandLineArgumentParser.parse(DEFINITION, Tokenizer.tokenize(line))
        require(parseResult is ParseResult.ParseSuccess)
        val validationResult = CommandLineArgumentParser.convertParseResults(DEFINITION, parseResult.result)
        require(validationResult is ParseResult.ParseError)
        assertEquals(expectedError, validationResult.error)
    }

    @ParameterizedTest
    @MethodSource("helpMessages")
    fun `Test help messages`(line: String) {
        val parseResult = CommandLineArgumentParser.parse(DEFINITION, Tokenizer.tokenize(line))
        require(parseResult is ParseResult.Help) {
            "Expected help message, got $parseResult instead."
        }
    }

    companion object {
        const val COMMAND_NAME = "set"
        val DEFINITION = StrictSetterCommand.commandDefinition

        @JvmStatic
        @BeforeAll
        fun setUp() {
            Converter.registerConverter(Color::class) { value ->
                Color.entries.firstOrNull { it.name.equals(value, ignoreCase = true) }
            }
            Converter.registerConverter(Tag::class) { if (Tag.isTag(it)) Tag(it) else null }
        }

        @JvmStatic
        fun positiveCases(): List<Pair<String, StrictSetterCommand>> {
            return listOf(
                "set --id  14 --name Goobis" to StrictSetterCommand(14, "Goobis", 18),
                "set --id 14 --name Goobis --id 15 --read-only" to StrictSetterCommand(15, "Goobis", 18, readOnly = true),
                "set --id 14 --name Goobis --color white" to StrictSetterCommand(14, "Goobis", 18, color = Color.WHITE),
                "set --id 14 --name Goobis --age 10 --color white" to StrictSetterCommand(14, "Goobis", 10, color = Color.WHITE),
                "set --id 14 --name Goobis --surname Kazakh --id 15 --read-only" to
                        StrictSetterCommand(15, "Goobis", 18, surname="Kazakh", readOnly = true),
                "set --id 14 --name Goobis  --height 3.14" to StrictSetterCommand(14, "Goobis", 18, height = 3.14),
                "set --id 14 --name Goobis --age 10 --surname Kazakh --height 3.14" to
                        StrictSetterCommand(14, "Goobis", 10, surname="Kazakh", height = 3.14),
                "set --id 14 --name Goobis --color green --tag pook,guke" to
                        StrictSetterCommand(14, "Goobis", 18, color = Color.GREEN, tags = listOf(Tag("pook"), Tag("guke"))),
                "set --id 14 --name Goobis --color green --tag pook,guke --read-only" to
                        StrictSetterCommand(14, "Goobis", 18, color = Color.GREEN, readOnly = true, tags = listOf(Tag("pook"), Tag("guke"))),
                "set --id 14 --name Goobis --age 10 --color green --tag pook,guke --read-only --person Anton Sergeev" to
                        StrictSetterCommand(14, "Goobis", 10, color = Color.GREEN, readOnly = true, tags = listOf(Tag("pook"), Tag("guke")), person = listOf("Anton", "Sergeev"))
            )
        }

        @JvmStatic
        fun parsingErrors(): List<Pair<String, ErrorType>> {
            return listOf(
                "set --id " to MissingParameterValue(COMMAND_NAME, "--id"),
                "set --id --help" to MissingParameterValue(COMMAND_NAME, "--id"),
                "ste --id 1" to WrongCommand(COMMAND_NAME, "ste"),
                "set --id  14 --name" to MissingParameterValue(COMMAND_NAME, "--name"),
                "set --name --id  14" to MissingParameterValue(COMMAND_NAME, "--name"),
                "set --id --name Slam" to MissingParameterValue(COMMAND_NAME, "--id"),
                "set -- id 14 --name Goobis" to TooManyArguments(COMMAND_NAME, 0, "--"),
                "set --id 14 --name Goobis --height " to MissingParameterValue(COMMAND_NAME, "--height"),
                "set --id 14 --name Goobis --color --help" to MissingParameterValue(COMMAND_NAME, "--color"),
                "set --id 14 --name Goobis --color green --tag" to
                        MissingParameterValue(COMMAND_NAME, "--tag"),
                "set --id 14 --name Goobis --color green --tag pook,guke --read-only --no-read-only" to
                        TooManyArguments(COMMAND_NAME, 0, "--no-read-only"),
                "set --id 14 --name Goobis --color green --tag pook,guke --read-only --person Anton" to
                        MissingParameters(COMMAND_NAME, "--person", 2)
            )
        }

        @JvmStatic
        fun conversionErrors(): List<Pair<String, ErrorType>> {
            return listOf(
                "set --id 1" to RequiredParameterNotSet(COMMAND_NAME, "--name"),
                "set --id name" to CompositeError(listOf(
                    ValueConversionFailed(COMMAND_NAME, "--id", "name", Int::class),
                    RequiredParameterNotSet(COMMAND_NAME, "--name"))),
                "set --read-only" to CompositeError(listOf(
                    RequiredParameterNotSet(COMMAND_NAME, "--id"),
                    RequiredParameterNotSet(COMMAND_NAME, "--name"))),
                "set --id 14 --name Goobis --color slam" to
                        ValueConversionFailed(COMMAND_NAME, "--color", "slam", Color::class),
                "set --id 14 --name Goobis --surname Kazakh --color green --tag pook,guke,__luke" to
                        ValueConversionFailed(COMMAND_NAME, "--tag", "__luke", Tag::class),
                "set --id 14 --name Goobis --surname Kazakh --color green --tag pook,guke;luke" to
                        ValueConversionFailed(COMMAND_NAME, "--tag", "guke;luke", Tag::class))
        }

        @JvmStatic
        fun helpMessages(): List<String> {
            return listOf(
                "set --help",
                "set --help --id",
                "set --color slam --tag guke;luke --help"
            )
        }
    }
}