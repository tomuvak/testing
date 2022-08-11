package com.tomuvak.testing.assertions

import kotlin.test.assertEquals
import kotlin.test.fail

/**
 * A nullary function which asserts it's never actually called.
 */
val mootProvider: () -> Nothing = { fail("Not supposed to actually be called") }

/**
 * A unary function which asserts it's never actually called.
 */
val mootFunction: (Any?) -> Nothing = { fail("Not supposed to actually be called") }

/**
 * Returns a function which yields the [scriptedReturnValues], in order, in successive invocations, and throws if called
 * again after all [scriptedReturnValues] have been exhausted.
 *
 * For example, the function returned from `scriptedProvider(1, 4, 1)` will:
 * * return `1` when called for the first time;
 * * return `4` when called for the second time;
 * * return `1` when called for the third time;
 * * throw whenever called again after that.
 */
fun <TOutput> scriptedProvider(vararg scriptedReturnValues: TOutput): () -> TOutput {
    var numCalls = 0
    return {
        if (numCalls >= scriptedReturnValues.size) fail("Unexpected call to exhausted scripted provider")
        scriptedReturnValues[numCalls++]
    }
}

/**
 * Returns a function which only executes successfully when it receives as argument the first components of
 * [scriptedCalls], in order, in successive calls, and which returns the second components of [scriptedCalls], in order,
 * in successive calls. In particular, the returned function cannot execute successfully more times than the number of
 * elements in [scriptedCalls].
 *
 * For example, the function returned from `scriptedFunction(1 to "x", 3 to "yz", 1 to "")` will:
 * * throw when called for the first time, unless the argument passed is `1`, in which case it will return `"x"`;
 * * throw when called for the second time, unless the argument passed is `3`, in which case it will return `"xy"`;
 * * throw when called for the third time, unless the argument passed is `1`, in which case it will return `""`;
 * * throw when called for the fourth time, whatever the argument passed.
 *
 * Note: each step above assumes the previous steps all completed successfully; once the function throws the behavior in
 * further invocations is unspecified (this should not normally happen: the idea is that the function is used in some
 * test, which should normally fail as soon as any invocation of the function fails).
 */
fun <TInput, TOutput> scriptedFunction(vararg scriptedCalls: Pair<TInput, TOutput>): (TInput) -> TOutput {
    var numCalls = 0
    return {
        if (numCalls >= scriptedCalls.size) fail("Unexpected call to exhausted scripted function (argument: $it)")
        val (expectedArgument, mockResult) = scriptedCalls[numCalls++]
        assertEquals(expectedArgument, it, "Unexpected argument passed to scripted function")
        mockResult
    }
}

/**
 * Instances of this class can serve as functions from [TInput] to [TOutput] (either directly, or, when needed, through
 * their [invoke] function). The actual function which gets executed and whose return value is returned is [function].
 * Each invocation adds the argument passed to it to [calls].
 *
 * When no further action is needed other than recording the calls and returning a value, [SimpleMockFunction] may
 * suffice.
 */
class MockFunction<TInput, TOutput>(var function: (TInput) -> TOutput) {
    val calls: MutableList<TInput> = mutableListOf()

    operator fun invoke(argument: TInput): TOutput {
        calls.add(argument)
        return function(argument)
    }
}

/**
 * Instances of this class can serve as functions from [TInput] to [TOutput] (either directly, or, when needed, through
 * their [invoke] function). Each invocation adds the argument passed to it to [calls], and returns [returnValue].
 *
 * When it is desired for invocations to have additional side effects and/or more complicated logic, [MockFunction] can
 * be used instead.
 */
class SimpleMockFunction<TInput, TOutput>(var returnValue: TOutput) {
    private val underlying: MockFunction<TInput, TOutput> = MockFunction { returnValue }
    val calls: MutableList<TInput> get() = underlying.calls
    operator fun invoke(argument: TInput): TOutput = underlying(argument)
}
