package com.guidetradeai.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
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
import com.guidetradeai.viewModel.AuthViewModel
import com.guidetradeai.viewModel.ChatViewModel
import com.guidetradeai.viewModel.GuideTradeAgentViewModel
import com.guidetradeai.viewModel.HomeViewModel
import com.guidetradeai.viewModel.MarketsViewModel
import com.guidetradeai.viewModel.PaperTradingViewModel
import com.guidetradeai.viewModel.ResearchViewModel
import com.guidetradeai.viewModel.SettingsViewModel

@Composable
fun GuideTradeNavGraph(
    navController: NavHostController = rememberNavController(),
    startDestination: String = NavRoutes.HOME,
    authViewModel: AuthViewModel = hiltViewModel(),
) {
    val authState by authViewModel.uiState.collectAsState()
    val isAuthenticated = authState is com.guidetradeai.viewModel.AuthUiState.Authenticated

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
        }
    }
}
