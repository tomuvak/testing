package com.tomuvak.testing.assertions

fun messagePrefix(message: String?): String = if (message == null) "" else "$message. "
