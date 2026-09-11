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
    object Settings : NavRoutes("settings")
    object TelegramSettings : NavRoutes("telegram_settings")
    object VoiceSettings : NavRoutes("voice_settings")
    object Profile : NavRoutes("profile")
}
