package com.tomuvak.testing.assertions

import kotlin.test.fail

/**
 * A function which asserts it's never actually called.
 */
val mootFunction: (Any?) -> Nothing = { fail("Not supposed to actually be called") }

/**
 * Instances of this class can serve as functions from [TInput] to [TOutput] (either directly, or, when needed, through
 * their [invoke] function). The actual function which gets executed and whose return value is returned is [function].
 * Each invocation adds the argument passed to it to [calls].
 */
class MockFunction<TInput, TOutput>(var function: (TInput) -> TOutput) {
    val calls: MutableList<TInput> = mutableListOf()

    operator fun invoke(argument: TInput): TOutput {
        calls.add(argument)
        return function(argument)
    }
}
