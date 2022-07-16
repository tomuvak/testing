package com.tomuvak.testing.assertions

import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.fail

/**
 * Asserts that the receiver sequence [this] starts with the given [expectedValues] (in the order given). No assertions
 * are made regarding how the sequence continues after the given expected elements. Calling this function without
 * arguments makes no assertions about the sequence's elements, but does assert the sequence produces an iterator on
 * demand.
 */
fun <T> Sequence<T>.assertStartsWith(vararg expectedValues: T) {
    val iterator = iterator()
    for ((index, value) in expectedValues.withIndex()) {
        if (!iterator.hasNext()) fail("Expected sequence with $value at index $index, but only got $index elements")
        assertEquals(value, iterator.next(), "Wrong element at index $index")
    }
}

/**
 * Asserts that the receiver sequence [this] contains exactly the given [expectedValues] (no less, no more, in the order
 * given). In particular, calling this function with a single argument asserts the sequence is a singleton, and calling
 * it without arguments asserts the sequence is empty.
 */
fun <T> Sequence<T>.assertValues(vararg expectedValues: T) {
    val iterator = iterator()
    iterator.asSequence().assertStartsWith(*expectedValues)
    if (iterator.hasNext()) fail("Expected sequence with ${expectedValues.size} elements, got more")
}

/**
 * Verifies that the sequence obtained by running the given "intermediate" [operation] passes the given [test] block.
 *
 * Also verifies (non-)reiterability is preserved. Specifically, the receiver sequence [this] is expected to be
 * reiterable, and this function also verifies that:
 * 1. The sequence obtained by [operation] is also reiterable, and passes the given [test] a second time as well, and
 * 2. The result of running [operation] on the [constrainOnce] version of [this] also passes the given [test], and is
 * itself also not reiterable.
 *
 * This function is useful for testing "intermediate" operations on sequences, but note that it does NOT verify that the
 * given "intermediate" [operation] enumerates the receiver sequence [this] lazily (verifying that depends on the
 * specific nature of the [operation] and should be done separately on a case-by-case basis).
 */
fun <T, R> Sequence<T>.testIntermediateOperation(
    operation: Sequence<T>.() -> Sequence<R>,
    test: Sequence<R>.() -> Unit
) {
    val result = try { operation() } catch (e: Throwable) { fail("The operation failed", e) }
    try { result.test() } catch (e: Throwable) { fail("The resulting sequence failed to pass the test", e) }
    try { result.test() } catch (e: Throwable) {
        fail("The resulting sequence failed to pass the test on second iteration", e)
    }
    val constrainedOnce = try { constrainOnce().operation() } catch (e: Throwable) {
        fail("The operation failed on a constrained-once sequence", e)
    }
    try { constrainedOnce.test() } catch (e: Throwable) {
        fail("The resulting sequence when operating on a constrained-once sequence failed to pass the test", e)
    }
    assertFailsWith<IllegalStateException>(
        "The operation on a constrained-once sequence yields a seemingly reiterable sequence"
    ) { constrainedOnce.iterator() }
}
