package com.tomuvak.testing.assertions

import kotlin.test.Test
import kotlin.test.assertEquals

class FunctionsTest {
    @Test fun mootFunction() =
        assertFailsWithTypeAndMessageContaining<AssertionError>("ot supposed", "be called") { mootFunction(Unit) }

    @Test fun mockFunction() {
        val calls = mutableListOf<Int>()
        var mockResult = ""
        val mock = MockFunction<Int, String> {
            calls.add(it)
            mockResult
        }
        fun verify(argument: Int, result: String) {
            calls.clear()
            mockResult = result
            val originalRecordedCalls = mock.calls.toList()

            assertEquals(result, mock(argument))

            assertEquals(listOf(argument), calls)
            assertEquals(originalRecordedCalls + argument, mock.calls)
        }

        assertEquals(emptyList(), mock.calls)
        verify(3, "abc")
        verify(-7, "asdfg")
        verify(20, "")
        verify(0, "xyz")
    }
}
