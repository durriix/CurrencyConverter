package ui

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import data.model.CurrencyPricePoint

@Composable
fun CurrencyChart(currencyPoints: List<CurrencyPricePoint>, modifier: Modifier = Modifier) {
    val points = currencyPoints.map { it.price.toFloat() }
        Canvas(modifier = modifier) {
            val spaceBetweenPoints = size.width / points.size
            val min = points.minOrNull() ?: 0f
            val max = points.maxOrNull() ?: 0f
            val variance = max - min
            val multiplier = size.height / variance
            var lastPoint = (points[0] - min) * multiplier
            points.forEachIndexed { i, item ->
                val newPoint = (item - min) * multiplier
                drawLine(
                    Color.Blue,
                    Offset(i * spaceBetweenPoints, size.height - lastPoint),
                    Offset((i + 1) * spaceBetweenPoints, size.height - newPoint)
                )
                lastPoint = newPoint
            }
        }
}