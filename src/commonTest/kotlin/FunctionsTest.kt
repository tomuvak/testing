package com.tomuvak.testing.assertions

import kotlin.test.Test

class FunctionsTest {
    @Test fun mootFunction() =
        assertFailsWithTypeAndMessageContaining<AssertionError>("ot supposed", "be called") { mootFunction(Unit) }
}
