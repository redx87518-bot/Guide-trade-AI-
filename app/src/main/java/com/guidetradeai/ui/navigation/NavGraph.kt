package com.guidetradeai.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.guidetradeai.ui.screens.*

@Composable
fun GuideTradeNavGraph(
    startDestination: String = NavRoutes.Splash.route,
) {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = startDestination) {
        composable(NavRoutes.Splash.route) {
            SplashScreen(navController = navController)
        }
        composable(NavRoutes.Login.route) {
            LoginScreen(navController = navController)
        }
        composable(NavRoutes.Signup.route) {
            SignUpScreen(navController = navController)
        }
        composable(NavRoutes.ForgotPassword.route) {
            ForgotPasswordScreen(navController = navController)
        }
        composable(NavRoutes.Verification.route) { backStackEntry ->
            val email = backStackEntry.arguments?.getString("email") ?: ""
            EmailVerificationScreen(navController = navController, email = email)
        }
        composable(NavRoutes.Home.route) {
            HomeScreen(navController = navController)
        }
        composable(NavRoutes.Settings.route) {
            SettingsScreen(navController = navController)
        }
        composable(NavRoutes.TelegramSettings.route) {
            TelegramSettingsScreen(navController = navController)
        }
        composable(NavRoutes.VoiceSettings.route) {
            VoiceSettingsScreen(navController = navController)
        }
        composable(NavRoutes.Profile.route) {
            ProfileScreen(navController = navController)
        }
    }
}
