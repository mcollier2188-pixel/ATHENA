package com.example.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import com.example.model.NetworkNode
import com.example.ui.theme.EchoCard
import com.example.ui.theme.EchoCardStroke
import com.example.ui.theme.EchoCyan
import com.example.ui.theme.EchoGreen
import kotlin.math.sin

@Composable
fun OscilloscopeCanvas(
    frequencies: List<Double>,
    isPlaying: Boolean,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "oscilloscope_phase")
    val phaseAnim by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = if (isPlaying) (2f * Math.PI.toFloat()) else 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1800, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "phase"
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(2.4f)
            .clip(RoundedCornerShape(12.dp))
            .background(EchoCard)
    ) {
        Canvas(modifier = Modifier.matchParentSize()) {
            val w = size.width
            val h = size.height
            val centerY = h / 2f

            // Oscilloscope grid lines
            val gridXStep = w / 8f
            val gridYStep = h / 4f
            for (i in 0..8) {
                drawLine(
                    color = EchoCardStroke.copy(alpha = 0.25f),
                    start = Offset(i * gridXStep, 0f),
                    end = Offset(i * gridXStep, h),
                    strokeWidth = 1f
                )
            }
            for (j in 0..4) {
                drawLine(
                    color = EchoCardStroke.copy(alpha = 0.25f),
                    start = Offset(0f, j * gridYStep),
                    end = Offset(w, j * gridYStep),
                    strokeWidth = 1f
                )
            }

            // Zero baseline
            drawLine(
                color = EchoCardStroke.copy(alpha = 0.7f),
                start = Offset(0f, centerY),
                end = Offset(w, centerY),
                strokeWidth = 1.5f
            )

            if (frequencies.isEmpty()) return@Canvas

            val period = 2.0 / NetworkNode.BASE_FREQUENCY_HZ
            val path = Path()
            val numSteps = (w / 2).toInt().coerceAtLeast(100)
            val amplitudeScale = (h / 2f - 14f)

            for (step in 0..numSteps) {
                val px = step.toFloat() / numSteps * w
                val t = (step.toDouble() / numSteps) * period
                var composite = 0.0

                for (freq in frequencies) {
                    val angle = 2.0 * Math.PI * freq * t + phaseAnim
                    composite += sin(angle)
                }
                composite /= frequencies.size

                val py = centerY - (composite.toFloat() * amplitudeScale)

                if (step == 0) {
                    path.moveTo(px, py)
                } else {
                    path.lineTo(px, py)
                }
            }

            // Draw waveform with glowing gradient
            drawPath(
                path = path,
                brush = Brush.horizontalGradient(
                    colors = listOf(
                        EchoCyan,
                        EchoGreen,
                        EchoCyan
                    )
                ),
                style = Stroke(width = 3.5f)
            )
        }
    }
}
