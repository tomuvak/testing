package com.tomuvak.testing

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
 * Return a sequence which yields the exact same elements as the receiver sequence [this], but throws an
 * [AssertionError] upon any attempt to enumerate the sequence further (including merely checking whether there are
 * further elements). Useful in asserting certain operations on a given sequence don't enumerate it further than
 * expected.
 */
fun <T> Sequence<T>.capEnumeration(): Sequence<T> = Sequence { CappedIterator(iterator()) }

private class CappedIterator<T>(private val source: Iterator<T>): Iterator<T> {
    override fun hasNext(): Boolean = source.hasNext() || throw AssertionError("Not supposed to be enumerated thus far")
    override fun next(): T {
        hasNext()
        return source.next()
    }
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
 * specific nature of the [operation] and should be done separately on a case-by-case basis; for some cases
 * [testLazyIntermediateOperation] might be suitable).
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

/**
 * Verifies that the sequence obtained by running the given "intermediate" [operation] passes the given [test] block,
 * and also that at no point is the receiver sequence [this] enumerated to exhaustion (i.e. it's possible that its last
 * element is retrieved, but no attempt is made by the [operation] and/or the [test] to retrieve further elements or to
 * verify that that's the end of the sequence).
 *
 * Also verifies (non-)reiterability is preserved. Specifically, the receiver sequence [this] is expected to be
 * reiterable, and this function also verifies that:
 * 1. The sequence obtained by [operation] is also reiterable, and passes the given [test] a second time as well, and
 * 2. The result of running [operation] on the [constrainOnce] version of [this] also passes the given [test], and is
 * itself also not reiterable.
 *
 * This function is useful for testing "intermediate" operations on sequences, and also, in some cases, and unlike
 * [testIntermediateOperation], that the tested operations enumerate the receiver sequence [this] lazily (specifically
 * that no attempt is made to enumerate the sequence past the last given element; some tests might require finer
 * granularity, and should be crafted on a case-by-case basis).
 */
fun <T, R> Sequence<T>.testLazyIntermediateOperation(
    operation: Sequence<T>.() -> Sequence<R>,
    test: Sequence<R>.() -> Unit
) = capEnumeration().testIntermediateOperation(operation, test)

/**
 * Verifies that the result obtained by running the given "terminal" [operation] passes the given [test] block and that
 * the receiver sequence [this] is not enumerated more than once in the process.
 *
 * The separation between [operation] and [test] is rather superficial; "testing" might, and, in fact, sometimes has to,
 * be done as part of [operation], e.g. when the aim of the test is to verify that the operation fails.
 *
 * This function does not verify that the given [operation] enumerates the receiver sequence [this] lazily (verifying
 * that depends on the specific nature of the [operation] and should be done separately on a case-by-case basis; for
 * some cases [testLazyTerminalOperation] might be suitable).
 */
fun <T, R> Sequence<T>.testTerminalOperation(operation: Sequence<T>.() -> R, test: (R) -> Unit = {}) {
    val result = try { constrainOnce().operation() } catch (e: Throwable) { fail("The operation failed", e) }
    try { test(result) } catch (e: Throwable) { fail("The result failed to pass the test", e) }
}

/**
 * Verifies that the result obtained by running the given "terminal" [operation] passes the given [test] block, and that
 * the receiver sequence [this] is neither enumerated more than once in the process, nor enumerated to exhaustion (i.e.
 * it's possible that its last element is retrieved, but no attempt is made by the [operation] and/or the [test] to
 * retrieve further elements or to verify that that's the end of the sequence).
 *
 * The separation between [operation] and [test] is rather superficial; "testing" might, and, in fact, sometimes has to,
 * be done as part of [operation], e.g. when the aim of the test is to verify that the operation fails.
 *
 * Unlike [testTerminalOperation], this function is useful for verifying that operations on sequences are performed
 * lazily in some sense (specifically that no attempt is made to enumerate the sequence past the last given element;
 * some tests might require finer granularity, and should be crafted on a case-by-case basis).
 */
fun <T, R> Sequence<T>.testLazyTerminalOperation(operation: Sequence<T>.() -> R, test: (R) -> Unit = {}) =
    capEnumeration().testTerminalOperation(operation, test)
