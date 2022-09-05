package com.tomuvak.testing

fun messagePrefix(message: String?): String = if (message == null) "" else "$message. "
