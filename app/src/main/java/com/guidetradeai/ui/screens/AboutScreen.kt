import com.guidetradeai.BuildConfig
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
fun AboutScreen(navController: NavHostController) {
    Scaffold(
        topBar = {
            GuideTradeTopBar(
                title = "About",
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
                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .background(
                                brush = Brush.radialGradient(
                                    colors = listOf(GuideTradeColors.BrightPurple, GuideTradeColors.PrimaryPurple),
                                ),
                                shape = CircleShape,
                            ),
                    )
                }
            }
                Text(text = "GuideTrade AI", color = GuideTradeColors.TextPrimary, fontSize = 24.sp, fontWeight = FontWeight.Bold, modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center)
                Text(text = "AI Market Intelligence", color = GuideTradeColors.TextSecondary, fontSize = 14.sp, modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center)
                Text(text = "Version 1.0.0", color = GuideTradeColors.MutedText, fontSize = 12.sp, modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center)
                Text(text = com.guidetradeai.BuildConfig.APPLICATION_ID, color = GuideTradeColors.MutedText, fontSize = 11.sp, modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center)
                GuideTradeCard(onClick = { /* TODO */ }) {
                    Row(modifier = Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(text = "Privacy Policy", color = GuideTradeColors.TextPrimary, fontSize = 15.sp)
                        Icon(imageVector = Icons.Default.ArrowForward, contentDescription = null, tint = GuideTradeColors.MutedText, modifier = Modifier.size(18.dp))
                    }
                        Text(text = "Terms of Service", color = GuideTradeColors.TextPrimary, fontSize = 15.sp)
                        Text(text = "Support", color = GuideTradeColors.TextPrimary, fontSize = 15.sp)
        }
    }
}