package com.tomuvak.testing.assertions

import kotlin.test.fail

/**
 * A predicate which asserts it's never actually called.
 */
val mootPredicate: (Any?) -> Boolean = { fail("Not supposed to actually be called") }
