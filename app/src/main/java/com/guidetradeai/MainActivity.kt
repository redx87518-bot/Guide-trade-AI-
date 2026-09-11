package com.guidetradeai

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.lifecycle.viewmodel.compose.viewModel
import com.guidetradeai.ui.components.BottomNavItem
import com.guidetradeai.ui.components.GuideTradeBottomNav
import com.guidetradeai.ui.navigation.GuideTradeNavGraph
import com.guidetradeai.ui.navigation.NavRoutes
import com.guidetradeai.ui.theme.GuideTradeTheme
import com.guidetradeai.viewmodel.AuthViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            GuideTradeTheme {
                MaterialTheme {
                    val navController = rememberNavController()
                    val authViewModel: AuthViewModel = viewModel()
                    val navBackStackEntry by navController.currentBackStackEntryAsState()
                    val currentRoute = navBackStackEntry?.destination?.route ?: NavRoutes.Splash.route

                    val bottomNavItems = listOf(
                        BottomNavItem("Home", androidx.compose.material.icons.Icons.Default.Home, NavRoutes.Home.route),
                        BottomNavItem("Signals", androidx.compose.material.icons.Icons.Default.ShowChart, NavRoutes.Signals.route),
                        BottomNavItem("Analyze", androidx.compose.material.icons.Icons.Default.Analytics, NavRoutes.Analyze.route),
                        BottomNavItem("Agent", androidx.compose.material.icons.Icons.Default.Chat, NavRoutes.Agent.route),
                        BottomNavItem("Settings", androidx.compose.material.icons.Icons.Default.Settings, NavRoutes.Settings.route),
                    )
                    val bottomNavRoutes: List<String> = bottomNavItems.map { it.route }
                    val showBottomNav = currentRoute in bottomNavRoutes

                    androidx.compose.foundation.layout.Box(modifier = Modifier.fillMaxSize()) {
                        GuideTradeNavGraph(
                            navController = navController,
                            authViewModel = authViewModel,
                        )
                        if (showBottomNav) {
                            GuideTradeBottomNav(
                                items = bottomNavItems,
                                currentRoute = currentRoute,
                                onItemClick = { route ->
                                    if (route == currentRoute) return@GuideTradeBottomNav
                                    navController.navigate(route) {
                                        popUpTo(NavRoutes.Home.route) { inclusive = false }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                },
                            )
                        }
                    }
                }
            }
        }
    }
}
