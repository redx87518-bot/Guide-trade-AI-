package com.guidetradeai.ui.screens

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.clickable
import androidx.compose.foundation.border
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Briefcase
import androidx.compose.material.icons.filled.ChartArea
import androidx.compose.material.icons.filled.Sparkles
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

package com.guidetradeai.ui.screens

@Composable
fun ProfileScreen(navController: NavHostController) {
    val authViewModel: AuthViewModel = viewModel()
    val user = authViewModel.currentUser.collectAsState().value
    Scaffold(
        topBar = {
            GuideTradeTopBar(
                title = "Profile",
                navigationIcon = Icons.AutoMirrored.Filled.ArrowBack,
                onNavigationClick = { navController.popBackStack() },
            )
        },
        bottomBar = { GuideTradeBottomBar(navController = navController) },
        containerColor = GuideTradeColors.Background,
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            item {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    AvatarInitials(
                        name = user?.fullName ?: user?.email ?: "U",
                        size = 80,
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(text = user?.fullName ?: "User", color = GuideTradeColors.TextPrimary, fontSize = 20.sp, fontWeight = FontWeight.SemiBold)
                    Text(text = user?.email ?: "", color = GuideTradeColors.TextSecondary, fontSize = 14.sp)
                }
            }
            item { Divider(color = GuideTradeColors.SubtleBorder) }
                GuideTradeCard(onClick = { /* TODO */ }) {
                    Row(modifier = Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.Lock, contentDescription = null, tint = GuideTradeColors.BrightPurple, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(text = "Security", color = GuideTradeColors.TextPrimary, fontSize = 15.sp, fontWeight = FontWeight.Medium)
                            Text(text = "Password, 2FA", color = GuideTradeColors.TextSecondary, fontSize = 12.sp)
                        }
                        Icon(imageVector = Icons.Default.ArrowForward, contentDescription = null, tint = GuideTradeColors.MutedText, modifier = Modifier.size(18.dp))
                    }
                PrimaryButton(
                    text = "Sign Out",
                    onClick = {
                        authViewModel.signOut()
                        navController.navigate(NavRoutes.LOGIN) {
                            popUpTo(NavRoutes.HOME) { inclusive = true }
                    },
                )
        }
    }
}