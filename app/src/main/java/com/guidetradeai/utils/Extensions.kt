package com.guidetradeai.utils

import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

fun String?.formatDate(pattern: String = "MMM dd, yyyy"): String {
    if (this.isNullOrBlank()) return ""
    return try {
        val instant = Instant.parse(this)
        val formatter = DateTimeFormatter.ofPattern(pattern)
            .withZone(ZoneId.systemDefault())
        formatter.format(instant)
    } catch (e: Exception) {
        this
    }
}

fun String.toGreeting(): String {
    return this
}

fun String.formatAsMarkdown(text: String): String {
    return this
}

fun Long?.formatDuration(): String {
    if (this == null) return "0:00"
    val totalSeconds = this / 1000
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return String.format("%d:%02d", minutes, seconds)
}

fun String.truncate(maxLength: Int): String {
    return if (this.length > maxLength) {
        this.substring(0, maxLength - 3) + "..."
    } else {
        this
    }
}

fun String.isEmailValid(): Boolean {
    return android.util.Patterns.EMAIL_ADDRESS.matcher(this).matches()
}

fun String.isPasswordValid(): Boolean {
    return this.length >= 8
}
