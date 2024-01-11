package org.some.project.kotlin.cmdparsing

object Tokenizer {

    fun tokenize(argLine: String, delimiter: Regex = "\\s+".toRegex()): List<String> {
        return argLine.split(delimiter).filter { it.isNotBlank() }.map { it }
    }

    fun tokenize(argLine: String, delimiter: String): List<String> {
        return argLine.split(delimiter).filter { it.isNotBlank() }
    }
}
