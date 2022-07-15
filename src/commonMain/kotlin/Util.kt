package com.tomuvak.testing.assertions

import kotlin.test.DefaultAsserter.fail

/**
 * A predicate which asserts it's never actually called.
 */
val mootPredicate: (Any?) -> Boolean = { fail("Not supposed to actually be called") }

fun messagePrefix(message: String?): String = if (message == null) "" else "$message. "
