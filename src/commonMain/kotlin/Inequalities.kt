package com.tomuvak.testing

import kotlin.test.fail

fun <T : Comparable<T>> assertLessThan(bound: T, actual: T, message: String? = null) {
    if (actual >= bound)
        // Currently (1.7.0), .toString() seems to be necessary to produce the right result on Kotlin JS
        fail("${messagePrefix(message)}Expected <less than ${bound.toString()}>, actual <${actual.toString()}>.")
}

fun <T : Comparable<T>> assertLessThanOrEqualTo(bound: T, actual: T, message: String? = null) {
    if (actual > bound)
        fail(
            // Currently (1.7.0), .toString() seems to be necessary to produce the right result on Kotlin JS
            "${messagePrefix(message)}Expected <less than or equal to ${bound.toString()}>, " +
                    "actual <${actual.toString()}>."
        )
}

fun <T : Comparable<T>> assertGreaterThan(bound: T, actual: T, message: String? = null) {
    if (actual <= bound)
        // Currently (1.7.0), .toString() seems to be necessary to produce the right result on Kotlin JS
        fail("${messagePrefix(message)}Expected <greater than ${bound.toString()}>, actual <${actual.toString()}>.")
}

fun <T : Comparable<T>> assertGreaterThanOrEqualTo(bound: T, actual: T, message: String? = null) {
    if (actual < bound)
        fail(
            // Currently (1.7.0), .toString() seems to be necessary to produce the right result on Kotlin JS
            "${messagePrefix(message)}Expected <greater than or equal to ${bound.toString()}>, " +
                    "actual <${actual.toString()}>."
        )
}
