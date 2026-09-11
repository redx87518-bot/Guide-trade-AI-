package com.guidetradeai.ui.navigation

object NavRoutes {
    const val SPLASH = "splash"
    const val ONBOARDING = "onboarding"
    const val LOGIN = "login"
    const val SIGNUP = "signup"
    const val FORGOT_PASSWORD = "forgot_password"
    const val VERIFICATION = "verification/{email}"
    const val HOME = "home"
    const val MARKETS = "markets"
    const val ASSET_DETAIL = "asset_detail/{symbol}"
    const val CHAT_HISTORY = "chat_history"
    const val SETTINGS = "settings"
    const val TELEGRAM_SETTINGS = "telegram_settings"
    const val VOICE_SETTINGS = "voice_settings"
    const val ABOUT = "about"
    const val ORB = "orb"
    const val PROFILE = "profile"
    fun chatRoute(sessionId: String) = "chat/$sessionId"
    fun researchDetailRoute(researchId: String) = "research_detail/$researchId"
    fun verificationRoute(email: String) = "verification/$email"
    fun assetDetailRoute(symbol: String) = "asset_detail/$symbol"
}
