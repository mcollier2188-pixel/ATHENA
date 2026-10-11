package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.NetworkNode
import com.example.ui.theme.EchoCard
import com.example.ui.theme.EchoCardStroke
import com.example.ui.theme.EchoCyan
import com.example.ui.theme.EchoGreen
import com.example.ui.theme.EchoText
import kotlin.math.hypot

@Composable
fun NodeMapCanvas(
    nodes: List<NetworkNode>,
    selectedNodeIndex: Int,
    onSelectNode: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val textMeasurer = rememberTextMeasurer()

    Box(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(1.15f)
            .clip(RoundedCornerShape(12.dp))
            .background(EchoCard)
            .testTag("node_map_canvas")
            .pointerInput(nodes) {
                detectTapGestures { tapOffset ->
                    val w = size.width.toFloat()
                    val h = size.height.toFloat()
                    var closestIdx = -1
                    var closestDist = Float.MAX_VALUE
                    val threshold = 40.dp.toPx()

                    nodes.forEachIndexed { i, node ->
                        val nx = (node.longitude.toFloat() + 7.5f) / 10f * w
                        val ny = (58.2f - node.latitude.toFloat()) / 8.4f * h
                        val dist = hypot(tapOffset.x - nx, tapOffset.y - ny)
                        if (dist < threshold && dist < closestDist) {
                            closestDist = dist
                            closestIdx = i
                        }
                    }

                    if (closestIdx != -1) {
                        onSelectNode(closestIdx)
                    }
                }
            }
    ) {
        Canvas(modifier = Modifier.matchParentSize()) {
            val w = size.width
            val h = size.height

            // Subtle background grid
            val gridStep = 40.dp.toPx()
            for (gx in 0..(w / gridStep).toInt()) {
                drawLine(
                    color = EchoCardStroke.copy(alpha = 0.35f),
                    start = Offset(gx * gridStep, 0f),
                    end = Offset(gx * gridStep, h),
                    strokeWidth = 1f
                )
            }
            for (gy in 0..(h / gridStep).toInt()) {
                drawLine(
                    color = EchoCardStroke.copy(alpha = 0.35f),
                    start = Offset(0f, gy * gridStep),
                    end = Offset(w, gy * gridStep),
                    strokeWidth = 1f
                )
            }

            if (nodes.isEmpty()) return@Canvas

            val masterNode = nodes[0]
            val masterX = (masterNode.longitude.toFloat() + 7.5f) / 10f * w
            val masterY = (58.2f - masterNode.latitude.toFloat()) / 8.4f * h

            // Draw harmonic connection lines from N1 Portsmouth to all peripheral nodes
            for (i in 1 until nodes.size) {
                val node = nodes[i]
                val nx = (node.longitude.toFloat() + 7.5f) / 10f * w
                val ny = (58.2f - node.latitude.toFloat()) / 8.4f * h

                val isConnectedToSelected = (i == selectedNodeIndex)
                drawLine(
                    color = if (isConnectedToSelected) EchoCyan.copy(alpha = 0.85f) else EchoCardStroke.copy(alpha = 0.65f),
                    start = Offset(masterX, masterY),
                    end = Offset(nx, ny),
                    strokeWidth = if (isConnectedToSelected) 2.5f else 1.2f,
                    pathEffect = if (isConnectedToSelected) null else PathEffect.dashPathEffect(floatArrayOf(12f, 8f))
                )
            }

            // Draw each node point and label
            nodes.forEachIndexed { i, node ->
                val nx = (node.longitude.toFloat() + 7.5f) / 10f * w
                val ny = (58.2f - node.latitude.toFloat()) / 8.4f * h
                val isSelected = (i == selectedNodeIndex)
                val isMaster = (node.id == "N1")

                val nodeColor = when {
                    isMaster -> EchoGreen
                    node.baseRatio == 1 -> Color(0xFF38BDF8)
                    node.baseRatio == 2 -> Color(0xFF60A5FA)
                    node.baseRatio == 3 -> Color(0xFF818CF8)
                    node.baseRatio == 4 -> Color(0xFFA78BFA)
                    node.baseRatio == 5 -> Color(0xFFC084FC)
                    node.baseRatio == 6 -> Color(0xFFF472B6)
                    else -> Color(0xFFFB7185)
                }

                // Halo if selected
                if (isSelected) {
                    drawCircle(
                        color = nodeColor.copy(alpha = 0.35f),
                        radius = 18f,
                        center = Offset(nx, ny)
                    )
                }

                // Node circle
                drawCircle(
                    color = nodeColor,
                    radius = if (isSelected) 9f else 6.5f,
                    center = Offset(nx, ny)
                )

                // White/contrast border
                drawCircle(
                    color = if (isSelected) Color.White else Color(0xFFE4EEF2).copy(alpha = 0.8f),
                    radius = if (isSelected) 9f else 6.5f,
                    center = Offset(nx, ny),
                    style = Stroke(width = if (isSelected) 2.5f else 1f)
                )

                // Node label text
                val labelText = if (isSelected) "${node.id} (${node.name})" else node.id
                val textLayoutResult = textMeasurer.measure(
                    text = labelText,
                    style = TextStyle(
                        fontSize = if (isSelected) 12.sp else 10.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        color = if (isSelected) Color.White else EchoText.copy(alpha = 0.85f)
                    )
                )

                drawText(
                    textLayoutResult = textLayoutResult,
                    topLeft = Offset(nx + 10f, ny - textLayoutResult.size.height / 2f)
                )
            }
        }
    }
}
