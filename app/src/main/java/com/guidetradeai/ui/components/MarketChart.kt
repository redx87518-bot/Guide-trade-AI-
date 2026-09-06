package com.guidetradeai.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.guidetradeai.domain.model.ChartPoint
import com.guidetradeai.ui.theme.AccentCyan
import com.guidetradeai.ui.theme.AccentPurple
import com.guidetradeai.ui.theme.Background
import com.guidetradeai.ui.theme.DividerColor
import com.guidetradeai.ui.theme.TextPrimary
import com.guidetradeai.ui.theme.TextSecondary

@Composable
fun MarketCard(data: com.guidetradeai.domain.model.MarketDataResponse) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = Background,
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column {
                    Text(
                        text = data.symbol.ifBlank { data.name },
                        color = TextPrimary,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                    )
                    Text(
                        text = "${data.provider} · ${data.market}",
                        color = TextSecondary,
                        fontSize = 12.sp,
                    )
                }
                Column(horizontalAlignment = Alignment.End) {
                    data.price?.let {
                        Text(
                            text = "$${"%.2f".format(it)}",
                            color = TextPrimary,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                        )
                    }
                    data.changePercent?.let {
                        val color = if (it >= 0) Color.Green.copy(alpha = 0.8f) else Color.Red.copy(alpha = 0.8f)
                        Text(
                            text = "${if (it >= 0) "+" else ""}${"%.2f".format(it)}%",
                            color = color,
                            fontSize = 14.sp,
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            if (data.chartData.isNotEmpty()) {
                SimpleLineChart(
                    points = data.chartData,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp),
                )
                Spacer(modifier = Modifier.height(12.dp))
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                InfoBadge(label = "Signal", value = data.signal.ifBlank { "N/A" })
                data.score?.let { InfoBadge(label = "Score", value = "${"%.2f".format(it)}") }
                data.rsi?.let { InfoBadge(label = "RSI", value = "%.1f".format(it)) }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                InfoBadge(label = "Oscillator", value = data.oscillator.ifBlank { "N/A" })
                InfoBadge(label = "MA", value = data.movingAverage.ifBlank { "N/A" })
                if (data.trend.isNotBlank()) {
                    InfoBadge(label = "Trend", value = data.trend)
                }
            }

            if (data.news.isNotEmpty()) {
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "News",
                    color = TextSecondary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                )
                Spacer(modifier = Modifier.height(4.dp))
                data.news.take(3).forEach { news ->
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        color = DividerColor.copy(alpha = 0.3f),
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(
                                text = news.title,
                                color = TextPrimary,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                            )
                            if (news.summary.isNotBlank()) {
                                Text(
                                    text = news.summary,
                                    color = TextSecondary,
                                    fontSize = 12.sp,
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                }
            }
        }
    }
}

@Composable
fun InfoBadge(label: String, value: String) {
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = DividerColor.copy(alpha = 0.3f),
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
        ) {
            Text(
                text = label,
                color = TextSecondary,
                fontSize = 10.sp,
            )
            Text(
                text = value,
                color = TextPrimary,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
            )
        }
    }
}

@Composable
fun SimpleLineChart(
    points: List<ChartPoint>,
    modifier: Modifier = Modifier,
    lineColor: Color = AccentCyan,
    fillColor: Color = AccentCyan.copy(alpha = 0.1f),
) {
    if (points.isEmpty()) return

    val values = points.map { it.value }
    val minVal = values.minOrNull() ?: 0.0
    val maxVal = values.maxOrNull() ?: 1.0
    val range = if (maxVal > minVal) maxVal - minVal else 1.0

    Canvas(modifier = modifier) {
        val width = size.width
        val height = size.height
        val padding = 8.dp.toPx()
        val chartWidth = width - padding * 2
        val chartHeight = height - padding * 2

        val path = Path()
        points.forEachIndexed { index, point ->
            val x = padding + (index.toFloat() / (points.size - 1).coerceAtLeast(1)) * chartWidth
            val y = padding + chartHeight - ((point.value - minVal) / range).toFloat() * chartHeight
            if (index == 0) {
                path.moveTo(x, y)
            } else {
                path.lineTo(x, y)
            }
        }

        val fillPath = Path()
        fillPath.addPath(path)
        fillPath.lineTo(padding + chartWidth, padding + chartHeight)
        fillPath.lineTo(padding, padding + chartHeight)
        fillPath.close()

        drawPath(
            path = fillPath,
            color = fillColor,
        )

        drawPath(
            path = path,
            color = lineColor,
            style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round),
        )

        val lastX = padding + chartWidth
        val lastY = padding + chartHeight - ((points.last().value - minVal) / range).toFloat() * chartHeight
        drawCircle(
            color = lineColor,
            radius = 6.dp.toPx(),
            center = Offset(lastX, lastY),
        )
    }
}
