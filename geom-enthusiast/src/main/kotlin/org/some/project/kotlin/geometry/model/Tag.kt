package org.some.project.kotlin.geometry.model

@JvmInline
value class Tag private constructor(val tag: String) {

    companion object {
        val TAG_REGEX = """^[a-z][a-z0-9\-]*${'$'}""".toRegex()
        private const val message = "Value does not satisfy the requirements of a valid tag."

        fun isTag(arg: String): Boolean = arg.matches(TAG_REGEX)

        fun toTagOrNull(tag: String): Tag? = if (tag.matches(TAG_REGEX)) Tag(tag) else null
    }
}
