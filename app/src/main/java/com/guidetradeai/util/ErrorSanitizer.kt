package com.guidetradeai.util

object ErrorSanitizer {
    private val sensitivePatterns = listOf(
        Regex("""(?i)authorization[\s:=]+bearer[\s]+[A-Za-z0-9_.-]+"""),
        Regex("""(?i)bearer[\s]+[A-Za-z0-9_.-]+"""),
        Regex("""(?i)api[_-]?key[\s:=]+[A-Za-z0-9_.-]+"""),
        Regex("""(?i)token[\s:=]+[A-Za-z0-9_.-]+"""),
        Regex("""(?i)secret[\s:=]+[A-Za-z0-9_.-]+"""),
        Regex("""(?i)password[\s:=]+[A-Za-z0-9_.-]+"""),
        Regex("""eyJ[A-Za-z0-9_-]+\.[A-Za-z0-9_-]+\.[A-Za-z0-9_-]+"""),
    )

    fun sanitize(raw: String?): String {
        if (raw == null) return "Unknown error"
        var sanitized = raw
        sensitivePatterns.forEach { regex ->
            sanitized = sanitized.replace(regex, "[REDACTED]")
        }
        return sanitized
    }

    fun userFriendly(raw: String?): String {
        val sanitized = sanitize(raw ?: return "Unknown error")
        return when {
            sanitized.contains("Failed to load signals") -> "Unable to load signals. Pull to refresh."
            sanitized.contains("Failed to load dashboard") -> "Unable to load paper trading dashboard."
            sanitized.contains("Failed to load MCP") -> "Unable to load connector catalog."
            sanitized.contains("Agent request failed") -> "Unable to reach GuideTrade Agent."
            sanitized.contains("Analysis failed") -> "Analysis failed. Please try again."
            sanitized.contains("Not authenticated") -> "Session expired. Please sign in again."
            else -> sanitized
        }
    }
}
