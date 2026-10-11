package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Badge
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.FrequencyHistoryEntry
import com.example.ui.theme.EchoBg
import com.example.ui.theme.EchoBgAlt
import com.example.ui.theme.EchoCard
import com.example.ui.theme.EchoCardStroke
import com.example.ui.theme.EchoCyan
import com.example.ui.theme.EchoGreen
import com.example.ui.theme.EchoMuted
import com.example.ui.theme.EchoRed
import com.example.ui.theme.EchoText
import java.util.Locale
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

/**
 * Data model for a single frequency histogram bin in the historical heatmap.
 */
data class FrequencyHeatmapBin(
    val binIndex: Int,
    val minHz: Double,
    val maxHz: Double,
    val centerHz: Double,
    val count: Int,
    val lockCount: Int,
    val intensity: Float, // Normalized 0.0f..1.0f
    val nodeLabel: String? = null
)

/**
 * Mode of heatmap visualization.
 */
enum class HeatmapVisualMode(val title: String) {
    GRADIENT_SPECTROGRAM("Thermal Gradient"),
    HISTOGRAM_BARS("Density Bins"),
    COMPACT_HEATSTRIP("Heat Track")
}

/**
 * Mathematical engine for analyzing historical session frequencies across 0-190 Hz.
 */
object FrequencyHeatmapAnalyzer {

    // Known node reference harmonics for labeling hotspots
    private val HARMONIC_LABELS = mapOf(
        0.0 to "Zero DC",
        42.6 to "N5 Bedrock (42.6 Hz)",
        85.2 to "N1 Master (85.2 Hz)",
        127.8 to "N7 Chalk (127.8 Hz)",
        170.4 to "N2 Vault (170.4 Hz)",
        190.0 to "Ceiling (190 Hz)"
    )

    /**
     * Bins historical session entries into discrete frequency buckets across [minHz]..[maxHz].
     */
    fun computeBins(
        history: List<FrequencyHistoryEntry>,
        minHz: Double = 0.0,
        maxHz: Double = 190.0,
        binCount: Int = 38,
        onlyLocks: Boolean = false
    ): List<FrequencyHeatmapBin> {
        val safeMin = min(minHz, maxHz)
        val safeMax = max(minHz, maxHz).coerceAtLeast(safeMin + 0.1)
        val binWidth = (safeMax - safeMin) / binCount

        val filteredEntries = if (onlyLocks) history.filter { it.isResonanceLock } else history
        val counts = IntArray(binCount)
        val lockCounts = IntArray(binCount)

        for (entry in filteredEntries) {
            val freq = entry.frequencyHz
            if (freq in safeMin..safeMax) {
                val index = ((freq - safeMin) / binWidth).toInt().coerceIn(0, binCount - 1)
                counts[index]++
                if (entry.isResonanceLock) {
                    lockCounts[index]++
                }
            }
        }

        val maxCount = counts.maxOrNull()?.takeIf { it > 0 } ?: 1

        return List(binCount) { idx ->
            val bMin = safeMin + idx * binWidth
            val bMax = bMin + binWidth
            val bCenter = (bMin + bMax) / 2.0
            val count = counts[idx]
            val lCount = lockCounts[idx]

            // Quadratic scaling so single sessions show subtle warmth, while clusters glow hot
            val rawRatio = count.toFloat() / maxCount.toFloat()
            val intensity = if (count == 0) 0f else (0.25f + 0.75f * rawRatio).coerceIn(0f, 1f)

            // Find matching node label if close
            val matchedLabel = HARMONIC_LABELS.entries.find { (target, _) ->
                abs(bCenter - target) <= binWidth / 1.5
            }?.value

            FrequencyHeatmapBin(
                binIndex = idx,
                minHz = bMin,
                maxHz = bMax,
                centerHz = bCenter,
                count = count,
                lockCount = lCount,
                intensity = intensity,
                nodeLabel = matchedLabel
            )
        }
    }

    /**
     * Maps normalized intensity [0.0..1.0] to multi-stop thermal color gradient.
     */
    fun getThermalColor(intensity: Float, alpha: Float = 1.0f): Color {
        return when {
            intensity <= 0.01f -> Color(0x1800E5FF).copy(alpha = alpha * 0.12f) // Cool baseline
            intensity < 0.25f -> Color(0xFF00E5FF).copy(alpha = alpha * 0.45f) // Electric Cyan
            intensity < 0.50f -> Color(0xFF6EB5FF).copy(alpha = alpha * 0.70f) // Blue
            intensity < 0.70f -> Color(0xFF4ADE80).copy(alpha = alpha * 0.85f) // Emerald Green
            intensity < 0.88f -> Color(0xFFF59E0B).copy(alpha = alpha * 0.95f) // Amber Orange
            else -> Color(0xFFFF3D00).copy(alpha = alpha)                      // Radiant Red/Hot
        }
    }

    /**
     * Finds the nearest bin to the user's currently selected frequency.
     */
    fun findIntensityAtFrequency(
        bins: List<FrequencyHeatmapBin>,
        currentHz: Double
    ): Pair<Float, FrequencyHeatmapBin?> {
        val targetBin = bins.find { currentHz >= it.minHz && currentHz <= it.maxHz }
            ?: bins.minByOrNull { abs(it.centerHz - currentHz) }
        return Pair(targetBin?.intensity ?: 0f, targetBin)
    }

    /**
     * Returns the highest intensity hotspot bins.
     */
    fun getTopHotspots(bins: List<FrequencyHeatmapBin>, limit: Int = 4): List<FrequencyHeatmapBin> {
        return bins.filter { it.count > 0 }
            .sortedByDescending { it.count }
            .take(limit)
    }
}

/**
 * Data Visualization Component:
 * Renders a rich heatmap overlay directly around the frequency slider track,
 * displaying historical usage intensity, session counts, and node harmonic hotspots.
 */
@Composable
fun FrequencyHeatmapSlider(
    value: Float,
    onValueChange: (Float) -> Unit,
    valueRange: ClosedFloatingPointRange<Float>,
    historyEntries: List<FrequencyHistoryEntry>,
    modifier: Modifier = Modifier,
    isResonanceLocked: Boolean = false,
    nearestNodeName: String? = null,
    testTag: String = "frequency_heatmap_slider"
) {
    var visualMode by remember { mutableStateOf(HeatmapVisualMode.GRADIENT_SPECTROGRAM) }
    var filterOnlyLocks by remember { mutableStateOf(false) }
    var showInfoPanel by remember { mutableStateOf(false) }

    // Bin calculations
    val bins by remember(historyEntries, valueRange, filterOnlyLocks) {
        derivedStateOf {
            FrequencyHeatmapAnalyzer.computeBins(
                history = historyEntries,
                minHz = valueRange.start.toDouble(),
                maxHz = valueRange.endInclusive.toDouble(),
                binCount = 38,
                onlyLocks = filterOnlyLocks
            )
        }
    }

    val (currentIntensity, currentBin) = remember(bins, value) {
        derivedStateOf {
            FrequencyHeatmapAnalyzer.findIntensityAtFrequency(bins, value.toDouble())
        }
    }.value

    val topHotspots by remember(bins) {
        derivedStateOf {
            FrequencyHeatmapAnalyzer.getTopHotspots(bins, limit = 4)
        }
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .testTag(testTag)
    ) {
        // 1. Heatmap Mode & Filter Selector Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.LocalFireDepartment,
                    contentDescription = "Historical Heatmap",
                    tint = if (currentIntensity > 0.7f) EchoRed else EchoCyan,
                    modifier = Modifier.size(16.dp)
                )
                Text(
                    text = "HISTORICAL USAGE HEATMAP",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = EchoText,
                    fontFamily = FontFamily.Monospace
                )
                Badge(
                    containerColor = if (currentIntensity > 0.7f) EchoRed.copy(alpha = 0.2f) else EchoCyan.copy(alpha = 0.15f),
                    contentColor = if (currentIntensity > 0.7f) EchoRed else EchoCyan
                ) {
                    Text(
                        text = "${(currentIntensity * 100).toInt()}% Hot",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                // Info / Diagnostic Toggle Button (Accessible 48dp)
                IconButton(
                    onClick = { showInfoPanel = !showInfoPanel },
                    modifier = Modifier
                        .size(48.dp)
                        .testTag("heatmap_info_toggle")
                ) {
                    Icon(
                        imageVector = if (showInfoPanel) Icons.Default.CheckCircle else Icons.Default.Info,
                        contentDescription = "Toggle Heatmap Info",
                        tint = if (showInfoPanel) EchoCyan else EchoMuted,
                        modifier = Modifier.size(18.dp)
                    )
                }

                // Visual Mode Cycling Button (Accessible 48dp)
                IconButton(
                    onClick = {
                        visualMode = when (visualMode) {
                            HeatmapVisualMode.GRADIENT_SPECTROGRAM -> HeatmapVisualMode.HISTOGRAM_BARS
                            HeatmapVisualMode.HISTOGRAM_BARS -> HeatmapVisualMode.COMPACT_HEATSTRIP
                            HeatmapVisualMode.COMPACT_HEATSTRIP -> HeatmapVisualMode.GRADIENT_SPECTROGRAM
                        }
                    },
                    modifier = Modifier
                        .size(48.dp)
                        .testTag("heatmap_mode_toggle")
                ) {
                    Icon(
                        imageVector = when (visualMode) {
                            HeatmapVisualMode.GRADIENT_SPECTROGRAM -> Icons.Default.GraphicEq
                            HeatmapVisualMode.HISTOGRAM_BARS -> Icons.Default.BarChart
                            HeatmapVisualMode.COMPACT_HEATSTRIP -> Icons.Default.Layers
                        },
                        contentDescription = "Cycle Heatmap Mode",
                        tint = EchoCyan,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }

        // Live Readout of Current Frequency Heatmap Intensity
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 2.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            val sessionCountText = currentBin?.let {
                if (it.count > 0) "${it.count} sessions (${it.lockCount} locked)" else "No sessions recorded"
            } ?: "Zero history"

            Text(
                text = "Selected: ${String.format(Locale.US, "%.3f Hz", value)} • $sessionCountText",
                fontSize = 11.sp,
                color = if (currentIntensity > 0.7f) EchoRed else EchoMuted,
                fontWeight = if (currentIntensity > 0.7f) FontWeight.SemiBold else FontWeight.Normal
            )

            Text(
                text = visualMode.title,
                fontSize = 10.sp,
                color = EchoCyan,
                fontFamily = FontFamily.Monospace
            )
        }

        Spacer(Modifier.height(4.dp))

        // 2. THE VISUAL HEATMAP OVERLAY & SLIDER STACK
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            contentAlignment = Alignment.Center
        ) {
            // Background Canvas: Renders the thermal heatmap overlay behind the slider track
            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(when (visualMode) {
                        HeatmapVisualMode.GRADIENT_SPECTROGRAM -> 32.dp
                        HeatmapVisualMode.HISTOGRAM_BARS -> 36.dp
                        HeatmapVisualMode.COMPACT_HEATSTRIP -> 18.dp
                    })
                    .clip(RoundedCornerShape(8.dp))
                    .background(EchoBgAlt)
                    .border(1.dp, EchoCardStroke, RoundedCornerShape(8.dp))
                    .testTag("heatmap_canvas")
            ) {
                when (visualMode) {
                    HeatmapVisualMode.GRADIENT_SPECTROGRAM -> {
                        drawThermalGradientOverlay(bins, size, value, valueRange)
                    }
                    HeatmapVisualMode.HISTOGRAM_BARS -> {
                        drawHistogramBarsOverlay(bins, size, value, valueRange)
                    }
                    HeatmapVisualMode.COMPACT_HEATSTRIP -> {
                        drawHeatStripOverlay(bins, size, value, valueRange)
                    }
                }
            }

            // Foreground: The Interactive Jetpack Compose Slider
            Slider(
                value = value.coerceIn(valueRange.start, valueRange.endInclusive),
                onValueChange = onValueChange,
                valueRange = valueRange,
                colors = SliderDefaults.colors(
                    thumbColor = if (isResonanceLocked) EchoGreen else if (currentIntensity > 0.7f) EchoRed else EchoCyan,
                    activeTrackColor = Color.Transparent, // Transparent so heatmap shines through clearly
                    inactiveTrackColor = Color.Transparent
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp)
                    .testTag("interactive_heatmap_slider")
            )
        }

        Spacer(Modifier.height(6.dp))

        // 3. Thermal Gradient Legend & Hotspot Jump Chips
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            // Legend Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("0 Hz (Low: 0%)", fontSize = 10.sp, color = EchoMuted)

                // Thermal Gradient Bar Pill
                Box(
                    modifier = Modifier
                        .width(130.dp)
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp))
                        .background(
                            Brush.horizontalGradient(
                                listOf(
                                    Color(0x3300E5FF),
                                    Color(0xFF00E5FF),
                                    Color(0xFF4ADE80),
                                    Color(0xFFF59E0B),
                                    Color(0xFFFF3D00)
                                )
                            )
                        )
                )

                Text("190 Hz (Peak: 100%)", fontSize = 10.sp, color = EchoMuted)
            }

            // Quick Hotspot Snap Chips
            if (topHotspots.isNotEmpty()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Historical Hotspots:", fontSize = 10.sp, color = EchoMuted)
                    topHotspots.forEach { hotspot ->
                        val isSelected = abs(value - hotspot.centerHz) <= 1.0
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = if (isSelected) EchoCyan else EchoCard,
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                if (hotspot.intensity > 0.7f) EchoRed else EchoCardStroke
                            ),
                            modifier = Modifier
                                .clickable { onValueChange(hotspot.centerHz.toFloat()) }
                                .testTag("hotspot_chip_${hotspot.binIndex}")
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(6.dp)
                                        .clip(CircleShape)
                                        .background(FrequencyHeatmapAnalyzer.getThermalColor(hotspot.intensity))
                                )
                                Text(
                                    text = String.format(Locale.US, "%.1f Hz (%d)", hotspot.centerHz, hotspot.count),
                                    fontSize = 10.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isSelected) EchoBg else EchoText
                                )
                            }
                        }
                    }
                }
            }
        }

        // 4. Expanded Diagnostic & Filter Panel
        AnimatedVisibility(
            visible = showInfoPanel,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                colors = CardDefaults.cardColors(containerColor = EchoCard),
                border = CardDefaults.outlinedCardBorder().copy(brush = Brush.horizontalGradient(listOf(EchoCardStroke, EchoCyan)))
            ) {
                Column(
                    modifier = Modifier.padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Heatmap Telemetry & Filtering",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = EchoCyan
                        )

                        FilterChip(
                            selected = filterOnlyLocks,
                            onClick = { filterOnlyLocks = !filterOnlyLocks },
                            label = { Text("Resonance Locks Only", fontSize = 10.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = EchoGreen,
                                selectedLabelColor = EchoBg,
                                containerColor = EchoBgAlt,
                                labelColor = EchoText
                            ),
                            modifier = Modifier.testTag("filter_locks_chip")
                        )
                    }

                    Text(
                        text = "The heatmap calculates usage density across 38 spectral bins between 0.0 Hz and 190.0 Hz. Frequency bins with recorded sessions illuminate from cool cyan to thermal red. Snap to top hotspots to re-engage historically resonant frequencies.",
                        fontSize = 10.sp,
                        color = EchoMuted,
                        lineHeight = 14.sp
                    )

                    currentBin?.let { bin ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(4.dp))
                                .background(EchoBgAlt)
                                .padding(8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text("Bin Range", fontSize = 9.sp, color = EchoMuted)
                                Text("${String.format(Locale.US, "%.1f", bin.minHz)} - ${String.format(Locale.US, "%.1f", bin.maxHz)} Hz", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = EchoText)
                            }
                            Column {
                                Text("Recorded Sessions", fontSize = 9.sp, color = EchoMuted)
                                Text("${bin.count} logs", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = EchoCyan)
                            }
                            Column {
                                Text("Harmonic Anchor", fontSize = 9.sp, color = EchoMuted)
                                Text(bin.nodeLabel ?: "None", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = EchoGreen)
                            }
                        }
                    }
                }
            }
        }
    }
}

/**
 * Draws continuous thermal spectrogram overlay with height modulation and color gradient.
 */
private fun DrawScope.drawThermalGradientOverlay(
    bins: List<FrequencyHeatmapBin>,
    size: Size,
    currentValue: Float,
    valueRange: ClosedFloatingPointRange<Float>
) {
    val totalWidth = size.width
    val totalHeight = size.height
    val binCount = bins.size
    if (binCount == 0) return

    val binWidth = totalWidth / binCount

    // Draw baseline subtle gridlines
    for (i in 0..4) {
        val x = totalWidth * (i / 4f)
        drawLine(
            color = EchoCardStroke.copy(alpha = 0.5f),
            start = Offset(x, 0f),
            end = Offset(x, totalHeight),
            strokeWidth = 1f
        )
    }

    // Draw continuous spectrogram density envelope
    val path = Path()
    path.moveTo(0f, totalHeight)

    bins.forEachIndexed { idx, bin ->
        val x = idx * binWidth + binWidth / 2f
        val amplitude = (bin.intensity * (totalHeight * 0.85f)).coerceAtLeast(totalHeight * 0.15f)
        val y = totalHeight - amplitude
        if (idx == 0) {
            path.lineTo(0f, y)
        }
        path.lineTo(x, y)
    }
    path.lineTo(totalWidth, totalHeight)
    path.close()

    // Create multi-stop gradient based on bin thermal colors
    val gradientColors = bins.map { bin ->
        FrequencyHeatmapAnalyzer.getThermalColor(bin.intensity, alpha = 0.85f)
    }
    drawPath(
        path = path,
        brush = Brush.horizontalGradient(gradientColors)
    )

    // Draw current thumb cursor vertical line
    val rangeSpan = (valueRange.endInclusive - valueRange.start).coerceAtLeast(0.01f)
    val thumbX = ((currentValue - valueRange.start) / rangeSpan) * totalWidth
    drawLine(
        color = EchoText,
        start = Offset(thumbX, 0f),
        end = Offset(thumbX, totalHeight),
        strokeWidth = 2f
    )
}

/**
 * Draws discrete histogram density bars for each frequency bin.
 */
private fun DrawScope.drawHistogramBarsOverlay(
    bins: List<FrequencyHeatmapBin>,
    size: Size,
    currentValue: Float,
    valueRange: ClosedFloatingPointRange<Float>
) {
    val totalWidth = size.width
    val totalHeight = size.height
    val binCount = bins.size
    if (binCount == 0) return

    val binWidth = totalWidth / binCount
    val barGap = 1.5f

    bins.forEachIndexed { idx, bin ->
        val x = idx * binWidth + barGap / 2f
        val w = (binWidth - barGap).coerceAtLeast(1f)
        val h = (bin.intensity * (totalHeight * 0.9f)).coerceAtLeast(totalHeight * 0.12f)
        val y = totalHeight - h
        val barColor = FrequencyHeatmapAnalyzer.getThermalColor(bin.intensity, alpha = if (bin.count > 0) 0.9f else 0.25f)

        drawRoundRect(
            color = barColor,
            topLeft = Offset(x, y),
            size = Size(w, h),
            cornerRadius = CornerRadius(2f, 2f)
        )

        // Draw top cap glow if it's a hotspot
        if (bin.intensity > 0.7f) {
            drawCircle(
                color = EchoRed,
                radius = 2.5f,
                center = Offset(x + w / 2f, y)
            )
        }
    }

    // Draw cursor indicator
    val rangeSpan = (valueRange.endInclusive - valueRange.start).coerceAtLeast(0.01f)
    val thumbX = ((currentValue - valueRange.start) / rangeSpan) * totalWidth
    drawLine(
        color = EchoCyan,
        start = Offset(thumbX, 0f),
        end = Offset(thumbX, totalHeight),
        strokeWidth = 2f
    )
}

/**
 * Draws compact continuous heat track along the slider groove.
 */
private fun DrawScope.drawHeatStripOverlay(
    bins: List<FrequencyHeatmapBin>,
    size: Size,
    currentValue: Float,
    valueRange: ClosedFloatingPointRange<Float>
) {
    val totalWidth = size.width
    val totalHeight = size.height
    val binCount = bins.size
    if (binCount == 0) return

    val binWidth = totalWidth / binCount

    bins.forEachIndexed { idx, bin ->
        val x = idx * binWidth
        val color = FrequencyHeatmapAnalyzer.getThermalColor(bin.intensity, alpha = 0.8f)
        drawRect(
            color = color,
            topLeft = Offset(x, 0f),
            size = Size(binWidth + 0.5f, totalHeight)
        )
    }

    val rangeSpan = (valueRange.endInclusive - valueRange.start).coerceAtLeast(0.01f)
    val thumbX = ((currentValue - valueRange.start) / rangeSpan) * totalWidth
    drawLine(
        color = EchoText,
        start = Offset(thumbX, 0f),
        end = Offset(thumbX, totalHeight),
        strokeWidth = 2f
    )
}
