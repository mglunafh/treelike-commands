package org.some.project.kotlin.cmdparsing

data class TokenizedParseObject(val positionalArguments: List<String>, val options: Map<String, OptionValue>)
