package org.some.project.kotlin.cmdparsing

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import org.some.project.kotlin.cmdparsing.command.Color
import org.some.project.kotlin.cmdparsing.command.PersonObject

class PersonParsingTest {

    @ParameterizedTest
    @MethodSource("positiveCases")
    fun `Test positive cases`(pair: Pair<String, PersonObject>) {
        val line = pair.first
        val expected = pair.second

        val parseResult = CommandLineArgumentParser.parse(DEFINITION, Tokenizer.tokenize(line))
        require(parseResult is ParseResult.ParseSuccess)
        val validatedResult =
            CommandLineArgumentParser.convertParseResults(DEFINITION, parseResult.result)
        require(validatedResult is ParseResult.ParseSuccess)

        val result = PersonObject.parse(validatedResult.result)
        assertEquals(expected, result)
    }

    @ParameterizedTest
    @MethodSource("parseErrors")
    fun `Test parse errors`(pair: Pair<String, ErrorType>) {
        val line = pair.first
        val expected = pair.second
        val parseResult = CommandLineArgumentParser.parse(DEFINITION, Tokenizer.tokenize(line))
        require(parseResult is ParseResult.ParseError)
        assertEquals(expected, parseResult.error)
    }

    @ParameterizedTest
    @MethodSource("conversionErrors")
    fun `Test conversion and validation errors`(pair: Pair<String, ErrorType>) {
        val line = pair.first
        val expected = pair.second
        val parseResult = CommandLineArgumentParser.parse(DEFINITION, Tokenizer.tokenize(line))
        require(parseResult is ParseResult.ParseSuccess)
        val validatedResult =
            CommandLineArgumentParser.convertParseResults(DEFINITION, parseResult.result)
        require(validatedResult is ParseResult.ParseError)
        assertEquals(expected, validatedResult.error)
    }

    companion object {
        const val COMMAND_NAME = "person"
        val DEFINITION = PersonObject.commandDefinition

        @JvmStatic
        @BeforeAll
        fun setUp() {
            Converter.registerConverter(Color::class) { value ->
                Color.entries.firstOrNull { it.name.equals(value, ignoreCase = true) }
            }
        }

        @JvmStatic
        fun positiveCases(): List<Pair<String, PersonObject>> {
            val kids = listOf("Sonny", "Lenny", "Jeany")
            val coordinate = Pair(1.0, 2.0)
            return listOf(
                "person Anton Antonov --age 15" to PersonObject("Anton", "Antonov", 15),
                "person Anton Antonovich Antonov --age 15" to
                        PersonObject("Anton", "Antonov", 15, middleName = "Antonovich"),

                "person Anton Antonov --age 15 --hair-color white" to
                        PersonObject("Anton", "Antonov", 15, hairColor = Color.WHITE),
                "person --age 15 Anton Antonov --hair-color white " to
                        PersonObject("Anton", "Antonov", 15, hairColor = Color.WHITE),
                "person --age 15 --hair-color white Anton Antonov" to
                        PersonObject("Anton", "Antonov", 15, hairColor = Color.WHITE),

                "person Anton Antonov --age 35 --kids Sonny,Lenny,Jeany" to
                        PersonObject("Anton", "Antonov", 35, kids = kids),
                "person --age 35 Anton Antonov --kids Sonny,Lenny,Jeany" to
                        PersonObject("Anton", "Antonov", 35, kids = kids),
                "person --age 35 --kids Sonny,Lenny,Jeany Anton Antonov" to
                        PersonObject("Anton", "Antonov", 35, kids = kids),

                "person Anton Antonov --age 35 --kids Sonny,Lenny,Jeany --hair-color blue" to
                        PersonObject("Anton", "Antonov", 35, hairColor = Color.BLUE, kids = kids),
                "person --age 35 Anton Antonov --kids Sonny,Lenny,Jeany --hair-color blue" to
                        PersonObject("Anton", "Antonov", 35, hairColor = Color.BLUE, kids = kids),
                "person --age 35 --kids Sonny,Lenny,Jeany Anton Antonov --hair-color blue" to
                        PersonObject("Anton", "Antonov", 35, hairColor = Color.BLUE, kids = kids),
                "person --age 35 --kids Sonny,Lenny,Jeany  --hair-color blue Anton Antonov" to
                        PersonObject("Anton", "Antonov", 35, hairColor = Color.BLUE, kids = kids),

                "person Anton Antonov --age 15 --coord 1.0 2.0" to
                        PersonObject("Anton", "Antonov", 15, coord = coordinate),
                "person Anton Antonov --age 15 --coord 1 2" to
                        PersonObject("Anton", "Antonov", 15, coord = coordinate),
                "person Anton Antonov --age 15 --coord 1f 2f" to
                        PersonObject("Anton", "Antonov", 15, coord = coordinate),
                "person --coord 1 2 Anton Antonov --age 15" to
                        PersonObject("Anton", "Antonov", 15, coord = coordinate),
                "person Anton --coord 1 2 --age 15 Antonov" to
                        PersonObject("Anton", "Antonov", 15, coord = coordinate),

                "person --age 35 --kids Sonny,Lenny,Jeany --hair-color blue Anton Antonov" to
                        PersonObject("Anton", "Antonov", 35, hairColor = Color.BLUE, kids = kids),
                "person --age 35 --kids Sonny,Lenny,Jeany --coord 1.0 2.0 --hair-color blue Anton Antonov" to
                        PersonObject("Anton", "Antonov", 35, hairColor = Color.BLUE, kids = kids, coord = coordinate),
                )
        }

        @JvmStatic
        fun parseErrors(): List<Pair<String, ErrorType>> {
            val tooFewArgs = TooFewRequiredArguments(COMMAND_NAME, 2, 1)
            return listOf(
                "" to EmptyArguments,
                "set --person Anton Antonov" to WrongCommand(COMMAND_NAME, "set"),
                "person" to TooFewRequiredArguments(COMMAND_NAME, 2, 0),
                "person Anton" to tooFewArgs,
                "person Jean Claude Van Damme" to TooManyArguments(COMMAND_NAME, 3),
                "person Anton Antonov --age" to MissingParameterValue(COMMAND_NAME, "age"),
                "person Anton --age 35 --kids Sonny,Lenny,Jeany --hair-color blue" to tooFewArgs,
                "person --age 35 --kids Sonny,Lenny,Jeany --hair-color blue Anton" to tooFewArgs,
                "person --age 35 --kids Sonny,Lenny,Jeany --hair-color blue Anton --coord " to
                        MissingParameters(COMMAND_NAME, "coord"),
                "person --age 35 --kids Sonny,Lenny,Jeany --hair-color blue Anton --coord 1" to
                        MissingParameters(COMMAND_NAME, "coord"),
                "person --age 35 --kids Sonny,Lenny,Jeany --hair-color blue Anton --coord 1 2" to tooFewArgs
            )
        }

        @JvmStatic
        fun conversionErrors(): List<Pair<String, ErrorType>> {
            return listOf(
                "person Anton Antonov --age value" to ValueConversionFailed(COMMAND_NAME, "age", "value", Int::class),
                "person Anton --age 35 --hair-color blue --coord one two Antonov" to
                        ValueConversionFailed(COMMAND_NAME, "coord", "one, two", Double::class),
                "person 1 2 --age 35 --kids Sonny,Lenny,Jeany --hair-color blue --coord Anton Antonov" to
                        ValueConversionFailed(COMMAND_NAME, "coord", "Anton, Antonov", Double::class)
            )
        }
    }
}
