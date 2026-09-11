package com.guidetradeai.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
<<<<<<< ours
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.guidetradeai.ui.screens.*
import com.guidetradeai.viewmodel.AuthViewModel
import com.guidetradeai.viewmodel.ChatViewModel
import com.guidetradeai.viewmodel.GuideTradeAgentViewModel
import com.guidetradeai.viewmodel.HomeViewModel
import com.guidetradeai.viewmodel.MarketsViewModel
import com.guidetradeai.viewmodel.PaperTradingViewModel
import com.guidetradeai.viewmodel.ResearchViewModel
import com.guidetradeai.viewmodel.SettingsViewModel

@Composable
fun GuideTradeNavGraph(
    navController: NavHostController = rememberNavController(),
    startDestination: String = NavRoutes.HOME,
    authViewModel: AuthViewModel = hiltViewModel(),
) {
    val authState by authViewModel.uiState.collectAsState()
    val isAuthenticated = authState is com.guidetradeai.viewmodel.AuthUiState.Authenticated

    if (!isAuthenticated && startDestination != NavRoutes.LOGIN) {
        navController.navigate(NavRoutes.LOGIN) {
            popUpTo(NavRoutes.HOME) { inclusive = true }
        }
        return
    }

    NavHost(navController = navController, startDestination = startDestination) {
        composable(NavRoutes.HOME) {
            HomeScreen(navController = navController)
        }
        composable(NavRoutes.MARKETS) {
            MarketsScreen(navController = navController)
        }
        composable(NavRoutes.AGENT) {
            AgentScreen(navController = navController)
        }
        composable(NavRoutes.CHAT_NEW) {
            AgentScreen(navController = navController)
        }
        composable(NavRoutes.CHAT_HISTORY) {
            ChatHistoryScreen(navController = navController)
        }
        composable(NavRoutes.CHAT) { backStackEntry ->
            val sessionId = backStackEntry.arguments?.getString("sessionId") ?: ""
            AgentScreen(navController = navController)
        }
        composable(NavRoutes.PAPER) {
            PaperTradingScreen(navController = navController)
        }
        composable(NavRoutes.SETTINGS) {
            SettingsScreen(navController = navController)
        }
        composable(NavRoutes.RESEARCH_HISTORY) {
            ResearchHistoryScreen(navController = navController)
        }
        composable(NavRoutes.RESEARCH_DETAIL) { backStackEntry ->
            val researchId = backStackEntry.arguments?.getString("researchId") ?: ""
            ResearchDetailScreen(navController = navController, researchId = researchId)
        }
        composable(NavRoutes.TELEGRAM_SETTINGS) {
            TelegramSettingsScreen(navController = navController)
        }
        composable(NavRoutes.VOICE_SETTINGS) {
            VoiceSettingsScreen(navController = navController)
        }
        composable(NavRoutes.MCP_CONNECTIONS) {
            McpConnectionsScreen(navController = navController)
        }
        composable(NavRoutes.ABOUT) {
            AboutScreen(navController = navController)
        }
        composable(NavRoutes.PROFILE) {
            ProfileScreen(navController = navController)
        }
        composable(NavRoutes.ASSET_DETAIL) { backStackEntry ->
            val symbol = backStackEntry.arguments?.getString("symbol") ?: ""
            AssetDetailScreen(symbol = symbol, navController = navController)
        }
        composable(NavRoutes.SPLASH) {
            SplashScreen(navController = navController, authViewModel = authViewModel)
        }
        composable(NavRoutes.LOGIN) {
            LoginScreen(navController = navController, authViewModel = authViewModel)
=======
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.guidetradeai.ui.screens.SplashScreen
import com.guidetradeai.ui.screens.LoginScreen
import com.guidetradeai.ui.screens.SignUpScreen
import com.guidetradeai.ui.screens.ForgotPasswordScreen
import com.guidetradeai.ui.screens.EmailVerificationScreen
import com.guidetradeai.ui.screens.HomeScreen
import com.guidetradeai.ui.screens.SettingsScreen
import com.guidetradeai.ui.screens.TelegramSettingsScreen
import com.guidetradeai.ui.screens.VoiceSettingsScreen
import com.guidetradeai.ui.screens.ProfileScreen
import com.guidetradeai.ui.screens.PaperTradingScreen
import com.guidetradeai.ui.screens.McpConnectionsScreen
import com.guidetradeai.ui.screens.ChatScreen
import com.guidetradeai.ui.screens.MarketsScreen

@Composable
fun GuideTradeNavGraph(
    navController: NavHostController,
    startDestination: String = NavRoutes.Splash.route,
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
    ) {
        composable(NavRoutes.Splash.route) {
            SplashScreen(navController)
        }
        composable(NavRoutes.Login.route) {
            LoginScreen(navController)
        }
        composable(NavRoutes.Signup.route) {
            SignUpScreen(navController)
        }
        composable(NavRoutes.ForgotPassword.route) {
            ForgotPasswordScreen(navController)
        }
        composable(NavRoutes.Verification.route) { backStackEntry ->
            val email = backStackEntry.arguments?.get("email") ?: ""
            EmailVerificationScreen(navController, email)
        }
        composable(NavRoutes.Home.route) {
            HomeScreen(navController)
        }
        composable(NavRoutes.Chat.route) { backStackEntry ->
            val sessionId = backStackEntry.arguments?.get("sessionId")
            ChatScreen(navController, sessionId)
        }
        composable(NavRoutes.Markets.route) {
            MarketsScreen(navController)
        }
        composable(NavRoutes.Settings.route) {
            SettingsScreen(navController)
        }
        composable(NavRoutes.TelegramSettings.route) {
            TelegramSettingsScreen(navController)
        }
        composable(NavRoutes.VoiceSettings.route) {
            VoiceSettingsScreen(navController)
        }
        composable(NavRoutes.Profile.route) {
            ProfileScreen(navController)
        }
        composable(NavRoutes.PaperTrading.route) {
            PaperTradingScreen(navController)
        }
        composable(NavRoutes.McpConnections.route) {
            McpConnectionsScreen(navController)
>>>>>>> theirs
        }
    }
}