package com.guidetradeai

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.ShowChart
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
                androidx.compose.material3.MaterialTheme {
                    val navController = rememberNavController()
                    val authViewModel: AuthViewModel = viewModel()
                    val navBackStackEntry by navController.currentBackStackEntryAsState()
                    val currentRoute = navBackStackEntry?.destination?.route ?: NavRoutes.Splash.route

                    val bottomNavItems = listOf(
                        BottomNavItem("Home", Icons.Default.Home, NavRoutes.Home.route),
                        BottomNavItem("Signals", Icons.Default.ShowChart, NavRoutes.Signals.route),
                        BottomNavItem("Analyze", Icons.Default.Analytics, NavRoutes.Analyze.route),
                        BottomNavItem("Agent", Icons.Default.Chat, NavRoutes.Agent.route),
                        BottomNavItem("More", Icons.Default.MoreHoriz, NavRoutes.More.route),
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
