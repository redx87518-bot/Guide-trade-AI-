package com.guidetradeai.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.guidetradeai.ui.theme.GuideTradeColors

@Composable
fun MarkdownText(
    text: String,
    modifier: Modifier = Modifier,
) {
    Text(
        text = text,
        color = GuideTradeColors.TextPrimary,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        modifier = modifier.fillMaxWidth(),
    )
}
