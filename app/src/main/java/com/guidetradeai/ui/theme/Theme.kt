package com.guidetradeai.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    primary = androidx.compose.ui.graphics.Color(0xFF8B5CF6),
    secondary = androidx.compose.ui.graphics.Color(0xFFA78BFA),
    background = androidx.compose.ui.graphics.Color(0xFF07070B),
    surface = androidx.compose.ui.graphics.Color(0xFF0D0D14),
    onPrimary = androidx.compose.ui.graphics.Color.White,
    onSecondary = androidx.compose.ui.graphics.Color.White,
    onBackground = androidx.compose.ui.graphics.Color(0xFFF8F7FC),
    onSurface = androidx.compose.ui.graphics.Color(0xFFF8F7FC),
)

private val LightColorScheme = lightColorScheme(
    primary = androidx.compose.ui.graphics.Color(0xFF6D28D9),
    secondary = androidx.compose.ui.graphics.Color(0xFF7C3AED),
    background = androidx.compose.ui.graphics.Color(0xFFF7F7FA),
    surface = androidx.compose.ui.graphics.Color(0xFFFFFFFF),
    onPrimary = androidx.compose.ui.graphics.Color.White,
    onSecondary = androidx.compose.ui.graphics.Color.White,
    onBackground = androidx.compose.ui.graphics.Color(0xFF17171F),
    onSurface = androidx.compose.ui.graphics.Color(0xFF17171F),
)

@Composable
fun GuideTradeTheme(
    darkTheme: Boolean = true,
    content: @Composable () -> Unit,
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    MaterialTheme(
        colorScheme = colorScheme,
        content = content,
    )
}
