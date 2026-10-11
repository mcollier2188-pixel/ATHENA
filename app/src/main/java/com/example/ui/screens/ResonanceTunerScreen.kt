package com.example.ui.screens

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.Waves
import androidx.compose.material.icons.automirrored.filled.Label
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Slider
import android.content.Intent
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.BookmarkAdd
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Label
import androidx.compose.material.icons.filled.Sensors
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.TextButton
import com.example.data.FrequencyPreset
import com.example.model.NetworkNode
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import com.example.util.FrequencyCsvExporter
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.FrequencyHistoryEntry
import com.example.ui.viewmodel.FrequencyHistoryViewModel
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.audio.HarmonicToneGenerator
import com.example.audio.WaveformTimbre
import com.example.audio.HardwareAudioCalibrator
import com.example.audio.HardwareAudioProfile
import com.example.audio.CalibrationStepResult
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Devices
import androidx.compose.material.icons.filled.Headphones
import androidx.compose.material.icons.filled.VolumeUp
import com.example.ui.components.FrequencyHeatmapSlider
import com.example.ui.theme.EchoBg
import com.example.ui.theme.EchoBgAlt
import com.example.ui.theme.EchoCard
import com.example.ui.theme.EchoCardStroke
import com.example.ui.theme.EchoCyan
import com.example.ui.theme.EchoGreen
import com.example.ui.theme.EchoMuted
import com.example.ui.theme.EchoOnCyan
import com.example.ui.theme.EchoText
import java.util.Locale
import kotlin.math.abs
import kotlin.math.sin
import kotlin.math.sqrt

/**
 * Dedicated Composable screen with:
 * 1. Slider controlling frequency strictly between 42.0 Hz and 43.0 Hz
 * 2. Visual indicator for the 42.6 Hz resonance
 * 3. Real-time waveform visualizer using Canvas API reflecting the current selected frequency
 */
@Composable
fun ResonanceTunerScreen(
    toneGenerator: HarmonicToneGenerator = HarmonicToneGenerator.SHARED,
    historyViewModel: FrequencyHistoryViewModel = viewModel(),
    modifier: Modifier = Modifier
) {
    val isPlaying by toneGenerator.isPlayingState.collectAsState()
    val historyList by historyViewModel.filteredHistory.collectAsStateWithLifecycle()
    val allHistoryList by historyViewModel.allHistory.collectAsStateWithLifecycle()
    val onlyLocksFilter by historyViewModel.filterOnlyLocks.collectAsStateWithLifecycle()

    // Frequency state between 0.0 Hz and 190.0 Hz
    var frequency by remember { mutableDoubleStateOf(42.600) }
    var zoomMode by remember { mutableStateOf(false) } // false = Full 0-190 Hz Spectrum, true = Bedrock 42-43 Hz Zoom
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    // Hardware Audio Calibration State
    var showCalibrationDialog by remember { mutableStateOf(false) }
    var hardwareProfile by remember {
        mutableStateOf(
            HardwareAudioCalibrator.getSavedCalibration(context)
                ?: HardwareAudioCalibrator.detectHardwareProfile(context)
        )
    }
    var octaveMult by remember { mutableDoubleStateOf(hardwareProfile.recommendedOctaveMult) }
    var volume by remember { mutableFloatStateOf(hardwareProfile.recommendedVolume) }

    // Session tracking state
    var sessionStartTime by remember { mutableLongStateOf(0L) }
    var showHistoryDialog by remember { mutableStateOf(false) }
    var showSaveNoteDialog by remember { mutableStateOf(false) }
    var manualSessionNote by remember { mutableStateOf("") }
    var showExportDialog by remember { mutableStateOf(false) }

    // Preset labels & favorites state
    val presetList by historyViewModel.presets.collectAsStateWithLifecycle()
    var showPresetLabelsDialog by remember { mutableStateOf(false) }
    var showSaveCurrentPresetDialog by remember { mutableStateOf(false) }
    var currentPresetLabelInput by remember { mutableStateOf("") }

    val exportStatusMessage by historyViewModel.exportStatusMessage.collectAsStateWithLifecycle()
    val isExporting by historyViewModel.isExporting.collectAsStateWithLifecycle()

    val createDocumentLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("text/csv")
    ) { uri ->
        uri?.let {
            historyViewModel.exportToUri(context, it)
        }
    }

    LaunchedEffect(exportStatusMessage) {
        exportStatusMessage?.let { msg ->
            Toast.makeText(context, msg, Toast.LENGTH_LONG).show()
            historyViewModel.dismissExportMessage()
        }
    }

    // Apply saved hardware profile on startup
    LaunchedEffect(Unit) {
        HardwareAudioCalibrator.applyProfileToAudio(hardwareProfile)
    }

    // Track active playback duration and auto-log when tone generator stops
    LaunchedEffect(isPlaying) {
        if (isPlaying) {
            sessionStartTime = System.currentTimeMillis()
        } else if (sessionStartTime > 0L) {
            val durationSec = ((System.currentTimeMillis() - sessionStartTime) / 1000L).coerceAtLeast(1L)
            historyViewModel.logSession(
                frequencyHz = frequency,
                durationSeconds = durationSec,
                note = if (abs(frequency - 42.600) <= 0.018) "Resonance Lock Session (${durationSec}s)" else "Tuning Session (${durationSec}s)"
            )
            sessionStartTime = 0L
        }
    }

    // Target resonance is exactly 42.600 Hz (Bedrock Fundamental Node N5/N6/N8)
    val targetResonance = 42.600
    val deltaFromResonance by remember(frequency) {
        derivedStateOf { frequency - targetResonance }
    }
    val absDelta by remember(frequency) {
        derivedStateOf { abs(frequency - targetResonance) }
    }

    // Identify nearest network node across the 0 - 190 Hz spectrum
    val nearestNode by remember(frequency) {
        derivedStateOf {
            val validNodes = NetworkNode.ALL_NODES.filter { it.frequency <= 190.5 }
            validNodes.minByOrNull { abs(frequency - it.frequency) }
        }
    }
    val nearestNodeDistance by remember(frequency, nearestNode) {
        derivedStateOf {
            nearestNode?.let { abs(frequency - it.frequency) } ?: 999.0
        }
    }
    val isNodeLocked by remember(nearestNodeDistance) {
        derivedStateOf { nearestNodeDistance <= 0.060 }
    }

    // Lorentzian physical resonance coupling: R = 1 / sqrt(1 + Q^2 * (f/f0 - f0/f)^2)
    val resonanceQualityQ = 140.0
    val resonanceCoupling by remember(frequency) {
        derivedStateOf {
            val safeFreq = frequency.coerceAtLeast(0.001)
            val ratio = safeFreq / targetResonance
            val detuning = ratio - (1.0 / ratio)
            val denominator = 1.0 + (resonanceQualityQ * resonanceQualityQ * detuning * detuning)
            (1.0 / sqrt(denominator)).coerceIn(0.04, 1.0)
        }
    }

    val isResonanceLocked by remember(absDelta) {
        derivedStateOf { absDelta <= 0.018 }
    }

    fun syncAudio(startIfPlaying: Boolean = isPlaying) {
        toneGenerator.setOctaveMultiplier(octaveMult)
        toneGenerator.setVolumeLevel(volume)
        toneGenerator.setTimbreMode(hardwareProfile.recommendedTimbre)
        toneGenerator.setHardwareCalibrationOffset(hardwareProfile.hardwareFineOffsetHz)
        if (startIfPlaying) {
            toneGenerator.start(
                frequencies = listOf(frequency),
                mult = octaveMult,
                tidalLfo = false,
                timbreMode = hardwareProfile.recommendedTimbre
            )
        } else {
            toneGenerator.stop()
        }
    }

    val infiniteTransition = rememberInfiniteTransition(label = "res_pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.96f,
        targetValue = if (isResonanceLocked) 1.14f else 1.02f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = if (isResonanceLocked) 500 else 1600,
                easing = FastOutSlowInEasing
            ),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(EchoBg)
            .testTag("resonance_tuner_screen"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Title & Header Badge
        item {
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "BEDROCK RESONANCE TUNER",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = EchoCyan,
                            letterSpacing = 2.sp
                        )
                        Text(
                            text = "0.0 Hz — 190.0 Hz Harmonic Metre",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = EchoText
                        )
                    }

                    // Resonance Lock Status Pill
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(if (isResonanceLocked) EchoGreen.copy(alpha = 0.2f) else if (isNodeLocked) EchoCyan.copy(alpha = 0.2f) else EchoCardStroke.copy(alpha = 0.3f))
                            .border(
                                width = 1.dp,
                                color = if (isResonanceLocked) EchoGreen else if (isNodeLocked) EchoCyan else EchoCardStroke,
                                shape = RoundedCornerShape(20.dp)
                            )
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                            .testTag("resonance_lock_status_badge")
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = if (isResonanceLocked || isNodeLocked) Icons.Default.Lock else Icons.Default.LockOpen,
                                contentDescription = null,
                                tint = if (isResonanceLocked) EchoGreen else if (isNodeLocked) EchoCyan else EchoMuted,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(Modifier.width(4.dp))
                            Text(
                                text = if (isResonanceLocked) "42.6 LOCKED" else if (isNodeLocked) "${nearestNode?.id ?: "NODE"} LOCKED" else "0-190 Hz SWEEP",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isResonanceLocked) EchoGreen else if (isNodeLocked) EchoCyan else EchoMuted
                            )
                        }
                    }
                }

                Text(
                    text = "Acoustic waveguide calibration tuned to bedrock strata across the 0 — 190 Hz spectrum. Calibrated to physical network node harmonics.",
                    fontSize = 12.sp,
                    color = EchoMuted,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }

        // HARDWARE AUDIO AUTO-CALIBRATION CARD
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = EchoBgAlt),
                border = CardDefaults.outlinedCardBorder().copy(brush = Brush.horizontalGradient(listOf(EchoCyan, EchoGreen)))
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Speed,
                                contentDescription = null,
                                tint = EchoGreen,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(Modifier.width(8.dp))
                            Text(
                                text = "HARDWARE AUDIO AUTO-CALIBRATION",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = EchoCyan,
                                letterSpacing = 1.sp
                            )
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(EchoGreen.copy(alpha = 0.2f))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "${hardwareProfile.spectrumCalibrationScore}% ACCURACY",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = EchoGreen
                            )
                        }
                    }

                    Spacer(Modifier.height(8.dp))

                    Text(
                        text = "Device Transducer: ${hardwareProfile.deviceType} • Native DAC: ${hardwareProfile.nativeSampleRate} Hz (${hardwareProfile.optimalBufferSize} frames) • Low-cut: ~${hardwareProfile.estimatedLowCutoffHz.toInt()} Hz (${hardwareProfile.recommendedOctaveMult.toInt()}x Octave Shift)",
                        fontSize = 11.sp,
                        color = EchoMuted,
                        lineHeight = 16.sp
                    )

                    Spacer(Modifier.height(10.dp))

                    Button(
                        onClick = { showCalibrationDialog = true },
                        colors = ButtonDefaults.buttonColors(containerColor = EchoCyan, contentColor = EchoOnCyan),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("run_auto_calibration_button")
                    ) {
                        Icon(Icons.Default.Tune, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(6.dp))
                        Text("Auto-Calibrate 0-190 Hz Hardware", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // 1. REAL-TIME WAVEFORM VISUALIZER USING CANVAS API
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = EchoBgAlt),
                border = CardDefaults.outlinedCardBorder().copy(
                    brush = Brush.horizontalGradient(
                        if (isResonanceLocked) listOf(EchoGreen, EchoCyan, EchoGreen)
                        else listOf(EchoCardStroke, EchoCardStroke)
                    )
                )
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Waves,
                                contentDescription = null,
                                tint = if (isResonanceLocked) EchoGreen else EchoCyan,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(Modifier.width(6.dp))
                            Text(
                                text = "Real-Time Canvas Waveform Visualizer",
                                fontWeight = FontWeight.Bold,
                                color = EchoText,
                                fontSize = 14.sp
                            )
                        }

                        Text(
                            text = String.format(Locale.US, "%.3f Hz", frequency),
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            color = if (isResonanceLocked) EchoGreen else EchoCyan,
                            fontSize = 14.sp
                        )
                    }

                    Spacer(Modifier.height(10.dp))

                    // Dedicated Real-time Canvas Waveform Composable
                    RealTimeWaveformCanvas(
                        frequency = frequency,
                        resonanceCoupling = resonanceCoupling,
                        isPlaying = isPlaying,
                        isResonanceLocked = isResonanceLocked,
                        modifier = Modifier.testTag("waveform_canvas")
                    )

                    Spacer(Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Sweep: ${String.format(Locale.US, "%.1f ms/cycle", 1000.0 / frequency)}",
                            fontSize = 11.sp,
                            color = EchoMuted,
                            fontFamily = FontFamily.Monospace
                        )
                        Text(
                            text = "Coupling: ${(resonanceCoupling * 100).toInt()}% ${if (isResonanceLocked) "· MAX RESONANCE" else ""}",
                            fontSize = 11.sp,
                            color = if (isResonanceLocked) EchoGreen else EchoMuted,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }

        // 2. VISUAL INDICATOR FOR THE 42.6 HZ RESONANCE
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = EchoCard),
                border = CardDefaults.outlinedCardBorder().copy(
                    brush = if (isResonanceLocked) Brush.sweepGradient(listOf(EchoCyan, EchoGreen, EchoCyan))
                    else Brush.linearGradient(listOf(EchoCardStroke, EchoCardStroke))
                )
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "42.6 HZ HARMONIC RESONANCE INDICATOR",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = EchoCyan,
                        letterSpacing = 1.5.sp
                    )

                    Spacer(Modifier.height(14.dp))

                    // Animated Glowing Resonance Sphere Indicator
                    Box(
                        modifier = Modifier
                            .size(140.dp)
                            .scale(pulseScale)
                            .clip(CircleShape)
                            .background(
                                Brush.radialGradient(
                                    colors = listOf(
                                        if (isResonanceLocked) EchoGreen.copy(alpha = 0.45f)
                                        else EchoCyan.copy(alpha = (resonanceCoupling * 0.35f).toFloat()),
                                        Color.Transparent
                                    )
                                )
                            )
                            .border(
                                width = if (isResonanceLocked) 3.5.dp else 1.5.dp,
                                brush = if (isResonanceLocked) Brush.sweepGradient(listOf(EchoGreen, EchoCyan, EchoGreen))
                                else Brush.linearGradient(listOf(EchoCardStroke, EchoCardStroke)),
                                shape = CircleShape
                            )
                            .clickable {
                                if (isPlaying) toneGenerator.stop() else syncAudio(startIfPlaying = true)
                            }
                            .testTag("resonance_sphere_indicator"),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = String.format(Locale.US, "%.3f", frequency),
                                fontSize = 28.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isResonanceLocked) EchoGreen else EchoText,
                                fontFamily = FontFamily.Monospace
                            )
                            Text(
                                text = "Hz",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Medium,
                                color = EchoMuted
                            )
                            Text(
                                text = if (isResonanceLocked) "RESONANCE PEAK" else "TUNING NODE",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isResonanceLocked) EchoGreen else EchoCyan
                            )
                        }
                    }

                    Spacer(Modifier.height(14.dp))

                    // Resonance Energy Coupling Progress Meter
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Harmonic Standing Wave Coupling",
                                fontSize = 12.sp,
                                color = EchoMuted
                            )
                            Text(
                                text = String.format(Locale.US, "%.1f%%", resonanceCoupling * 100.0),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isResonanceLocked) EchoGreen else EchoCyan
                            )
                        }

                        LinearProgressIndicator(
                            progress = { resonanceCoupling.toFloat() },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp)
                                .clip(RoundedCornerShape(4.dp)),
                            color = if (isResonanceLocked) EchoGreen else EchoCyan,
                            trackColor = EchoBgAlt
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Nominal: 42.600 Hz",
                                fontSize = 10.sp,
                                color = EchoMuted
                            )
                            Text(
                                text = "Delta: ${String.format(Locale.US, "%+.3f Hz", deltaFromResonance)}",
                                fontSize = 10.sp,
                                fontFamily = FontFamily.Monospace,
                                color = if (absDelta < 0.05) EchoCyan else EchoMuted
                            )
                        }
                    }

                    Spacer(Modifier.height(16.dp))

                    // Audio Playback Master Button
                    Button(
                        onClick = {
                            if (isPlaying) toneGenerator.stop() else syncAudio(startIfPlaying = true)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("tuner_play_button"),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isPlaying) Color(0xFFEF4444) else EchoCyan,
                            contentColor = if (isPlaying) Color.White else EchoOnCyan
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(
                            imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                            contentDescription = null,
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(
                            text = if (isPlaying) "Stop Tone Generator" else "Generate Audio at ${String.format(Locale.US, "%.3f Hz", frequency)}",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                    }
                }
            }
        }

        // 3. FREQUENCY METRE & SLIDER: 0.0 HZ TO 190.0 HZ
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = EchoCard),
                border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(EchoCardStroke))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    // Header with Metre range and Zoom Toggle
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Tune,
                                contentDescription = null,
                                tint = EchoCyan,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(Modifier.width(6.dp))
                            Text(
                                text = if (zoomMode) "Bedrock Zoom (42.0 — 43.0 Hz)" else "Spectrum Metre (0 — 190 Hz)",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = EchoText
                            )
                        }

                        // Zoom Toggle Button
                        FilterChip(
                            selected = zoomMode,
                            onClick = { zoomMode = !zoomMode },
                            label = { Text(if (zoomMode) "0-190 Hz Mode" else "Zoom 42-43 Hz", fontSize = 11.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = EchoCyan,
                                selectedLabelColor = EchoOnCyan,
                                containerColor = EchoBgAlt,
                                labelColor = EchoCyan
                            ),
                            modifier = Modifier.testTag("toggle_zoom_mode")
                        )
                    }

                    Spacer(Modifier.height(8.dp))

                    // Metre Display Value & Node Reference Indicator
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Bottom
                    ) {
                        Column {
                            Text(
                                text = String.format(Locale.US, "%.3f Hz", frequency),
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isResonanceLocked) EchoGreen else if (isNodeLocked) EchoCyan else EchoText,
                                fontFamily = FontFamily.Monospace
                            )
                            if (nearestNode != null && isNodeLocked) {
                                Text(
                                    text = "Node Reference: ${nearestNode!!.id} ${nearestNode!!.name} (${nearestNode!!.baseRatio}x, ${nearestNode!!.setting})",
                                    fontSize = 11.sp,
                                    color = if (nearestNode!!.frequency == 42.6) EchoGreen else EchoCyan,
                                    fontWeight = FontWeight.SemiBold
                                )
                            } else {
                                Text(
                                    text = "0.0 — 190.0 Hz Transducer Sweep",
                                    fontSize = 11.sp,
                                    color = EchoMuted
                                )
                            }
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(EchoBgAlt)
                                .border(1.dp, EchoCardStroke, RoundedCornerShape(6.dp))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "${hardwareProfile.deviceType.take(16)} (${hardwareProfile.recommendedOctaveMult.toInt()}x)",
                                fontSize = 10.sp,
                                color = EchoCyan,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }

                    Spacer(Modifier.height(10.dp))

                    // The Slider with Historical Usage Heatmap Overlay (0.0 - 190.0 Hz)
                    val sliderMin = if (zoomMode) 42.0f else 0.0f
                    val sliderMax = if (zoomMode) 43.0f else 190.0f
                    FrequencyHeatmapSlider(
                        value = frequency.toFloat().coerceIn(sliderMin, sliderMax),
                        onValueChange = { newFreq ->
                            frequency = newFreq.toDouble()
                            syncAudio()
                        },
                        valueRange = sliderMin..sliderMax,
                        historyEntries = allHistoryList,
                        isResonanceLocked = isResonanceLocked,
                        nearestNodeName = nearestNode?.name,
                        testTag = if (zoomMode) "freq_slider_42_to_43" else "freq_slider_0_to_190"
                    )

                    // Reference Node markers along metre
                    if (!zoomMode) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("0 Hz", fontSize = 10.sp, color = EchoMuted)
                            Text("42.6 N5", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = if (isResonanceLocked) EchoGreen else EchoCyan)
                            Text("85.2 N1", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = EchoCyan)
                            Text("127.8 N7", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = EchoCyan)
                            Text("170.4 N2", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = EchoCyan)
                            Text("190 Hz", fontSize = 10.sp, color = EchoMuted)
                        }
                    } else {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("42.0 Hz (Min)", fontSize = 11.sp, color = EchoMuted)
                            Text(
                                "▼ 42.600 Hz (Resonance)",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isResonanceLocked) EchoGreen else EchoCyan
                            )
                            Text("43.0 Hz (Max)", fontSize = 11.sp, color = EchoMuted)
                        }
                    }

                    Spacer(Modifier.height(14.dp))

                    // Quick-Access Node References Row
                    Text(
                        text = "Network Node Harmonic References (0 - 190 Hz)",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = EchoMuted
                    )

                    Spacer(Modifier.height(8.dp))

                    val nodeReferences = listOf(
                        Triple(0.000, "0.0 Hz", "Zero DC"),
                        Triple(42.600, "42.6 Hz", "N5 Solent (1x)"),
                        Triple(85.200, "85.2 Hz", "N1 Master (2x)"),
                        Triple(127.800, "127.8 Hz", "N7 Chalk (3x)"),
                        Triple(170.400, "170.4 Hz", "N2 Vault (4x)"),
                        Triple(190.000, "190.0 Hz", "Ceiling")
                    )

                    LazyRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(nodeReferences) { (presetFreq, label, tag) ->
                            val isSelected = abs(frequency - presetFreq) < 0.05
                            Button(
                                onClick = {
                                    frequency = presetFreq
                                    syncAudio()
                                },
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
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                                modifier = Modifier.testTag("node_snap_${presetFreq.toInt()}")
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(text = label, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                    Text(
                                        text = tag,
                                        fontSize = 9.sp,
                                        color = if (isSelected) {
                                            if (presetFreq == 42.600) Color.Black.copy(alpha = 0.8f) else EchoOnCyan.copy(alpha = 0.8f)
                                        } else EchoMuted
                                    )
                                }
                            }
                        }
                    }

                    Spacer(Modifier.height(16.dp))

                    Text(
                        text = "Precision Step Adjustments",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = EchoMuted
                    )

                    Spacer(Modifier.height(8.dp))

                    // Fine Tuning Buttons
                    val minBound = if (zoomMode) 42.000 else 0.000
                    val maxBound = if (zoomMode) 43.000 else 190.000
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        OutlinedButton(
                            onClick = {
                                frequency = (frequency - 1.000).coerceAtLeast(minBound)
                                syncAudio()
                            },
                            contentPadding = PaddingValues(horizontal = 6.dp, vertical = 2.dp),
                            modifier = Modifier.testTag("step_minus_1_0")
                        ) {
                            Text("-1.0", fontSize = 11.sp, color = EchoText)
                        }
                        OutlinedButton(
                            onClick = {
                                frequency = (frequency - 0.100).coerceAtLeast(minBound)
                                syncAudio()
                            },
                            contentPadding = PaddingValues(horizontal = 6.dp, vertical = 2.dp),
                            modifier = Modifier.testTag("step_minus_0_1")
                        ) {
                            Text("-0.1", fontSize = 11.sp, color = EchoText)
                        }
                        Button(
                            onClick = {
                                frequency = 42.600
                                syncAudio()
                            },
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = EchoCyan,
                                contentColor = EchoOnCyan
                            ),
                            modifier = Modifier.testTag("snap_resonance_button")
                        ) {
                            Icon(Icons.Default.RestartAlt, contentDescription = null, modifier = Modifier.size(14.dp))
                            Spacer(Modifier.width(4.dp))
                            Text("Snap 42.6", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                        OutlinedButton(
                            onClick = {
                                frequency = (frequency + 0.100).coerceAtMost(maxBound)
                                syncAudio()
                            },
                            contentPadding = PaddingValues(horizontal = 6.dp, vertical = 2.dp),
                            modifier = Modifier.testTag("step_plus_0_1")
                        ) {
                            Text("+0.1", fontSize = 11.sp, color = EchoText)
                        }
                        OutlinedButton(
                            onClick = {
                                frequency = (frequency + 1.000).coerceAtMost(maxBound)
                                syncAudio()
                            },
                            contentPadding = PaddingValues(horizontal = 6.dp, vertical = 2.dp),
                            modifier = Modifier.testTag("step_plus_1_0")
                        ) {
                            Text("+1.0", fontSize = 11.sp, color = EchoText)
                        }
                    }

                    Spacer(Modifier.height(14.dp))

                    Spacer(Modifier.height(14.dp))

                    // Header for Favorite Resonance Frequency Presets
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.Label,
                                contentDescription = null,
                                tint = EchoCyan,
                                modifier = Modifier.size(15.dp)
                            )
                            Spacer(Modifier.width(5.dp))
                            Text(
                                text = "FAVORITE PRESETS",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = EchoCyan,
                                letterSpacing = 0.5.sp
                            )
                        }

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            TextButton(
                                onClick = {
                                    currentPresetLabelInput = "Resonance " + String.format(Locale.US, "%.3f", frequency)
                                    showSaveCurrentPresetDialog = true
                                },
                                contentPadding = PaddingValues(horizontal = 6.dp, vertical = 2.dp),
                                modifier = Modifier.testTag("save_current_preset_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.BookmarkAdd,
                                    contentDescription = null,
                                    modifier = Modifier.size(14.dp),
                                    tint = EchoCyan
                                )
                                Spacer(Modifier.width(4.dp))
                                Text("Save Current", fontSize = 11.sp, color = EchoCyan)
                            }

                            TextButton(
                                onClick = { showPresetLabelsDialog = true },
                                contentPadding = PaddingValues(horizontal = 6.dp, vertical = 2.dp),
                                modifier = Modifier.testTag("edit_preset_labels_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Edit,
                                    contentDescription = null,
                                    modifier = Modifier.size(14.dp),
                                    tint = EchoText
                                )
                                Spacer(Modifier.width(4.dp))
                                Text("Edit Labels", fontSize = 11.sp, color = EchoText)
                            }
                        }
                    }

                    Spacer(Modifier.height(6.dp))

                    // Preset Calibrations from Room with User-Defined Labels
                    if (presetList.size <= 4) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            presetList.forEach { preset ->
                                val isSelected = abs(frequency - preset.frequencyHz) < 0.004
                                FilterChip(
                                    selected = isSelected,
                                    onClick = {
                                        frequency = preset.frequencyHz
                                        syncAudio()
                                    },
                                    label = {
                                        Column(
                                            horizontalAlignment = Alignment.CenterHorizontally,
                                            modifier = Modifier.padding(vertical = 2.dp)
                                        ) {
                                            Text(
                                                text = preset.label,
                                                fontSize = 11.sp,
                                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                                maxLines = 1,
                                                overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                                            )
                                            Text(
                                                text = String.format(Locale.US, "%.3f Hz", preset.frequencyHz),
                                                fontSize = 9.sp,
                                                color = if (isSelected) EchoOnCyan.copy(alpha = 0.85f) else EchoMuted
                                            )
                                        }
                                    },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = if (abs(preset.frequencyHz - 42.600) < 0.004) EchoGreen else EchoCyan,
                                        selectedLabelColor = EchoOnCyan,
                                        containerColor = EchoBgAlt,
                                        labelColor = EchoText
                                    ),
                                    modifier = Modifier
                                        .weight(1f)
                                        .testTag("preset_chip_${preset.id}")
                                )
                            }
                        }
                    } else {
                        LazyRow(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            items(presetList, key = { it.id }) { preset ->
                                val isSelected = abs(frequency - preset.frequencyHz) < 0.004
                                FilterChip(
                                    selected = isSelected,
                                    onClick = {
                                        frequency = preset.frequencyHz
                                        syncAudio()
                                    },
                                    label = {
                                        Column(
                                            horizontalAlignment = Alignment.CenterHorizontally,
                                            modifier = Modifier.padding(vertical = 2.dp)
                                        ) {
                                            Text(
                                                text = preset.label,
                                                fontSize = 11.sp,
                                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                                maxLines = 1,
                                                overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                                            )
                                            Text(
                                                text = String.format(Locale.US, "%.3f Hz", preset.frequencyHz),
                                                fontSize = 9.sp,
                                                color = if (isSelected) EchoOnCyan.copy(alpha = 0.85f) else EchoMuted
                                            )
                                        }
                                    },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = if (abs(preset.frequencyHz - 42.600) < 0.004) EchoGreen else EchoCyan,
                                        selectedLabelColor = EchoOnCyan,
                                        containerColor = EchoBgAlt,
                                        labelColor = EchoText
                                    ),
                                    modifier = Modifier.testTag("preset_chip_${preset.id}")
                                )
                            }
                        }
                    }
                }
            }
        }

        // Speaker Octave Multiplier Note
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = EchoBgAlt),
                border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(EchoCardStroke))
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "Acoustic Transducer Octave Scaling",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = EchoText
                    )
                    Text(
                        text = "Mobile smartphone speakers physically cut off frequencies below 100 Hz. Octave multiplication shifts the tone into audible harmonics while maintaining the exact phase relation.",
                        fontSize = 11.sp,
                        color = EchoMuted,
                        modifier = Modifier.padding(top = 2.dp, bottom = 8.dp)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf(
                            Pair(1.0, "1x (42.6 Hz Sub)"),
                            Pair(2.0, "2x (85.2 Hz)"),
                            Pair(4.0, "4x (170.4 Hz)"),
                            Pair(8.0, "8x (340.8 Hz Mobile)")
                        ).forEach { (mult, label) ->
                            FilterChip(
                                selected = octaveMult == mult,
                                onClick = {
                                    octaveMult = mult
                                    syncAudio()
                                },
                                label = { Text(label, fontSize = 11.sp) },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }
        }

        // 5. LOCAL ROOM DATABASE: FREQUENCY USAGE HISTORY & RESONANCE SESSIONS TRACKER
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("frequency_history_card"),
                colors = CardDefaults.cardColors(containerColor = EchoCard),
                border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(EchoCardStroke))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.History,
                                contentDescription = null,
                                tint = EchoCyan,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(Modifier.width(8.dp))
                            Text(
                                text = "Resonance Session History",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = EchoText
                            )
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            FilterChip(
                                selected = onlyLocksFilter,
                                onClick = { historyViewModel.toggleFilterOnlyLocks() },
                                label = {
                                    Text(
                                        text = if (onlyLocksFilter) "Locks Only" else "All Logs",
                                        fontSize = 10.sp
                                    )
                                },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = EchoGreen.copy(alpha = 0.3f),
                                    selectedLabelColor = EchoGreen,
                                    containerColor = EchoBgAlt,
                                    labelColor = EchoMuted
                                ),
                                modifier = Modifier.testTag("filter_locks_chip")
                            )

                            Spacer(Modifier.width(6.dp))

                            IconButton(
                                onClick = { showSaveNoteDialog = true },
                                modifier = Modifier
                                    .size(32.dp)
                                    .testTag("log_current_frequency_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.BookmarkAdd,
                                    contentDescription = "Log Current Frequency",
                                    tint = EchoCyan,
                                    modifier = Modifier.size(20.dp)
                                )
                            }

                            Spacer(Modifier.width(4.dp))

                            IconButton(
                                onClick = { showExportDialog = true },
                                modifier = Modifier
                                    .size(32.dp)
                                    .testTag("export_csv_header_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.FileDownload,
                                    contentDescription = "Export History to CSV",
                                    tint = EchoCyan,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }

                    Text(
                        text = "Tracks frequency (Hz), timestamps, and resonance lock status stored in local Room SQLite database.",
                        fontSize = 11.sp,
                        color = EchoMuted,
                        modifier = Modifier.padding(top = 4.dp, bottom = 12.dp)
                    )

                    if (historyList.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(EchoBgAlt)
                                .padding(16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "No frequency sessions recorded yet. Start the tone generator or tap '+' to log current ${String.format(Locale.US, "%.3f Hz", frequency)}.",
                                fontSize = 12.sp,
                                color = EchoMuted,
                                textAlign = TextAlign.Center
                            )
                        }
                    } else {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            historyList.take(6).forEach { entry ->
                                val dateStr = remember(entry.timestamp) {
                                    val sdf = java.text.SimpleDateFormat("MMM dd, HH:mm:ss", Locale.getDefault())
                                    sdf.format(java.util.Date(entry.timestamp))
                                }
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(if (entry.isResonanceLock) EchoGreen.copy(alpha = 0.08f) else EchoBgAlt)
                                        .border(
                                            width = 1.dp,
                                            color = if (entry.isResonanceLock) EchoGreen.copy(alpha = 0.3f) else EchoCardStroke.copy(alpha = 0.5f),
                                            shape = RoundedCornerShape(8.dp)
                                        )
                                        .padding(horizontal = 12.dp, vertical = 8.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text(
                                                text = String.format(Locale.US, "%.3f Hz", entry.frequencyHz),
                                                fontSize = 15.sp,
                                                fontWeight = FontWeight.Bold,
                                                fontFamily = FontFamily.Monospace,
                                                color = if (entry.isResonanceLock) EchoGreen else EchoCyan
                                            )
                                            if (entry.isResonanceLock) {
                                                Spacer(Modifier.width(6.dp))
                                                Box(
                                                    modifier = Modifier
                                                        .clip(RoundedCornerShape(4.dp))
                                                        .background(EchoGreen.copy(alpha = 0.2f))
                                                        .padding(horizontal = 5.dp, vertical = 2.dp)
                                                ) {
                                                    Text(
                                                        text = "RESONANCE LOCK",
                                                        fontSize = 9.sp,
                                                        fontWeight = FontWeight.Bold,
                                                        color = EchoGreen
                                                    )
                                                }
                                            }
                                            if (entry.durationSeconds > 0) {
                                                Spacer(Modifier.width(6.dp))
                                                Text(
                                                    text = "${entry.durationSeconds}s duration",
                                                    fontSize = 10.sp,
                                                    color = EchoMuted
                                                )
                                            }
                                        }

                                        if (!entry.sessionNote.isNullOrBlank()) {
                                            Text(
                                                text = entry.sessionNote,
                                                fontSize = 11.sp,
                                                color = EchoText,
                                                modifier = Modifier.padding(top = 2.dp)
                                            )
                                        }

                                        Text(
                                            text = dateStr,
                                            fontSize = 10.sp,
                                            color = EchoMuted,
                                            modifier = Modifier.padding(top = 2.dp)
                                        )
                                    }

                                    IconButton(
                                        onClick = { historyViewModel.deleteEntry(entry) },
                                        modifier = Modifier.size(28.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Delete,
                                            contentDescription = "Delete entry",
                                            tint = EchoMuted,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                }
                            }
                        }

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 10.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            OutlinedButton(
                                onClick = { showExportDialog = true },
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("export_csv_card_footer_button"),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = EchoCyan),
                                border = androidx.compose.foundation.BorderStroke(1.dp, EchoCyan.copy(alpha = 0.5f))
                            ) {
                                Icon(
                                    imageVector = Icons.Default.FileDownload,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(Modifier.width(6.dp))
                                Text(
                                    text = "Export CSV",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }

                            if (historyList.size > 6) {
                                TextButton(
                                    onClick = { showHistoryDialog = true },
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text(
                                        text = "View All (${historyList.size})",
                                        fontSize = 12.sp,
                                        color = EchoCyan,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Modal Dialog: Add Manual Session Note / Log
    if (showSaveNoteDialog) {
        AlertDialog(
            onDismissRequest = { showSaveNoteDialog = false },
            title = {
                Text(
                    text = "Log Resonance Session",
                    fontWeight = FontWeight.Bold,
                    color = EchoText
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "Store current frequency ${String.format(Locale.US, "%.3f Hz", frequency)} into local Room database with optional notes.",
                        fontSize = 12.sp,
                        color = EchoMuted
                    )
                    OutlinedTextField(
                        value = manualSessionNote,
                        onValueChange = { manualSessionNote = it },
                        label = { Text("Session Note (e.g. Standing wave peak)") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("session_note_input"),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = EchoCyan,
                            unfocusedBorderColor = EchoCardStroke,
                            focusedTextColor = EchoText,
                            unfocusedTextColor = EchoText
                        )
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val note = manualSessionNote.ifBlank {
                            if (isResonanceLocked) "42.6 Hz Bedrock Resonance Manual Bookmark" else "Manual Tuning Point"
                        }
                        historyViewModel.logSession(
                            frequencyHz = frequency,
                            durationSeconds = 0,
                            note = note
                        )
                        manualSessionNote = ""
                        showSaveNoteDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = EchoCyan, contentColor = EchoOnCyan),
                    modifier = Modifier.testTag("confirm_save_session_button")
                ) {
                    Text("Save to Room DB")
                }
            },
            dismissButton = {
                TextButton(onClick = { showSaveNoteDialog = false }) {
                    Text("Cancel", color = EchoMuted)
                }
            },
            containerColor = EchoCard
        )
    }

    // Modal Dialog: View Full History
    if (showHistoryDialog) {
        AlertDialog(
            onDismissRequest = { showHistoryDialog = false },
            title = {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "All Resonance Sessions",
                        fontWeight = FontWeight.Bold,
                        color = EchoText
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(
                            onClick = {
                                showHistoryDialog = false
                                showExportDialog = true
                            },
                            modifier = Modifier
                                .size(32.dp)
                                .testTag("dialog_export_csv_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.FileDownload,
                                contentDescription = "Export CSV",
                                tint = EchoCyan,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        IconButton(
                            onClick = { historyViewModel.clearAllHistory() },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Clear All History",
                                tint = Color(0xFFEF4444)
                            )
                        }
                    }
                }
            },
            text = {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(380.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(historyList.size) { index ->
                        val entry = historyList[index]
                        val dateStr = remember(entry.timestamp) {
                            val sdf = java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                            sdf.format(java.util.Date(entry.timestamp))
                        }
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = EchoBgAlt)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(10.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = String.format(Locale.US, "%.3f Hz", entry.frequencyHz),
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold,
                                        fontFamily = FontFamily.Monospace,
                                        color = if (entry.isResonanceLock) EchoGreen else EchoCyan
                                    )
                                    if (!entry.sessionNote.isNullOrBlank()) {
                                        Text(text = entry.sessionNote, fontSize = 11.sp, color = EchoText)
                                    }
                                    Text(text = dateStr, fontSize = 9.sp, color = EchoMuted)
                                }
                                IconButton(
                                    onClick = { historyViewModel.deleteEntry(entry) },
                                    modifier = Modifier.size(26.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Delete,
                                        contentDescription = "Delete",
                                        tint = EchoMuted,
                                        modifier = Modifier.size(15.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedButton(
                        onClick = {
                            showHistoryDialog = false
                            showExportDialog = true
                        },
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = EchoCyan),
                        border = androidx.compose.foundation.BorderStroke(1.dp, EchoCyan.copy(alpha = 0.5f))
                    ) {
                        Icon(
                            imageVector = Icons.Default.FileDownload,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(Modifier.width(4.dp))
                        Text("Export CSV", fontSize = 12.sp)
                    }
                    TextButton(onClick = { showHistoryDialog = false }) {
                        Text("Close", color = EchoCyan)
                    }
                }
            },
            containerColor = EchoCard
        )
    }

    // Modal Dialog: Export CSV for Analysis
    if (showExportDialog) {
        AlertDialog(
            onDismissRequest = { showExportDialog = false },
            title = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.FileDownload,
                        contentDescription = null,
                        tint = EchoCyan,
                        modifier = Modifier.size(24.dp)
                    )
                    Text(
                        text = "Export Frequency History (CSV)",
                        fontWeight = FontWeight.Bold,
                        color = EchoText,
                        fontSize = 17.sp
                    )
                }
            },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "Export tracked resonance sessions from the local Room database as an RFC 4180 CSV spreadsheet file for scientific and frequency analysis.",
                        fontSize = 12.sp,
                        color = EchoMuted
                    )

                    // Session stats overview
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = EchoBgAlt)
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Total Records:", fontSize = 11.sp, color = EchoMuted)
                                Text(
                                    text = "${historyList.size} sessions",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = EchoText
                                )
                            }
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Resonance Locks (42.6 Hz):", fontSize = 11.sp, color = EchoMuted)
                                Text(
                                    text = "${historyList.count { it.isResonanceLock }} locks",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = EchoGreen
                                )
                            }
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("CSV Format:", fontSize = 11.sp, color = EchoMuted)
                                Text(
                                    text = "RFC 4180 (UTF-8)",
                                    fontSize = 11.sp,
                                    fontFamily = FontFamily.Monospace,
                                    color = EchoCyan
                                )
                            }
                        }
                    }

                    // CSV Preview
                    Column {
                        Text(
                            text = "CSV Preview:",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = EchoText
                        )
                        Spacer(Modifier.height(4.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(6.dp))
                                .background(EchoBg)
                                .border(1.dp, EchoCardStroke, RoundedCornerShape(6.dp))
                                .padding(8.dp)
                        ) {
                            val previewText = remember(historyList) {
                                val header = "id,frequency_hz,timestamp_epoch_ms,datetime_utc,duration_seconds,is_resonance_lock,session_note"
                                val sampleLines = historyList.take(2).map { entry ->
                                    val dateStr = java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US).format(java.util.Date(entry.timestamp))
                                    val noteEscaped = (entry.sessionNote ?: "").replace("\"", "\"\"")
                                    "${entry.id},${String.format(Locale.US, "%.3f", entry.frequencyHz)},${entry.timestamp},$dateStr,${entry.durationSeconds},${entry.isResonanceLock},\"$noteEscaped\""
                                }
                                (listOf(header) + sampleLines).joinToString("\n")
                            }
                            Text(
                                text = previewText,
                                fontSize = 9.sp,
                                fontFamily = FontFamily.Monospace,
                                color = EchoMuted,
                                maxLines = 4
                            )
                        }
                    }

                    // Action buttons
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = {
                                val timestamp = java.text.SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(java.util.Date())
                                createDocumentLauncher.launch("resonance_frequency_history_$timestamp.csv")
                                showExportDialog = false
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("save_csv_device_button"),
                            colors = ButtonDefaults.buttonColors(containerColor = EchoCyan, contentColor = EchoOnCyan)
                        ) {
                            Icon(
                                imageVector = Icons.Default.FileDownload,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(Modifier.width(8.dp))
                            Text("Save CSV File to Device / Storage")
                        }

                        OutlinedButton(
                            onClick = {
                                historyViewModel.shareCsv(
                                    context = context,
                                    onReady = { intent ->
                                        context.startActivity(Intent.createChooser(intent, "Share Frequency History CSV"))
                                        showExportDialog = false
                                    },
                                    onError = { err ->
                                        Toast.makeText(context, err, Toast.LENGTH_SHORT).show()
                                    }
                                )
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("share_csv_button"),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = EchoCyan),
                            border = androidx.compose.foundation.BorderStroke(1.dp, EchoCyan.copy(alpha = 0.6f))
                        ) {
                            Icon(
                                imageVector = Icons.Default.Share,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(Modifier.width(8.dp))
                            Text("Share CSV via Apps (Drive, Email, etc.)")
                        }

                        OutlinedButton(
                            onClick = {
                                historyViewModel.copyCsvToClipboard(context) { count ->
                                    Toast.makeText(context, "Copied $count records in CSV format to clipboard!", Toast.LENGTH_SHORT).show()
                                    showExportDialog = false
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("copy_csv_clipboard_button"),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = EchoText),
                            border = androidx.compose.foundation.BorderStroke(1.dp, EchoCardStroke)
                        ) {
                            Icon(
                                imageVector = Icons.Default.ContentCopy,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(Modifier.width(8.dp))
                            Text("Copy CSV to Clipboard")
                        }
                    }
                }
            },
            confirmButton = {},
            dismissButton = {
                TextButton(onClick = { showExportDialog = false }) {
                    Text("Close", color = EchoMuted)
                }
            },
            containerColor = EchoCard
        )
    }

    // -----------------------------------------------------------------
    // DIALOG: PRESET LABELS & FAVORITE FREQUENCIES MANAGER
    // -----------------------------------------------------------------
    if (showPresetLabelsDialog) {
        var newPresetFreqText by remember { mutableStateOf("42.800") }
        var newPresetLabelText by remember { mutableStateOf("") }
        var showAddNewSection by remember { mutableStateOf(false) }

        AlertDialog(
            onDismissRequest = { showPresetLabelsDialog = false },
            title = {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.AutoMirrored.Filled.Label, contentDescription = null, tint = EchoCyan, modifier = Modifier.size(20.dp))
                        Spacer(Modifier.width(8.dp))
                        Text("Resonance Preset Labels", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = EchoText)
                    }
                    IconButton(
                        onClick = { showAddNewSection = !showAddNewSection },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = if (showAddNewSection) Icons.Default.Close else Icons.Default.Add,
                            contentDescription = "Add Preset",
                            tint = EchoCyan
                        )
                    }
                }
            },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "Name your favorite resonance frequencies in the UI. Tapping a preset snaps the synthesizer and audio engine to that resonance node.",
                        fontSize = 12.sp,
                        color = EchoMuted
                    )

                    // Optional Add New Preset Section
                    if (showAddNewSection) {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = EchoBgAlt),
                            border = androidx.compose.foundation.BorderStroke(1.dp, EchoCyan.copy(alpha = 0.5f))
                        ) {
                            Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                Text("Add Custom Frequency", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = EchoCyan)
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    OutlinedTextField(
                                        value = newPresetFreqText,
                                        onValueChange = { newPresetFreqText = it },
                                        label = { Text("Hz (0.0 - 190.0)", fontSize = 10.sp) },
                                        singleLine = true,
                                        modifier = Modifier.weight(1f),
                                        colors = OutlinedTextFieldDefaults.colors(
                                            focusedBorderColor = EchoCyan,
                                            unfocusedBorderColor = EchoCardStroke,
                                            focusedTextColor = EchoText,
                                            unfocusedTextColor = EchoText
                                        )
                                    )
                                    OutlinedTextField(
                                        value = newPresetLabelText,
                                        onValueChange = { newPresetLabelText = it },
                                        label = { Text("Label / Name", fontSize = 10.sp) },
                                        singleLine = true,
                                        modifier = Modifier.weight(1.4f),
                                        colors = OutlinedTextFieldDefaults.colors(
                                            focusedBorderColor = EchoCyan,
                                            unfocusedBorderColor = EchoCardStroke,
                                            focusedTextColor = EchoText,
                                            unfocusedTextColor = EchoText
                                        )
                                    )
                                }
                                Button(
                                    onClick = {
                                        val freqParsed = newPresetFreqText.toDoubleOrNull()?.coerceIn(0.0, 190.0) ?: 42.6
                                        val lbl = newPresetLabelText.trim().ifEmpty { String.format(Locale.US, "%.3f Hz", freqParsed) }
                                        historyViewModel.addCustomPreset(freqParsed, lbl)
                                        newPresetLabelText = ""
                                        showAddNewSection = false
                                        Toast.makeText(context, "Added preset: $lbl", Toast.LENGTH_SHORT).show()
                                    },
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = ButtonDefaults.buttonColors(containerColor = EchoCyan, contentColor = EchoOnCyan)
                                ) {
                                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(Modifier.width(6.dp))
                                    Text("Save New Preset", fontSize = 12.sp)
                                }
                            }
                        }
                    }

                    // Existing Presets List with In-Place Editable Name Fields
                    Text("ACTIVE PRESETS (${presetList.size}):", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = EchoCyan)

                    presetList.forEach { preset ->
                        var labelValue by remember(preset.id, preset.label) { mutableStateOf(preset.label) }
                        val isDirty = labelValue != preset.label

                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = EchoBgAlt),
                            border = androidx.compose.foundation.BorderStroke(1.dp, if (abs(frequency - preset.frequencyHz) < 0.004) EchoCyan else EchoCardStroke)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                // Frequency Badge
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(if (abs(preset.frequencyHz - 42.600) < 0.004) EchoGreen.copy(alpha = 0.2f) else EchoCyan.copy(alpha = 0.15f))
                                        .padding(horizontal = 8.dp, vertical = 6.dp)
                                ) {
                                    Text(
                                        text = String.format(Locale.US, "%.3f", preset.frequencyHz),
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (abs(preset.frequencyHz - 42.600) < 0.004) EchoGreen else EchoCyan,
                                        fontFamily = FontFamily.Monospace
                                    )
                                }

                                // Editable Label Input
                                OutlinedTextField(
                                    value = labelValue,
                                    onValueChange = { labelValue = it },
                                    singleLine = true,
                                    label = { Text("Preset Label", fontSize = 9.sp) },
                                    modifier = Modifier.weight(1f),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = EchoCyan,
                                        unfocusedBorderColor = EchoCardStroke,
                                        focusedTextColor = EchoText,
                                        unfocusedTextColor = EchoText
                                    )
                                )

                                // Save / Update button if modified
                                if (isDirty) {
                                    IconButton(
                                        onClick = {
                                            if (labelValue.isNotBlank()) {
                                                historyViewModel.updatePresetLabel(preset.id, labelValue)
                                                Toast.makeText(context, "Renamed to '$labelValue'", Toast.LENGTH_SHORT).show()
                                            }
                                        },
                                        modifier = Modifier.size(32.dp)
                                    ) {
                                        Icon(Icons.Default.Check, contentDescription = "Save Name", tint = EchoGreen)
                                    }
                                }

                                // Delete option if not default
                                if (!preset.isDefault) {
                                    IconButton(
                                        onClick = {
                                            historyViewModel.deletePreset(preset.id)
                                            Toast.makeText(context, "Removed preset", Toast.LENGTH_SHORT).show()
                                        },
                                        modifier = Modifier.size(30.dp)
                                    ) {
                                        Icon(Icons.Default.Delete, contentDescription = "Delete", tint = EchoMuted)
                                    }
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(
                    onClick = { showPresetLabelsDialog = false },
                    colors = ButtonDefaults.textButtonColors(contentColor = EchoCyan)
                ) {
                    Text("Done")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        historyViewModel.resetPresetsToDefault()
                        Toast.makeText(context, "Reset presets to standard calibrations", Toast.LENGTH_SHORT).show()
                    },
                    colors = ButtonDefaults.textButtonColors(contentColor = EchoMuted)
                ) {
                    Text("Reset Defaults")
                }
            },
            containerColor = EchoCard
        )
    }

    // -----------------------------------------------------------------
    // DIALOG: SAVE CURRENT FREQUENCY AS NAMED PRESET
    // -----------------------------------------------------------------
    if (showSaveCurrentPresetDialog) {
        AlertDialog(
            onDismissRequest = { showSaveCurrentPresetDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.BookmarkAdd, contentDescription = null, tint = EchoCyan)
                    Spacer(Modifier.width(8.dp))
                    Text("Save Favorite Resonance", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = EchoText)
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        text = "Save current frequency (${String.format(Locale.US, "%.3f Hz", frequency)}) to your quick presets bar with a custom label.",
                        fontSize = 12.sp,
                        color = EchoMuted
                    )

                    OutlinedTextField(
                        value = currentPresetLabelInput,
                        onValueChange = { currentPresetLabelInput = it },
                        label = { Text("Preset Name / Label") },
                        placeholder = { Text("e.g. Bedrock Alignment, Tesla Peak") },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("save_preset_name_input"),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = EchoCyan,
                            unfocusedBorderColor = EchoCardStroke,
                            focusedTextColor = EchoText,
                            unfocusedTextColor = EchoText
                        )
                    )

                    // Quick name suggestion tags
                    Text("Suggestions:", fontSize = 10.sp, color = EchoMuted)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        listOf("Bedrock Lock", "Hilsea Chalk", "Tesla Terrestrial", "Deep Wave").forEach { suggestion ->
                            FilterChip(
                                selected = currentPresetLabelInput == suggestion,
                                onClick = { currentPresetLabelInput = suggestion },
                                label = { Text(suggestion, fontSize = 10.sp) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = EchoCyan,
                                    selectedLabelColor = EchoOnCyan,
                                    containerColor = EchoBgAlt,
                                    labelColor = EchoText
                                )
                            )
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val labelToSave = currentPresetLabelInput.trim().ifEmpty {
                            String.format(Locale.US, "%.3f Hz", frequency)
                        }
                        historyViewModel.addCustomPreset(frequency, labelToSave)
                        Toast.makeText(context, "Saved preset '$labelToSave'", Toast.LENGTH_SHORT).show()
                        showSaveCurrentPresetDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = EchoCyan, contentColor = EchoOnCyan),
                    modifier = Modifier.testTag("confirm_save_preset_button")
                ) {
                    Text("Save Preset")
                }
            },
            dismissButton = {
                TextButton(onClick = { showSaveCurrentPresetDialog = false }) {
                    Text("Cancel", color = EchoMuted)
                }
            },
            containerColor = EchoCard
        )
    }

    // -----------------------------------------------------------------
    // DIALOG: HARDWARE AUDIO AUTO-CALIBRATION (0 - 190 HZ)
    // -----------------------------------------------------------------
    if (showCalibrationDialog) {
        HardwareAudioCalibrationDialog(
            initialProfile = hardwareProfile,
            onApplyProfile = { newProfile ->
                hardwareProfile = newProfile
                octaveMult = newProfile.recommendedOctaveMult
                volume = newProfile.recommendedVolume
                HardwareAudioCalibrator.applyProfileToAudio(newProfile)
                HardwareAudioCalibrator.saveCalibration(context, newProfile)
                Toast.makeText(context, "Hardware audio calibrated (${newProfile.spectrumCalibrationScore}% accuracy)", Toast.LENGTH_SHORT).show()
                showCalibrationDialog = false
            },
            onDismiss = { showCalibrationDialog = false }
        )
    }
}

/**
 * Real-Time Waveform Visualizer using Compose Canvas API.
 * The waveform path dynamically evaluates sine / standing wave functions based on the current frequency selected on the slider.
 * When the frequency approaches the 42.6 Hz resonance, the amplitude and harmonic glow visibly amplify.
 */
@Composable
fun RealTimeWaveformCanvas(
    frequency: Double,
    resonanceCoupling: Double,
    isPlaying: Boolean,
    isResonanceLocked: Boolean,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "canvas_wave")
    val phaseAnim by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 2f * Math.PI.toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1400, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "phase"
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(2.2f)
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFF070E17))
            .border(1.dp, EchoCardStroke, RoundedCornerShape(12.dp))
    ) {
        Canvas(modifier = Modifier.matchParentSize()) {
            val w = size.width
            val h = size.height
            val centerY = h / 2f

            // 1. Oscilloscope CRT Background Grid
            val gridXCols = 10
            val gridYRows = 6
            val xStep = w / gridXCols
            val yStep = h / gridYRows

            for (i in 0..gridXCols) {
                drawLine(
                    color = EchoCardStroke.copy(alpha = if (i == gridXCols / 2) 0.5f else 0.2f),
                    start = Offset(i * xStep, 0f),
                    end = Offset(i * xStep, h),
                    strokeWidth = if (i == gridXCols / 2) 1.2f else 0.8f
                )
            }
            for (j in 0..gridYRows) {
                drawLine(
                    color = EchoCardStroke.copy(alpha = if (j == gridYRows / 2) 0.6f else 0.2f),
                    start = Offset(0f, j * yStep),
                    end = Offset(w, j * yStep),
                    strokeWidth = if (j == gridYRows / 2) 1.5f else 0.8f
                )
            }

            // 2. Wave calculation: dynamically reflects the current frequency selected on the slider!
            // Number of wavelengths displayed scales across 0.0 - 190.0 Hz spectrum
            val visibleCycles = (1.5 + (frequency.coerceIn(0.0, 190.0) / 190.0) * 4.5).coerceIn(1.0, 6.0)
            val numPoints = 200

            // Amplitude blooms to maximum when resonance coupling is 1.0 (at 42.6 Hz)
            val maxAmplitude = (h / 2f - 16f)
            val dynamicAmplitude = maxAmplitude * (0.45f + (resonanceCoupling.toFloat() * 0.55f))

            val wavePath = Path()
            val standingEnvelopeTop = Path()
            val standingEnvelopeBottom = Path()

            for (step in 0..numPoints) {
                val fraction = step.toFloat() / numPoints
                val px = fraction * w

                // Phase calculation: dynamic frequency drives wave velocity and period
                val spatialAngle = (fraction * visibleCycles * 2.0 * Math.PI)
                val timeAngle = (phaseAnim * (frequency.coerceAtLeast(0.01) / 42.6)).toDouble()

                // Composite harmonic wave: primary carrier plus bedrock standing node
                val waveVal = sin(spatialAngle - timeAngle)
                val py = centerY - (waveVal.toFloat() * dynamicAmplitude)

                // Standing wave envelope
                val envVal = abs(sin(spatialAngle)).toFloat() * dynamicAmplitude
                val envTop = centerY - envVal
                val envBottom = centerY + envVal

                if (step == 0) {
                    wavePath.moveTo(px, py)
                    standingEnvelopeTop.moveTo(px, envTop)
                    standingEnvelopeBottom.moveTo(px, envBottom)
                } else {
                    wavePath.lineTo(px, py)
                    standingEnvelopeTop.lineTo(px, envTop)
                    standingEnvelopeBottom.lineTo(px, envBottom)
                }
            }

            // 3. Draw standing wave faint envelope if resonance is approaching
            if (resonanceCoupling > 0.4) {
                val envelopeAlpha = (resonanceCoupling * 0.35f).toFloat()
                drawPath(
                    path = standingEnvelopeTop,
                    color = EchoCyan.copy(alpha = envelopeAlpha),
                    style = Stroke(width = 1f)
                )
                drawPath(
                    path = standingEnvelopeBottom,
                    color = EchoCyan.copy(alpha = envelopeAlpha),
                    style = Stroke(width = 1f)
                )
            }

            // 4. Draw primary dynamic waveform with gradient
            val waveColorStart = if (isResonanceLocked) EchoGreen else EchoCyan
            val waveColorCenter = if (isResonanceLocked) Color(0xFF6EE7B7) else EchoGreen
            val waveColorEnd = if (isResonanceLocked) EchoGreen else EchoCyan

            drawPath(
                path = wavePath,
                brush = Brush.horizontalGradient(
                    colors = listOf(waveColorStart, waveColorCenter, waveColorEnd)
                ),
                style = Stroke(width = if (isResonanceLocked) 3.8f else 2.5f)
            )

            // 5. Resonance Node Markers at standing wave crests
            if (isResonanceLocked) {
                val numNodes = 4
                for (n in 1..numNodes) {
                    val nx = (n.toFloat() / (numNodes + 1)) * w
                    drawCircle(
                        color = EchoGreen.copy(alpha = 0.8f),
                        radius = 4f,
                        center = Offset(nx, centerY)
                    )
                }
            }
        }

        // Overlay Annotations
        Row(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(6.dp)
                    .clip(CircleShape)
                    .background(if (isPlaying) EchoGreen else EchoCyan)
            )
            Spacer(Modifier.width(6.dp))
            Text(
                text = "CH1 · ${(frequency).let { String.format(Locale.US, "%.3f Hz", it) }}",
                fontSize = 10.sp,
                fontFamily = FontFamily.Monospace,
                color = if (isResonanceLocked) EchoGreen else EchoCyan,
                fontWeight = FontWeight.Bold
            )
        }

        Text(
            text = if (isResonanceLocked) "RESONANCE LOCK: Q=140" else "Δf: ${String.format(Locale.US, "%+.3f", frequency - 42.600)} Hz",
            fontSize = 10.sp,
            fontFamily = FontFamily.Monospace,
            color = if (isResonanceLocked) EchoGreen else EchoMuted,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(8.dp)
        )
    }
}

/**
 * Interactive Hardware Audio Auto-Calibration Dialog.
 * Tests physical audio hardware capabilities across the 0 - 190 Hz spectrum.
 */
@Composable
fun HardwareAudioCalibrationDialog(
    initialProfile: HardwareAudioProfile,
    onApplyProfile: (HardwareAudioProfile) -> Unit,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    var isCalibrating by remember { mutableStateOf(false) }
    var calibrationProgress by remember { mutableFloatStateOf(0f) }
    var currentStep by remember { mutableStateOf<CalibrationStepResult?>(null) }
    var calibratedProfile by remember { mutableStateOf(initialProfile) }
    var sweepCompleted by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = { if (!isCalibrating) onDismiss() },
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Speed, contentDescription = null, tint = EchoGreen)
                Spacer(Modifier.width(8.dp))
                Text("Hardware Audio Auto-Calibration", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = EchoText)
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Calibrate the 0 — 190 Hz resonance metre against your physical device audio output and speaker acoustic roll-off.",
                    fontSize = 12.sp,
                    color = EchoMuted
                )

                // Transducer Detection Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = EchoBgAlt),
                    border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(EchoCardStroke))
                ) {
                    Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("PHYSICAL TRANSDUCER", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = EchoCyan)
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(EchoGreen.copy(alpha = 0.2f))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = if (calibratedProfile.isHeadphoneOrDac) "DIRECT" else "8x SHIFT",
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = EchoGreen
                                )
                            }
                        }
                        Text(
                            text = calibratedProfile.deviceType,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = EchoText
                        )
                        Text(
                            text = "Native Sample Rate: ${calibratedProfile.nativeSampleRate} Hz • Buffer: ${calibratedProfile.optimalBufferSize} frames",
                            fontSize = 11.sp,
                            color = EchoMuted
                        )
                    }
                }

                // Live Calibration Sweep Progress / Status
                if (isCalibrating) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = EchoCard),
                        border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(EchoCyan))
                    ) {
                        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Acoustic Sweep In Progress...", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = EchoCyan)
                                Text("${(calibrationProgress * 100).toInt()}%", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = EchoGreen)
                            }

                            LinearProgressIndicator(
                                progress = { calibrationProgress },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(6.dp)
                                    .clip(RoundedCornerShape(3.dp)),
                                color = EchoGreen,
                                trackColor = EchoCardStroke
                            )

                            currentStep?.let { step ->
                                Spacer(Modifier.height(4.dp))
                                Text(
                                    text = "Step ${step.stepIndex}/6: ${step.testFrequencyHz} Hz (${step.nodeReference})",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = EchoText
                                )
                                Text(
                                    text = step.statusText,
                                    fontSize = 10.sp,
                                    color = EchoMuted
                                )
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("Transducer Response:", fontSize = 10.sp, color = EchoMuted)
                                    Text("${(step.transducerResponse * 100).toInt()}% coupling", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = EchoGreen)
                                }
                            }
                        }
                    }
                } else if (sweepCompleted) {
                    // Completed Results Card
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = EchoBgAlt),
                        border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(EchoGreen))
                    ) {
                        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("CALIBRATION COMPLETE", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = EchoGreen)
                                Text("${calibratedProfile.spectrumCalibrationScore}% SCORE", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = EchoGreen)
                            }
                            Text(
                                text = "• Low-end acoustic cutoff: ~${calibratedProfile.estimatedLowCutoffHz.toInt()} Hz\n" +
                                        "• Optimal octave synthesis: ${calibratedProfile.recommendedOctaveMult.toInt()}x Multiplier\n" +
                                        "• Timbre profile: ${calibratedProfile.recommendedTimbre.name.replace("_", " ")}\n" +
                                        "• Calibrated master gain: ${(calibratedProfile.recommendedVolume * 100).toInt()}%",
                                fontSize = 11.sp,
                                color = EchoText,
                                lineHeight = 16.sp
                            )
                        }
                    }
                }

                if (!isCalibrating) {
                    Button(
                        onClick = {
                            isCalibrating = true
                            sweepCompleted = false
                            coroutineScope.launch {
                                val resultProfile = HardwareAudioCalibrator.runAutoCalibration(context) { step, progress ->
                                    currentStep = step
                                    calibrationProgress = progress
                                }
                                calibratedProfile = resultProfile
                                isCalibrating = false
                                sweepCompleted = true
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = EchoCyan, contentColor = EchoOnCyan),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("start_calibration_sweep_button")
                    ) {
                        Icon(Icons.Default.Tune, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(6.dp))
                        Text(if (sweepCompleted) "Re-Run 0-190 Hz Sweep" else "Run 0-190 Hz Sweep", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        },
        confirmButton = {
            if (sweepCompleted && !isCalibrating) {
                Button(
                    onClick = {
                        onApplyProfile(calibratedProfile)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = EchoGreen, contentColor = Color.Black),
                    modifier = Modifier.testTag("apply_calibration_profile_button")
                ) {
                    Text("Apply & Save Profile", fontWeight = FontWeight.Bold)
                }
            }
        },
        dismissButton = {
            if (!isCalibrating) {
                TextButton(onClick = onDismiss) {
                    Text(if (sweepCompleted) "Close" else "Cancel", color = EchoMuted)
                }
            }
        },
        containerColor = EchoCard
    )
}
