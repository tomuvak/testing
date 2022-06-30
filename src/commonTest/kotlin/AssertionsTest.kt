package com.tomuvak.testing.assertions

import kotlin.test.*

class AssertionsTest {
    companion object {
        private val bytes: List<Byte> =
            listOf(Byte.MIN_VALUE, -100, -10, -5, -3, -2, -1, 0, 1, 2, 3, 5, 10, 100, Byte.MAX_VALUE)
        private val shorts: List<Short> =
            listOf(Short.MIN_VALUE, -10000, -100, -10, -5, -3, -2, -1, 0, 1, 2, 3, 5, 10, 100, 10000, Short.MAX_VALUE)
        private val ints: List<Int> =
            listOf(Int.MIN_VALUE, -1000000, -1000, -10, -5, -3, -2, -1, 0, 1, 2, 3, 5, 10, 1000, 1000000, Int.MAX_VALUE)
        private val longs: List<Long> = listOf(
            Long.MIN_VALUE, -1000000000, -1000000, -1000, -10, -5, -3, -2, -1,
            0, 1, 2, 3, 5, 10, 1000, 1000000, 1000000000, Long.MAX_VALUE
        )
        private val uBytes: List<UByte> = listOf(0u, 1u, 2u, 3u, 5u, 10u, 100u, UByte.MAX_VALUE)
        private val uShorts: List<UShort> = listOf(0u, 1u, 2u, 3u, 5u, 10u, 100u, 10000u, UShort.MAX_VALUE)
        private val uInts: List<UInt> = listOf(0u, 1u, 2u, 3u, 5u, 10u, 1000u, 1000000u, UInt.MAX_VALUE)
        private val uLongs: List<ULong> = listOf(0u, 1u, 2u, 3u, 5u, 10u, 1000u, 1000000u, 1000000000u, ULong.MAX_VALUE)
        private val floats: List<Float> = listOf(
            Float.NEGATIVE_INFINITY, -Float.MAX_VALUE -1e20f, -1e10f, -1234.567f, -123.45f, -5f, -5e-10f,
            -Float.MIN_VALUE, 0f, Float.MIN_VALUE,
            5e-10f, 5f, 123.45f, 1234.567f, 1e10f, 1e20f, Float.MAX_VALUE, Float.POSITIVE_INFINITY
        )
        private val doubles: List<Double> = listOf(
            Double.NEGATIVE_INFINITY, -Double.MAX_VALUE -1e20, -1e10, -1234.567, -123.45, -5.0, -5e-10,
            -Double.MIN_VALUE, 0.0, Double.MIN_VALUE,
            5e-10, 5.0, 123.45, 1234.567, 1e10, 1e20, Double.MAX_VALUE, Double.POSITIVE_INFINITY
        )

        private fun <T> List<T>.pairs(includeSame: Boolean): Sequence<Pair<T, T>> = sequence {
            for (i in indices)
                for (j in (if (includeSame) i else (i + 1)) until size)
                    yield(Pair(get(i), get(j)))
        }
    }

    @Test fun nonSamePairs() {
        assertEquals(emptyList(), emptyList<Int>().pairs(false).toList())
        assertEquals(emptyList(), listOf(1).pairs(false).toList())
        assertEquals(listOf(Pair(1, 2)), listOf(1, 2).pairs(false).toList())
        assertEquals(listOf(Pair(1, 2), Pair(1, 3), Pair(2, 3)), listOf(1, 2, 3).pairs(false).toList())
    }

    @Test fun potentiallySamePairs() {
        assertEquals(emptyList(), emptyList<Int>().pairs(true).toList())
        assertEquals(listOf(Pair(1, 1)), listOf(1).pairs(true).toList())
        assertEquals(listOf(Pair(1, 1), Pair(1, 2), Pair(2, 2)), listOf(1, 2).pairs(true).toList())
        assertEquals(
            listOf(Pair(1, 1), Pair(1, 2), Pair(1, 3), Pair(2, 2), Pair(2, 3), Pair(3, 3)),
            listOf(1, 2, 3).pairs(true).toList()
        )
    }

    @Test fun assertLessThanSucceeds() {
        for ((i, j) in bytes.pairs(false)) assertLessThan(j, i)
        for ((i, j) in shorts.pairs(false)) assertLessThan(j, i)
        for ((i, j) in ints.pairs(false)) assertLessThan(j, i)
        for ((i, j) in longs.pairs(false)) assertLessThan(j, i)
        for ((i, j) in uBytes.pairs(false)) assertLessThan(j, i)
        for ((i, j) in uShorts.pairs(false)) assertLessThan(j, i)
        for ((i, j) in uInts.pairs(false)) assertLessThan(j, i)
        for ((i, j) in uLongs.pairs(false)) assertLessThan(j, i)
        for ((i, j) in floats.pairs(false)) assertLessThan(j, i)
        for ((i, j) in doubles.pairs(false)) assertLessThan(j, i)
    }

    @Test fun assertLessThanSucceedsWithCustomMessage() {
        for ((i, j) in bytes.pairs(false)) assertLessThan(j, i, "Custom message")
        for ((i, j) in shorts.pairs(false)) assertLessThan(j, i, "Custom message")
        for ((i, j) in ints.pairs(false)) assertLessThan(j, i, "Custom message")
        for ((i, j) in longs.pairs(false)) assertLessThan(j, i, "Custom message")
        for ((i, j) in uBytes.pairs(false)) assertLessThan(j, i, "Custom message")
        for ((i, j) in uShorts.pairs(false)) assertLessThan(j, i, "Custom message")
        for ((i, j) in uInts.pairs(false)) assertLessThan(j, i, "Custom message")
        for ((i, j) in uLongs.pairs(false)) assertLessThan(j, i, "Custom message")
        for ((i, j) in floats.pairs(false)) assertLessThan(j, i, "Custom message")
        for ((i, j) in doubles.pairs(false)) assertLessThan(j, i, "Custom message")
    }

    @Test fun assertLessThanFails() {
        for ((i, j) in bytes.pairs(true)) thenFailsWith("less than $i", j) { assertLessThan(i, j) }
        for ((i, j) in shorts.pairs(true)) thenFailsWith("less than $i", j) { assertLessThan(i, j) }
        for ((i, j) in ints.pairs(true)) thenFailsWith("less than $i", j) { assertLessThan(i, j) }
        for ((i, j) in longs.pairs(true)) thenFailsWith("less than $i", j) { assertLessThan(i, j) }
        for ((i, j) in uBytes.pairs(true)) thenFailsWith("less than $i", j) { assertLessThan(i, j) }
        for ((i, j) in uShorts.pairs(true)) thenFailsWith("less than $i", j) { assertLessThan(i, j) }
        for ((i, j) in uInts.pairs(true)) thenFailsWith("less than $i", j) { assertLessThan(i, j) }
        for ((i, j) in uLongs.pairs(true)) thenFailsWith("less than $i", j) { assertLessThan(i, j) }
        for ((i, j) in floats.pairs(true)) thenFailsWith("less than $i", j) { assertLessThan(i, j) }
        for ((i, j) in doubles.pairs(true)) thenFailsWith("less than $i", j) { assertLessThan(i, j) }
    }

    @Test fun assertLessThanFailsWithCustomMessage() {
        for ((i, j) in bytes.pairs(true)) thenFailsWith("msg", "less than $i", j) { assertLessThan(i, j, "msg") }
        for ((i, j) in shorts.pairs(true)) thenFailsWith("msg", "less than $i", j) { assertLessThan(i, j, "msg") }
        for ((i, j) in ints.pairs(true)) thenFailsWith("msg", "less than $i", j) { assertLessThan(i, j, "msg") }
        for ((i, j) in longs.pairs(true)) thenFailsWith("msg", "less than $i", j) { assertLessThan(i, j, "msg") }
        for ((i, j) in uBytes.pairs(true)) thenFailsWith("msg", "less than $i", j) { assertLessThan(i, j, "msg") }
        for ((i, j) in uShorts.pairs(true)) thenFailsWith("msg", "less than $i", j) { assertLessThan(i, j, "msg") }
        for ((i, j) in uInts.pairs(true)) thenFailsWith("msg", "less than $i", j) { assertLessThan(i, j, "msg") }
        for ((i, j) in uLongs.pairs(true)) thenFailsWith("msg", "less than $i", j) { assertLessThan(i, j, "msg") }
        for ((i, j) in floats.pairs(true)) thenFailsWith("msg", "less than $i", j) { assertLessThan(i, j, "msg") }
        for ((i, j) in doubles.pairs(true)) thenFailsWith("msg", "less than $i", j) { assertLessThan(i, j, "msg") }
    }

    @Test fun assertLessThanOrEqualToSucceeds() {
        for ((i, j) in bytes.pairs(true)) assertLessThanOrEqualTo(j, i)
        for ((i, j) in shorts.pairs(true)) assertLessThanOrEqualTo(j, i)
        for ((i, j) in ints.pairs(true)) assertLessThanOrEqualTo(j, i)
        for ((i, j) in longs.pairs(true)) assertLessThanOrEqualTo(j, i)
        for ((i, j) in uBytes.pairs(true)) assertLessThanOrEqualTo(j, i)
        for ((i, j) in uShorts.pairs(true)) assertLessThanOrEqualTo(j, i)
        for ((i, j) in uInts.pairs(true)) assertLessThanOrEqualTo(j, i)
        for ((i, j) in uLongs.pairs(true)) assertLessThanOrEqualTo(j, i)
        for ((i, j) in floats.pairs(true)) assertLessThanOrEqualTo(j, i)
        for ((i, j) in doubles.pairs(true)) assertLessThanOrEqualTo(j, i)
    }

    @Test fun assertLessThanOrEqualToSucceedsWithCustomMessage() {
        for ((i, j) in bytes.pairs(true)) assertLessThanOrEqualTo(j, i, "Custom message")
        for ((i, j) in shorts.pairs(true)) assertLessThanOrEqualTo(j, i, "Custom message")
        for ((i, j) in ints.pairs(true)) assertLessThanOrEqualTo(j, i, "Custom message")
        for ((i, j) in longs.pairs(true)) assertLessThanOrEqualTo(j, i, "Custom message")
        for ((i, j) in uBytes.pairs(true)) assertLessThanOrEqualTo(j, i, "Custom message")
        for ((i, j) in uShorts.pairs(true)) assertLessThanOrEqualTo(j, i, "Custom message")
        for ((i, j) in uInts.pairs(true)) assertLessThanOrEqualTo(j, i, "Custom message")
        for ((i, j) in uLongs.pairs(true)) assertLessThanOrEqualTo(j, i, "Custom message")
        for ((i, j) in floats.pairs(true)) assertLessThanOrEqualTo(j, i, "Custom message")
        for ((i, j) in doubles.pairs(true)) assertLessThanOrEqualTo(j, i, "Custom message")
    }

    @Test fun assertLessThanOrEqualToFails() {
        for ((i, j) in bytes.pairs(false))
            thenFailsWith("less than or equal to $i", j) { assertLessThanOrEqualTo(i, j) }
        for ((i, j) in shorts.pairs(false))
            thenFailsWith("less than or equal to $i", j) { assertLessThanOrEqualTo(i, j) }
        for ((i, j) in ints.pairs(false))
            thenFailsWith("less than or equal to $i", j) { assertLessThanOrEqualTo(i, j) }
        for ((i, j) in longs.pairs(false))
            thenFailsWith("less than or equal to $i", j) { assertLessThanOrEqualTo(i, j) }
        for ((i, j) in uBytes.pairs(false))
            thenFailsWith("less than or equal to $i", j) { assertLessThanOrEqualTo(i, j) }
        for ((i, j) in uShorts.pairs(false))
            thenFailsWith("less than or equal to $i", j) { assertLessThanOrEqualTo(i, j) }
        for ((i, j) in uInts.pairs(false))
            thenFailsWith("less than or equal to $i", j) { assertLessThanOrEqualTo(i, j) }
        for ((i, j) in uLongs.pairs(false))
            thenFailsWith("less than or equal to $i", j) { assertLessThanOrEqualTo(i, j) }
        for ((i, j) in floats.pairs(false))
            thenFailsWith("less than or equal to $i", j) { assertLessThanOrEqualTo(i, j) }
        for ((i, j) in doubles.pairs(false))
            thenFailsWith("less than or equal to $i", j) { assertLessThanOrEqualTo(i, j) }
    }

    @Test fun assertLessThanOrEqualToFailsWithCustomMessage() {
        for ((i, j) in bytes.pairs(false))
            thenFailsWith("msg", "less than or equal to $i", j) { assertLessThanOrEqualTo(i, j, "msg") }
        for ((i, j) in shorts.pairs(false))
            thenFailsWith("msg", "less than or equal to $i", j) { assertLessThanOrEqualTo(i, j, "msg") }
        for ((i, j) in ints.pairs(false))
            thenFailsWith("msg", "less than or equal to $i", j) { assertLessThanOrEqualTo(i, j, "msg") }
        for ((i, j) in longs.pairs(false))
            thenFailsWith("msg", "less than or equal to $i", j) { assertLessThanOrEqualTo(i, j, "msg") }
        for ((i, j) in uBytes.pairs(false))
            thenFailsWith("msg", "less than or equal to $i", j) { assertLessThanOrEqualTo(i, j, "msg") }
        for ((i, j) in uShorts.pairs(false))
            thenFailsWith("msg", "less than or equal to $i", j) { assertLessThanOrEqualTo(i, j, "msg") }
        for ((i, j) in uInts.pairs(false))
            thenFailsWith("msg", "less than or equal to $i", j) { assertLessThanOrEqualTo(i, j, "msg") }
        for ((i, j) in uLongs.pairs(false))
            thenFailsWith("msg", "less than or equal to $i", j) { assertLessThanOrEqualTo(i, j, "msg") }
        for ((i, j) in floats.pairs(false))
            thenFailsWith("msg", "less than or equal to $i", j) { assertLessThanOrEqualTo(i, j, "msg") }
        for ((i, j) in doubles.pairs(false))
            thenFailsWith("msg", "less than or equal to $i", j) { assertLessThanOrEqualTo(i, j, "msg") }
    }

    @Test fun assertGreaterThanSucceeds() {
        for ((i, j) in bytes.pairs(false)) assertGreaterThan(i, j)
        for ((i, j) in shorts.pairs(false)) assertGreaterThan(i, j)
        for ((i, j) in ints.pairs(false)) assertGreaterThan(i, j)
        for ((i, j) in longs.pairs(false)) assertGreaterThan(i, j)
        for ((i, j) in uBytes.pairs(false)) assertGreaterThan(i, j)
        for ((i, j) in uShorts.pairs(false)) assertGreaterThan(i, j)
        for ((i, j) in uInts.pairs(false)) assertGreaterThan(i, j)
        for ((i, j) in uLongs.pairs(false)) assertGreaterThan(i, j)
        for ((i, j) in floats.pairs(false)) assertGreaterThan(i, j)
        for ((i, j) in doubles.pairs(false)) assertGreaterThan(i, j)
    }

    @Test fun assertGreaterThanSucceedsWithCustomMessage() {
        for ((i, j) in bytes.pairs(false)) assertGreaterThan(i, j, "Custom message")
        for ((i, j) in shorts.pairs(false)) assertGreaterThan(i, j, "Custom message")
        for ((i, j) in ints.pairs(false)) assertGreaterThan(i, j, "Custom message")
        for ((i, j) in longs.pairs(false)) assertGreaterThan(i, j, "Custom message")
        for ((i, j) in uBytes.pairs(false)) assertGreaterThan(i, j, "Custom message")
        for ((i, j) in uShorts.pairs(false)) assertGreaterThan(i, j, "Custom message")
        for ((i, j) in uInts.pairs(false)) assertGreaterThan(i, j, "Custom message")
        for ((i, j) in uLongs.pairs(false)) assertGreaterThan(i, j, "Custom message")
        for ((i, j) in floats.pairs(false)) assertGreaterThan(i, j, "Custom message")
        for ((i, j) in doubles.pairs(false)) assertGreaterThan(i, j, "Custom message")
    }

    @Test fun assertGreaterThanFails() {
        for ((i, j) in bytes.pairs(true)) thenFailsWith("greater than $j", i) { assertGreaterThan(j, i) }
        for ((i, j) in shorts.pairs(true)) thenFailsWith("greater than $j", i) { assertGreaterThan(j, i) }
        for ((i, j) in ints.pairs(true)) thenFailsWith("greater than $j", i) { assertGreaterThan(j, i) }
        for ((i, j) in longs.pairs(true)) thenFailsWith("greater than $j", i) { assertGreaterThan(j, i) }
        for ((i, j) in uBytes.pairs(true)) thenFailsWith("greater than $j", i) { assertGreaterThan(j, i) }
        for ((i, j) in uShorts.pairs(true)) thenFailsWith("greater than $j", i) { assertGreaterThan(j, i) }
        for ((i, j) in uInts.pairs(true)) thenFailsWith("greater than $j", i) { assertGreaterThan(j, i) }
        for ((i, j) in uLongs.pairs(true)) thenFailsWith("greater than $j", i) { assertGreaterThan(j, i) }
        for ((i, j) in floats.pairs(true)) thenFailsWith("greater than $j", i) { assertGreaterThan(j, i) }
        for ((i, j) in doubles.pairs(true)) thenFailsWith("greater than $j", i) { assertGreaterThan(j, i) }
    }

    @Test fun assertGreaterThanFailsWithCustomMessage() {
        for ((i, j) in bytes.pairs(true))
            thenFailsWith("msg", "greater than $j", i) { assertGreaterThan(j, i, "msg") }
        for ((i, j) in shorts.pairs(true))
            thenFailsWith("msg", "greater than $j", i) { assertGreaterThan(j, i, "msg") }
        for ((i, j) in ints.pairs(true))
            thenFailsWith("msg", "greater than $j", i) { assertGreaterThan(j, i, "msg") }
        for ((i, j) in longs.pairs(true))
            thenFailsWith("msg", "greater than $j", i) { assertGreaterThan(j, i, "msg") }
        for ((i, j) in uBytes.pairs(true))
            thenFailsWith("msg", "greater than $j", i) { assertGreaterThan(j, i, "msg") }
        for ((i, j) in uShorts.pairs(true))
            thenFailsWith("msg", "greater than $j", i) { assertGreaterThan(j, i, "msg") }
        for ((i, j) in uInts.pairs(true))
            thenFailsWith("msg", "greater than $j", i) { assertGreaterThan(j, i, "msg") }
        for ((i, j) in uLongs.pairs(true))
            thenFailsWith("msg", "greater than $j", i) { assertGreaterThan(j, i, "msg") }
        for ((i, j) in floats.pairs(true))
            thenFailsWith("msg", "greater than $j", i) { assertGreaterThan(j, i, "msg") }
        for ((i, j) in doubles.pairs(true))
            thenFailsWith("msg", "greater than $j", i) { assertGreaterThan(j, i, "msg") }
    }

    @Test fun assertGreaterThanOrEqualToSucceeds() {
        for ((i, j) in bytes.pairs(true)) assertGreaterThanOrEqualTo(i, j)
        for ((i, j) in shorts.pairs(true)) assertGreaterThanOrEqualTo(i, j)
        for ((i, j) in ints.pairs(true)) assertGreaterThanOrEqualTo(i, j)
        for ((i, j) in longs.pairs(true)) assertGreaterThanOrEqualTo(i, j)
        for ((i, j) in uBytes.pairs(true)) assertGreaterThanOrEqualTo(i, j)
        for ((i, j) in uShorts.pairs(true)) assertGreaterThanOrEqualTo(i, j)
        for ((i, j) in uInts.pairs(true)) assertGreaterThanOrEqualTo(i, j)
        for ((i, j) in uLongs.pairs(true)) assertGreaterThanOrEqualTo(i, j)
        for ((i, j) in floats.pairs(true)) assertGreaterThanOrEqualTo(i, j)
        for ((i, j) in doubles.pairs(true)) assertGreaterThanOrEqualTo(i, j)
    }

    @Test fun assertGreaterThanOrEqualToSucceedsWithCustomMessage() {
        for ((i, j) in bytes.pairs(true)) assertGreaterThanOrEqualTo(i, j, "Custom message")
        for ((i, j) in shorts.pairs(true)) assertGreaterThanOrEqualTo(i, j, "Custom message")
        for ((i, j) in ints.pairs(true)) assertGreaterThanOrEqualTo(i, j, "Custom message")
        for ((i, j) in longs.pairs(true)) assertGreaterThanOrEqualTo(i, j, "Custom message")
        for ((i, j) in uBytes.pairs(true)) assertGreaterThanOrEqualTo(i, j, "Custom message")
        for ((i, j) in uShorts.pairs(true)) assertGreaterThanOrEqualTo(i, j, "Custom message")
        for ((i, j) in uInts.pairs(true)) assertGreaterThanOrEqualTo(i, j, "Custom message")
        for ((i, j) in uLongs.pairs(true)) assertGreaterThanOrEqualTo(i, j, "Custom message")
        for ((i, j) in floats.pairs(true)) assertGreaterThanOrEqualTo(i, j, "Custom message")
        for ((i, j) in doubles.pairs(true)) assertGreaterThanOrEqualTo(i, j, "Custom message")
    }

    @Test fun assertGreaterThanOrEqualToFails() {
        for ((i, j) in bytes.pairs(false))
            thenFailsWith("greater than or equal to $j", i) { assertGreaterThanOrEqualTo(j, i) }
        for ((i, j) in shorts.pairs(false))
            thenFailsWith("greater than or equal to $j", i) { assertGreaterThanOrEqualTo(j, i) }
        for ((i, j) in ints.pairs(false))
            thenFailsWith("greater than or equal to $j", i) { assertGreaterThanOrEqualTo(j, i) }
        for ((i, j) in longs.pairs(false))
            thenFailsWith("greater than or equal to $j", i) { assertGreaterThanOrEqualTo(j, i) }
        for ((i, j) in uBytes.pairs(false))
            thenFailsWith("greater than or equal to $j", i) { assertGreaterThanOrEqualTo(j, i) }
        for ((i, j) in uShorts.pairs(false))
            thenFailsWith("greater than or equal to $j", i) { assertGreaterThanOrEqualTo(j, i) }
        for ((i, j) in uInts.pairs(false))
            thenFailsWith("greater than or equal to $j", i) { assertGreaterThanOrEqualTo(j, i) }
        for ((i, j) in uLongs.pairs(false))
            thenFailsWith("greater than or equal to $j", i) { assertGreaterThanOrEqualTo(j, i) }
        for ((i, j) in floats.pairs(false))
            thenFailsWith("greater than or equal to $j", i) { assertGreaterThanOrEqualTo(j, i) }
        for ((i, j) in doubles.pairs(false))
            thenFailsWith("greater than or equal to $j", i) { assertGreaterThanOrEqualTo(j, i) }
    }

    @Test fun assertGreaterThanOrEqualToFailsWithCustomMessage() {
        for ((i, j) in bytes.pairs(false))
            thenFailsWith("msg", "greater than or equal to $j", i) { assertGreaterThanOrEqualTo(j, i, "msg") }
        for ((i, j) in shorts.pairs(false))
            thenFailsWith("msg", "greater than or equal to $j", i) { assertGreaterThanOrEqualTo(j, i, "msg") }
        for ((i, j) in ints.pairs(false))
            thenFailsWith("msg", "greater than or equal to $j", i) { assertGreaterThanOrEqualTo(j, i, "msg") }
        for ((i, j) in longs.pairs(false))
            thenFailsWith("msg", "greater than or equal to $j", i) { assertGreaterThanOrEqualTo(j, i, "msg") }
        for ((i, j) in uBytes.pairs(false))
            thenFailsWith("msg", "greater than or equal to $j", i) { assertGreaterThanOrEqualTo(j, i, "msg") }
        for ((i, j) in uShorts.pairs(false))
            thenFailsWith("msg", "greater than or equal to $j", i) { assertGreaterThanOrEqualTo(j, i, "msg") }
        for ((i, j) in uInts.pairs(false))
            thenFailsWith("msg", "greater than or equal to $j", i) { assertGreaterThanOrEqualTo(j, i, "msg") }
        for ((i, j) in uLongs.pairs(false))
            thenFailsWith("msg", "greater than or equal to $j", i) { assertGreaterThanOrEqualTo(j, i, "msg") }
        for ((i, j) in floats.pairs(false))
            thenFailsWith("msg", "greater than or equal to $j", i) { assertGreaterThanOrEqualTo(j, i, "msg") }
        for ((i, j) in doubles.pairs(false))
            thenFailsWith("msg", "greater than or equal to $j", i) { assertGreaterThanOrEqualTo(j, i, "msg") }
    }

    private fun thenFailsWith(vararg texts: Any, block: () -> Unit) {
        val failure = assertFails(block)
        assertIs<AssertionError>(failure)
        for (text in texts) assertTrue(failure.message!!.indexOf(text.toString()) >= 0)
    }
}
