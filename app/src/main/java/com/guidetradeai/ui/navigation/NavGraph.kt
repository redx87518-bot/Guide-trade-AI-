package com.guidetradeai.ui.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.navigation.NavType
import com.guidetradeai.ui.screens.ChatScreen
import com.guidetradeai.ui.screens.EmailVerificationScreen
import com.guidetradeai.ui.screens.ForgotPasswordScreen
import com.guidetradeai.ui.screens.HomeScreen
import com.guidetradeai.ui.screens.LoginScreen
import com.guidetradeai.ui.screens.OrbScreen
import com.guidetradeai.ui.screens.ResearchHistoryScreen
import com.guidetradeai.ui.screens.ResearchDetailScreen
import com.guidetradeai.ui.screens.SettingsScreen
import com.guidetradeai.ui.screens.SignUpScreen
import com.guidetradeai.ui.screens.SplashScreen
import com.guidetradeai.ui.screens.TelegramSettingsScreen
import com.guidetradeai.ui.screens.VoiceSettingsScreen
import com.guidetradeai.ui.screens.AboutScreen
import com.guidetradeai.ui.screens.ChatHistoryScreen
import com.guidetradeai.ui.screens.ProfileScreen
import com.guidetradeai.ui.screens.OnboardingScreen
import com.guidetradeai.viewmodel.AuthViewModel

sealed class BottomNavItem(val title: String, val icon: ImageVector, val route: String) {
    data object Chat : BottomNavItem("Chat", Icons.Default.Chat, NavRoutes.CHAT_NEW)
    data object Research : BottomNavItem("Research", Icons.Default.Analytics, NavRoutes.RESEARCH_HISTORY)
    data object Voice : BottomNavItem("Voice", Icons.Default.Mic, NavRoutes.ORB)
    data object Settings : BottomNavItem("Settings", Icons.Default.Settings, NavRoutes.SETTINGS)
}

@Composable
fun NavGraph(
    modifier: Modifier = Modifier,
    startDestination: String = NavRoutes.SPLASH,
    authViewModel: AuthViewModel,
    navController: NavHostController = rememberNavController(),
) {
    val items = listOf(
        BottomNavItem.Chat,
        BottomNavItem.Research,
        BottomNavItem.Voice,
        BottomNavItem.Settings,
    )
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val hideBottomBarRoutes = setOf(
        NavRoutes.SPLASH,
        NavRoutes.ONBOARDING,
        NavRoutes.LOGIN,
        NavRoutes.SIGNUP,
        NavRoutes.FORGOT_PASSWORD,
        NavRoutes.ABOUT,
        NavRoutes.TELEGRAM_SETTINGS,
        NavRoutes.VOICE_SETTINGS,
        NavRoutes.PROFILE,
    )
    val showBottomBar = currentRoute != null && !hideBottomBarRoutes.any { currentRoute == it || currentRoute?.startsWith(it) == true }

    NavHost(
        modifier = modifier,
        navController = navController,
        startDestination = startDestination,
        enterTransition = { fadeIn(tween(200)) + scaleIn(initialScale = 0.96f) },
        exitTransition = { fadeOut(tween(150)) },
        popEnterTransition = { fadeIn(tween(200)) + scaleIn(initialScale = 0.96f) },
        popExitTransition = { fadeOut(tween(150)) },
    ) {
        composable(NavRoutes.SPLASH) {
            SplashScreen(navController = navController, authViewModel = authViewModel)
        }
        composable(NavRoutes.ONBOARDING) {
            OnboardingScreen(navController = navController)
        }
        composable(NavRoutes.LOGIN) {
            LoginScreen(navController = navController, authViewModel = authViewModel)
        }
        composable(NavRoutes.SIGNUP) {
            SignUpScreen(navController = navController, authViewModel = authViewModel)
        }
        composable(
            route = NavRoutes.VERIFICATION,
            arguments = listOf(navArgument("email") { type = NavType.StringType }),
        ) {
            EmailVerificationScreen(
                navController = navController,
                authViewModel = authViewModel,
                email = it.arguments?.getString("email") ?: "",
            )
        }
        composable(NavRoutes.FORGOT_PASSWORD) {
            ForgotPasswordScreen(navController = navController, authViewModel = authViewModel)
        }
        composable(NavRoutes.HOME) {
            HomeScreen(navController = navController, authViewModel = authViewModel)
        }
        composable(NavRoutes.CHAT_NEW) {
            ChatScreen(navController = navController, sessionId = null)
        }
        composable(
            route = NavRoutes.CHAT,
            arguments = listOf(navArgument("sessionId") { type = NavType.StringType }),
        ) {
            ChatScreen(navController = navController, sessionId = it.arguments?.getString("sessionId"))
        }
        composable(NavRoutes.CHAT_HISTORY) {
            ChatHistoryScreen(navController = navController)
        }
        composable(NavRoutes.RESEARCH_HISTORY) {
            ResearchHistoryScreen(navController = navController)
        }
        composable(
            route = NavRoutes.RESEARCH_DETAIL,
            arguments = listOf(navArgument("researchId") { type = NavType.StringType }),
        ) {
            ResearchDetailScreen(
                navController = navController,
                researchId = it.arguments?.getString("researchId") ?: "",
            )
        }
        composable(NavRoutes.PROFILE) {
            ProfileScreen(navController = navController, authViewModel = authViewModel)
        }
        composable(NavRoutes.SETTINGS) {
            SettingsScreen(navController = navController, authViewModel = authViewModel)
        }
        composable(NavRoutes.TELEGRAM_SETTINGS) {
            TelegramSettingsScreen(navController = navController)
        }
        composable(NavRoutes.VOICE_SETTINGS) {
            VoiceSettingsScreen(navController = navController)
        }
        composable(NavRoutes.ABOUT) {
            AboutScreen(navController = navController)
        }
        composable(NavRoutes.ORB) {
            OrbScreen(navController = navController)
        }
    }
}
