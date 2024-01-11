package org.some.project.kotlin.cmdparsing.command

@JvmInline
value class Tag(val tag: String) {

    init {
        require(tag.matches(TAG_REGEX)) { "Value '$tag' does not satisfy the requirements of a valid tag." }
    }

    companion object {

        val TAG_REGEX = """^[a-z][a-z0-9\-]*${'$'}""".toRegex()

        fun isTag(arg: String): Boolean {
            return arg.matches(TAG_REGEX)
        }
    }
}
