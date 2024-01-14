package org.some.project.kotlin.cmdparsing

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import org.some.project.kotlin.cmdparsing.command.Color
import org.some.project.kotlin.cmdparsing.command.FieldSetterCommandObject
import org.some.project.kotlin.cmdparsing.command.Tag

class FieldSetterParsingTest {

    @ParameterizedTest
    @MethodSource("positiveCases")
    fun `Test positive cases`(pair: Pair<String, FieldSetterCommandObject>) {
        val line = pair.first
        val expected = pair.second

        val parseResult = CommandLineArgumentParser.parseOptional(line, COMMAND_NAME, COMMAND_DEFINITION.paramDefinitions)
        require(parseResult is ParseResult.ParseSuccess)
        val validationResult =
            CommandLineArgumentParser.validateAndConvertParseResults(COMMAND_DEFINITION, parseResult.result)
        require(validationResult is ParseResult.ParseSuccess)

        val result = FieldSetterCommandObject.parse(validationResult.result)
        assertEquals(expected, result)
    }

    @ParameterizedTest
    @MethodSource("parsingErrors")
    fun `Test parsing errors`(pair: Pair<String, ErrorType>) {
        val line = pair.first
        val expectedError = pair.second

        val parseResult = CommandLineArgumentParser.parseOptional(line, COMMAND_NAME, COMMAND_DEFINITION.paramDefinitions)
        require(parseResult is ParseResult.ParseError)
        assertEquals(expectedError, parseResult.error)
    }

    @ParameterizedTest
    @MethodSource("conversionErrors")
    fun `Test conversion and validation errors`(pair: Pair<String, ErrorType>) {
        val line = pair.first
        val expectedError = pair.second

        val parseResult = CommandLineArgumentParser.parseOptional(line, COMMAND_NAME, COMMAND_DEFINITION.paramDefinitions)
        require(parseResult is ParseResult.ParseSuccess)
        val validationResult =
            CommandLineArgumentParser.validateAndConvertParseResults(COMMAND_DEFINITION, parseResult.result)
        require(validationResult is ParseResult.ParseError)
        assertEquals(expectedError, validationResult.error)
    }

    companion object {

        const val COMMAND_NAME = "set"
        val COMMAND_DEFINITION = FieldSetterCommandObject.fieldSetterCommandDefinition

        @JvmStatic
        @BeforeAll
        fun setUp() {
            Converter.registerConverter(Color::class) { convertToColor(it) }
            Converter.registerConverter(Tag::class) { if (Tag.isTag(it)) Tag(it) else null }
        }

        private fun convertToColor(value: String): Color? {
            return Color.entries.firstOrNull { it.name.equals(value, ignoreCase = true) }
        }

        @JvmStatic
        fun positiveCases(): List<Pair<String, FieldSetterCommandObject>> {
            return listOf(
                "set --id  14 --name Goobis" to FieldSetterCommandObject(14, "Goobis", Color.WHITE),
                "set --read-only" to  FieldSetterCommandObject(null, null, Color.WHITE, readOnly = true),
                "set --id 14 --name Goobis --id 15 --read-only" to FieldSetterCommandObject(15, "Goobis", Color.WHITE, readOnly = true),
                "set --id 14 --name Goobis --color blue" to FieldSetterCommandObject(14, "Goobis", color = Color.BLUE),
                "set --id 14 --name Goobis --color green --tag pook,guke" to
                        FieldSetterCommandObject(14, "Goobis", color = Color.GREEN, tags = listOf(Tag("pook"), Tag("guke"))),
                "set --id 14 --name Goobis --color green --tag pook,guke --read-only" to
                        FieldSetterCommandObject(14, "Goobis", color = Color.GREEN, readOnly = true, tags = listOf(Tag("pook"), Tag("guke"))),
                "set --id 14 --name Goobis --color white --tag pook,guke --read-only --person Anton Sergeev" to
                        FieldSetterCommandObject(14, "Goobis", color = Color.WHITE, readOnly = true, tags = listOf(Tag("pook"), Tag("guke")), person = Pair("Anton", "Sergeev"))
            )
        }

        @JvmStatic
        fun parsingErrors(): List<Pair<String, ErrorType>> {
            return listOf(
                "set --id " to MissingParameterValue(COMMAND_NAME, "id"),
                "ste --id 1" to WrongCommand(COMMAND_NAME, "ste"),
                "set --id  14 --name" to MissingParameterValue(COMMAND_NAME, "name"),
                "set -- id 14 --name Goobis" to UnrecognizedFlag(COMMAND_NAME, "--"),
                "set --id 14 --name Goobis --surname Kazakh --id 15 --read-only" to
                        UnrecognizedFlag(COMMAND_NAME, "--surname"),
                "set --id 14 --name Goobis --color green --tag" to
                        MissingParameterValue(COMMAND_NAME, "tag"),
                "set --id 14 --name Goobis --color green --tag pook,guke --read-only --no-read-only" to
                        UnrecognizedFlag(COMMAND_NAME, "--no-read-only"),
                "set --id 14 --name Goobis --color green --tag pook,guke --read-only --person Anton" to
                        MissingParameters(COMMAND_NAME, "person")
            )
        }

        @JvmStatic
        fun conversionErrors(): List<Pair<String, ErrorType>> {
            return listOf(
                "set --id name" to ValueConversionFailed(COMMAND_NAME, "id", "name", Int::class),
                "set --id 14 --name Goobis --color slam" to ValueConversionFailed(COMMAND_NAME, "color", "slam", Color::class),
                "set --id 14 --name Goobis --color green --tag pook,guke,__luke" to
                        ValueConversionFailed(COMMAND_NAME, "tag", "__luke", Tag::class),
                "set --id 14 --name Goobis --color green --tag pook,_guke,_luke" to
                        ValueConversionFailed(COMMAND_NAME, "tag", "_guke, _luke", Tag::class),
                "set --id 14 --name Goobis --color blue --tag pook,guke;luke" to
                        ValueConversionFailed(COMMAND_NAME, "tag", "guke;luke", Tag::class),
                "set --id 14 --name Goobis --color green --tag pook;guke" to
                        ValueConversionFailed(COMMAND_NAME, "tag", "pook;guke", Tag::class),
            )
        }
    }
}
