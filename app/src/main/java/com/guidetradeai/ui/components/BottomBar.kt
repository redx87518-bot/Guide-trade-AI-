package com.guidetradeai.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.NavGraph.Companion.findStartDestination
import com.guidetradeai.ui.navigation.NavRoutes
import com.guidetradeai.ui.theme.AccentCyan
import com.guidetradeai.ui.theme.AccentGlow
import com.guidetradeai.ui.theme.DividerColor
import com.guidetradeai.ui.theme.SurfaceDark
import com.guidetradeai.ui.theme.TextSecondary

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
        BottomNavItem("Chat", Icons.Default.Chat, NavRoutes.CHAT_NEW),
        BottomNavItem("Research", Icons.Default.Analytics, NavRoutes.RESEARCH_HISTORY),
        BottomNavItem("Voice", Icons.Default.Mic, NavRoutes.ORB),
        BottomNavItem("Settings", Icons.Default.Settings, NavRoutes.SETTINGS),
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
                .background(
                    brush = androidx.compose.ui.graphics.Brush.verticalGradient(
                        colors = listOf(SurfaceDark, SurfaceDark),
                    ),
                ),
            containerColor = SurfaceDark,
            tonalElevation = 0.dp,
        ) {
            items.forEach { item ->
                val selected = currentRoute == item.route || (item.route == NavRoutes.ORB && currentRoute == NavRoutes.ORB)
                val isCenter = item.route == NavRoutes.ORB
                NavigationBarItem(
                    selected = selected,
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
                        if (isCenter) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .shadow(
                                        elevation = 8.dp,
                                        shape = CircleShape,
                                        spotColor = AccentCyan.copy(alpha = 0.4f),
                                    )
                                    .clip(CircleShape)
                                    .background(
                                        brush = androidx.compose.ui.graphics.Brush.radialGradient(
                                            colors = listOf(AccentCyan, Color(0xFF7B61FF)),
                                        ),
                                    ),
                                contentAlignment = Alignment.Center,
                            ) {
                                Icon(
                                    imageVector = item.icon,
                                    contentDescription = item.title,
                                    tint = Color.White,
                                    modifier = Modifier.size(22.dp),
                                )
                            }
                        } else {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .padding(4.dp),
                            ) {
                                Icon(
                                    imageVector = item.icon,
                                    contentDescription = item.title,
                                    tint = if (selected) AccentCyan else TextSecondary,
                                )
                            }
                        }
                    },
                    label = {
                        Text(
                            text = item.title,
                            color = if (selected) AccentCyan else TextSecondary,
                            fontSize = 11.sp,
                            fontWeight = if (selected) FontWeight.Medium else FontWeight.Normal,
                        )
                    },
                    colors = NavigationBarItemDefaults.colors(
                        indicatorColor = AccentGlow,
                        selectedIconColor = AccentCyan,
                        unselectedIconColor = TextSecondary,
                        selectedTextColor = AccentCyan,
                        unselectedTextColor = TextSecondary,
                    ),
                )
            }
        }
    }
}
