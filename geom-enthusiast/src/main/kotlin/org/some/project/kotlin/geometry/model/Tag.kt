package org.some.project.kotlin.geometry.model

import kotlinx.serialization.Serializable

@Serializable
@JvmInline
value class Tag private constructor(val tag: String) {

    companion object {

        val EMPTY_TAGS = listOf<Tag>()

        private val TAG_REGEX = """^[a-z][a-z0-9\-]*${'$'}""".toRegex()

        fun isTag(arg: String): Boolean = arg.matches(TAG_REGEX)

        fun toTagOrNull(tag: String): Tag? = if (tag.matches(TAG_REGEX)) Tag(tag) else null

        fun from(arg: String): Tag = if (arg.matches(TAG_REGEX)) Tag(arg) else
            throw IllegalArgumentException("Value '$arg' does not satisfy the requirements of a valid tag.")
    }
}
