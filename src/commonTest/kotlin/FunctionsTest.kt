package com.tomuvak.testing.assertions

import kotlin.test.Test

class FunctionsTest {
    @Test fun mootPredicate() =
        assertFailsWithTypeAndMessageContaining<AssertionError>("ot supposed", "be called") { mootPredicate(Unit) }
}
