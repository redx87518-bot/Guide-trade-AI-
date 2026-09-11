package com.guidetradeai.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Work
import androidx.compose.material.icons.filled.ShowChart
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material.icons.filled.Star
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.guidetradeai.ui.theme.GuideTradeColors

data class BottomNavItem(
    val title: String,
    val icon: ImageVector,
    val route: String,
    val isCenter: Boolean = false,
)

@Composable
fun GuideTradeBottomBar(
    navController: androidx.navigation.NavHostController,
    modifier: Modifier = Modifier,
    items: List<BottomNavItem> = listOf(
        BottomNavItem("Home", Icons.Default.SmartToy, "home"),
        BottomNavItem("Markets", Icons.Default.ShowChart, "markets"),
        BottomNavItem("AI", Icons.Default.Star, "agent", isCenter = true),
        BottomNavItem("Paper", Icons.Default.Work, "paper"),
        BottomNavItem("Settings", Icons.Default.Star, "settings"),
    ),
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    NavigationBar(
        modifier = modifier
            .fillMaxWidth()
            .height(72.dp)
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        GuideTradeColors.PrimarySurface,
                        GuideTradeColors.PrimarySurface.copy(alpha = 0.95f),
                    ),
                ),
            ),
        containerColor = Color.Transparent,
        tonalElevation = 0.dp,
        contentColor = GuideTradeColors.TextSecondary,
    ) {
        items.forEach { item ->
            val selected = currentRoute == item.route
            val scale by animateFloatAsState(
                targetValue = if (selected && item.isCenter) 1.05f else 1f,
                animationSpec = tween(150),
                label = "nav_scale",
            )

            NavigationBarItem(
                selected = selected,
                onClick = {
                    if (currentRoute != item.route) {
                        navController.navigate(item.route) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                },
                icon = {
                    if (item.isCenter) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .scale(scale)
                                .shadow(
                                    elevation = 12.dp,
                                    shape = CircleShape,
                                    spotColor = GuideTradeColors.PrimaryPurple.copy(alpha = 0.5f),
                                )
                                .clip(CircleShape)
                                .background(
                                    brush = Brush.radialGradient(
                                        colors = listOf(
                                            GuideTradeColors.BrightPurple,
                                            GuideTradeColors.PrimaryPurple,
                                        ),
                                    ),
                                ),
                            contentAlignment = Alignment.Center,
                        ) {
                            Icon(
                                imageVector = item.icon,
                                contentDescription = item.title,
                                tint = GuideTradeColors.White,
                                modifier = Modifier.size(22.dp),
                            )
                        }
                    } else {
                        Icon(
                            imageVector = item.icon,
                            contentDescription = item.title,
                            tint = if (selected) GuideTradeColors.BrightPurple else GuideTradeColors.MutedText,
                            modifier = Modifier.size(24.dp),
                        )
                    }
                },
                label = {
                    Text(
                        text = item.title,
                        color = if (selected) GuideTradeColors.TextPrimary else GuideTradeColors.MutedText,
                        fontSize = 11.sp,
                        fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal,
                        letterSpacing = 0.3.sp,
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    indicatorColor = Color.Transparent,
                    selectedIconColor = GuideTradeColors.BrightPurple,
                    unselectedIconColor = GuideTradeColors.MutedText,
                    selectedTextColor = GuideTradeColors.TextPrimary,
                    unselectedTextColor = GuideTradeColors.MutedText,
                ),
            )
        }
    }
}