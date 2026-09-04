package com.guidetradeai.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.guidetradeai.viewmodel.AuthViewModel

@Composable
fun NavGraph(
    modifier: Modifier = Modifier,
    startDestination: String = NavRoutes.SPLASH,
    authViewModel: AuthViewModel,
    navController: NavHostController = rememberNavController(),
) {
    NavHost(
        modifier = modifier,
        navController = navController,
        startDestination = startDestination,
    ) {
        composable(NavRoutes.SPLASH) {
            com.guidetradeai.ui.screens.SplashScreen(
                navController = navController,
                authViewModel = authViewModel,
            )
        }
        composable(NavRoutes.ONBOARDING) {
            com.guidetradeai.ui.screens.OnboardingScreen(navController = navController)
        }
        composable(NavRoutes.LOGIN) {
            com.guidetradeai.ui.screens.LoginScreen(
                navController = navController,
                authViewModel = authViewModel,
            )
        }
        composable(NavRoutes.SIGNUP) {
            com.guidetradeai.ui.screens.SignUpScreen(
                navController = navController,
                authViewModel = authViewModel,
            )
        }
        composable(NavRoutes.VERIFICATION) {
            com.guidetradeai.ui.screens.EmailVerificationScreen(
                navController = navController,
                authViewModel = authViewModel,
                email = it.arguments?.getString("email") ?: "",
            )
        }
        composable(NavRoutes.FORGOT_PASSWORD) {
            com.guidetradeai.ui.screens.ForgotPasswordScreen(
                navController = navController,
                authViewModel = authViewModel,
            )
        }
        composable(NavRoutes.HOME) {
            com.guidetradeai.ui.screens.HomeScreen(
                navController = navController,
                authViewModel = authViewModel,
            )
        }
        composable(NavRoutes.CHAT_NEW) {
            com.guidetradeai.ui.screens.ChatScreen(
                navController = navController,
                sessionId = null,
            )
        }
        composable(
            route = NavRoutes.CHAT,
            arguments = listOf(androidx.navigation.navArgument("sessionId") { type = androidx.navigation.NavType.StringType }),
        ) {
            com.guidetradeai.ui.screens.ChatScreen(
                navController = navController,
                sessionId = it.arguments?.getString("sessionId"),
            )
        }
        composable(NavRoutes.CHAT_HISTORY) {
            com.guidetradeai.ui.screens.ChatHistoryScreen(navController = navController)
        }
        composable(NavRoutes.RESEARCH_HISTORY) {
            com.guidetradeai.ui.screens.ResearchHistoryScreen(navController = navController)
        }
        composable(
            route = NavRoutes.RESEARCH_DETAIL,
            arguments = listOf(androidx.navigation.navArgument("researchId") { type = androidx.navigation.NavType.StringType }),
        ) {
            com.guidetradeai.ui.screens.ResearchDetailScreen(
                navController = navController,
                researchId = it.arguments?.getString("researchId") ?: "",
            )
        }
        composable(NavRoutes.PROFILE) {
            com.guidetradeai.ui.screens.ProfileScreen(
                navController = navController,
                authViewModel = authViewModel,
            )
        }
        composable(NavRoutes.SETTINGS) {
            com.guidetradeai.ui.screens.SettingsScreen(
                navController = navController,
                authViewModel = authViewModel,
            )
        }
        composable(NavRoutes.TELEGRAM_SETTINGS) {
            com.guidetradeai.ui.screens.TelegramSettingsScreen(navController = navController)
        }
        composable(NavRoutes.VOICE_SETTINGS) {
            com.guidetradeai.ui.screens.VoiceSettingsScreen(navController = navController)
        }
        composable(NavRoutes.ABOUT) {
            com.guidetradeai.ui.screens.AboutScreen(navController = navController)
        }
    }
}
