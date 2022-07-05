package com.tomuvak.testing.assertions

import kotlin.test.Test
import kotlin.test.assertFails

class FailuresTest {
    private class Exception1(message: String?) : Exception(message)
    private class Exception2(message: String?) : Exception(message)

    @Test fun assertFailsWithMessageContainingFailsWhenDoesntFail() {
        assertFails { assertFailsWithMessageContaining {} }
        assertFails { assertFailsWithMessageContaining("") {} }
        assertFails { assertFailsWithMessageContaining(1, 2.3, null) {} }
    }

    @Test fun assertFailsWithMessageContainingFailsWhenNoMessage() {
        assertFails { assertFailsWithMessageContaining { throw Exception1(null) } }
        assertFails { assertFailsWithMessageContaining("") { throw Exception1(null) } }
        assertFails { assertFailsWithMessageContaining(1, 2.3, null) { throw Exception1(null) } }
    }

    @Test fun assertFailsWithMessageContainingFailsWhenMessageDoesntContainAllParts() {
        assertFails { assertFailsWithMessageContaining("xyz") { error("abc") } }
        assertFails { assertFailsWithMessageContaining(1, 2.3, null) { error("1 2.3") } }
        assertFails { assertFailsWithMessageContaining(1, 2.3, null) { error("1 null") } }
        assertFails { assertFailsWithMessageContaining(1, 2.3, null) { error("2.3 null") } }
    }

    @Test fun assertFailsWithMessageContainingSucceeds() {
        assertFailsWithMessageContaining { error("abc") }
        assertFailsWithMessageContaining("abc") { error("abc") }
        assertFailsWithMessageContaining(1, 2.3, null) { error("1 and 2.3 and null") }
    }

    @Test fun assertFailsWithTypeAndMessageContainingFailsWhenDoesntFail() {
        assertFails { assertFailsWithTypeAndMessageContaining<Exception1> {} }
        assertFails { assertFailsWithTypeAndMessageContaining<Exception1>("") {} }
        assertFails { assertFailsWithTypeAndMessageContaining<Exception1>(1, 2.3, null) {} }
    }

    @Test fun assertFailsWithTypeAndMessageContainingFailsWhenNoMessage() {
        assertFails { assertFailsWithTypeAndMessageContaining<Exception1> { throw Exception1(null) } }
        assertFails { assertFailsWithTypeAndMessageContaining<Exception1>("") { throw Exception1(null) } }
        assertFails { assertFailsWithTypeAndMessageContaining<Exception1>(1, 2.3, null) { throw Exception1(null) } }
    }

    @Test fun assertFailsWithTypeAndMessageContainingFailsWhenMessageDoesntContainAllParts() {
        assertFails { assertFailsWithTypeAndMessageContaining<Exception1>("xyz") { throw Exception1("abc") } }
        assertFails { assertFailsWithTypeAndMessageContaining<Exception1>(1, 2.3, null) { throw Exception1("1 2.3") } }
        assertFails { assertFailsWithTypeAndMessageContaining<Exception1>(1, 2.3, null) { throw Exception1("1 null") } }
        assertFails {
            assertFailsWithTypeAndMessageContaining<Exception1>(1, 2.3, null) { throw Exception1("2.3 null") }
        }
    }

    @Test fun assertFailsWithTypeAndMessageContainingFailsWhenWrongExceptionType() {
        assertFails { assertFailsWithTypeAndMessageContaining<Exception1> { throw Exception2("abc") } }
        assertFails { assertFailsWithTypeAndMessageContaining<Exception1>("abc") { throw Exception2("abc") } }
        assertFails {
            assertFailsWithTypeAndMessageContaining<Exception1>(1, 2.3, null) { throw Exception2("1 and 2.3 and null") }
        }
    }

    @Test fun assertFailsWithTypeAndMessageContainingSucceeds() {
        assertFailsWithTypeAndMessageContaining<Exception1> { throw Exception1("abc") }
        assertFailsWithTypeAndMessageContaining<Exception1>("abc") { throw Exception1("abc") }
        assertFailsWithTypeAndMessageContaining<Exception1>(1, 2.3, null) { throw Exception1("1 and 2.3 and null") }
    }

    @Test fun assertMessageContainsFailsWhenNoMessage() {
        assertFails { Exception1(null).assertMessageContains() }
        assertFails { Exception1(null).assertMessageContains("abc") }
        assertFails { Exception1(null).assertMessageContains(1, 2.3, null) }
    }

    @Test fun assertMessageContainsFailsWhenMessageDoesntContainAllParts() {
        assertFails { Exception("abc").assertMessageContains("xyz") }
        assertFails { Exception("1 2.3").assertMessageContains(1, 2.3, null) }
        assertFails { Exception("1 null").assertMessageContains(1, 2.3, null) }
        assertFails { Exception("2.3 null").assertMessageContains(1, 2.3, null) }
    }

    @Test fun assertMessageContainsSucceeds() {
        Exception("abc").assertMessageContains()
        Exception("abc").assertMessageContains("abc")
        Exception("1 and 2.3 and null").assertMessageContains(1, 2.3, null)
    }
}
