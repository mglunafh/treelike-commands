package org.some.project.kotlin.geometry.command

import java.util.concurrent.atomic.AtomicInteger

@JvmInline
value class Id private constructor(val id: Int) {

    companion object {
        private val sequence = AtomicInteger(0)
        private val registry = mutableSetOf<Int>()

        fun next(): Id {
            val result = sequence.incrementAndGet()
            registry.add(result)
            return Id(result)
        }

        operator fun get(id: Int): Id? {
            return if (registry.contains(id)) Id(id) else null
        }
    }
}
