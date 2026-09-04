package com.guidetradeai.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.icons.Icons
import androidx.compose.material3.icons.filled.Chat
import androidx.compose.material3.icons.filled.Home
import androidx.compose.material3.icons.filled.History
import androidx.compose.material3.icons.filled.Person
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.guidetradeai.ui.navigation.NavRoutes

data class BottomNavItem(
    val title: String,
    val icon: ImageVector,
    val route: String,
)

@Composable
fun BottomBar(
    navController: NavHostController,
    modifier: Modifier = Modifier,
) {
    val items = listOf(
        BottomNavItem("Home", Icons.Default.Home, NavRoutes.HOME),
        BottomNavItem("Chat", Icons.Default.Chat, NavRoutes.CHAT_NEW),
        BottomNavItem("History", Icons.Default.History, NavRoutes.CHAT_HISTORY),
        BottomNavItem("Profile", Icons.Default.Person, NavRoutes.PROFILE),
    )
    val currentRoute = navController.currentDestination?.route
    val hideRoutes = setOf(
        NavRoutes.SPLASH, NavRoutes.ONBOARDING, NavRoutes.LOGIN,
        NavRoutes.SIGNUP, NavRoutes.FORGOT_PASSWORD, NavRoutes.ABOUT,
        NavRoutes.SETTINGS, NavRoutes.TELEGRAM_SETTINGS, NavRoutes.VOICE_SETTINGS,
        NavRoutes.RESEARCH_DETAIL,
    )
    val showBottomBar = currentRoute != null &&
        !hideRoutes.contains(currentRoute) &&
        !currentRoute.startsWith("chat/") &&
        !currentRoute.startsWith("research_detail/")

    if (showBottomBar) {
        NavigationBar(
            modifier = modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
                .height(80.dp),
        ) {
            items.forEach { item ->
                NavigationBarItem(
                    selected = currentRoute == item.route,
                    onClick = {
                        navController.navigate(item.route) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    icon = {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .padding(4.dp),
                        ) {
                            Icon(
                                imageVector = item.icon,
                                contentDescription = item.title,
                            )
                        }
                    },
                    label = { Text(item.title) },
                    colors = NavigationBarItemDefaults.navigationBarItemColors(),
                )
            }
        }
    }
}
