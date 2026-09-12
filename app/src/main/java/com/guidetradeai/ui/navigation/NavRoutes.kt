package com.guidetradeai.ui.navigation

sealed class NavRoutes(val route: String) {
    object Splash : NavRoutes("splash")
    object Login : NavRoutes("login")
    object Signup : NavRoutes("signup")
    object ForgotPassword : NavRoutes("forgot_password")
    object Verification : NavRoutes("verification/{email}") {
        fun route(email: String) = "verification/$email"
    }
    object Home : NavRoutes("home")
    object Signals : NavRoutes("signals")
    object Analyze : NavRoutes("analyze")
    object Agent : NavRoutes("agent")
    object More : NavRoutes("more")
    object SignalDetails : NavRoutes("signal_details/{signalId}") {
        fun route(signalId: String) = "signal_details/$signalId"
    }
    object Watchlist : NavRoutes("watchlist")
    object SignalHistory : NavRoutes("signal_history")
    object Settings : NavRoutes("settings")
    object PaperTrading : NavRoutes("paper_trading")
    object McpConnections : NavRoutes("mcp_connections")
    object TelegramSettings : NavRoutes("telegram_settings")
    object VoiceSettings : NavRoutes("voice_settings")
    object Profile : NavRoutes("profile")
    object Chat : NavRoutes("chat/{sessionId}") {
        fun route(sessionId: String? = null) = if (sessionId != null) "chat/$sessionId" else "chat"
    }
    object Markets : NavRoutes("markets")
}
