package com.guidetradeai.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.guidetradeai.ui.screens.SplashScreen
import com.guidetradeai.ui.screens.LoginScreen
import com.guidetradeai.ui.screens.SignUpScreen
import com.guidetradeai.ui.screens.ForgotPasswordScreen
import com.guidetradeai.ui.screens.EmailVerificationScreen
import com.guidetradeai.ui.screens.HomeScreen
import com.guidetradeai.ui.screens.SignalsScreen
import com.guidetradeai.ui.screens.AnalyzeScreen
import com.guidetradeai.ui.screens.AgentScreen
import com.guidetradeai.ui.screens.SettingsScreen
import com.guidetradeai.ui.screens.SignalDetailsScreen
import com.guidetradeai.ui.screens.WatchlistScreen
import com.guidetradeai.ui.screens.SignalHistoryScreen
import com.guidetradeai.ui.screens.TelegramSettingsScreen
import com.guidetradeai.ui.screens.VoiceSettingsScreen
import com.guidetradeai.ui.screens.ProfileScreen
import com.guidetradeai.ui.screens.PaperTradingScreen
import com.guidetradeai.ui.screens.McpConnectionsScreen
import com.guidetradeai.ui.screens.ChatScreen
import com.guidetradeai.ui.screens.MarketsScreen
import com.guidetradeai.viewmodel.AuthViewModel
import com.guidetradeai.viewmodel.SignalsViewModel
import com.guidetradeai.viewmodel.AnalyzeViewModel
import com.guidetradeai.viewmodel.AgentViewModel
import com.guidetradeai.viewmodel.WatchlistViewModel

@Composable
fun GuideTradeNavGraph(
    navController: NavHostController,
    authViewModel: AuthViewModel,
    startDestination: String = NavRoutes.Splash.route,
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
    ) {
        composable(NavRoutes.Splash.route) {
            SplashScreen(navController = navController, authViewModel = authViewModel)
        }
        composable(NavRoutes.Login.route) {
            LoginScreen(navController, authViewModel)
        }
        composable(NavRoutes.Signup.route) {
            SignUpScreen(navController, authViewModel)
        }
        composable(NavRoutes.ForgotPassword.route) {
            ForgotPasswordScreen(navController, authViewModel)
        }
        composable(NavRoutes.Verification.route) { backStackEntry ->
            val email = backStackEntry.arguments?.getString("email") ?: ""
            EmailVerificationScreen(navController, email)
        }
        composable(NavRoutes.Home.route) {
            val viewModel: SignalsViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
            HomeScreen(navController, viewModel)
        }
        composable(NavRoutes.Signals.route) {
            val viewModel: SignalsViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
            SignalsScreen(navController, viewModel)
        }
        composable(NavRoutes.Analyze.route) {
            val viewModel: AnalyzeViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
            AnalyzeScreen(navController, viewModel)
        }
        composable(NavRoutes.Agent.route) {
            val viewModel: AgentViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
            AgentScreen(navController, viewModel)
        }
        composable(NavRoutes.Settings.route) {
            SettingsScreen(navController, authViewModel)
        }
        composable(NavRoutes.SignalDetails.route) { backStackEntry ->
            val signalId = backStackEntry.arguments?.getString("signalId") ?: ""
            SignalDetailsScreen(navController, signalId)
        }
        composable(NavRoutes.Watchlist.route) {
            val viewModel: WatchlistViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
            WatchlistScreen(navController, viewModel)
        }
        composable(NavRoutes.SignalHistory.route) {
            SignalHistoryScreen(navController)
        }
        composable(NavRoutes.Chat.route) { backStackEntry ->
            val sessionId = backStackEntry.arguments?.getString("sessionId")
            ChatScreen(navController, sessionId)
        }
        composable(NavRoutes.Markets.route) {
            MarketsScreen(navController)
        }
        composable(NavRoutes.TelegramSettings.route) {
            TelegramSettingsScreen(navController)
        }
        composable(NavRoutes.VoiceSettings.route) {
            VoiceSettingsScreen(navController)
        }
        composable(NavRoutes.Profile.route) {
            ProfileScreen(navController, authViewModel)
        }
        composable(NavRoutes.PaperTrading.route) {
            PaperTradingScreen(navController)
        }
        composable(NavRoutes.McpConnections.route) {
            McpConnectionsScreen(navController)
        }
    }
}
