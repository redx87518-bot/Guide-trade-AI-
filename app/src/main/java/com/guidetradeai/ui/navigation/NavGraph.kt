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
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.guidetradeai.ui.screens.*
import androidx.lifecycle.viewmodel.compose.viewModel
import com.guidetradeai.ui.navigation.NavRoutes

@Composable
fun GuideTradeNavGraph(
    navController: NavHostController = rememberNavController(),
    startDestination: String = NavRoutes.HOME,
) {


    NavHost(navController = navController, startDestination = startDestination) {
        composable(NavRoutes.MARKETS) {
            MarketsScreen(navController = navController)
        }
        composable(NavRoutes.SETTINGS) {
            SettingsScreen(navController = navController)
        }
        composable(NavRoutes.TELEGRAM_SETTINGS) {
            TelegramSettingsScreen(navController = navController)
        }
        composable(NavRoutes.VOICE_SETTINGS) {
            VoiceSettingsScreen(navController = navController)
        }
        composable(NavRoutes.SPLASH) {
            SplashScreen(navController = navController)
        }
        composable(NavRoutes.LOGIN) {
            LoginScreen(navController = navController)
        }
    }
}
