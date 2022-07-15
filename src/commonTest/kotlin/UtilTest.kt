package com.tomuvak.testing.assertions

import kotlin.test.Test
import kotlin.test.assertEquals

class UtilTest {
    @Test fun mootPredicate() {
        assertFailsWithTypeAndMessageContaining<AssertionError>("ot supposed", "be called") { mootPredicate(Unit) }
    }

    @Test fun emptyMessagePrefix() = assertEquals("", messagePrefix(null))
    @Test fun nonEmptyMessagePrefix() = assertEquals("Test. ", messagePrefix("Test"))
}
