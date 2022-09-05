package com.tomuvak.testing

import kotlin.test.assertFails
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

fun assertFailsWithMessageContaining(vararg messageParts: Any?, block: () -> Unit) =
    assertFails(block).assertMessageContains(*messageParts)

inline fun <reified T : Throwable> assertFailsWithTypeAndMessageContaining(
    vararg messageParts: Any?,
    block: () -> Unit
) = assertFailsWith<T>(block=block).assertMessageContains(*messageParts)

fun Throwable.assertMessageContains(vararg messageParts: Any?) {
    val message = this.message
    assertNotNull(message, "Expected failure with a message, got $this")
    for (part in messageParts) assertTrue(
        message.indexOf("$part") >= 0,
        "Expected failure with message containing \"$part\", got $this"
    )
}
