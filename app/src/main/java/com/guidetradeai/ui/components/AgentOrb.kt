package com.guidetradeai.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.guidetradeai.ui.theme.GuideTradeColors

@Composable
fun AgentOrb(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
) {
    Box(
        modifier = modifier
            .size(80.dp)
            .shadow(
                elevation = 16.dp,
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
            )
            .clickable(enabled = onClick != null) { onClick?.invoke() },
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            imageVector = Icons.Default.SmartToy,
            contentDescription = "Agent Orb",
            tint = Color.White,
            modifier = Modifier.size(36.dp),
        )
    }
}

@Composable
fun MiniAgentOrb(
    state: String,
    sizeDp: Float = 56f,
    onClick: (() -> Unit)? = null,
) {
    val alpha by androidx.compose.animation.core.animateFloatAsState(
        targetValue = if (state == "listening") 1f else 0.6f,
        animationSpec = androidx.compose.animation.core.tween(300),
        label = "mini_orb_alpha",
    )
    Box(
        modifier = Modifier
            .size(sizeDp.dp)
            .shadow(
                elevation = 12.dp,
                shape = CircleShape,
                spotColor = GuideTradeColors.PrimaryPurple.copy(alpha = 0.4f),
            )
            .clip(CircleShape)
            .background(
                brush = Brush.radialGradient(
                    colors = listOf(
                        GuideTradeColors.BrightPurple.copy(alpha = alpha),
                        GuideTradeColors.PrimaryPurple.copy(alpha = alpha * 0.7f),
                    ),
                ),
            )
            .clickable(enabled = onClick != null) { onClick?.invoke() },
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            imageVector = Icons.Default.SmartToy,
            contentDescription = "Mini Agent Orb",
            tint = Color.White,
            modifier = Modifier.size((sizeDp * 0.4f).dp),
        )
    }
}
