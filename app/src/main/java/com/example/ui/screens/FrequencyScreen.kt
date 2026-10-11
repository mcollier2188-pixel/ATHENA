package com.example.ui.screens

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.Waves
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.audio.HarmonicToneGenerator
import com.example.audio.WaveformTimbre
import com.example.model.NetworkNode
import com.example.ui.components.OscilloscopeCanvas
import com.example.ui.theme.EchoBg
import com.example.ui.theme.EchoBgAlt
import com.example.ui.theme.EchoCard
import com.example.ui.theme.EchoCardStroke
import com.example.ui.theme.EchoCyan
import com.example.ui.theme.EchoGreen
import com.example.ui.theme.EchoMuted
import com.example.ui.theme.EchoOnCyan
import com.example.ui.theme.EchoText
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.components.FrequencyHeatmapSlider
import com.example.ui.viewmodel.FrequencyHistoryViewModel
import java.util.Locale
import kotlin.math.abs

@Composable
fun FrequencyScreen(
    toneGenerator: HarmonicToneGenerator = HarmonicToneGenerator.SHARED,
    historyViewModel: FrequencyHistoryViewModel = viewModel(),
    modifier: Modifier = Modifier
) {
    val isPlaying by toneGenerator.isPlayingState.collectAsState()
    val allHistoryList by historyViewModel.allHistory.collectAsStateWithLifecycle()

    var customFrequency by remember { mutableDoubleStateOf(42.6) }
    var selectedOctaveMult by remember { mutableDoubleStateOf(8.0) } // Default 3 octaves for phones
    var isTidalLfoOn by remember { mutableStateOf(false) }
    var selectedTimbre by remember { mutableStateOf(WaveformTimbre.PURE_SINE) }
    var volumeLevel by remember { mutableFloatStateOf(0.85f) }

    // Multi-node selection for harmonic chords
    val layeredNodeIds = remember { mutableStateListOf("N1") }

    val currentFrequencies = remember(customFrequency, layeredNodeIds.toList()) {
        if (layeredNodeIds.isEmpty()) {
            listOf(customFrequency)
        } else {
            val fromNodes = layeredNodeIds.mapNotNull { id ->
                NetworkNode.ALL_NODES.find { it.id == id }?.frequency
            }
            if (fromNodes.isEmpty()) listOf(customFrequency) else fromNodes
        }
    }

    fun applyAudioUpdate(startIfPlaying: Boolean = isPlaying) {
        toneGenerator.setOctaveMultiplier(selectedOctaveMult)
        toneGenerator.setTidalLfo(isTidalLfoOn)
        toneGenerator.setVolumeLevel(volumeLevel)
        toneGenerator.setTimbreMode(selectedTimbre)
        if (startIfPlaying) {
            toneGenerator.start(
                frequencies = currentFrequencies,
                mult = selectedOctaveMult,
                tidalLfo = isTidalLfoOn,
                timbreMode = selectedTimbre
            )
        } else {
            toneGenerator.stop()
        }
    }

    val infinitePulse = rememberInfiniteTransition(label = "freq_pulse")
    val pulseScale by infinitePulse.animateFloat(
        initialValue = 0.95f,
        targetValue = if (isPlaying) 1.15f else 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = if (isPlaying) 700 else 2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_scale"
    )

    var activeMode by remember { mutableIntStateOf(0) } // 0 = Bedrock Tuner, 1 = Full Matrix, 2 = Resonance Calc

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(EchoBg)
    ) {
        // Mode Selector Tab Row
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item {
                FilterChip(
                    selected = activeMode == 0,
                    onClick = { activeMode = 0 },
                    label = { Text("Bedrock Tuner", fontSize = 11.sp, fontWeight = if (activeMode == 0) FontWeight.Bold else FontWeight.Normal) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = EchoCyan,
                        selectedLabelColor = EchoOnCyan,
                        containerColor = EchoCard,
                        labelColor = EchoText
                    ),
                    modifier = Modifier.testTag("mode_bedrock_tuner")
                )
            }
            item {
                FilterChip(
                    selected = activeMode == 1,
                    onClick = { activeMode = 1 },
                    label = { Text("Harmonic Matrix", fontSize = 11.sp, fontWeight = if (activeMode == 1) FontWeight.Bold else FontWeight.Normal) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = EchoCyan,
                        selectedLabelColor = EchoOnCyan,
                        containerColor = EchoCard,
                        labelColor = EchoText
                    ),
                    modifier = Modifier.testTag("mode_full_matrix")
                )
            }
            item {
                FilterChip(
                    selected = activeMode == 2,
                    onClick = { activeMode = 2 },
                    label = { Text("Resonance Calc", fontSize = 11.sp, fontWeight = if (activeMode == 2) FontWeight.Bold else FontWeight.Normal) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = EchoCyan,
                        selectedLabelColor = EchoOnCyan,
                        containerColor = EchoCard,
                        labelColor = EchoText
                    ),
                    modifier = Modifier.testTag("mode_resonance_calc")
                )
            }
            item {
                FilterChip(
                    selected = activeMode == 3,
                    onClick = { activeMode = 3 },
                    label = { Text("Binaural & Sleep", fontSize = 11.sp, fontWeight = if (activeMode == 3) FontWeight.Bold else FontWeight.Normal) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = EchoCyan,
                        selectedLabelColor = EchoOnCyan,
                        containerColor = EchoCard,
                        labelColor = EchoText
                    ),
                    modifier = Modifier.testTag("mode_binaural_beats")
                )
            }
            item {
                FilterChip(
                    selected = activeMode == 4,
                    onClick = { activeMode = 4 },
                    label = { Text("🌊 Cymatics Lab", fontSize = 11.sp, fontWeight = if (activeMode == 4) FontWeight.Bold else FontWeight.Normal) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = EchoCyan,
                        selectedLabelColor = EchoOnCyan,
                        containerColor = EchoCard,
                        labelColor = EchoText
                    ),
                    modifier = Modifier.testTag("mode_cymatics_lab")
                )
            }
            item {
                FilterChip(
                    selected = activeMode == 5,
                    onClick = { activeMode = 5 },
                    label = { Text("🎙️ Live Spectrogram", fontSize = 11.sp, fontWeight = if (activeMode == 5) FontWeight.Bold else FontWeight.Normal) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = EchoCyan,
                        selectedLabelColor = EchoOnCyan,
                        containerColor = EchoCard,
                        labelColor = EchoText
                    ),
                    modifier = Modifier.testTag("mode_live_spectrogram")
                )
            }
            item {
                FilterChip(
                    selected = activeMode == 6,
                    onClick = { activeMode = 6 },
                    label = { Text("🏛️ King's Chamber", fontSize = 11.sp, fontWeight = if (activeMode == 6) FontWeight.Bold else FontWeight.Normal) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = EchoCyan,
                        selectedLabelColor = EchoOnCyan,
                        containerColor = EchoCard,
                        labelColor = EchoText
                    ),
                    modifier = Modifier.testTag("mode_kings_chamber")
                )
            }
        }

        when (activeMode) {
            0 -> {
                ResonanceTunerScreen(toneGenerator = toneGenerator, modifier = Modifier.weight(1f))
            }
            1 -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f)
                        .background(EchoBg)
                        .testTag("frequency_screen"),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(18.dp)
                ) {
        // Hero Card with Frequency Indicator & Big Resonance Ring
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = EchoCard),
                border = CardDefaults.outlinedCardBorder().copy(brush = Brush.horizontalGradient(listOf(EchoCyan, EchoGreen)))
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "THE 42.6 HZ RESONANCE",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = EchoCyan,
                        letterSpacing = 2.sp
                    )
                    Spacer(Modifier.height(14.dp))

                    // Animated Glowing Resonance Sphere
                    Box(
                        modifier = Modifier
                            .size(130.dp)
                            .scale(pulseScale)
                            .clip(CircleShape)
                            .background(
                                Brush.radialGradient(
                                    colors = listOf(
                                        if (isPlaying) EchoCyan.copy(alpha = 0.4f) else EchoCardStroke.copy(alpha = 0.3f),
                                        Color.Transparent
                                    )
                                )
                            )
                            .border(
                                width = if (isPlaying) 3.dp else 1.5.dp,
                                brush = if (isPlaying) Brush.sweepGradient(listOf(EchoCyan, EchoGreen, EchoCyan)) else Brush.linearGradient(listOf(EchoCardStroke, EchoCardStroke)),
                                shape = CircleShape
                            )
                            .clickable {
                                if (isPlaying) {
                                    toneGenerator.stop()
                                } else {
                                    applyAudioUpdate(startIfPlaying = true)
                                }
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = String.format(Locale.US, "%.3f", customFrequency),
                                fontSize = 26.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isPlaying) EchoGreen else EchoText
                            )
                            Text(
                                text = "Hz",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium,
                                color = EchoMuted
                            )
                            Text(
                                text = if (isPlaying) "PLAYING" else "TAP TO PLAY",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isPlaying) EchoCyan else EchoMuted
                            )
                        }
                    }

                    Spacer(Modifier.height(14.dp))
                    Text(
                        text = "Continuous subterranean standing wave discovered in bedrock strata.",
                        fontSize = 13.sp,
                        color = EchoMuted,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )

                    Spacer(Modifier.height(16.dp))

                    // Master Play/Stop Action Button
                    Button(
                        onClick = {
                            if (isPlaying) {
                                toneGenerator.stop()
                            } else {
                                applyAudioUpdate(startIfPlaying = true)
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .testTag("freq_main_play_button"),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isPlaying) Color(0xFFEF4444) else EchoCyan,
                            contentColor = if (isPlaying) Color.White else EchoOnCyan
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(
                            imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                            contentDescription = null,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(
                            text = if (isPlaying) "Stop Frequency Tone" else "Generate 42.6 Hz Audio",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                    }
                }
            }
        }

        // Real-Time Oscilloscope Wave Canvas
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = EchoBgAlt),
                border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(EchoCardStroke))
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Live Oscilloscope Tracing",
                            fontWeight = FontWeight.Bold,
                            color = EchoText,
                            fontSize = 14.sp
                        )
                        Text(
                            text = if (isTidalLfoOn) "11.74s Tidal LFO Active" else "Constant Amplitude",
                            fontSize = 11.sp,
                            color = if (isTidalLfoOn) EchoGreen else EchoMuted
                        )
                    }
                    Spacer(Modifier.height(10.dp))
                    OscilloscopeCanvas(
                        frequencies = currentFrequencies,
                        isPlaying = isPlaying
                    )
                }
            }
        }

        // Canonical Historical Calibrations
        item {
            Column {
                Text(
                    text = "Canonical Frequency Standards",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = EchoCyan
                )
                Spacer(Modifier.height(8.dp))

                val presets = listOf(
                    Triple(0.000, "0.000 Hz", "Zero-Point DC Baseline"),
                    Triple(42.600, "42.600 Hz", "N5 Solent Deep (1x Bedrock Nominal)"),
                    Triple(42.598, "42.598 Hz", "Hilsea 1986 Exact Bedrock Measurement"),
                    Triple(85.200, "85.200 Hz", "N1 Portsmouth Master Clock (2x)"),
                    Triple(127.800, "127.800 Hz", "N7 Dorchester Chalk (3x)"),
                    Triple(170.400, "170.400 Hz", "N2 Farnworth Buried Vault (4x)"),
                    Triple(190.000, "190.000 Hz", "190.0 Hz Spectrum Ceiling")
                )

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(presets) { (freq, label, desc) ->
                        val isSelected = (Math.abs(customFrequency - freq) < 0.005)
                        FilterChip(
                            selected = isSelected,
                            onClick = {
                                customFrequency = freq
                                layeredNodeIds.clear()
                                applyAudioUpdate()
                            },
                            label = {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(label, fontWeight = FontWeight.Bold)
                                    Text(desc.take(24) + "...", fontSize = 9.sp, color = EchoMuted)
                                }
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = EchoCyan,
                                selectedLabelColor = EchoOnCyan,
                                containerColor = EchoCard,
                                labelColor = EchoText
                            ),
                            modifier = Modifier.testTag("preset_chip_${freq}")
                        )
                    }
                }
            }
        }

        // Continuous Frequency Sweep Tuner (0.0 — 190.0 Hz)
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = EchoCard),
                border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(EchoCardStroke))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Precision Frequency Tuner (0-190 Hz)",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = EchoText
                        )
                        Text(
                            text = String.format(Locale.US, "%.2f Hz", customFrequency),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = EchoCyan
                        )
                    }

                    Spacer(Modifier.height(8.dp))

                    FrequencyHeatmapSlider(
                        value = customFrequency.toFloat().coerceIn(0.0f, 190.0f),
                        onValueChange = {
                            customFrequency = it.toDouble()
                            layeredNodeIds.clear()
                            applyAudioUpdate()
                        },
                        valueRange = 0.0f..190.0f,
                        historyEntries = allHistoryList,
                        isResonanceLocked = abs(customFrequency - 42.6) <= 0.02,
                        nearestNodeName = NetworkNode.ALL_NODES.find { abs(it.frequency - customFrequency) <= 0.5 }?.name,
                        testTag = "freq_slider"
                    )

                    // Reference tick marks along slider
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("0 Hz", fontSize = 10.sp, color = EchoMuted)
                        Text("42.6 N5", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = EchoGreen)
                        Text("85.2 N1", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = EchoCyan)
                        Text("127.8 N7", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = EchoCyan)
                        Text("170.4 N2", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = EchoCyan)
                        Text("190 Hz", fontSize = 10.sp, color = EchoMuted)
                    }

                    Spacer(Modifier.height(10.dp))

                    // Quick-Access Preset Buttons for instant resonance snapping
                    Text(
                        text = "Quick-Access Node References",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = EchoMuted
                    )

                    Spacer(Modifier.height(6.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        val quickResPresets = listOf(
                            Triple(0.000, "0.0 Hz", "Zero DC"),
                            Triple(42.600, "42.6 Hz", "N5 1x"),
                            Triple(85.200, "85.2 Hz", "N1 2x"),
                            Triple(127.800, "127.8 Hz", "N7 3x"),
                            Triple(170.400, "170.4 Hz", "N2 4x"),
                            Triple(190.000, "190.0 Hz", "Ceiling")
                        )
                        quickResPresets.forEach { (presetFreq, label, sub) ->
                            val isSelected = Math.abs(customFrequency - presetFreq) < 0.05
                            Button(
                                onClick = {
                                    customFrequency = presetFreq
                                    layeredNodeIds.clear()
                                    applyAudioUpdate()
                                },
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("full_matrix_preset_${label.replace(".", "_").replace(" ", "_")}"),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (isSelected) {
                                        if (presetFreq == 42.600) EchoGreen else EchoCyan
                                    } else EchoBgAlt,
                                    contentColor = if (isSelected) {
                                        if (presetFreq == 42.600) Color.Black else EchoOnCyan
                                    } else EchoText
                                ),
                                border = if (isSelected) null else androidx.compose.foundation.BorderStroke(1.dp, EchoCardStroke),
                                shape = RoundedCornerShape(10.dp),
                                contentPadding = PaddingValues(horizontal = 2.dp, vertical = 6.dp)
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(
                                        text = label,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = sub,
                                        fontSize = 8.sp,
                                        color = if (isSelected) (if (presetFreq == 42.600) Color.Black.copy(alpha = 0.8f) else EchoOnCyan.copy(alpha = 0.8f)) else EchoMuted
                                    )
                                }
                            }
                        }
                    }

                    Spacer(Modifier.height(12.dp))

                    // Fine Tuning Buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        OutlinedButton(
                            onClick = {
                                customFrequency = (customFrequency - 0.1).coerceAtLeast(0.0)
                                applyAudioUpdate()
                            },
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                        ) {
                            Text("-0.1 Hz", fontSize = 11.sp, color = EchoText)
                        }
                        OutlinedButton(
                            onClick = {
                                customFrequency = (customFrequency - 1.0).coerceAtLeast(0.0)
                                applyAudioUpdate()
                            },
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                        ) {
                            Text("-1 Hz", fontSize = 11.sp, color = EchoText)
                        }
                        OutlinedButton(
                            onClick = {
                                customFrequency = 42.600
                                applyAudioUpdate()
                            },
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                        ) {
                            Text("Reset 42.6", fontSize = 11.sp, color = EchoCyan, fontWeight = FontWeight.Bold)
                        }
                        OutlinedButton(
                            onClick = {
                                customFrequency = (customFrequency + 1.0).coerceAtMost(190.0)
                                applyAudioUpdate()
                            },
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                        ) {
                            Text("+1 Hz", fontSize = 11.sp, color = EchoText)
                        }
                        OutlinedButton(
                            onClick = {
                                customFrequency = (customFrequency + 0.1).coerceAtMost(190.0)
                                applyAudioUpdate()
                            },
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                        ) {
                            Text("+0.1 Hz", fontSize = 11.sp, color = EchoText)
                        }
                    }

                    Spacer(Modifier.height(10.dp))
                    // Wavelength in chalk bedrock (~3,200 m/s)
                    val acousticSpeedInChalk = 3200.0 // meters / sec
                    val wavelength = acousticSpeedInChalk / customFrequency
                    Text(
                        text = String.format(Locale.US, "Estimated Bedrock Acoustic Wavelength (Chalk): %.1f meters", wavelength),
                        fontSize = 11.sp,
                        color = EchoMuted
                    )
                }
            }
        }

        // 11.74s Tidal Envelope (Southampton Rhythm) Toggle
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = EchoBgAlt),
                border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(EchoCardStroke))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "11.74-Second Tidal Rhythm (LFO)",
                            fontWeight = FontWeight.SemiBold,
                            color = EchoText,
                            fontSize = 15.sp
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(
                            text = "Discovered by Southampton oceanographers in 1991. The 42.6 Hz carrier amplitude breathes in an 11.74s standing lunar wave cycle.",
                            fontSize = 12.sp,
                            color = EchoMuted,
                            lineHeight = 16.sp
                        )
                    }
                    Spacer(Modifier.width(12.dp))
                    Switch(
                        checked = isTidalLfoOn,
                        onCheckedChange = {
                            isTidalLfoOn = it
                            applyAudioUpdate()
                        },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = EchoOnCyan,
                            checkedTrackColor = EchoCyan,
                            uncheckedTrackColor = EchoCardStroke
                        ),
                        modifier = Modifier.testTag("tidal_lfo_switch")
                    )
                }
            }
        }

        // Octave Lift Selection (Speaker compatibility)
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = EchoCard),
                border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(EchoCardStroke))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Acoustic Octave Scaling",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = EchoText
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = "Select reproduction register. Smartphone speakers cannot produce sub-50Hz without octave lifting.",
                        fontSize = 12.sp,
                        color = EchoMuted
                    )
                    Spacer(Modifier.height(10.dp))

                    val octaveOptions = listOf(
                        Pair(1.0, "Fundamental 1x (42.6 Hz Raw)"),
                        Pair(2.0, "+1 Octave 2x (85.2 Hz)"),
                        Pair(4.0, "+2 Octaves 4x (170.4 Hz)"),
                        Pair(8.0, "+3 Octaves 8x (340.8 Hz Phone Speaker)")
                    )

                    octaveOptions.forEach { (mult, label) ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .clickable {
                                    selectedOctaveMult = mult
                                    applyAudioUpdate()
                                }
                                .padding(vertical = 6.dp, horizontal = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(16.dp)
                                    .clip(CircleShape)
                                    .border(
                                        width = 2.dp,
                                        color = if (selectedOctaveMult == mult) EchoCyan else EchoMuted,
                                        shape = CircleShape
                                    )
                                    .background(if (selectedOctaveMult == mult) EchoCyan else Color.Transparent)
                            )
                            Spacer(Modifier.width(10.dp))
                            Text(
                                text = label,
                                fontSize = 13.sp,
                                color = if (selectedOctaveMult == mult) EchoText else EchoMuted,
                                fontWeight = if (selectedOctaveMult == mult) FontWeight.SemiBold else FontWeight.Normal
                            )
                        }
                    }
                }
            }
        }

        // Layer All 11 Nodes Multi-Node Harmonics
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = EchoBgAlt),
                border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(EchoCardStroke))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Harmonic Node Layering",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = EchoCyan
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = "Combine nodes across Great Britain to produce composite acoustic waveforms.",
                        fontSize = 12.sp,
                        color = EchoMuted
                    )
                    Spacer(Modifier.height(10.dp))

                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(NetworkNode.ALL_NODES) { node ->
                            val isLayered = layeredNodeIds.contains(node.id)
                            FilterChip(
                                selected = isLayered,
                                onClick = {
                                    if (isLayered) {
                                        if (layeredNodeIds.size > 1) layeredNodeIds.remove(node.id)
                                    } else {
                                        layeredNodeIds.add(node.id)
                                    }
                                    applyAudioUpdate()
                                },
                                label = { Text("${node.id} (${node.frequency.toInt()}Hz)") },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = EchoCyan,
                                    selectedLabelColor = EchoOnCyan,
                                    containerColor = EchoCard,
                                    labelColor = EchoText
                                )
                            )
                        }
                    }

                    Spacer(Modifier.height(10.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        TextButton(onClick = {
                            layeredNodeIds.clear()
                            layeredNodeIds.addAll(NetworkNode.ALL_NODES.map { it.id })
                            applyAudioUpdate()
                        }) {
                            Text("Select All 11 Nodes", color = EchoCyan, fontSize = 12.sp)
                        }
                        TextButton(onClick = {
                            layeredNodeIds.clear()
                            layeredNodeIds.add("N1")
                            customFrequency = 42.600
                            applyAudioUpdate()
                        }) {
                            Text("Reset to N1", color = EchoMuted, fontSize = 12.sp)
                        }
                    }
                }
            }
        }

        // Timbre & Volume Controls
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = EchoCard),
                border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(EchoCardStroke))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Waveform Timbre & Volume",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = EchoText
                    )
                    Spacer(Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        WaveformTimbre.values().forEach { t ->
                            val isChosen = (selectedTimbre == t)
                            Button(
                                onClick = {
                                    selectedTimbre = t
                                    applyAudioUpdate()
                                },
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (isChosen) EchoCyan else EchoBgAlt,
                                    contentColor = if (isChosen) EchoOnCyan else EchoText
                                ),
                                shape = RoundedCornerShape(8.dp),
                                contentPadding = PaddingValues(4.dp)
                            ) {
                                Text(
                                    text = when (t) {
                                        WaveformTimbre.PURE_SINE -> "Pure Sine"
                                        WaveformTimbre.BEDROCK_HARMONIC -> "Bedrock"
                                        WaveformTimbre.TECTONIC_PULSE -> "Tectonic"
                                    },
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    Spacer(Modifier.height(14.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.AutoMirrored.Filled.VolumeUp, contentDescription = "Volume", tint = EchoMuted, modifier = Modifier.size(20.dp))
                        Spacer(Modifier.width(10.dp))
                        Slider(
                            value = volumeLevel,
                            onValueChange = {
                                volumeLevel = it
                                applyAudioUpdate()
                            },
                            valueRange = 0.0f..1.0f,
                            colors = SliderDefaults.colors(
                                thumbColor = EchoCyan,
                                activeTrackColor = EchoCyan,
                                inactiveTrackColor = EchoCardStroke
                            ),
                            modifier = Modifier.weight(1f)
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(
                            text = "${(volumeLevel * 100).toInt()}%",
                            fontSize = 12.sp,
                            color = EchoText,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }
            }
        }
        2 -> {
            ResonancePowerCalculatorScreen(
                toneGenerator = toneGenerator,
                modifier = Modifier.weight(1f)
            )
        }
        3 -> {
            BinauralHarmonicsScreen(
                toneGenerator = toneGenerator,
                modifier = Modifier.weight(1f)
            )
        }
        4 -> {
            CymaticsSimulatorScreen(
                toneGenerator = toneGenerator,
                modifier = Modifier.weight(1f)
            )
        }
        5 -> {
            LiveAcousticSpectrogramScreen(
                modifier = Modifier.weight(1f)
            )
        }
        6 -> {
            KingsChamberWaveguideScreen(
                toneGenerator = toneGenerator,
                modifier = Modifier.weight(1f)
            )
        }
    }
}
}
