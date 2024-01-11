package org.some.project.kotlin.cmdparsing

import org.some.project.kotlin.cmdparsing.ConversionResult.*
import org.some.project.kotlin.cmdparsing.ConversionResult.ConversionFailureEnum.*
import org.some.project.kotlin.cmdparsing.ConversionResult.ValueConversionFailure
import org.some.project.kotlin.cmdparsing.ParseResult.ParseError
import org.some.project.kotlin.cmdparsing.ParseResult.ParseSuccess
import kotlin.reflect.KClass
import kotlin.reflect.cast


data class ParseObject(val positionalArguments: List<String>, val options: Map<String, OptionValue>)

object CommandLineArgumentParser {

    fun validateAndConvertParseResults(
        commandDefinition: CommandDefinition,
        parseObject: ParseObject
    ): ParseResult<Map<String, Any>> {
        val options = parseObject.options
        val commandName = commandDefinition.commandName
        val resultMap = mutableMapOf<String, Any>()
        val errors = mutableListOf<ErrorType>()

        for (paramDefinition in commandDefinition.paramDefinitions) {
            val optionName = paramDefinition.name
            val optionType = paramDefinition.type
            val arity = paramDefinition.arity
            val delimiter = paramDefinition.withDelimiter

            val value = options[optionName]

            when {
                value == null && paramDefinition.required ->   {
                    errors.add(RequiredParameterNotSet(commandName, optionName))
                }

                value == null -> {
                    continue
                }

                optionType == Boolean::class -> {
                    if (value is SwitchValue) {
                        resultMap[optionName] = value.switch
                    } else {
                        errors.add(SwitchValueExpected(commandName, optionName))
                    }
                }

                arity == 1 && delimiter != null -> {
                    val convertedValues = convertListOfValues(value, delimiter, optionType)
                    if (convertedValues.isSuccessful) {
                        resultMap[optionName] = convertedValues.result
                    } else {
                        errors.add(conversionError(convertedValues.error, commandName, optionName, optionType))
                    }
                }

                arity == 1 -> {
                    val convertedValue = convertSingleValue(value, optionType)
                    if (convertedValue.isSuccessful) {
                        resultMap[optionName] = optionType.cast(convertedValue.result)
                    } else {
                        errors.add(conversionError(convertedValue.error, commandName, optionName, optionType))
                    }
                }

                arity > 1 -> {
                    val convertedValues = convertListOfValues(value, optionType)
                    if (convertedValues.isSuccessful) {
                        resultMap[optionName] = convertedValues.result
                    } else {
                        errors.add(conversionError(convertedValues.error, commandName, optionName, optionType))
                    }
                }
            }
        }
        return when {
            errors.isEmpty() -> ParseSuccess(resultMap)
            errors.size == 1 -> ParseError(errors[0])
            else -> ParseError(CompositeError(errors))
        }
    }

    private fun conversionError(
        error: ConversionFailure,
        command: String,
        option: String,
        type: KClass<out Any>
    ): GenericConversionError {

        return when (error) {
            SWITCH_VALUE_EXPECTED -> SwitchValueExpected(command, option)
            STRING_VALUE_EXPECTED -> StringValueExpected(command, option)
            LIST_VALUE_EXPECTED -> ListValueExpected(command, option)
            CONVERTER_NOT_FOUND -> ConverterNotFound(command, option, type)
            is ListEntryConversionFailure ->
                ValueConversionFailed(command, option, error.nonConvertibles.joinToString(separator = ", "), type)
            is ValueConversionFailure -> ValueConversionFailed(command, option, error.value, type)
        }
    }

    private fun <T : Any> convertSingleValue(value: OptionValue, type: KClass<T>): ConversionResult<T> {
        if (value !is StringValue) {
            return ConversionResult.failure(STRING_VALUE_EXPECTED)
        }
        val converter = Converter.getConverter(type) ?: return ConversionResult.failure(CONVERTER_NOT_FOUND)

        return converter.convert(value.str)?.let { ConversionResult.result(it) } ?:
                ConversionResult.failure(ValueConversionFailure(value.str))
    }

    private fun <T : Any> convertListOfValues(
        value: OptionValue,
        delimiter: String,
        type: KClass<T>
    ): ConversionResult<List<T>> {
        if (value !is StringValue) {
            return ConversionResult.failure(STRING_VALUE_EXPECTED)
        }
        val converter = Converter.getConverter(type) ?: return ConversionResult.failure(CONVERTER_NOT_FOUND)

        val values = value.str.split(delimiter)
        return mapValues(values, converter)
    }

    private fun <T : Any> convertListOfValues(value: OptionValue, type: KClass<T>): ConversionResult<List<T>> {
        if (value !is ListStringValue) {
            return ConversionResult.failure(LIST_VALUE_EXPECTED)
        }
        val converter = Converter.getConverter(type) ?: return ConversionResult.failure(CONVERTER_NOT_FOUND)

        return mapValues(value.list, converter)
    }

    fun parseOptional(
        argLine: String,
        commandName: String,
        definitions: List<ParameterDefinition<out Any>>
    ): ParseResult<ParseObject> {
        val flags = createFlags(definitions)
        val args = Tokenizer.tokenize(argLine)

        var currState = StateMachine.INIT
        var currOption: ParameterDefinition<out Any>? = null

        var flagArgsParsed = 0
        var currListOfFlagArgs: MutableList<String>? = null

        val parsedObjects = mutableMapOf<String, OptionValue>()

        for (arg in args) {
            when (currState) {
                StateMachine.INIT -> {
                    if (commandName == arg) {
                        currState = StateMachine.COMMAND
                    } else {
                        return ParseError(WrongCommand(commandName, arg))
                    }
                }

                StateMachine.COMMAND -> {
                    val flag = flags[arg] ?: return ParseError(UnrecognizedFlag(commandName, arg))

                    when {
                        flag.type == Boolean::class -> {
                            parsedObjects[flag.name] = SwitchValue()
                        }

                        flag.arity == 0 -> {
                            throw IllegalStateException("Only boolean switches are allowed to have 0 arity.")
                        }

                        flag.arity == 1 -> {
                            currState = StateMachine.UNARY_FLAG
                            currOption = flag
                        }

                        else -> {
                            currState = StateMachine.LONG_FLAG
                            currOption = flag
                            flagArgsParsed = 0
                            currListOfFlagArgs = mutableListOf()
                        }
                    }
                }

                StateMachine.UNARY_FLAG -> {
                    requireNotNull(currOption) {
                        "State $currState requires an option to be present"
                    }
                    require(currOption.arity == 1) {
                        "State $currState requires option to be unary, got $currOption with arity ${currOption!!.arity} instead."
                    }
                    val possibleFlag = flags[arg]
                    if (possibleFlag != null) {
                        return ParseError(MissingParameterValue(commandName, currOption.name))
                    }

                    parsedObjects[currOption.name] = StringValue(arg)
                    currState = StateMachine.COMMAND
                    currOption = null
                }

                StateMachine.LONG_FLAG -> {
                    requireNotNull(currOption) {
                        "State $currState requires an option to be present"
                    }
                    requireNotNull(currListOfFlagArgs) {
                        "State $currState requires a list of args to be already initialized"
                    }
                    require(flagArgsParsed < currOption.arity) {
                        "For some reason flagArgsParsed ($flagArgsParsed) got bigger than current option arity (${currOption!!.arity})"
                    }
                    currListOfFlagArgs.add(arg)
                    flagArgsParsed++
                    if (flagArgsParsed == currOption.arity) {
                        parsedObjects[currOption.name] = ListStringValue(currListOfFlagArgs.toList())
                        currListOfFlagArgs = null
                        currState = StateMachine.COMMAND
                        currOption = null
                    }
                }
            }
        }

        return when (currState) {
            StateMachine.INIT -> ParseError(EmptyArguments)
            StateMachine.COMMAND -> ParseSuccess(ParseObject(listOf(), parsedObjects.toMap()))
            StateMachine.UNARY_FLAG -> ParseError(MissingParameterValue(commandName, currOption!!.name))
            StateMachine.LONG_FLAG -> ParseError(MissingParameters(commandName, currOption!!.name))
        }
    }

    fun parsePositional(argLine: String, commandName: String, posArgs: Int, reqPosArgs: Int): ParseObject? {
        require(posArgs > 0) { "The number of positional arguments must be positive" }
        require(reqPosArgs <= posArgs) {
            "The number of required positional arguments($reqPosArgs) " +
                    "must not be greater than the total number of positional arguments($posArgs)"
        }

        val args = Tokenizer.tokenize(argLine)

        var state = StateMachine.INIT
        var argsParsed = 0

        val posArgsList = mutableListOf<String>()

        for (arg in args) {
            when (state) {
                StateMachine.INIT -> {
                    if (commandName == arg) {
                        state = StateMachine.COMMAND
                    } else {
                        println("Wrong command")
                        return null
                    }
                }

                StateMachine.COMMAND -> {
                    if (argsParsed < posArgs) {
                        posArgsList.add(arg)
                        argsParsed++
                    } else {
                        println("Too many arguments, expected $posArgs at most")
                        return null
                    }
                }

                else -> TODO()
            }
        }

        println("Parsed $argsParsed arguments")
        return if (argsParsed >= reqPosArgs) {
            ParseObject(posArgsList.toList(), mapOf())
        } else {
            println("Too few arguments ($argsParsed) have been passed to the command '$commandName', it requires at least $reqPosArgs.")
            null
        }
    }

    private fun <T> mapValues(values: List<String>, converter: ConvertibleTo<T>): ConversionResult<List<T>> {
        val nonConvertible = mutableListOf<String>()
        val result = mutableListOf<T>()
        for (v in values) {
            converter.convert(v)?.let { result.add(it) } ?: run { nonConvertible.add(v) }
        }
        return if (nonConvertible.isEmpty()) {
            ConversionResult.result(result)
        } else {
            ConversionResult.failure(ListEntryConversionFailure(nonConvertible))
        }
    }

    private fun createFlags(flagDefinitions: List<ParameterDefinition<out Any>>): Map<String, ParameterDefinition<out Any>> {
        return flagDefinitions.associateBy { "--${it.name}" }
    }

    private enum class StateMachine {
        INIT, COMMAND, UNARY_FLAG, LONG_FLAG
    }
}
