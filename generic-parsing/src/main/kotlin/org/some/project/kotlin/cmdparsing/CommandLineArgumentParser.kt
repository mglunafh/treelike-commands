package org.some.project.kotlin.cmdparsing

import org.some.project.kotlin.cmdparsing.ConversionResult.*
import org.some.project.kotlin.cmdparsing.ConversionResult.ConversionFailureEnum.*
import org.some.project.kotlin.cmdparsing.ConversionResult.ValueConversionFailure
import org.some.project.kotlin.cmdparsing.ParseResult.ParseError
import org.some.project.kotlin.cmdparsing.ParseResult.ParseSuccess
import kotlin.reflect.KClass
import kotlin.reflect.cast

object CommandLineArgumentParser {

    fun convertParseResults(
        commandDefinition: CommandDefinition,
        parseObject: TokenizedParseObject
    ): ParseResult<ValueParseObject> {
        val options = parseObject.options
        val commandName = commandDefinition.commandName
        val resultMap = mutableMapOf<String, Any>()
        val errors = mutableListOf<ErrorType>()

        for (paramDefinition in commandDefinition.paramDefinitions) {
            val optionName = paramDefinition.name
            val optionType = paramDefinition.type
            val arity = paramDefinition.arity
            val delimiter = paramDefinition.delimiter

            val value = options[optionName]

            when {
                value == null && paramDefinition.required && paramDefinition.default == null -> {
                    errors.add(RequiredParameterNotSet(commandName, optionName))
                }

                value == null -> {
                    paramDefinition.default?.let { resultMap[optionName] = it }
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
            errors.isEmpty() -> ParseSuccess(ValueParseObject(parseObject.positionalArguments, resultMap))
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

    fun parse(commandDefinition: CommandDefinition, args: List<String>): ParseResult<TokenizedParseObject> {
        val commandName = commandDefinition.commandName
        val flags = createFlags(commandDefinition.paramDefinitions)

        var currState = StateMachine.INIT
        var currOption: ParameterDefinition<out Any>? = null

        var flagArgsParsed = 0
        var currListOfFlagArgs: MutableList<String>? = null

        val posArgsList = mutableListOf<String>()
        val parsedObjects = mutableMapOf<String, OptionValue>()

        for (arg in args) {
            when (currState) {
                StateMachine.INIT -> {
                    currState = if (commandName == arg)
                        StateMachine.COMMAND
                    else
                        return ParseError(WrongCommand(commandName, arg))
                }

                StateMachine.COMMAND -> {
                    val flag = flags[arg]

                    when {
                        flag == null -> {
                            if (posArgsList.size < commandDefinition.positionalArguments) {
                                posArgsList.add(arg)
                            } else {
                                return ParseError(TooManyArguments(commandName, commandDefinition.positionalArguments, arg))
                            }
                        }
                        flag.type == Boolean::class -> {
                            if (flag == CommandDefinition.HELP_FLAG) {
                                return ParseResult.Help(commandDefinition.description())
                            } else {
                                parsedObjects[flag.name] = SwitchValue()
                            }
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
            StateMachine.COMMAND -> {
                if (posArgsList.size >= commandDefinition.requiredPositionalArguments) {
                    ParseSuccess(TokenizedParseObject(posArgsList.toList(), parsedObjects.toMap()))
                } else {
                    ParseError(TooFewRequiredArguments(commandName, commandDefinition.requiredPositionalArguments, posArgsList.size))
                }
            }
            StateMachine.UNARY_FLAG -> ParseError(MissingParameterValue(commandName, currOption!!.name))
            StateMachine.LONG_FLAG -> ParseError(MissingParameters(commandName, currOption!!.name, currOption.arity))
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
        return flagDefinitions.associateBy { it.name }
    }

    private enum class StateMachine {
        INIT, COMMAND, UNARY_FLAG, LONG_FLAG
    }
}
