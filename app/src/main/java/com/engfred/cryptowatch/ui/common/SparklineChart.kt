package com.engfred.cryptowatch.ui.common

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp

@Composable
fun SparklineChart(data: List<Double>, modifier: Modifier = Modifier, graphColor: Color = Color.Green) {
    if (data.isEmpty()) return
    val points = remember(data) {
        val max = data.maxOrNull() ?: 1.0
        val min = data.minOrNull() ?: 0.0
        val range = if (max == min) 1.0 else max - min
        data.map { (it - min) / range }
    }

    Canvas(modifier = modifier) {
        val width = size.width
        val height = size.height
        val stepX = width / (points.size - 1).coerceAtLeast(1)

        val path = Path().apply {
            moveTo(0f, height - (points.first().toFloat() * height))
            points.forEachIndexed { index, value ->
                lineTo(index * stepX, height - (value.toFloat() * height))
            }
        }
        drawPath(path = path, color = graphColor, style = Stroke(width = 2.dp.toPx()))
    }
}