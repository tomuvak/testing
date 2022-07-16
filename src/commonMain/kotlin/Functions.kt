package com.tomuvak.testing.assertions

import kotlin.test.fail

/**
 * A function which asserts it's never actually called.
 */
val mootFunction: (Any?) -> Nothing = { fail("Not supposed to actually be called") }
