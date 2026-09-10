package com.guidetradeai.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shape
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

val DarkColorScheme = darkColorScheme(
    primary = GuideTradeColors.PrimaryPurple,
    onPrimary = GuideTradeColors.White,
    secondary = GuideTradeColors.SecondaryAccent,
    onSecondary = GuideTradeColors.White,
    tertiary = GuideTradeColors.BrightPurple,
    onTertiary = GuideTradeColors.White,
    background = GuideTradeColors.Background,
    onBackground = GuideTradeColors.TextPrimary,
    surface = GuideTradeColors.PrimarySurface,
    onSurface = GuideTradeColors.TextPrimary,
    surfaceVariant = GuideTradeColors.SecondarySurface,
    onSurfaceVariant = GuideTradeColors.TextSecondary,
    outline = GuideTradeColors.Border,
    inverseOnSurface = GuideTradeColors.TextPrimary,
    inverseSurface = GuideTradeColors.ElevatedSurface,
)

val LightColorScheme = lightColorScheme(
    primary = GuideTradeLightColors.PrimaryPurple,
    onPrimary = GuideTradeLightColors.White,
    secondary = GuideTradeLightColors.SecondaryAccent,
    onSecondary = GuideTradeLightColors.White,
    tertiary = GuideTradeLightColors.BrightPurple,
    onTertiary = GuideTradeLightColors.White,
    background = GuideTradeLightColors.Background,
    onBackground = GuideTradeLightColors.TextPrimary,
    surface = GuideTradeLightColors.PrimarySurface,
    onSurface = GuideTradeLightColors.TextPrimary,
    surfaceVariant = GuideTradeLightColors.SecondarySurface,
    onSurfaceVariant = GuideTradeLightColors.TextSecondary,
    outline = GuideTradeLightColors.Border,
    inverseOnSurface = GuideTradeLightColors.TextPrimary,
    inverseSurface = GuideTradeLightColors.ElevatedSurface,
)

val GuideTradeShapes = Shape(
    extraSmall = RoundedCornerShape(8.dp),
    small = RoundedCornerShape(12.dp),
    medium = RoundedCornerShape(16.dp),
    large = RoundedCornerShape(20.dp),
    extraLarge = RoundedCornerShape(28.dp),
)

val GuideTradeTypography = Typography(
    displayLarge = TextStyle(
        fontSize = 34.sp,
        lineHeight = 42.sp,
        fontWeight = FontWeight.W700,
        letterSpacing = (-0.5.sp),
    ),
    headlineLarge = TextStyle(
        fontSize = 28.sp,
        lineHeight = 36.sp,
        fontWeight = FontWeight.W700,
        letterSpacing = (-0.3.sp),
    ),
    headlineMedium = TextStyle(
        fontSize = 24.sp,
        lineHeight = 32.sp,
        fontWeight = FontWeight.W600,
    ),
    headlineSmall = TextStyle(
        fontSize = 20.sp,
        lineHeight = 28.sp,
        fontWeight = FontWeight.W600,
    ),
    titleLarge = TextStyle(
        fontSize = 18.sp,
        lineHeight = 24.sp,
        fontWeight = FontWeight.W600,
    ),
    titleMedium = TextStyle(
        fontSize = 16.sp,
        lineHeight = 22.sp,
        fontWeight = FontWeight.W500,
    ),
    titleSmall = TextStyle(
        fontSize = 14.sp,
        lineHeight = 20.sp,
        fontWeight = FontWeight.W500,
    ),
    bodyLarge = TextStyle(
        fontSize = 16.sp,
        lineHeight = 24.sp,
        fontWeight = FontWeight.W400,
    ),
    bodyMedium = TextStyle(
        fontSize = 14.sp,
        lineHeight = 20.sp,
        fontWeight = FontWeight.W400,
    ),
    bodySmall = TextStyle(
        fontSize = 12.sp,
        lineHeight = 16.sp,
        fontWeight = FontWeight.W400,
    ),
    labelLarge = TextStyle(
        fontSize = 13.sp,
        lineHeight = 18.sp,
        fontWeight = FontWeight.W500,
    ),
    labelMedium = TextStyle(
        fontSize = 12.sp,
        lineHeight = 16.sp,
        fontWeight = FontWeight.W500,
    ),
    labelSmall = TextStyle(
        fontSize = 11.sp,
        lineHeight = 15.sp,
        fontWeight = FontWeight.W500,
    ),
)

@Composable
fun GuideTradeTheme(
    darkTheme: Boolean = true,
    content: @Composable () -> Unit,
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    androidx.compose.material3.MaterialTheme(
        colorScheme = colorScheme,
        typography = GuideTradeTypography,
        shapes = androidx.compose.material3.Shapes(
            extraSmall = RoundedCornerShape(8.dp),
            small = RoundedCornerShape(12.dp),
            medium = RoundedCornerShape(16.dp),
            large = RoundedCornerShape(20.dp),
            extraLarge = RoundedCornerShape(28.dp),
        ),
        content = content,
    )
}

object GuideTradeTheme {
    val colors: GuideTradeColors get() = GuideTradeColors
}
