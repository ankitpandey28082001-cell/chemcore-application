package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import kotlin.random.Random

@Composable
fun FlaskSimulationView(
    liquidColor: Color,
    hasGasBubbles: Boolean,
    hasPrecipitate: Boolean,
    precipitateColor: Color,
    modifier: Modifier = Modifier
) {
    val animatedLiquidColor by animateColorAsState(
        targetValue = liquidColor,
        animationSpec = tween(durationMillis = 1000),
        label = "liquid_color"
    )

    val infiniteTransition = rememberInfiniteTransition(label = "flask_sim")
    val waveOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "wave_offset"
    )

    // Bubble states generated once
    val bubblePositions = remember {
        List(18) {
            Triple(Random.nextFloat(), Random.nextFloat(), Random.nextFloat() * 4f + 3f)
        }
    }

    // Precipitate particle positions
    val precipitatePositions = remember {
        List(30) {
            Pair(Random.nextFloat(), Random.nextFloat())
        }
    }

    Canvas(modifier = modifier.fillMaxSize()) {
        val w = size.width
        val h = size.height

        val beakerLeft = w * 0.18f
        val beakerRight = w * 0.82f
        val beakerWidth = beakerRight - beakerLeft
        val beakerTop = h * 0.12f
        val beakerBottom = h * 0.90f
        val beakerHeight = beakerBottom - beakerTop

        val liquidHeight = beakerHeight * 0.65f
        val liquidTop = beakerBottom - liquidHeight

        // Draw Liquid
        val liquidPath = Path().apply {
            moveTo(beakerLeft + 4f, beakerBottom - 8f)
            lineTo(beakerLeft + 4f, liquidTop)

            // Gentle wave on liquid surface
            val waveAmplitude = 4f
            val segments = 20
            for (i in 0..segments) {
                val x = beakerLeft + 4f + (beakerWidth - 8f) * (i / segments.toFloat())
                val phase = (i / segments.toFloat()) * 2 * Math.PI + (waveOffset * 2 * Math.PI)
                val y = liquidTop + (Math.sin(phase) * waveAmplitude).toFloat()
                lineTo(x, y)
            }

            lineTo(beakerRight - 4f, beakerBottom - 8f)
            close()
        }

        // Draw liquid body
        drawPath(
            path = liquidPath,
            brush = Brush.verticalGradient(
                colors = listOf(
                    animatedLiquidColor.copy(alpha = 0.70f),
                    animatedLiquidColor.copy(alpha = 0.92f)
                ),
                startY = liquidTop,
                endY = beakerBottom
            )
        )

        // Draw Gas Bubbles if reaction produces gas
        if (hasGasBubbles) {
            bubblePositions.forEach { (relX, relY, radius) ->
                val bubbleProgress = (relY + waveOffset) % 1f
                val bx = beakerLeft + 12f + (beakerWidth - 24f) * relX
                val by = beakerBottom - 12f - (liquidHeight - 16f) * bubbleProgress

                drawCircle(
                    color = Color.White.copy(alpha = 0.75f),
                    radius = radius,
                    center = Offset(bx, by),
                    style = Stroke(width = 1.5f)
                )
                drawCircle(
                    color = Color.White.copy(alpha = 0.35f),
                    radius = radius * 0.6f,
                    center = Offset(bx - radius * 0.3f, by - radius * 0.3f)
                )
            }
        }

        // Draw Precipitate Crystals on Bottom
        if (hasPrecipitate) {
            precipitatePositions.forEach { (relX, relH) ->
                val px = beakerLeft + 8f + (beakerWidth - 16f) * relX
                val py = beakerBottom - 4f - (relH * 22f)
                drawCircle(
                    color = precipitateColor.copy(alpha = 0.90f),
                    radius = 3.5f,
                    center = Offset(px, py)
                )
            }
        }

        // Draw Beaker Glass Outline
        val glassColor = Color(0xFF94A3B8).copy(alpha = 0.65f)
        val lipOffset = 10f

        // Lip flare
        val lipPath = Path().apply {
            moveTo(beakerLeft - lipOffset, beakerTop)
            lineTo(beakerLeft + lipOffset, beakerTop + 6f)
            lineTo(beakerLeft, beakerTop + 6f)
            lineTo(beakerLeft, beakerBottom - 12f)
        }
        drawPath(lipPath, color = glassColor, style = Stroke(width = 4f))

        // Right side
        drawLine(
            color = glassColor,
            start = Offset(beakerRight, beakerTop + 6f),
            end = Offset(beakerRight, beakerBottom - 12f),
            strokeWidth = 4f
        )
        // Right lip
        drawLine(
            color = glassColor,
            start = Offset(beakerRight, beakerTop + 6f),
            end = Offset(beakerRight + lipOffset, beakerTop),
            strokeWidth = 4f
        )

        // Beaker Bottom
        drawRoundRect(
            color = glassColor,
            topLeft = Offset(beakerLeft, beakerBottom - 16f),
            size = Size(beakerWidth, 16f),
            cornerRadius = CornerRadius(8f, 8f),
            style = Stroke(width = 4f)
        )

        // Beaker graduation measurement lines
        val graduationCount = 5
        for (g in 1..graduationCount) {
            val gy = beakerBottom - (beakerHeight * 0.15f * g)
            val lineLength = if (g % 2 == 0) 24f else 14f
            drawLine(
                color = Color.White.copy(alpha = 0.4f),
                start = Offset(beakerLeft + 8f, gy),
                end = Offset(beakerLeft + 8f + lineLength, gy),
                strokeWidth = 2f
            )
        }

        // Light reflection streak on glass
        drawLine(
            color = Color.White.copy(alpha = 0.30f),
            start = Offset(beakerRight - 14f, beakerTop + 14f),
            end = Offset(beakerRight - 14f, beakerBottom - 20f),
            strokeWidth = 3f
        )
    }
}
