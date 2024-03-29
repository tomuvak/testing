package com.tomuvak.testing

import kotlin.test.Test
import kotlin.test.assertEquals

class FunctionsTest {
    @Test fun mootProviderFails() = thenFailsWith("oot provider", "ot supposed", "be called") { mootProvider() }
    @Test fun mootFunctionFails() = thenFailsWith("oot function", "ot supposed", "be called", 3) { mootFunction(3) }
    @Test fun mootFunction2Fails() =
        thenFailsWith("oot function", "ot supposed", "be called", 3, "xyz") { mootFunction2(3, "xyz") }
    @Test fun mootFunction3Fails() =
        thenFailsWith("oot function", "ot supposed", "be called", 3, "xyz", "true") { mootFunction3(3, "xyz", "true") }
    @Test fun mootFunction4Fails() = thenFailsWith("oot function", "ot supposed", "be called", 3, "xyz", "true", 2.4) {
        mootFunction4(3, "xyz", "true", 2.4)
    }

    @Test fun scriptedProviderObeysScript() {
        val f1 = scriptedProvider("xy")
        assertEquals("xy", f1())

        val f2 = scriptedProvider("xyz", "", "gfdsa")
        assertEquals("xyz", f2())
        assertEquals("", f2())
        assertEquals("gfdsa", f2())
    }
    @Test fun scriptedProviderFailsWhenScriptExhausted() {
        val f1 = scriptedProvider("xy")
        f1()
        thenFailsWith("Unexpected call", "exhausted") { f1() }

        val f2 = scriptedProvider("xyz", "", "gfdsa")
        f2()
        f2()
        f2()
        thenFailsWith("Unexpected call", "exhausted") { f2() }
    }

    @Test fun scriptedFunctionObeysScript() {
        val f1 = scriptedFunction(2 to "xy")
        assertEquals("xy", f1(2))

        val f2 = scriptedFunction(5 to "xyz", -3 to "", 8 to "gfdsa")
        assertEquals("xyz", f2(5))
        assertEquals("", f2(-3))
        assertEquals("gfdsa", f2(8))
    }
    @Test fun scriptedFunctionFailsOnUnexpectedInput() {
        val f1 = scriptedFunction(2 to "xy")
        thenFailsWith("Unexpected argument", "scripted function", 2, 3) { f1(3) }

        val f2 = scriptedFunction(5 to "xyz", -3 to "", 8 to "gfdsa")
        f2(5)
        thenFailsWith("Unexpected argument", "scripted function", -3, -4) { f2(-4) }
    }
    @Test fun scriptedFunctionFailsWhenScriptExhausted() {
        val f1 = scriptedFunction(2 to "xy")
        f1(2)
        thenFailsWith("Unexpected call", "exhausted", 2) { f1(2) }

        val f2 = scriptedFunction(5 to "xyz", -3 to "", 8 to "gfdsa")
        f2(5)
        f2(-3)
        f2(8)
        thenFailsWith("Unexpected call", "exhausted", 0) { f2(0) }
    }

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

    @Test fun simpleMockFunction() {
        var expectedResult = ""
        val mock = SimpleMockFunction<Int, String>(expectedResult)

        fun verify(argument: Int, result: String?) {
            val originalRecordedCalls = mock.calls.toList()
            if (result != null) {
                expectedResult = result
                mock.returnValue = result
            }
            assertEquals(expectedResult, mock(argument))
            assertEquals(originalRecordedCalls + argument, mock.calls)
        }

        assertEquals(emptyList(), mock.calls)
        verify(10, null)
        verify(3, "abc")
        verify(-7, "asdfg")
        verify(5, null)
        verify(8, null)
        verify(20, "")
        verify(0, "xyz")
    }

    @Test fun assertDelegationSucceeds() {
        val mock = SimpleMockFunction<Int, _>("return value")
        val delegator: (Int) -> String = { mock(it) }
        mock.assertDelegation(delegator, 3)

        mock.returnValue = "another return value"
        mock.assertDelegation(delegator, 7)

        mock.returnValue = "yet another return value"
        mock.assertDelegation(delegator, -5)
    }
    @Test fun assertDelegationFailsWhenDoesntCallDelegatee() {
        val mock = SimpleMockFunction<Int, _>("asdfg")
        val delegator: (Int) -> String = { "asdfg" } // happens to return the same value, without actually calling mock
        thenFailsWith("not", "call") { mock.assertDelegation(delegator, 3) }

        // Make sure calling the mock outside assertDelegation doesn't count:
        mock(5)
        thenFailsWith("not", "call") { mock.assertDelegation(delegator, 7) }
    }
    @Test fun assertDelegationFailsWhenCallsDelegateeMoreThanOnce() {
        val mock = SimpleMockFunction<Int, _>("asdfg")
        val delegator: (Int) -> String = {
            mock(it)
            mock(it)
        }
        thenFailsWith(2, "3, 3") { mock.assertDelegation(delegator, 3) }
    }
    @Test fun assertDelegationFailsWhenCallsDelegateeWithWrongArgument() {
        val mock = SimpleMockFunction<Int, _>("asdfg")
        val delegator: (Int) -> String = { mock(it + 1) }
        thenFailsWith("wrong argument", 3, 4) { mock.assertDelegation(delegator, 3) }
    }
    @Test fun assertDelegationFailsWhenWrongResult() {
        val mock = SimpleMockFunction<Int, _>("asdfg")
        val delegator: (Int) -> String = {
            mock(it)
            "Called delegatee, but returning some other result :P"
        }
        thenFailsWith("different result", "asdfg", ":P") { mock.assertDelegation(delegator, 3) }
    }
    @Test fun assertDelegationKeepsOriginalMockBehavior() {
        val mock = SimpleMockFunction<Int, _>("asdfg")
        mock.calls.addAll(listOf(1, 2, 3))
        for (delegator in listOf<(Int) -> String>(
            { mock(it) },
            { "No call to delegatee" },
            {
                mock(it)
                mock(it)
            },
            { mock(it + 1) },
            {
                mock(it)
                "Called delegatee, but returning some other result :P"
            }
        )) kotlin.runCatching { mock.assertDelegation(delegator, 5) }
        assertEquals("asdfg", mock.returnValue)
        assertEquals(listOf(1, 2, 3, 5, 5, 5, 6, 5), mock.calls)
    }

    private fun thenFailsWith(vararg messageParts: Any?, block: () -> Unit) =
        assertFailsWithTypeAndMessageContaining<AssertionError>(*messageParts, block=block)
}
