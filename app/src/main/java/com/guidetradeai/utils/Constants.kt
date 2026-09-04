package com.guidetradeai.utils

object Constants {
    const val SUPABASE_URL = "https://dnfutvafibliysnsetwm.supabase.co"
    const val SUPABASE_ANON_KEY = "sb_publishable_Y-kuMPpPKDT9NKKi9fCKcw_YaVdkIpL"
    const val EDGE_FUNCTION_AI_CHAT = "ai-chat"
    const val EDGE_FUNCTION_TTS = "text-to-speech"
    const val EDGE_FUNCTION_TELEGRAM_TEST = "telegram-test"
    const val EDGE_FUNCTION_TELEGRAM_SEND = "telegram-send"
    const val DEFAULT_VOICE_ID = "wBXNqKUATyqu0RtYt25i"
    val QUICK_ACTIONS = listOf(
        "Research Market",
        "Analyze Asset",
        "Explain Indicator",
        "Market Overview",
    )
    val VOICE_STATES = listOf("IDLE", "LISTENING", "PROCESSING", "SPEAKING", "ERROR")
    val THEMES = listOf("dark", "light", "system")
    const val MAX_HISTORY_MESSAGES = 20
    const val MAX_RESEARCH_HISTORY = 20
    const val TELEGRAM_MESSAGE_LIMIT = 4096
    const val DISCLAIMER_TEXT = "Guide Trade AI provides AI-generated market research and educational information. It is not financial advice and does not guarantee market outcomes. Always perform your own research and consult a qualified financial professional where appropriate."
}
