package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Sensors
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
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.audio.HarmonicToneGenerator
import com.example.ui.theme.EchoAmber
import com.example.ui.theme.EchoBg
import com.example.ui.theme.EchoBgAlt
import com.example.ui.theme.EchoCard
import com.example.ui.theme.EchoCardStroke
import com.example.ui.theme.EchoCyan
import com.example.ui.theme.EchoGreen
import com.example.ui.theme.EchoMuted
import com.example.ui.theme.EchoOnCyan
import com.example.ui.theme.EchoRed
import com.example.ui.theme.EchoText
import java.util.Locale
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.exp
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt

/**
 * Parameter preset model for the 42.6 Hz Resonance and Power Calculator.
 */
data class ResonanceCalculationPreset(
    val name: String,
    val description: String,
    val frequencyHz: Double,
    val velocityMs: Double,
    val cavityLengthKm: Double,
    val qualityFactorQ: Double,
    val transducerEfficiency: Double,
    val acousticImpedanceMRayl: Double,
    val inputPowerWatts: Double
)

/**
 * Dedicated Interactive Screen for 42.6 Hz Resonance and Acoustic/Transducer Power Calculation.
 * Allows users to input values, customize parameters, observe real-time Standing Wave & Power Budgets,
 * and test audio synthesis directly.
 */
@Composable
fun ResonancePowerCalculatorScreen(
    toneGenerator: HarmonicToneGenerator = HarmonicToneGenerator.SHARED,
    onSaveToWorkspaceNote: (String) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val isPlayingAudio by toneGenerator.isPlayingState.collectAsState()

    // 1. Core Acoustic & Cavity Parameters
    var frequencyInput by rememberSaveable { mutableStateOf("42.600") }
    var strataVelocityInput by rememberSaveable { mutableStateOf("3400.0") }
    var cavityLengthKmInput by rememberSaveable { mutableStateOf("39.906") }
    var harmonicNumberInput by rememberSaveable { mutableStateOf("1") }

    // 2. Transducer & Power Parameters
    var inputPowerWattsInput by rememberSaveable { mutableStateOf("250.0") }
    var qualityFactorQInput by rememberSaveable { mutableStateOf("140.0") }
    var transducerEfficiencyInput by rememberSaveable { mutableStateOf("82.0") } // in %
    var acousticImpedanceMRaylInput by rememberSaveable { mutableStateOf("7.20") } // MRayl (MegaRayls)
    var tidalModulationDepthInput by rememberSaveable { mutableStateOf("35.0") } // in %
    var isTidalCouplingEnabled by rememberSaveable { mutableStateOf(true) }

    // Preset selection
    val presets = remember {
        listOf(
            ResonanceCalculationPreset(
                name = "Bedrock 42.6 Hz (Standard)",
                description = "Hilsea - Solent chalk limestone bedrock standing wave",
                frequencyHz = 42.600,
                velocityMs = 3400.0,
                cavityLengthKm = 39.906,
                qualityFactorQ = 140.0,
                transducerEfficiency = 82.0,
                acousticImpedanceMRayl = 7.20,
                inputPowerWatts = 250.0
            ),
            ResonanceCalculationPreset(
                name = "1986 Hilsea Detection",
                description = "Empirical 42.598 Hz Hilsea deep fault resonance",
                frequencyHz = 42.598,
                velocityMs = 3399.8,
                cavityLengthKm = 39.905,
                qualityFactorQ = 145.0,
                transducerEfficiency = 85.0,
                acousticImpedanceMRayl = 7.15,
                inputPowerWatts = 300.0
            ),
            ResonanceCalculationPreset(
                name = "N1 Master Clock (85.2 Hz)",
                description = "2nd Harmonic node generator at Portsmouth Harbor",
                frequencyHz = 85.200,
                velocityMs = 3400.0,
                cavityLengthKm = 39.906,
                qualityFactorQ = 160.0,
                transducerEfficiency = 88.0,
                acousticImpedanceMRayl = 7.20,
                inputPowerWatts = 500.0
            ),
            ResonanceCalculationPreset(
                name = "Granite High-Q Vault",
                description = "High velocity dense bedrock strata (v=5000 m/s)",
                frequencyHz = 42.600,
                velocityMs = 5000.0,
                cavityLengthKm = 58.685,
                qualityFactorQ = 220.0,
                transducerEfficiency = 78.0,
                acousticImpedanceMRayl = 14.50,
                inputPowerWatts = 450.0
            ),
            ResonanceCalculationPreset(
                name = "Low Power Field Node",
                description = "Solar-powered coastal telemetry sensor node",
                frequencyHz = 42.600,
                velocityMs = 3400.0,
                cavityLengthKm = 39.906,
                qualityFactorQ = 120.0,
                transducerEfficiency = 90.0,
                acousticImpedanceMRayl = 6.80,
                inputPowerWatts = 45.0
            )
        )
    }

    fun applyPreset(preset: ResonanceCalculationPreset) {
        frequencyInput = String.format(Locale.US, "%.3f", preset.frequencyHz)
        strataVelocityInput = String.format(Locale.US, "%.1f", preset.velocityMs)
        cavityLengthKmInput = String.format(Locale.US, "%.3f", preset.cavityLengthKm)
        qualityFactorQInput = String.format(Locale.US, "%.1f", preset.qualityFactorQ)
        transducerEfficiencyInput = String.format(Locale.US, "%.1f", preset.transducerEfficiency)
        acousticImpedanceMRaylInput = String.format(Locale.US, "%.2f", preset.acousticImpedanceMRayl)
        inputPowerWattsInput = String.format(Locale.US, "%.1f", preset.inputPowerWatts)
    }

    // Parsing inputs safely with fallbacks
    val freqVal = frequencyInput.toDoubleOrNull() ?: 42.600
    val velocityVal = strataVelocityInput.toDoubleOrNull() ?: 3400.0
    val cavityLengthKmVal = cavityLengthKmInput.toDoubleOrNull() ?: 39.906
    val harmonicN = harmonicNumberInput.toIntOrNull()?.coerceIn(1, 12) ?: 1
    val powerInputW = inputPowerWattsInput.toDoubleOrNull() ?: 250.0
    val qualityFactorQ = qualityFactorQInput.toDoubleOrNull() ?: 140.0
    val efficiencyPercent = transducerEfficiencyInput.toDoubleOrNull() ?: 82.0
    val impedanceMRayl = acousticImpedanceMRaylInput.toDoubleOrNull() ?: 7.20
    val tidalDepthPercent = tidalModulationDepthInput.toDoubleOrNull() ?: 35.0

    // Calculations:
    // 1. Acoustic Wavelength λ = v / f
    val wavelengthMeters = if (freqVal > 0.0) velocityVal / freqVal else 0.0

    // 2. Theoretical Resonant Frequency from Cavity Length: f_calc = (n * v) / (2 * L)
    val cavityLengthMeters = cavityLengthKmVal * 1000.0
    val theoreticalResonanceHz = if (cavityLengthMeters > 0.0) {
        (harmonicN * velocityVal) / (2.0 * cavityLengthMeters)
    } else 0.0

    // 3. Resonant Frequency Delta from 42.600 Hz nominal
    val deltaFrom426 = freqVal - 42.600
    val isNearBedrockResonance = abs(deltaFrom426) < 0.25

    // 4. Acoustic Transducer Output Power: P_ac = P_in * efficiency
    val acousticOutputPowerW = powerInputW * (efficiencyPercent / 100.0).coerceIn(0.0, 1.0)
    val thermalLossW = (powerInputW - acousticOutputPowerW).coerceAtLeast(0.0)

    // 5. Standing Wave Peak Stored Power & Energy Density in Resonator:
    // Stored Energy E_stored = (Q * P_ac) / (2 * π * f)
    val storedEnergyJoules = if (freqVal > 0.0) {
        (qualityFactorQ * acousticOutputPowerW) / (2.0 * PI * freqVal)
    } else 0.0

    // Circulating Peak Reactive Power: P_circ = Q * P_ac
    val circulatingPeakPowerW = qualityFactorQ * acousticOutputPowerW

    // Acoustic Intensity I = P / A (assuming 1 m² effective waveguide cross-section)
    // Sound Pressure Level in Bedrock: SPL = 20 * log10(p / p0) where p = sqrt(I * Z)
    val zRayls = impedanceMRayl * 1_000_000.0 // kg/(m²·s)
    val acousticIntensityWattsPerM2 = circulatingPeakPowerW / 12.56 // dispersed over 4π
    val peakPressurePa = sqrt(acousticIntensityWattsPerM2 * zRayls)

    // Tidal Envelope Period (11.74s) Modulation Peak & Trough:
    val tidalMultiplier = if (isTidalCouplingEnabled) (tidalDepthPercent / 100.0) else 0.0
    val peakTidalPowerW = circulatingPeakPowerW * (1.0 + tidalMultiplier)
    val minTidalPowerW = circulatingPeakPowerW * (1.0 - tidalMultiplier)

    // Animated Standing Wave Visualizer phase
    val infiniteTransition = rememberInfiniteTransition(label = "wave_anim")
    val wavePhase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = (2 * PI).toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "phase"
    )

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(EchoBg)
            .testTag("resonance_power_calculator_screen"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header Section
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = EchoCard),
                border = CardDefaults.outlinedCardBorder().copy(
                    brush = Brush.horizontalGradient(listOf(EchoCyan, EchoGreen))
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(EchoCyan),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Calculate,
                                    contentDescription = null,
                                    tint = EchoOnCyan,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "42.6 Hz RESONANCE & POWER",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = EchoText,
                                    letterSpacing = 1.sp
                                )
                                Text(
                                    text = "Standing Wave Synthesis & Transducer Budget",
                                    fontSize = 11.sp,
                                    color = EchoCyan
                                )
                            }
                        }

                        // Audio Tone Generator Hook
                        IconButton(
                            onClick = {
                                if (isPlayingAudio) {
                                    toneGenerator.stop()
                                } else {
                                    toneGenerator.start(listOf(freqVal.coerceIn(0.1, 190.0)))
                                }
                            },
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(if (isPlayingAudio) EchoGreen.copy(alpha = 0.2f) else EchoBgAlt)
                                .border(1.dp, if (isPlayingAudio) EchoGreen else EchoCardStroke, CircleShape)
                                .testTag("calculator_audio_toggle_button")
                        ) {
                            Icon(
                                imageVector = if (isPlayingAudio) Icons.Default.Pause else Icons.Default.PlayArrow,
                                contentDescription = if (isPlayingAudio) "Stop Tone" else "Play Frequency",
                                tint = if (isPlayingAudio) EchoGreen else EchoCyan,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }

                    Spacer(Modifier.height(10.dp))

                    Text(
                        text = "Compute subterranean standing wave parameters, acoustic cavity sizing, reactive Q-amplification, and transducer electrical power budgets across the 0–190 Hz spectrum.",
                        fontSize = 12.sp,
                        color = EchoMuted,
                        lineHeight = 17.sp
                    )
                }
            }
        }

        // Preset Quick-Selectors
        item {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = "Acoustic Archetype Presets:",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = EchoText
                )
                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(presets) { preset ->
                        FilterChip(
                            selected = abs(freqVal - preset.frequencyHz) < 0.005 && abs(velocityVal - preset.velocityMs) < 1.0,
                            onClick = { applyPreset(preset) },
                            label = { Text(preset.name, fontSize = 11.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = EchoCyan,
                                selectedLabelColor = EchoOnCyan,
                                containerColor = EchoCard,
                                labelColor = EchoText
                            ),
                            shape = RoundedCornerShape(12.dp)
                        )
                    }
                }
            }
        }

        // Live Calculated Metric Cards
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Metric 1: Circulating Peak Power (Q Amplified)
                Card(
                    modifier = Modifier.weight(1f),
                    colors = CardDefaults.cardColors(containerColor = EchoBgAlt),
                    border = CardDefaults.outlinedCardBorder().copy(
                        brush = androidx.compose.ui.graphics.SolidColor(if (isNearBedrockResonance) EchoGreen else EchoCyan)
                    )
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Bolt, contentDescription = null, tint = EchoGreen, modifier = Modifier.size(16.dp))
                            Spacer(Modifier.width(4.dp))
                            Text("Peak Reactive Power", fontSize = 11.sp, color = EchoMuted)
                        }
                        Spacer(Modifier.height(4.dp))
                        Text(
                            text = if (circulatingPeakPowerW >= 1000.0) {
                                String.format(Locale.US, "%.2f kW", circulatingPeakPowerW / 1000.0)
                            } else {
                                String.format(Locale.US, "%.1f W", circulatingPeakPowerW)
                            },
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = EchoGreen,
                            fontFamily = FontFamily.Monospace
                        )
                        Text(
                            text = "Q factor: ${String.format(Locale.US, "%.0f", qualityFactorQ)}× amplification",
                            fontSize = 10.sp,
                            color = EchoMuted
                        )
                    }
                }

                // Metric 2: Acoustic Wavelength λ
                Card(
                    modifier = Modifier.weight(1f),
                    colors = CardDefaults.cardColors(containerColor = EchoBgAlt),
                    border = CardDefaults.outlinedCardBorder().copy(
                        brush = androidx.compose.ui.graphics.SolidColor(EchoCyan)
                    )
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Waves, contentDescription = null, tint = EchoCyan, modifier = Modifier.size(16.dp))
                            Spacer(Modifier.width(4.dp))
                            Text("Strata Wavelength (λ)", fontSize = 11.sp, color = EchoMuted)
                        }
                        Spacer(Modifier.height(4.dp))
                        Text(
                            text = String.format(Locale.US, "%.2f m", wavelengthMeters),
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = EchoCyan,
                            fontFamily = FontFamily.Monospace
                        )
                        Text(
                            text = "λ/2 = ${String.format(Locale.US, "%.2f m", wavelengthMeters / 2.0)} node",
                            fontSize = 10.sp,
                            color = EchoMuted
                        )
                    }
                }
            }
        }

        // Live Standing Wave Canvas
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = EchoBgAlt),
                border = CardDefaults.outlinedCardBorder().copy(
                    brush = androidx.compose.ui.graphics.SolidColor(EchoCardStroke)
                )
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Subterranean Standing Waveform Simulation",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = EchoText
                        )
                        Text(
                            text = if (isNearBedrockResonance) "● 42.6 Hz LOCK" else "Off-Resonance",
                            fontSize = 10.sp,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            color = if (isNearBedrockResonance) EchoGreen else EchoAmber
                        )
                    }

                    Spacer(Modifier.height(8.dp))

                    Canvas(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(90.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(EchoCard)
                    ) {
                        val w = size.width
                        val h = size.height
                        val midY = h / 2f

                        // Draw baseline
                        drawLine(
                            color = EchoCardStroke,
                            start = Offset(0f, midY),
                            end = Offset(w, midY),
                            strokeWidth = 1f
                        )

                        // Standing wave equation: y(x, t) = 2*A*sin(kx)*cos(omega*t)
                        val points = 120
                        val path = Path()
                        val envelopeUpper = Path()
                        val envelopeLower = Path()

                        val k = harmonicN * PI / w
                        val amp = (h * 0.38f) * (if (isNearBedrockResonance) 1.0f else 0.45f)
                        val timeMod = kotlin.math.cos(wavePhase.toDouble()).toFloat()

                        for (i in 0..points) {
                            val x = i * (w / points)
                            val spaceMod = sin(k * x).toFloat()
                            val y = midY - (amp * spaceMod * timeMod)
                            val yEnvUp = midY - (amp * abs(spaceMod))
                            val yEnvDown = midY + (amp * abs(spaceMod))

                            if (i == 0) {
                                path.moveTo(x, y)
                                envelopeUpper.moveTo(x, yEnvUp)
                                envelopeLower.moveTo(x, yEnvDown)
                            } else {
                                path.lineTo(x, y)
                                envelopeUpper.lineTo(x, yEnvUp)
                                envelopeLower.lineTo(x, yEnvDown)
                            }
                        }

                        // Draw envelopes
                        drawPath(envelopeUpper, color = EchoCyan.copy(alpha = 0.25f), style = Stroke(width = 1f))
                        drawPath(envelopeLower, color = EchoCyan.copy(alpha = 0.25f), style = Stroke(width = 1f))

                        // Draw active standing wave
                        drawPath(
                            path,
                            color = if (isNearBedrockResonance) EchoGreen else EchoCyan,
                            style = Stroke(width = 2.5f)
                        )
                    }

                    Spacer(Modifier.height(6.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Node 0 (Bedrock Anchor)", fontSize = 9.sp, color = EchoMuted)
                        Text("Center Anti-Node", fontSize = 9.sp, color = EchoCyan)
                        Text("Node L (Coastal Reflection)", fontSize = 9.sp, color = EchoMuted)
                    }
                }
            }
        }

        // Section 1: User Inputs for Acoustic & Cavity Parameters
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = EchoCard),
                border = CardDefaults.outlinedCardBorder().copy(
                    brush = androidx.compose.ui.graphics.SolidColor(EchoCardStroke)
                )
            ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Tune, contentDescription = null, tint = EchoCyan, modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(6.dp))
                        Text(
                            text = "1. Acoustic Cavity & Frequency Inputs",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = EchoText
                        )
                    }

                    // Frequency Input Field & Quick Presets
                    OutlinedTextField(
                        value = frequencyInput,
                        onValueChange = { frequencyInput = it },
                        label = { Text("Frequency f (Hz)") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("calculator_freq_input"),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        trailingIcon = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("Hz", color = EchoCyan, fontSize = 12.sp, modifier = Modifier.padding(end = 8.dp))
                                IconButton(onClick = { frequencyInput = "42.600" }) {
                                    Icon(Icons.Default.Refresh, contentDescription = "Reset to 42.6 Hz", tint = EchoMuted, modifier = Modifier.size(16.dp))
                                }
                            }
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = EchoCyan,
                            unfocusedBorderColor = EchoCardStroke,
                            focusedTextColor = EchoText,
                            unfocusedTextColor = EchoText
                        )
                    )

                    // Acoustic Velocity (v)
                    OutlinedTextField(
                        value = strataVelocityInput,
                        onValueChange = { strataVelocityInput = it },
                        label = { Text("Acoustic Strata Velocity v (m/s)") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("calculator_velocity_input"),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        trailingIcon = { Text("m/s", color = EchoMuted, fontSize = 12.sp, modifier = Modifier.padding(end = 12.dp)) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = EchoCyan,
                            unfocusedBorderColor = EchoCardStroke,
                            focusedTextColor = EchoText,
                            unfocusedTextColor = EchoText
                        )
                    )

                    // Waveguide Cavity Length (L)
                    OutlinedTextField(
                        value = cavityLengthKmInput,
                        onValueChange = { cavityLengthKmInput = it },
                        label = { Text("Cavity / Waveguide Length L (km)") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("calculator_length_input"),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        trailingIcon = { Text("km", color = EchoMuted, fontSize = 12.sp, modifier = Modifier.padding(end = 12.dp)) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = EchoCyan,
                            unfocusedBorderColor = EchoCardStroke,
                            focusedTextColor = EchoText,
                            unfocusedTextColor = EchoText
                        )
                    )

                    // Harmonic Order (n)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Harmonic Mode (n): $harmonicN",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = EchoText
                        )
                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            listOf(1 to "Fundamental", 2 to "2nd (85.2)", 4 to "4th (170.4)").forEach { (n, lbl) ->
                                FilterChip(
                                    selected = harmonicN == n,
                                    onClick = { harmonicNumberInput = n.toString() },
                                    label = { Text("n=$n", fontSize = 10.sp) },
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

                    // Theoretical Resonant Solution Box
                    Surface(
                        color = EchoBgAlt,
                        shape = RoundedCornerShape(8.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, EchoCardStroke),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Text(
                                text = "Formula: f_res = (n · v) / (2 · L)",
                                fontSize = 11.sp,
                                fontFamily = FontFamily.Monospace,
                                color = EchoCyan
                            )
                            Spacer(Modifier.height(2.dp))
                            Text(
                                text = "Solved Cavity Natural Resonance: ${String.format(Locale.US, "%.3f Hz", theoreticalResonanceHz)}",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (abs(theoreticalResonanceHz - 42.600) < 0.2) EchoGreen else EchoText
                            )
                            Text(
                                text = "Deviation from current input: ${String.format(Locale.US, "%+.3f Hz", theoreticalResonanceHz - freqVal)}",
                                fontSize = 11.sp,
                                color = EchoMuted
                            )
                        }
                    }
                }
            }
        }

        // Section 2: User Inputs for Transducer & Power Budget
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = EchoCard),
                border = CardDefaults.outlinedCardBorder().copy(
                    brush = androidx.compose.ui.graphics.SolidColor(EchoCardStroke)
                )
            ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Bolt, contentDescription = null, tint = EchoGreen, modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(6.dp))
                        Text(
                            text = "2. Transducer & Power Parameters",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = EchoText
                        )
                    }

                    // Input Electrical Power
                    OutlinedTextField(
                        value = inputPowerWattsInput,
                        onValueChange = { inputPowerWattsInput = it },
                        label = { Text("Transducer Electrical Power Input P_in (Watts)") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("calculator_power_input"),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        trailingIcon = { Text("W", color = EchoGreen, fontSize = 12.sp, modifier = Modifier.padding(end = 12.dp)) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = EchoGreen,
                            unfocusedBorderColor = EchoCardStroke,
                            focusedTextColor = EchoText,
                            unfocusedTextColor = EchoText
                        )
                    )

                    // Resonator Q Factor
                    OutlinedTextField(
                        value = qualityFactorQInput,
                        onValueChange = { qualityFactorQInput = it },
                        label = { Text("Cavity Mechanical Q Factor") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("calculator_q_input"),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        trailingIcon = { Text("Q", color = EchoCyan, fontSize = 12.sp, modifier = Modifier.padding(end = 12.dp)) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = EchoCyan,
                            unfocusedBorderColor = EchoCardStroke,
                            focusedTextColor = EchoText,
                            unfocusedTextColor = EchoText
                        )
                    )

                    // Transducer Electro-Acoustic Efficiency
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Transducer Efficiency (η): ${String.format(Locale.US, "%.1f%%", efficiencyPercent)}",
                            fontSize = 12.sp,
                            color = EchoText
                        )
                        Text(
                            text = "Acoustic Output: ${String.format(Locale.US, "%.1f W", acousticOutputPowerW)}",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = EchoGreen
                        )
                    }
                    Slider(
                        value = efficiencyPercent.toFloat(),
                        onValueChange = { transducerEfficiencyInput = String.format(Locale.US, "%.1f", it) },
                        valueRange = 30f..98f,
                        colors = SliderDefaults.colors(thumbColor = EchoGreen, activeTrackColor = EchoGreen)
                    )

                    // Acoustic Strata Impedance
                    OutlinedTextField(
                        value = acousticImpedanceMRaylInput,
                        onValueChange = { acousticImpedanceMRaylInput = it },
                        label = { Text("Bedrock Acoustic Impedance Z (MRayl)") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("calculator_impedance_input"),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        trailingIcon = { Text("MRayl", color = EchoMuted, fontSize = 11.sp, modifier = Modifier.padding(end = 12.dp)) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = EchoCyan,
                            unfocusedBorderColor = EchoCardStroke,
                            focusedTextColor = EchoText,
                            unfocusedTextColor = EchoText
                        )
                    )

                    // Tidal Solent Coupling Toggle (11.74s LFO)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("11.74s Solent Tidal Envelope Coupling", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = EchoText)
                            Text("Modulates standing wave amplitude by tide height", fontSize = 10.sp, color = EchoMuted)
                        }
                        Switch(
                            checked = isTidalCouplingEnabled,
                            onCheckedChange = { isTidalCouplingEnabled = it },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = EchoGreen,
                                checkedTrackColor = EchoGreen.copy(alpha = 0.3f),
                                uncheckedThumbColor = EchoCyan,
                                uncheckedTrackColor = EchoCardStroke
                            )
                        )
                    }
                }
            }
        }

        // Section 3: Detailed Power Breakdown & Thermal Budget
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = EchoBgAlt),
                border = CardDefaults.outlinedCardBorder().copy(
                    brush = androidx.compose.ui.graphics.SolidColor(EchoCardStroke)
                )
            ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "Calculated Power & Energy Dynamics",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = EchoText
                    )

                    // Stored Energy in Bedrock Cavity
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Stored Resonator Energy (E_stored):", fontSize = 11.sp, color = EchoMuted)
                        Text(
                            text = String.format(Locale.US, "%.2f Joules", storedEnergyJoules),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = EchoCyan,
                            fontFamily = FontFamily.Monospace
                        )
                    }

                    // Peak Acoustic Pressure
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Peak Strata Acoustic Pressure:", fontSize = 11.sp, color = EchoMuted)
                        Text(
                            text = if (peakPressurePa >= 1000.0) {
                                String.format(Locale.US, "%.1f kPa", peakPressurePa / 1000.0)
                            } else {
                                String.format(Locale.US, "%.1f Pa", peakPressurePa)
                            },
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = EchoGreen,
                            fontFamily = FontFamily.Monospace
                        )
                    }

                    // Thermal Dissipation Loss
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Transducer Heat Dissipation:", fontSize = 11.sp, color = EchoMuted)
                        Text(
                            text = String.format(Locale.US, "%.1f W (%.1f%%)", thermalLossW, 100.0 - efficiencyPercent),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = EchoAmber,
                            fontFamily = FontFamily.Monospace
                        )
                    }

                    // Tidal Peak to Trough Envelope Range
                    if (isTidalCouplingEnabled) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("11.74s Tidal Power Range (Trough → Peak):", fontSize = 11.sp, color = EchoMuted)
                            Text(
                                text = "${String.format(Locale.US, "%.0f W", minTidalPowerW)} → ${String.format(Locale.US, "%.0f W", peakTidalPowerW)}",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = EchoCyan,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }

                    HorizontalDivider(color = EchoCardStroke, thickness = 1.dp, modifier = Modifier.padding(vertical = 4.dp))

                    // Power efficiency bar
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Electro-Acoustic Power Distribution", fontSize = 10.sp, color = EchoMuted)
                            Text("${efficiencyPercent.toInt()}% Acoustic / ${(100 - efficiencyPercent).toInt()}% Thermal", fontSize = 10.sp, color = EchoCyan)
                        }
                        LinearProgressIndicator(
                            progress = { (efficiencyPercent / 100.0).toFloat().coerceIn(0f, 1f) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp)
                                .clip(RoundedCornerShape(3.dp)),
                            color = EchoGreen,
                            trackColor = EchoAmber
                        )
                    }
                }
            }
        }

        // Section 4: Export & Save Note Actions
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Copy Summary Button
                OutlinedButton(
                    onClick = {
                        val report = buildCalculationReport(
                            freqVal = freqVal,
                            velocityVal = velocityVal,
                            cavityLengthKmVal = cavityLengthKmVal,
                            harmonicN = harmonicN,
                            wavelengthMeters = wavelengthMeters,
                            powerInputW = powerInputW,
                            acousticOutputPowerW = acousticOutputPowerW,
                            qualityFactorQ = qualityFactorQ,
                            circulatingPeakPowerW = circulatingPeakPowerW,
                            storedEnergyJoules = storedEnergyJoules,
                            peakPressurePa = peakPressurePa
                        )
                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                        clipboard.setPrimaryClip(ClipData.newPlainText("42.6 Hz Calculation", report))
                        Toast.makeText(context, "Parameters copied to clipboard!", Toast.LENGTH_SHORT).show()
                    },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = EchoCyan)
                ) {
                    Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(6.dp))
                    Text("Copy Report", fontSize = 12.sp)
                }

                // Save into Workspace Notes
                Button(
                    onClick = {
                        val report = buildCalculationReport(
                            freqVal = freqVal,
                            velocityVal = velocityVal,
                            cavityLengthKmVal = cavityLengthKmVal,
                            harmonicN = harmonicN,
                            wavelengthMeters = wavelengthMeters,
                            powerInputW = powerInputW,
                            acousticOutputPowerW = acousticOutputPowerW,
                            qualityFactorQ = qualityFactorQ,
                            circulatingPeakPowerW = circulatingPeakPowerW,
                            storedEnergyJoules = storedEnergyJoules,
                            peakPressurePa = peakPressurePa
                        )
                        onSaveToWorkspaceNote(report)
                        Toast.makeText(context, "Saved to Workspace Notes!", Toast.LENGTH_SHORT).show()
                    },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = EchoCyan, contentColor = EchoOnCyan)
                ) {
                    Icon(Icons.Default.Calculate, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(6.dp))
                    Text("Save to Notes", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        // Canonical Bedrock Physical Reference
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = EchoCard),
                border = CardDefaults.outlinedCardBorder().copy(
                    brush = androidx.compose.ui.graphics.SolidColor(EchoCardStroke)
                )
            ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Info, contentDescription = null, tint = EchoCyan, modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(6.dp))
                        Text(
                            text = "Reference Constants for 42.6 Hz System",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = EchoText
                        )
                    }
                    Text(
                        text = "• Planetary Bedrock Fundamental: 42.600 Hz (Hilsea 1986 detection: 42.598 Hz)\n" +
                               "• Typical Strata Velocity: 3400 m/s (Chalk/Limestone), 5000 m/s (Granite)\n" +
                               "• Solent Tidal Envelope Modulation Period: 11.74 seconds\n" +
                               "• Half-Wave Natural Cavity: L = v / (2 · f) = 39.906 km at 42.6 Hz\n" +
                               "• Resonance Quality Factor: Q ≈ 140 (Sharp standing wave bedrock lock)",
                        fontSize = 11.sp,
                        color = EchoMuted,
                        lineHeight = 18.sp
                    )
                }
            }
        }
    }
}

/**
 * Builds a formatted report text for copying and saving to Workspace notes.
 */
private fun buildCalculationReport(
    freqVal: Double,
    velocityVal: Double,
    cavityLengthKmVal: Double,
    harmonicN: Int,
    wavelengthMeters: Double,
    powerInputW: Double,
    acousticOutputPowerW: Double,
    qualityFactorQ: Double,
    circulatingPeakPowerW: Double,
    storedEnergyJoules: Double,
    peakPressurePa: Double
): String {
    return """[42.6 Hz Resonance & Power Parameter Calculation]
Nominal Frequency (f): ${String.format(Locale.US, "%.3f Hz", freqVal)}
Strata Acoustic Velocity (v): ${String.format(Locale.US, "%.1f m/s", velocityVal)}
Waveguide Cavity Length (L): ${String.format(Locale.US, "%.3f km", cavityLengthKmVal)}
Harmonic Mode (n): $harmonicN
Strata Wavelength (λ): ${String.format(Locale.US, "%.2f m", wavelengthMeters)}

Electrical Power Input (P_in): ${String.format(Locale.US, "%.1f W", powerInputW)}
Acoustic Output Power (P_ac): ${String.format(Locale.US, "%.1f W", acousticOutputPowerW)}
Cavity Quality Factor (Q): ${String.format(Locale.US, "%.1f", qualityFactorQ)}
Circulating Reactive Peak Power: ${String.format(Locale.US, "%.1f W", circulatingPeakPowerW)}
Cavity Stored Energy: ${String.format(Locale.US, "%.2f Joules", storedEnergyJoules)}
Peak Strata Pressure: ${String.format(Locale.US, "%.1f Pa", peakPressurePa)}
11.74s Tidal Envelope: Active Solent Lock
Generated by ATHENA Echo Interface""".trimIndent()
}
