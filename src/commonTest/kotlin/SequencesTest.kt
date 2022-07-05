package com.tomuvak.testing.assertions

import kotlin.test.*

class SequencesTest {
    @Test fun assertStartsWithSucceeds() {
        emptySequence<Int>().assertStartsWith()
        sequenceOf(1).assertStartsWith()
        sequenceOf(1).assertStartsWith(1)
        sequenceOf(1, 2).assertStartsWith()
        sequenceOf(1, 2).assertStartsWith(1)
        sequenceOf(1, 2).assertStartsWith(1, 2)
    }

    @Test fun assertStartsWithTriesToEnumerateSequenceEvenWithEmptyPrefix() {
        val failure = Exception("Should fail with this specific exception")
        assertSame(
            failure,
            assertFails {
                (object : Sequence<Int> { override fun iterator(): Iterator<Int> = throw failure }).assertStartsWith()
            }
        )
    }

    @Test fun assertStartsWithEnumeratesSequenceLazily() {
        sequence<Int> { fail("Should not be evaluated") }.assertStartsWith()
        sequence {
            yield(1)
            fail("Should not be evaluated")
        }.assertStartsWith(1)
        sequence {
            yield(1)
            yield(2)
            fail("Should not be evaluated")
        }.assertStartsWith(1, 2)

        var hasEnumeratedTooMuch = false
        assertFails {
            sequence {
                yield(1)
                hasEnumeratedTooMuch = true
            }.assertStartsWith(2)
        }
        assertFalse(hasEnumeratedTooMuch)
    }

    @Test fun assertStartsWithFailsWhenWrongValue() {
        assertFailsWithMessageContaining("index 0", 2, 1) { sequenceOf(1).assertStartsWith(2) }
        assertFailsWithMessageContaining("index 0", 2, 1) { sequenceOf(1, 2).assertStartsWith(2) }
        assertFailsWithMessageContaining("index 1", 2, 3) { sequenceOf(2, 3).assertStartsWith(2, 2) }
        assertFailsWithMessageContaining("index 0", 2, 1) { sequenceOf(1, 2).assertStartsWith(2, 1) }
        assertFailsWithMessageContaining("index 0", 2, 1) { sequenceOf(1, 2).assertStartsWith(2, 2) }
    }

    @Test fun assertStartsWithFailsWhenSequenceExhausted() {
        assertFailsWithMessageContaining(0, 2) { emptySequence<Int>().assertStartsWith(2) }
        assertFailsWithMessageContaining(1, 3) { sequenceOf(2).assertStartsWith(2, 3) }
        assertFailsWithMessageContaining(2, 5) { sequenceOf(3, 4).assertStartsWith(3, 4, 5) }
    }
}
