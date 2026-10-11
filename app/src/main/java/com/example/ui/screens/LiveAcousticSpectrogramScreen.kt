package com.example.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MicOff
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.Warning
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
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Random
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.ln
import kotlin.math.log10
import kotlin.math.log2
import kotlin.math.sin
import kotlin.math.sqrt

data class HarmonicMilestone(
    val name: String,
    val category: String,
    val targetHz: Double,
    val toleranceHz: Double = 3.5
)

@Composable
fun LiveAcousticSpectrogramScreen(
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var hasMicPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED
        )
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasMicPermission = isGranted
    }

    var isRecording by remember { mutableStateOf(false) }
    var useSimulationFallback by remember { mutableStateOf(!hasMicPermission) }

    // Acoustic Analysis State
    var peakFrequencyHz by remember { mutableDoubleStateOf(0.0) }
    var peakAmplitudeDb by remember { mutableFloatStateOf(-60f) }
    var detectedMilestone by remember { mutableStateOf<HarmonicMilestone?>(null) }
    var tuningOffsetCents by remember { mutableDoubleStateOf(0.0) }

    // Waterfall buffer (60 historical frames of 80 frequency bins)
    val numBins = 72
    val maxDisplayFreq = 1200.0 // Hz
    val waterfallHistory = remember { mutableStateListOf<FloatArray>() }
    val latestSpectrum = remember { FloatArray(numBins) }

    val milestones = remember {
        listOf(
            HarmonicMilestone("Schumann Fundamental", "Earth Cavity", 7.83, 1.5),
            HarmonicMilestone("Schumann 2nd Harmonic", "Earth Cavity", 14.3, 2.0),
            HarmonicMilestone("Schumann 3rd Harmonic", "Earth Cavity", 20.8, 2.5),
            HarmonicMilestone("Solent Bedrock Fundamental", "Tectonic", 42.6, 2.0),
            HarmonicMilestone("Hypogeum Megalithic", "Acoustic Vault", 111.0, 3.0),
            HarmonicMilestone("King's Chamber Standing Wave", "Pyramid", 121.2, 3.0),
            HarmonicMilestone("Solfeggio Ut (Liberation)", "Solfeggio", 396.0, 4.0),
            HarmonicMilestone("Solfeggio Re (Change)", "Solfeggio", 417.0, 4.0),
            HarmonicMilestone("A432 Sacred Concert Pitch", "Tuning", 432.0, 3.0),
            HarmonicMilestone("A440 Standard Concert Pitch", "Tuning", 440.0, 3.0),
            HarmonicMilestone("Solfeggio Mi (Transformation)", "Solfeggio", 528.0, 4.0),
            HarmonicMilestone("Solfeggio Fa (Harmonize)", "Solfeggio", 639.0, 4.0),
            HarmonicMilestone("Solfeggio Sol (Intuition)", "Solfeggio", 741.0, 5.0),
            HarmonicMilestone("Solfeggio La (Spiritual)", "Solfeggio", 852.0, 5.0)
        )
    }

    // Processing Coroutine for Real or Simulated FFT
    LaunchedEffect(isRecording, hasMicPermission, useSimulationFallback) {
        if (!isRecording) return@LaunchedEffect

        withContext(Dispatchers.Default) {
            val sampleRate = 44100
            val fftSize = 2048
            val buffer = ShortArray(fftSize)
            val real = DoubleArray(fftSize)
            val imag = DoubleArray(fftSize)
            val window = DoubleArray(fftSize) { i ->
                0.5 * (1.0 - cos(2.0 * PI * i / (fftSize - 1))) // Hanning Window
            }

            var audioRecord: AudioRecord? = null
            if (hasMicPermission && !useSimulationFallback) {
                try {
                    val minBuf = AudioRecord.getMinBufferSize(
                        sampleRate,
                        AudioFormat.CHANNEL_IN_MONO,
                        AudioFormat.ENCODING_PCM_16BIT
                    )
                    audioRecord = AudioRecord(
                        MediaRecorder.AudioSource.MIC,
                        sampleRate,
                        AudioFormat.CHANNEL_IN_MONO,
                        AudioFormat.ENCODING_PCM_16BIT,
                        maxOf(minBuf, fftSize * 2)
                    )
                    if (audioRecord.state == AudioRecord.STATE_INITIALIZED) {
                        audioRecord.startRecording()
                    } else {
                        audioRecord.release()
                        audioRecord = null
                    }
                } catch (e: Exception) {
                    audioRecord = null
                }
            }

            var simPhase = 0.0
            val rand = Random()

            while (isActive && isRecording) {
                var readSuccess = false

                if (audioRecord != null && audioRecord.recordingState == AudioRecord.RECORDSTATE_RECORDING) {
                    val readSamples = audioRecord.read(buffer, 0, fftSize)
                    if (readSamples > 0) {
                        for (i in 0 until fftSize) {
                            real[i] = (buffer[i].toDouble() / 32768.0) * window[i]
                            imag[i] = 0.0
                        }
                        readSuccess = true
                    }
                }

                // If real mic unavailable or simulated mode enabled, synthesize ambient room harmonics
                if (!readSuccess) {
                    simPhase += 0.05
                    // Simulated room: 432 Hz carrier, 528 Hz harmonic overtone, and subtle pink noise
                    val targetSimFreq = 432.0
                    for (i in 0 until fftSize) {
                        val t = (i.toDouble() / sampleRate) + simPhase
                        val s1 = 0.55 * sin(2.0 * PI * targetSimFreq * t)
                        val s2 = 0.35 * sin(2.0 * PI * 528.0 * t)
                        val s3 = 0.20 * sin(2.0 * PI * 121.2 * t)
                        val noise = (rand.nextDouble() * 2.0 - 1.0) * 0.08
                        real[i] = (s1 + s2 + s3 + noise) * window[i]
                        imag[i] = 0.0
                    }
                }

                // Radix-2 Cooley-Tukey Fast Fourier Transform
                computeFFT(real, imag, fftSize)

                // Calculate magnitudes & map to bins
                val currentBins = FloatArray(numBins)
                var maxMag = 0.0
                var maxFreq = 0.0

                for (bin in 0 until numBins) {
                    val startFreq = (bin.toDouble() / numBins) * maxDisplayFreq
                    val endFreq = ((bin + 1).toDouble() / numBins) * maxDisplayFreq
                    val startIdx = (startFreq * fftSize / sampleRate).toInt().coerceIn(1, fftSize / 2 - 1)
                    val endIdx = (endFreq * fftSize / sampleRate).toInt().coerceIn(startIdx + 1, fftSize / 2)

                    var binSum = 0.0
                    for (k in startIdx until endIdx) {
                        val mag = sqrt(real[k] * real[k] + imag[k] * imag[k])
                        val freq = (k.toDouble() * sampleRate) / fftSize
                        if (mag > maxMag) {
                            maxMag = mag
                            maxFreq = freq
                        }
                        binSum += mag
                    }
                    val avgMag = (binSum / (endIdx - startIdx).coerceAtLeast(1)).toFloat()
                    val normalized = (avgMag * 6f).coerceIn(0f, 1f)
                    currentBins[bin] = normalized
                    latestSpectrum[bin] = normalized
                }

                // Resonant Peak Detection & Milestone Matching
                val detectedHz = maxFreq
                val detectedDb = (20.0 * log10(maxMag.coerceAtLeast(0.00001))).toFloat()

                val matchedMilestone = milestones.firstOrNull {
                    abs(detectedHz - it.targetHz) <= it.toleranceHz
                }

                // Calculate cents deviation from 440 Hz
                val centsOffset = if (detectedHz > 10.0) {
                    1200.0 * log2(detectedHz / 440.0)
                } else 0.0

                withContext(Dispatchers.Main) {
                    peakFrequencyHz = detectedHz
                    peakAmplitudeDb = detectedDb
                    detectedMilestone = matchedMilestone
                    tuningOffsetCents = centsOffset

                    waterfallHistory.add(0, currentBins)
                    if (waterfallHistory.size > 50) {
                        waterfallHistory.removeAt(waterfallHistory.size - 1)
                    }
                }

                kotlinx.coroutines.delay(45) // ~22 FPS update
            }

            try {
                audioRecord?.stop()
                audioRecord?.release()
            } catch (e: Exception) {
                // cleanup
            }
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            isRecording = false
        }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(EchoBg)
            .testTag("spectrogram_screen"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Hero Header & Resonant Peak Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = EchoCard),
                border = CardDefaults.outlinedCardBorder().copy(
                    brush = Brush.horizontalGradient(listOf(EchoCyan, EchoGreen))
                )
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "ACOUSTIC SPECTROGRAM",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = EchoCyan,
                                letterSpacing = 1.sp
                            )
                            Text(
                                text = "Environmental FFT & Resonant Peak Finder",
                                fontSize = 11.sp,
                                color = EchoMuted
                            )
                        }

                        Button(
                            onClick = {
                                if (!hasMicPermission && !useSimulationFallback) {
                                    permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                                } else {
                                    isRecording = !isRecording
                                }
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isRecording) EchoRed else EchoCyan,
                                contentColor = if (isRecording) Color.White else Color.Black
                            ),
                            modifier = Modifier.testTag("toggle_spectrogram_record")
                        ) {
                            Icon(
                                if (isRecording) Icons.Default.Stop else Icons.Default.Mic,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(Modifier.width(6.dp))
                            Text(if (isRecording) "Stop" else "Listen", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    Spacer(Modifier.height(14.dp))

                    // Peak frequency display banner
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(EchoBgAlt)
                            .border(1.dp, EchoCardStroke, RoundedCornerShape(12.dp))
                            .padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "DOMINANT RESONANT PEAK",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = EchoMuted
                            )
                            Row(verticalAlignment = Alignment.Bottom) {
                                Text(
                                    text = if (isRecording && peakFrequencyHz > 0) "%.1f".format(peakFrequencyHz) else "--.-",
                                    fontSize = 28.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Monospace,
                                    color = if (detectedMilestone != null) EchoGreen else EchoCyan
                                )
                                Text(
                                    text = " Hz",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = EchoMuted,
                                    modifier = Modifier.padding(bottom = 4.dp)
                                )
                            }
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            Text("A440 PITCH OFFSET", fontSize = 10.sp, color = EchoMuted)
                            Text(
                                text = if (isRecording && peakFrequencyHz > 10) "${if (tuningOffsetCents >= 0) "+" else ""}${"%.1f".format(tuningOffsetCents)} cents" else "0.0 cents",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace,
                                color = EchoText
                            )
                            Text(
                                text = if (abs(peakFrequencyHz - 432.0) <= 3.0) "★ A432 Sacred Pitch" else if (abs(peakFrequencyHz - 440.0) <= 3.0) "A440 Standard" else "Environmental Tone",
                                fontSize = 10.sp,
                                color = if (abs(peakFrequencyHz - 432.0) <= 3.0) EchoGreen else EchoMuted
                            )
                        }
                    }

                    // Milestone Match Notification
                    detectedMilestone?.let { match ->
                        Spacer(Modifier.height(10.dp))
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(EchoGreen.copy(alpha = 0.15f))
                                .border(1.dp, EchoGreen.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                                .padding(horizontal = 12.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = EchoGreen, modifier = Modifier.size(16.dp))
                            Spacer(Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = "Harmonic Milestone: ${match.name}",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = EchoGreen
                                )
                                Text(
                                    text = "Target: ${match.targetHz} Hz • Category: ${match.category}",
                                    fontSize = 10.sp,
                                    color = EchoText
                                )
                            }
                        }
                    }
                }
            }
        }

        // Waterfall Spectrogram Canvas
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = EchoBgAlt),
                border = CardDefaults.outlinedCardBorder().copy(brush = Brush.verticalGradient(listOf(EchoCyan.copy(alpha = 0.6f), EchoCardStroke)))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "WATERFALL SPECTRAL TIME-SERIES",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = EchoCyan
                        )
                        Text(
                            text = "0 Hz ─── $maxDisplayFreq Hz",
                            fontSize = 10.sp,
                            fontFamily = FontFamily.Monospace,
                            color = EchoMuted
                        )
                    }

                    Spacer(Modifier.height(8.dp))

                    // Waterfall Canvas (Height 210dp)
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(210.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFF06090F))
                            .border(1.dp, EchoCardStroke, RoundedCornerShape(8.dp))
                    ) {
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            val w = size.width
                            val h = size.height

                            val historyCount = waterfallHistory.size
                            if (historyCount > 0) {
                                val rowHeight = h / 50f
                                val colWidth = w / numBins.toFloat()

                                for (row in 0 until minOf(historyCount, 50)) {
                                    val rowBins = waterfallHistory[row]
                                    val yPos = row * rowHeight

                                    for (col in 0 until numBins) {
                                        val mag = rowBins[col]
                                        if (mag > 0.05f) {
                                            val xPos = col * colWidth
                                            // Spectral heatmap: Dark blue -> Cyan -> Emerald -> Gold -> White
                                            val cellColor = when {
                                                mag > 0.85f -> Color.White
                                                mag > 0.65f -> Color(0xFFFFD166) // Gold
                                                mag > 0.40f -> Color(0xFF06D6A0) // Emerald
                                                mag > 0.20f -> Color(0xFF00E5FF) // Cyan
                                                else -> Color(0xFF104E75)        // Deep navy
                                            }
                                            drawRect(
                                                color = cellColor.copy(alpha = (mag * 1.2f).coerceIn(0.2f, 1f)),
                                                topLeft = Offset(xPos, yPos),
                                                size = Size(colWidth + 0.5f, rowHeight + 0.5f)
                                            )
                                        }
                                    }
                                }
                            } else {
                                // Placeholder text / grid
                                drawLine(
                                    color = Color(0xFF1B2838),
                                    start = Offset(0f, h / 2),
                                    end = Offset(w, h / 2),
                                    strokeWidth = 1f
                                )
                            }

                            // Vertical frequency marker lines for 432 Hz and 528 Hz
                            val x432 = ((432.0 / maxDisplayFreq) * w).toFloat()
                            val x528 = ((528.0 / maxDisplayFreq) * w).toFloat()

                            drawLine(
                                color = Color(0xFFFFD166).copy(alpha = 0.4f),
                                start = Offset(x432, 0f),
                                end = Offset(x432, h),
                                strokeWidth = 1.5.dp.toPx()
                            )
                            drawLine(
                                color = Color(0xFF06D6A0).copy(alpha = 0.4f),
                                start = Offset(x528, 0f),
                                end = Offset(x528, h),
                                strokeWidth = 1.5.dp.toPx()
                            )
                        }

                        // Waterfall frequency label tags
                        Box(
                            modifier = Modifier
                                .align(Alignment.BottomStart)
                                .padding(6.dp)
                                .clip(RoundedCornerShape(4.dp))
                                .background(Color.Black.copy(alpha = 0.65f))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text("Time ↓ (Latest on top)", fontSize = 9.sp, color = EchoMuted)
                        }
                    }

                    Spacer(Modifier.height(10.dp))

                    // Instantaneous FFT Bar Spectrum
                    Text(
                        text = "INSTANTANEOUS FFT SPECTRUM",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = EchoMuted
                    )
                    Spacer(Modifier.height(6.dp))

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(70.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFF080C14))
                            .border(1.dp, EchoCardStroke, RoundedCornerShape(8.dp))
                    ) {
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            val w = size.width
                            val h = size.height
                            val barW = w / numBins.toFloat()

                            for (i in 0 until numBins) {
                                val mag = latestSpectrum[i]
                                val barH = mag * h
                                val x = i * barW
                                val y = h - barH

                                drawRect(
                                    brush = Brush.verticalGradient(listOf(EchoCyan, EchoGreen)),
                                    topLeft = Offset(x, y),
                                    size = Size(barW - 1f, barH)
                                )
                            }
                        }
                    }
                }
            }
        }

        // Harmonic Milestones Reference List
        item {
            Text(
                text = "HARMONIC MILESTONES TARGETED",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = EchoCyan,
                letterSpacing = 1.sp
            )
            Spacer(Modifier.height(8.dp))

            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(milestones) { m ->
                    val isMatched = detectedMilestone?.name == m.name
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = if (isMatched) EchoGreen.copy(alpha = 0.2f) else EchoCard
                        ),
                        border = CardDefaults.outlinedCardBorder().copy(
                            brush = Brush.horizontalGradient(
                                if (isMatched) listOf(EchoGreen, EchoCyan) else listOf(EchoCardStroke, EchoCardStroke)
                            )
                        )
                    ) {
                        Column(modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                if (isMatched) {
                                    Icon(Icons.Default.CheckCircle, contentDescription = null, tint = EchoGreen, modifier = Modifier.size(12.dp))
                                    Spacer(Modifier.width(4.dp))
                                }
                                Text(
                                    text = "${m.targetHz} Hz",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Monospace,
                                    color = if (isMatched) EchoGreen else EchoCyan
                                )
                            }
                            Text(text = m.name, fontSize = 10.sp, color = EchoText)
                        }
                    }
                }
            }
        }

        // Settings & Simulation Mode Toggle
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = EchoCard),
                border = CardDefaults.outlinedCardBorder().copy(brush = Brush.horizontalGradient(listOf(EchoCardStroke, EchoCardStroke)))
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("AUDIO SOURCE CONFIGURATION", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = EchoMuted)

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Acoustic Room Simulator Mode", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = EchoText)
                            Text("Generates synthetic 432 Hz / 528 Hz / 121 Hz room harmonics for testing without hardware mic", fontSize = 11.sp, color = EchoMuted)
                        }
                        Switch(
                            checked = useSimulationFallback,
                            onCheckedChange = { useSimulationFallback = it },
                            colors = SwitchDefaults.colors(checkedThumbColor = EchoCyan, checkedTrackColor = EchoCyan.copy(alpha = 0.4f)),
                            modifier = Modifier.testTag("toggle_simulation_mode")
                        )
                    }

                    if (!hasMicPermission) {
                        HorizontalDivider(color = EchoCardStroke)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Warning, contentDescription = null, tint = EchoRed, modifier = Modifier.size(16.dp))
                                Spacer(Modifier.width(8.dp))
                                Text("Microphone permission not granted", fontSize = 11.sp, color = EchoText)
                            }
                            OutlinedButton(
                                onClick = { permissionLauncher.launch(Manifest.permission.RECORD_AUDIO) },
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = EchoCyan)
                            ) {
                                Text("Grant Mic", fontSize = 11.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}

/**
 * Standard Radix-2 Cooley-Tukey In-Place Fast Fourier Transform
 */
private fun computeFFT(real: DoubleArray, imag: DoubleArray, n: Int) {
    var j = 0
    for (i in 0 until n - 1) {
        if (i < j) {
            val tr = real[i]
            val ti = imag[i]
            real[i] = real[j]
            imag[i] = imag[j]
            real[j] = tr
            imag[j] = ti
        }
        var k = n shr 1
        while (k in 1..j) {
            j -= k
            k = k shr 1
        }
        j += k
    }

    var length = 2
    while (length <= n) {
        val halfLength = length shr 1
        val angle = -2.0 * PI / length
        val wStepR = cos(angle)
        val wStepI = sin(angle)

        var i = 0
        while (i < n) {
            var wr = 1.0
            var wi = 0.0
            for (k in 0 until halfLength) {
                val matchIdx = i + k + halfLength
                val tr = wr * real[matchIdx] - wi * imag[matchIdx]
                val ti = wr * imag[matchIdx] + wi * real[matchIdx]

                real[matchIdx] = real[i + k] - tr
                imag[matchIdx] = imag[i + k] - ti
                real[i + k] += tr
                imag[i + k] += ti

                val nextWr = wr * wStepR - wi * wStepI
                wi = wr * wStepI + wi * wStepR
                wr = nextWr
            }
            i += length
        }
        length = length shl 1
    }
}
