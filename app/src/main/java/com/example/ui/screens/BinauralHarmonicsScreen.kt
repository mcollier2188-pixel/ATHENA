package com.example.ui.screens

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bedtime
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material.icons.filled.Waves
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.example.audio.AmbientSoundLayer
import com.example.audio.AmbientSoundscapeEngine
import kotlinx.coroutines.delay
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.audio.HarmonicToneGenerator
import androidx.compose.runtime.mutableStateOf
import com.example.ui.theme.EchoBg
import com.example.ui.theme.EchoBgAlt
import com.example.ui.theme.EchoCard
import com.example.ui.theme.EchoCardStroke
import com.example.ui.theme.EchoCyan
import com.example.ui.theme.EchoCyanSoft
import com.example.ui.theme.EchoGreen
import com.example.ui.theme.EchoMuted
import com.example.ui.theme.EchoOnCyan
import com.example.ui.theme.EchoRed
import com.example.ui.theme.EchoText
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

data class BinauralPreset(
    val title: String,
    val state: String,
    val baseFreq: Double,
    val beatOffset: Double,
    val description: String
)

@Composable
fun BinauralHarmonicsScreen(
    toneGenerator: HarmonicToneGenerator,
    modifier: Modifier = Modifier
) {
    val isPlaying by toneGenerator.isPlayingState.collectAsState()

    var baseCarrierFreq by remember { mutableDoubleStateOf(42.6) }
    var beatDifference by remember { mutableDoubleStateOf(7.83) } // Schumann Resonance
    var volumeLevel by remember { mutableFloatStateOf(0.75f) }

    val soundscapeEngine = remember { AmbientSoundscapeEngine.SHARED }
    val ambientLayers by soundscapeEngine.layers.collectAsState()
    val isSoundscapeActive by soundscapeEngine.isEngineActive.collectAsState()

    // Sleep Timer State
    var timerPresetMinutes by remember { mutableIntStateOf(0) } // 0 = off, 15, 30, 45, 60
    var remainingSeconds by remember { mutableIntStateOf(0) }
    var isTimerRunning by remember { mutableStateOf(false) }

    // Sleep Timer countdown and smooth fade-out effect
    LaunchedEffect(isTimerRunning, remainingSeconds) {
        if (isTimerRunning && remainingSeconds > 0) {
            delay(1000L)
            val next = remainingSeconds - 1
            remainingSeconds = next

            // Gentle fade-out curve in the last 60 seconds
            if (next in 1..60) {
                val fadeFactor = next / 60f
                toneGenerator.setVolumeLevel(volumeLevel * fadeFactor)
            } else if (next == 0) {
                isTimerRunning = false
                timerPresetMinutes = 0
                toneGenerator.stop()
                soundscapeEngine.stopSoundscape()
                toneGenerator.setVolumeLevel(volumeLevel)
            }
        }
    }

    val presets = remember {
        listOf(
            BinauralPreset(
                "Schumann Resonance",
                "Earth Bedrock (7.83 Hz)",
                42.600,
                7.83,
                "Earth's fundamental ionospheric cavity frequency superimposed on the 42.6 Hz Solent bedrock carrier."
            ),
            BinauralPreset(
                "Theta Deep Focus",
                "Theta Wave (4.50 Hz)",
                85.200,
                4.50,
                "Deep meditation, intuitive memory retrieval, and creative synthesis state."
            ),
            BinauralPreset(
                "Alpha Relaxation",
                "Alpha Wave (10.0 Hz)",
                42.600,
                10.00,
                "Calm wakefulness, lucid cognition, and stress dissolution."
            ),
            BinauralPreset(
                "Delta Deep Rest",
                "Delta Wave (2.00 Hz)",
                42.600,
                2.00,
                "Slow-wave restorative recovery and cell rejuvenation rhythm."
            ),
            BinauralPreset(
                "Gamma Synthesis",
                "Gamma Wave (40.0 Hz)",
                85.200,
                40.00,
                "High-level multi-sensory information binding and insight."
            )
        )
    }

    val leftFreq = baseCarrierFreq
    val rightFreq = baseCarrierFreq + beatDifference

    fun applyBinauralAudio(start: Boolean = isPlaying) {
        toneGenerator.setVolumeLevel(volumeLevel)
        if (start) {
            toneGenerator.start(
                frequencies = listOf(leftFreq, rightFreq),
                mult = 1.0,
                tidalLfo = false
            )
        } else {
            toneGenerator.stop()
        }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(EchoBg)
            .padding(16.dp)
            .testTag("binaural_screen"),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Hero Header & Player
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = EchoCard),
                border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(EchoCyan))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Waves, contentDescription = null, tint = EchoCyan, modifier = Modifier.size(24.dp))
                            Spacer(Modifier.width(10.dp))
                            Column {
                                Text("BINAURAL ENTRAINMENT", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = EchoCyan)
                                Text("Dual-channel acoustic carrier matrix", fontSize = 11.sp, color = EchoMuted)
                            }
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(if (isPlaying) EchoGreen.copy(alpha = 0.2f) else EchoCardStroke)
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = if (isPlaying) "DUAL EMISSION" else "STANDBY",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isPlaying) EchoGreen else EchoMuted
                            )
                        }
                    }

                    Spacer(Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Card(
                            modifier = Modifier.weight(1f),
                            colors = CardDefaults.cardColors(containerColor = EchoBgAlt)
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Text("CH 1 (LEFT)", fontSize = 10.sp, color = EchoMuted, fontWeight = FontWeight.Bold)
                                Text("${"%.2f".format(leftFreq)} Hz", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = EchoCyan)
                                Text("Base Carrier", fontSize = 9.sp, color = EchoMuted)
                            }
                        }

                        Card(
                            modifier = Modifier.weight(1f),
                            colors = CardDefaults.cardColors(containerColor = EchoBgAlt)
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Text("CH 2 (RIGHT)", fontSize = 10.sp, color = EchoMuted, fontWeight = FontWeight.Bold)
                                Text("${"%.2f".format(rightFreq)} Hz", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = EchoGreen)
                                Text("Offset Carrier", fontSize = 9.sp, color = EchoMuted)
                            }
                        }

                        Card(
                            modifier = Modifier.weight(1f),
                            colors = CardDefaults.cardColors(containerColor = EchoBgAlt)
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Text("BINAURAL BEAT", fontSize = 10.sp, color = EchoMuted, fontWeight = FontWeight.Bold)
                                Text("${"%.2f".format(beatDifference)} Hz", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color(0xFFF59E0B))
                                Text("Brain Entrainment", fontSize = 9.sp, color = EchoMuted)
                            }
                        }
                    }

                    Spacer(Modifier.height(14.dp))

                    Button(
                        onClick = {
                            if (isPlaying) {
                                toneGenerator.stop()
                            } else {
                                applyBinauralAudio(start = true)
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isPlaying) Color(0xFFEF4444) else EchoCyan,
                            contentColor = if (isPlaying) Color.White else EchoOnCyan
                        ),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(
                            imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(
                            text = if (isPlaying) "Stop Binaural Audio" else "Generate Binaural Wave",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }
                }
            }
        }

        // Animated Lissajous Acoustic Curve Visualizer
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF030A14)),
                border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(EchoCardStroke))
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.GraphicEq, contentDescription = null, tint = EchoGreen, modifier = Modifier.size(18.dp))
                            Spacer(Modifier.width(8.dp))
                            Text("Real-Time Lissajous Acoustic Harmonic Curve", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = EchoText)
                        }
                        Text(
                            text = if (isPlaying) "Active Phase Coupling" else "Static Preview",
                            fontSize = 10.sp,
                            color = if (isPlaying) EchoGreen else EchoMuted
                        )
                    }

                    Spacer(Modifier.height(10.dp))

                    LissajousVisualizerCanvas(
                        freqA = leftFreq.toFloat(),
                        freqB = rightFreq.toFloat(),
                        isPlaying = isPlaying,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp)
                    )
                }
            }
        }

        // Entrainment Presets
        item {
            Column {
                Text("Neurological Entrainment Presets:", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = EchoCyan)
                Spacer(Modifier.height(8.dp))

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(vertical = 4.dp)
                ) {
                    items(presets) { preset ->
                        val isSelected = Math.abs(beatDifference - preset.beatOffset) < 0.05
                        FilterChip(
                            selected = isSelected,
                            onClick = {
                                baseCarrierFreq = preset.baseFreq
                                beatDifference = preset.beatOffset
                                applyBinauralAudio()
                            },
                            label = {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(preset.title, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                                    Text(preset.state, fontSize = 9.sp, color = EchoMuted)
                                }
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = EchoCyan,
                                selectedLabelColor = EchoOnCyan,
                                containerColor = EchoCard,
                                labelColor = EchoText
                            )
                        )
                    }
                }
            }
        }

        // Beat Fine-Tuner Slider
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = EchoCard),
                border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(EchoCardStroke))
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Binaural Beat Difference", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = EchoText)
                        Text("${"%.2f".format(beatDifference)} Hz", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = EchoCyan)
                    }

                    Slider(
                        value = beatDifference.toFloat(),
                        onValueChange = {
                            beatDifference = it.toDouble()
                            applyBinauralAudio()
                        },
                        valueRange = 0.5f..50.0f,
                        colors = SliderDefaults.colors(
                            thumbColor = EchoCyan,
                            activeTrackColor = EchoCyan,
                            inactiveTrackColor = EchoCardStroke
                        )
                    )

                    Spacer(Modifier.height(6.dp))
                    Text(
                        text = "Use headphones for proper binaural hemispheric separation. Left channel receives ${"%.2f".format(leftFreq)} Hz, Right channel receives ${"%.2f".format(rightFreq)} Hz.",
                        fontSize = 11.sp,
                        color = EchoMuted,
                        lineHeight = 15.sp
                    )
                }
            }
        }

        // Meditation Sleep Timer Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = EchoCard),
                border = CardDefaults.outlinedCardBorder().copy(
                    brush = androidx.compose.ui.graphics.SolidColor(if (isTimerRunning) EchoGreen else EchoCardStroke)
                )
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Bedtime, contentDescription = null, tint = if (isTimerRunning) EchoGreen else EchoCyan, modifier = Modifier.size(20.dp))
                            Spacer(Modifier.width(8.dp))
                            Column {
                                Text("MEDITATION SLEEP TIMER", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = EchoText)
                                Text("Auto shut-off with smooth 60s fade curve", fontSize = 11.sp, color = EchoMuted)
                            }
                        }

                        if (isTimerRunning && remainingSeconds > 0) {
                            val mins = remainingSeconds / 60
                            val secs = remainingSeconds % 60
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(EchoGreen.copy(alpha = 0.2f))
                                    .border(1.dp, EchoGreen, RoundedCornerShape(8.dp))
                                    .padding(horizontal = 10.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = "%02d:%02d".format(mins, secs),
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
                                    color = EchoGreen
                                )
                            }
                        }
                    }

                    // Timer Preset Chips
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        val timerPresets = listOf(15, 30, 45, 60)
                        timerPresets.forEach { m ->
                            val isSelected = timerPresetMinutes == m && isTimerRunning
                            FilterChip(
                                selected = isSelected,
                                onClick = {
                                    if (isSelected) {
                                        isTimerRunning = false
                                        timerPresetMinutes = 0
                                        remainingSeconds = 0
                                    } else {
                                        timerPresetMinutes = m
                                        remainingSeconds = m * 60
                                        isTimerRunning = true
                                        if (!isPlaying) applyBinauralAudio(start = true)
                                    }
                                },
                                label = { Text("${m}m", fontSize = 11.sp, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = EchoGreen,
                                    selectedLabelColor = Color.Black,
                                    containerColor = EchoBgAlt,
                                    labelColor = EchoText
                                ),
                                modifier = Modifier.weight(1f).testTag("timer_preset_${m}m")
                            )
                        }
                    }

                    if (isTimerRunning) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End
                        ) {
                            OutlinedButton(
                                onClick = {
                                    isTimerRunning = false
                                    timerPresetMinutes = 0
                                    remainingSeconds = 0
                                },
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = EchoRed)
                            ) {
                                Icon(Icons.Default.RestartAlt, contentDescription = null, modifier = Modifier.size(14.dp))
                                Spacer(Modifier.width(4.dp))
                                Text("Cancel Timer", fontSize = 11.sp)
                            }
                        }
                    }
                }
            }
        }

        // Multi-Layer Ambient Soundscapes Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = EchoCard),
                border = CardDefaults.outlinedCardBorder().copy(
                    brush = androidx.compose.ui.graphics.SolidColor(EchoCardStroke)
                )
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("🌊", fontSize = 18.sp)
                            Spacer(Modifier.width(8.dp))
                            Column {
                                Text("AMBIENT SOUNDSCAPES", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = EchoText)
                                Text("Layer natural acoustic textures over carrier tones", fontSize = 11.sp, color = EchoMuted)
                            }
                        }

                        Button(
                            onClick = {
                                if (isSoundscapeActive) soundscapeEngine.stopSoundscape()
                                else soundscapeEngine.startSoundscape()
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isSoundscapeActive) EchoGreen else EchoCyanSoft,
                                contentColor = if (isSoundscapeActive) Color.Black else EchoCyan
                            ),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(if (isSoundscapeActive) "Ambient On" else "Enable All", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    HorizontalDivider(color = EchoCardStroke)

                    // 4 Ambient Texture Sliders
                    ambientLayers.forEach { layerState ->
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(EchoBgAlt)
                                .padding(10.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(layerState.layer.iconLabel, fontSize = 16.sp)
                                    Spacer(Modifier.width(8.dp))
                                    Column {
                                        Text(layerState.layer.title, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = EchoText)
                                        Text(layerState.layer.subtitle, fontSize = 9.sp, color = EchoMuted)
                                    }
                                }

                                Switch(
                                    checked = layerState.isEnabled,
                                    onCheckedChange = { soundscapeEngine.toggleLayer(layerState.layer) },
                                    colors = SwitchDefaults.colors(
                                        checkedThumbColor = EchoCyan,
                                        checkedTrackColor = EchoCyanSoft
                                    )
                                )
                            }

                            if (layerState.isEnabled) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(Icons.Default.VolumeUp, contentDescription = null, tint = EchoMuted, modifier = Modifier.size(14.dp))
                                    Spacer(Modifier.width(6.dp))
                                    Slider(
                                        value = layerState.volume,
                                        onValueChange = { soundscapeEngine.setLayerVolume(layerState.layer, it) },
                                        valueRange = 0f..1f,
                                        colors = SliderDefaults.colors(thumbColor = EchoCyan, activeTrackColor = EchoCyan),
                                        modifier = Modifier.weight(1f)
                                    )
                                    Spacer(Modifier.width(6.dp))
                                    Text("${(layerState.volume * 100).toInt()}%", fontSize = 10.sp, color = EchoText)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun LissajousVisualizerCanvas(
    freqA: Float,
    freqB: Float,
    isPlaying: Boolean,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "lissajous_phase")
    val phase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = (2 * PI).toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = if (isPlaying) 3500 else 9000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "phase_rot"
    )

    Canvas(modifier = modifier) {
        val centerX = size.width / 2f
        val centerY = size.height / 2f
        val radiusX = (size.width * 0.42f)
        val radiusY = (size.height * 0.42f)

        // Subtle background grid
        drawLine(Color(0xFF1E293B), Offset(0f, centerY), Offset(size.width, centerY), strokeWidth = 1f)
        drawLine(Color(0xFF1E293B), Offset(centerX, 0f), Offset(centerX, size.height), strokeWidth = 1f)

        val ratio = (freqA / freqB.coerceAtLeast(1f)).coerceIn(0.2f, 5.0f)
        val path = Path()
        val numPoints = 250

        for (i in 0..numPoints) {
            val t = (i.toFloat() / numPoints) * 2f * PI.toFloat()
            val x = centerX + radiusX * sin(ratio * t + phase)
            val y = centerY + radiusY * cos(t)

            if (i == 0) {
                path.moveTo(x, y)
            } else {
                path.lineTo(x, y)
            }
        }

        drawPath(
            path = path,
            color = if (isPlaying) EchoGreen else EchoCyan,
            style = Stroke(width = if (isPlaying) 2.5f else 1.8f, cap = StrokeCap.Round)
        )
    }
}
