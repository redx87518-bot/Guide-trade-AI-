package com.guidetradeai.ui.components

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.runtime.setValue

@Composable
fun MarkdownText(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.onSurface,
) {
    val annotatedString = buildMarkdownAnnotatedString(text, color)
    Text(
        text = annotatedString,
        modifier = modifier,
        color = color,
        lineHeight = 22.sp,
        fontSize = 16.sp,
    )
}

fun buildMarkdownAnnotatedString(text: String, color: Color = Color.Unspecified): AnnotatedString {
    val builder = AnnotatedString.Builder()
    val paragraphs = text.split("\n\n").filter { it.isNotBlank() }

    for (paragraph in paragraphs) {
        val lines = paragraph.split("\n")
        for (line in lines) {
            val trimmed = line.trim()
            when {
                trimmed.startsWith("### ") -> {
                    builder.pushStyle(SpanStyle(fontSize = 18.sp, fontWeight = FontWeight.W600, color = color))
                    builder.append(trimmed.substring(4))
                    builder.pop()
                }
                trimmed.startsWith("## ") -> {
                    builder.pushStyle(SpanStyle(fontSize = 22.sp, fontWeight = FontWeight.W700, color = color))
                    builder.append(trimmed.substring(3))
                    builder.pop()
                }
                trimmed.startsWith("# ") -> {
                    builder.pushStyle(SpanStyle(fontSize = 26.sp, fontWeight = FontWeight.W700, color = color))
                    builder.append(trimmed.substring(2))
                    builder.pop()
                }
                trimmed.startsWith("- ") || trimmed.startsWith("* ") -> {
                    builder.pushStyle(SpanStyle(color = color))
                    builder.append("\u2022 ${trimmed.substring(2)}")
                    builder.pop()
                }
                trimmed.matches(Regex("^[0-9]+\\. .*")) -> {
                    val content = trimmed.substringAfter(". ")
                    val num = trimmed.substringBefore(".")
                    builder.pushStyle(SpanStyle(color = color))
                    builder.append("$num. $content")
                    builder.pop()
                }
                else -> {
                    builder.pushStyle(SpanStyle(color = color))
                    builder.append(trimmed)
                    builder.pop()
                }
            }
            builder.pushStyle(SpanStyle(color = Color.Transparent))
            builder.append("\n")
            builder.pop()
        }
        builder.pushStyle(SpanStyle(color = Color.Transparent))
        builder.append("\n")
        builder.pop()
    }
    return builder.toAnnotatedString()
}

@Composable
fun ChatTypingIndicator() {
    val infiniteTransition = rememberInfiniteTransition(label = "typing_dots")
    Row(
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        repeat(3) { index ->
            val alpha by infiniteTransition.animateFloat(
                initialValue = 0.3f,
                targetValue = 1f,
                animationSpec = infiniteRepeatable(
                    animation = tween(durationMillis = 500, delayMillis = index * 150),
                    repeatMode = RepeatMode.Restart,
                ),
                label = "dot_alpha_$index",
            )
            Box(
                modifier = Modifier
                    .size(6.dp)
                    .background(
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = alpha),
                        shape = RoundedCornerShape(50),
                    ),
            )
        }
    }
}
