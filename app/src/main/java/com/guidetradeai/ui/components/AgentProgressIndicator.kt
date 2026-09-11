package com.guidetradeai.ui.components

import androidx.compose.foundation.Canvas
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
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun AgentProgressIndicator(
    steps: List<String>,
    currentStep: Int,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        steps.forEachIndexed { index, step ->
            val isDone = index < currentStep
            val isCurrent = index == currentStep
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Box(
                    modifier = Modifier.size(24.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Surface(
                        shape = CircleShape,
                        color = when {
                            isDone -> Color(0xFF22C55E)
                            isCurrent -> Color(0xFF7B61FF)
                            else -> Color(0xFF111E33)
                        },
                        modifier = Modifier.fillMaxWidth(),
                    ) {}
                    if (isDone) {
                        Text(text = "✓", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    } else if (isCurrent) {
                        Canvas(modifier = Modifier.size(16.dp)) {
                            drawCircle(color = Color.White, radius = size.minDimension / 2)
                        }
                    }
                }
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = step,
                    fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Normal,
                    color = when {
                        isDone -> Color(0xFF22C55E)
                        isCurrent -> Color(0xFFE8F4FF)
                        else -> Color(0xFF7A9CC0)
                    },
                    fontSize = 14.sp,
                )
            }
        }
    }
}
