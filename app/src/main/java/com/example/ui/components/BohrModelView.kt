package com.example.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.sp
import com.example.data.model.Element
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin

@Composable
fun BohrModelView(
    element: Element,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "bohr_orbit")
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 14000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "orbit_angle"
    )

    val textMeasurer = rememberTextMeasurer()
    val nucleusColor = element.category.color
    val electronColor = MaterialTheme.colorScheme.primary

    Canvas(modifier = modifier.fillMaxSize()) {
        val centerX = size.width / 2f
        val centerY = size.height / 2f
        val maxRadius = min(centerX, centerY) * 0.90f

        val shellCount = element.shells.size.coerceAtLeast(1)
        val nucleusRadius = min(28f, maxRadius / (shellCount + 2))
        val radiusStep = (maxRadius - nucleusRadius) / (shellCount + 0.5f)

        // Draw glowing Nucleus
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    nucleusColor.copy(alpha = 0.9f),
                    nucleusColor.copy(alpha = 0.4f),
                    Color.Transparent
                ),
                center = Offset(centerX, centerY),
                radius = nucleusRadius * 1.6f
            ),
            radius = nucleusRadius * 1.6f,
            center = Offset(centerX, centerY)
        )
        drawCircle(
            color = nucleusColor,
            radius = nucleusRadius,
            center = Offset(centerX, centerY)
        )

        // Draw symbol in nucleus
        val symbolLayout = textMeasurer.measure(
            text = element.symbol,
            style = TextStyle(
                color = Color.White,
                fontSize = (nucleusRadius * 0.9f).sp,
                fontWeight = FontWeight.Bold
            )
        )
        drawText(
            textLayoutResult = symbolLayout,
            topLeft = Offset(
                centerX - symbolLayout.size.width / 2f,
                centerY - symbolLayout.size.height / 2f
            )
        )

        // Draw concentric orbital shells and electrons
        element.shells.forEachIndexed { shellIdx, electronCount ->
            val shellRadius = nucleusRadius + radiusStep * (shellIdx + 1)

            // Orbit path (subtle dashed line)
            drawCircle(
                color = nucleusColor.copy(alpha = 0.25f),
                radius = shellRadius,
                center = Offset(centerX, centerY),
                style = Stroke(
                    width = 2f,
                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)
                )
            )

            // Draw electrons on this shell
            val shellSpeedMultiplier = if (shellIdx % 2 == 0) 1f else -1f
            val baseAngle = Math.toRadians((rotation * shellSpeedMultiplier * (1f + shellIdx * 0.25f)).toDouble())

            val angleStep = (2 * Math.PI) / electronCount.coerceAtLeast(1)
            for (e in 0 until electronCount) {
                val currentAngle = baseAngle + (e * angleStep)
                val ex = centerX + (shellRadius * cos(currentAngle)).toFloat()
                val ey = centerY + (shellRadius * sin(currentAngle)).toFloat()

                // Glow around electron
                drawCircle(
                    color = electronColor.copy(alpha = 0.4f),
                    radius = 7f,
                    center = Offset(ex, ey)
                )
                // Electron body
                drawCircle(
                    color = electronColor,
                    radius = 4.5f,
                    center = Offset(ex, ey)
                )
            }
        }
    }
}
