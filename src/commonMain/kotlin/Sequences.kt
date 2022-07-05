package com.tomuvak.testing.assertions

import kotlin.test.assertEquals
import kotlin.test.fail

fun <T> Sequence<T>.assertStartsWith(vararg expectedValues: T) {
    val iterator = iterator()
    for ((index, value) in expectedValues.withIndex()) {
        if (!iterator.hasNext()) fail("Expected sequence with $value at index $index, but only got $index elements")
        assertEquals(value, iterator.next(), "Wrong element at index $index")
    }
}
