package com.tomuvak.testing

import kotlin.test.Test
import kotlin.test.assertEquals

class UtilTest {
    @Test fun emptyMessagePrefix() = assertEquals("", messagePrefix(null))
    @Test fun nonEmptyMessagePrefix() = assertEquals("Test. ", messagePrefix("Test"))
}
