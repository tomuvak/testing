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
            assertFails { Sequence<Int> { throw failure }.assertStartsWith() }
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
        thenFailsWith("index 0", 2, 1) { sequenceOf(1).assertStartsWith(2) }
        thenFailsWith("index 0", 2, 1) { sequenceOf(1, 2).assertStartsWith(2) }
        thenFailsWith("index 1", 2, 3) { sequenceOf(2, 3).assertStartsWith(2, 2) }
        thenFailsWith("index 0", 2, 1) { sequenceOf(1, 2).assertStartsWith(2, 1) }
        thenFailsWith("index 0", 2, 1) { sequenceOf(1, 2).assertStartsWith(2, 2) }
    }

    @Test fun assertStartsWithFailsWhenSequenceExhausted() {
        thenFailsWith(0, 2) { emptySequence<Int>().assertStartsWith(2) }
        thenFailsWith(1, 3) { sequenceOf(2).assertStartsWith(2, 3) }
        thenFailsWith(2, 5) { sequenceOf(3, 4).assertStartsWith(3, 4, 5) }
    }

    @Test fun assertValuesSucceeds() {
        emptySequence<Int>().assertValues()
        sequenceOf(1).assertValues(1)
        sequenceOf(1, 2).assertValues(1, 2)
    }

    @Test fun assertValueEnumeratesSequenceLazily() {
        var hasEnumeratedTooMuch = false

        assertFails {
            sequence {
                yield(1)
                hasEnumeratedTooMuch = true
            }.assertValues()
        }
        assertFalse(hasEnumeratedTooMuch)

        assertFails {
            sequence {
                yield(1)
                hasEnumeratedTooMuch = true
            }.assertValues(2)
        }
        assertFalse(hasEnumeratedTooMuch)
    }

    @Test fun assertValuesFailsWhenWrongValue() {
        thenFailsWith("index 0", 2, 1) { sequenceOf(1).assertValues(2) }
        thenFailsWith("index 0", 2, 1) { sequenceOf(1, 2).assertValues(2) }
        thenFailsWith("index 1", 2, 3) { sequenceOf(2, 3).assertValues(2, 2) }
        thenFailsWith("index 0", 2, 1) { sequenceOf(1, 2).assertValues(2, 1) }
        thenFailsWith("index 0", 2, 1) { sequenceOf(1, 2).assertValues(2, 2) }
    }

    @Test fun assertValuesFailsWhenSequenceExhaustedTooSoon() {
        thenFailsWith(0, 2) { emptySequence<Int>().assertValues(2) }
        thenFailsWith(1, 3) { sequenceOf(2).assertValues(2, 3) }
        thenFailsWith(2, 5) { sequenceOf(3, 4).assertValues(3, 4, 5) }
    }

    @Test fun assertValuesFailsWhenSequenceNotExhausted() {
        thenFailsWith(0) { sequenceOf(1).assertValues() }
        thenFailsWith(0) { sequenceOf(1, 2).assertValues() }
        thenFailsWith(1) { sequenceOf(1, 2).assertValues(1) }
        thenFailsWith(0) { sequenceOf(1, 2, 3).assertValues() }
        thenFailsWith(1) { sequenceOf(1, 2, 3).assertValues(1) }
        thenFailsWith(2) { sequenceOf(1, 2, 3).assertValues(1, 2) }
    }

    @Test fun testIntermediateOperationFailsWhenOperationFails() {
        val operationFailure = Exception("Failure in operation itself")
        val failure = assertFailsWith<AssertionError> {
            sequenceOf(1, 2, 3).testIntermediateOperation<_, String>({ throw operationFailure }) {}
        }
        assertSame(operationFailure, failure.cause)
        failure.assertMessageContains("operation failed")
    }

    @Test fun testIntermediateOperationFailsWhenTestFails() {
        val testFailure = Exception("Failure in test")
        val failure = assertFailsWith<AssertionError> {
            sequenceOf(1, 2, 3).testIntermediateOperation({ this }) { throw testFailure }
        }
        assertSame(testFailure, failure.cause)
        failure.assertMessageContains("failed to pass the test")
    }

    @Test fun testIntermediateOperationFailsWhenTestFailsOnSecondIteration() {
        var secondTestFailure: Throwable? = null
        val failure = assertFailsWith<AssertionError> {
            sequenceOf(1, 2, 3).testIntermediateOperation({ this }) {
                if (secondTestFailure == null) secondTestFailure = Exception("Second iteration failure")
                else throw secondTestFailure!!
            }
        }
        assertSame(secondTestFailure, failure.cause)
        failure.assertMessageContains("on second iteration")
    }

    @Test fun testIntermediateOperationsFailsWhenOperationFailsOnConstrainedOnceSequence() {
        val failure = assertFailsWith<AssertionError> {
            sequenceOf(1, 2, 3).testIntermediateOperation({
                repeat(2) { iterator() }
                this
            }) {}
        }
        val illegalStateException = assertIs<IllegalStateException>(failure.cause)
        assertEquals("This sequence can be consumed only once.", illegalStateException.message)
        failure.assertMessageContains("operation failed", "constrained-once")
    }

    @Test fun testIntermediateOperationFailsWhenTestFailsOnConstrainedOnceSequence() {
        val failure = assertFailsWith<AssertionError> {
            sequenceOf(1, 2, 3).testIntermediateOperation({ this }) {
                repeat(2) { iterator() }
            }
        }
        val illegalStateException = assertIs<IllegalStateException>(failure.cause)
        assertEquals("This sequence can be consumed only once.", illegalStateException.message)
        failure.assertMessageContains("failed to pass the test", "constrained-once")
    }

    @Test fun testIntermediateOperationFailsWhenResultOfConstrainedOnceSequenceIsReiterable() =
        assertFailsWithTypeAndMessageContaining<AssertionError>("reiterable") {
            sequenceOf(1, 2, 3).testIntermediateOperation({ sequenceOf(4, 5, 6) }) {}
        }

    @Test fun testIntermediateOperationTestsAndPasses() {
        val originalSequence = sequence { yieldAll(listOf(1, 2, 3)) }
        val result = sequence { yieldAll(listOf("one", "two", "three")) }
        val constrainedOnceResult = sequenceOf("constrained", "once").constrainOnce()
        var numOperationIterations = 0
        var numTestIterations = 0
        originalSequence.testIntermediateOperation({
            if (numOperationIterations++ < 1) {
                assertSame(originalSequence, this)
                result
            } else {
                assertValues(1, 2, 3)
                assertFailsWith<IllegalStateException> { iterator() }
                constrainedOnceResult
            }
        }) {
            if (numTestIterations++ < 2) assertSame(result, this)
            else {
                assertSame(constrainedOnceResult, this)
                iterator() // an actual test would iterate over the result (preventing reiteration if constrained-once)
            }
        }
        assertEquals(2, numOperationIterations)
        assertEquals(3, numTestIterations)
    }

    @Test fun testLazyIntermediateOperationFailsWhenOperationFails() {
        val operationFailure = Exception("Failure in operation itself")
        val failure = assertFailsWith<AssertionError> {
            sequenceOf(1, 2, 3).testLazyIntermediateOperation<_, String>({ throw operationFailure }) {}
        }
        assertSame(operationFailure, failure.cause)
        failure.assertMessageContains("operation failed")
    }

    @Test fun testLazyIntermediateOperationFailsWhenTestFails() {
        val testFailure = Exception("Failure in test")
        val failure = assertFailsWith<AssertionError> {
            sequenceOf(1, 2, 3).testLazyIntermediateOperation({ this }) { throw testFailure }
        }
        assertSame(testFailure, failure.cause)
        failure.assertMessageContains("failed to pass the test")
    }

    @Test fun testLazyIntermediateOperationFailsWhenTestFailsOnSecondIteration() {
        var secondTestFailure: Throwable? = null
        val failure = assertFailsWith<AssertionError> {
            sequenceOf(1, 2, 3).testLazyIntermediateOperation({ this }) {
                if (secondTestFailure == null) secondTestFailure = Exception("Second iteration failure")
                else throw secondTestFailure!!
            }
        }
        assertSame(secondTestFailure, failure.cause)
        failure.assertMessageContains("on second iteration")
    }

    @Test fun testLazyIntermediateOperationsFailsWhenOperationFailsOnConstrainedOnceSequence() {
        val failure = assertFailsWith<AssertionError> {
            sequenceOf(1, 2, 3).testLazyIntermediateOperation({
                repeat(2) { iterator() }
                this
            }) {}
        }
        val illegalStateException = assertIs<IllegalStateException>(failure.cause)
        assertEquals("This sequence can be consumed only once.", illegalStateException.message)
        failure.assertMessageContains("operation failed", "constrained-once")
    }

    @Test fun testLazyIntermediateOperationFailsWhenTestFailsOnConstrainedOnceSequence() {
        val failure = assertFailsWith<AssertionError> {
            sequenceOf(1, 2, 3).testLazyIntermediateOperation({ this }) {
                repeat(2) { iterator() }
            }
        }
        val illegalStateException = assertIs<IllegalStateException>(failure.cause)
        assertEquals("This sequence can be consumed only once.", illegalStateException.message)
        failure.assertMessageContains("failed to pass the test", "constrained-once")
    }

    @Test fun testLazyIntermediateOperationFailsWhenResultOfConstrainedOnceSequenceIsReiterable() =
        assertFailsWithTypeAndMessageContaining<AssertionError>("reiterable") {
            sequenceOf(1, 2, 3).testLazyIntermediateOperation({ sequenceOf(4, 5, 6) }) {}
        }

    @Test fun testLazyIntermediateOperationTestsAndPasses() {
        val originalSequence = sequenceOf(1, 2, 3)
        val result = sequence { yieldAll(listOf("one", "two", "three")) }
        val constrainedOnceResult = sequenceOf("constrained", "once").constrainOnce()
        var numOperationIterations = 0
        var numTestIterations = 0
        originalSequence.testLazyIntermediateOperation({
            val iterator = iterator()
            iterator.asSequence().assertStartsWith(1, 2, 3)
            assertFailsWith<AssertionError> { iterator.next() }
            if (numOperationIterations++ < 1) {
                result
            } else {
                assertFailsWith<IllegalStateException> { iterator() }
                constrainedOnceResult
            }
        }) {
            if (numTestIterations++ < 2) assertSame(result, this)
            else {
                assertSame(constrainedOnceResult, this)
                iterator() // an actual test would iterate over the result (preventing reiteration if constrained-once)
            }
        }
        assertEquals(2, numOperationIterations)
        assertEquals(3, numTestIterations)
    }

    @Test fun testTerminalOperationFailsWhenOperationFails() {
        val operationFailure = Exception("Failure in operation itself")
        val failure = assertFailsWith<AssertionError> {
            sequenceOf(1, 2, 3).testTerminalOperation({ throw operationFailure })
        }
        assertSame(operationFailure, failure.cause)
        failure.assertMessageContains("operation failed")
    }

    @Test fun testTerminalOperationFailsWhenTestFails() {
        val testFailure = Exception("Failure in test")
        val failure = assertFailsWith<AssertionError> {
            sequenceOf(1, 2, 3).testTerminalOperation({}) { throw testFailure }
        }
        assertSame(testFailure, failure.cause)
        failure.assertMessageContains("failed to pass the test")
    }

    @Test fun testTerminalOperationTestsAndPasses() {
        val originalSequence = sequenceOf(1, 2, 3)
        val result = "result"
        var numOperationIterations = 0
        var numTestIterations = 0
        originalSequence.testTerminalOperation({
            assertValues(1, 2, 3)
            assertFailsWith<IllegalStateException> { iterator() }
            numOperationIterations++
            result
        }) {
            assertEquals(result, it)
            numTestIterations++
        }
        assertEquals(1, numOperationIterations)
        assertEquals(1, numTestIterations)
    }

    @Test fun testLazyTerminalOperationFailsWhenOperationFails() {
        val operationFailure = Exception("Failure in operation itself")
        val failure = assertFailsWith<AssertionError> {
            sequenceOf(1, 2, 3).testLazyTerminalOperation({ throw operationFailure })
        }
        assertSame(operationFailure, failure.cause)
        failure.assertMessageContains("operation failed")
    }

    @Test fun testLazyTerminalOperationFailsWhenTestFails() {
        val testFailure = Exception("Failure in test")
        val failure = assertFailsWith<AssertionError> {
            sequenceOf(1, 2, 3).testLazyTerminalOperation({}) { throw testFailure }
        }
        assertSame(testFailure, failure.cause)
        failure.assertMessageContains("failed to pass the test")
    }

    @Test fun testLazyTerminalOperationTestsAndPasses() {
        val originalSequence = sequenceOf(1, 2, 3)
        val result = "result"
        var numOperationIterations = 0
        var numTestIterations = 0
        originalSequence.testLazyTerminalOperation({
            val iterator = iterator()
            iterator.asSequence().assertStartsWith(1, 2, 3)
            assertFailsWithTypeAndMessageContaining<AssertionError>("enumerated thus far") { iterator.next() }
            assertFailsWith<IllegalStateException> { iterator() }
            numOperationIterations++
            result
        }) {
            assertEquals(result, it)
            numTestIterations++
        }
        assertEquals(1, numOperationIterations)
        assertEquals(1, numTestIterations)
    }

    private fun thenFailsWith(vararg messageParts: Any?, block: () -> Unit) =
        assertFailsWithTypeAndMessageContaining<AssertionError>(*messageParts, block=block)
}
