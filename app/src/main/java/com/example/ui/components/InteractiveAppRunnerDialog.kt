package com.example.ui.components

import android.widget.Toast
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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Sensors
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
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
import androidx.compose.ui.geometry.Offset
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
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.ai.ApkBuildResult
import com.example.ai.AppIconData
import com.example.audio.HarmonicToneGenerator
import com.example.ui.theme.EchoBg
import com.example.ui.theme.EchoBgAlt
import com.example.ui.theme.EchoCard
import com.example.ui.theme.EchoCardStroke
import com.example.ui.theme.EchoCyan
import com.example.ui.theme.EchoGreen
import com.example.ui.theme.EchoMuted
import com.example.ui.theme.EchoOnCyan
import com.example.ui.theme.EchoText
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InteractiveAppRunnerDialog(
    result: ApkBuildResult,
    iconData: AppIconData,
    toneGenerator: HarmonicToneGenerator = HarmonicToneGenerator.SHARED,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    var activeTab by remember { mutableIntStateOf(0) } // 0: Engine/DSP, 1: Telemetry, 2: Console
    var isAudioActive by remember { mutableStateOf(false) }
    var testFrequencyHz by remember { mutableDoubleStateOf(42.6) }
    var signalGain by remember { mutableFloatStateOf(0.8f) }

    val logs = remember {
        mutableStateListOf(
            "Application ${result.appName} process attached (PID 4821)",
            "Loaded Compose runtime v1.7.0 & M3 MaterialTheme",
            "Hardware DSP audio interface synchronized",
            "Awaiting user interactions..."
        )
    }

    fun addLog(msg: String) {
        val time = SimpleDateFormat("HH:mm:ss", Locale.US).format(Date())
        logs.add(0, "[$time] $msg")
    }

    DisposableEffect(isAudioActive, testFrequencyHz) {
        if (isAudioActive) {
            toneGenerator.updateFrequencies(listOf(testFrequencyHz))
            toneGenerator.start(frequencies = listOf(testFrequencyHz))
        } else {
            toneGenerator.stop()
        }
        onDispose {
            toneGenerator.stop()
        }
    }

    val infiniteTransition = rememberInfiniteTransition(label = "runner_wave")
    val wavePhase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 2f * PI.toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = if (isAudioActive) 1200 else 3000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "phase_anim"
    )

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .background(EchoBg)
                .testTag("interactive_app_runner_dialog"),
            color = EchoBg
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .statusBarsPadding()
            ) {
                // Device Status Bar
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFF040810))
                        .padding(horizontal = 16.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("9:41 AM", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = EchoText)
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("5G", fontSize = 10.sp, color = EchoMuted)
                        Text("100%", fontSize = 10.sp, color = EchoGreen)
                    }
                }

                // App Top Bar
                TopAppBar(
                    title = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(
                                        Brush.linearGradient(
                                            listOf(iconData.startColor, iconData.endColor)
                                        )
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(iconData.glyph, fontSize = 16.sp)
                            }
                            Spacer(Modifier.width(10.dp))
                            Column {
                                Text(result.appName, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = EchoText)
                                Text("LIVE TEST RUNNER • v1.0.0", fontSize = 9.sp, color = EchoCyan, letterSpacing = 0.5.sp)
                            }
                        }
                    },
                    actions = {
                        IconButton(onClick = onDismiss, modifier = Modifier.testTag("close_test_runner")) {
                            Icon(Icons.Default.Close, contentDescription = "Close", tint = EchoText)
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = EchoBgAlt)
                )

                // Sub-Tab Switcher
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(EchoBgAlt)
                        .padding(horizontal = 16.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilterChip(
                        selected = activeTab == 0,
                        onClick = { activeTab = 0 },
                        label = { Text("DSP Engine", fontSize = 11.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = EchoCyan,
                            selectedLabelColor = Color.Black,
                            containerColor = EchoCard,
                            labelColor = EchoText
                        ),
                        modifier = Modifier.weight(1f)
                    )
                    FilterChip(
                        selected = activeTab == 1,
                        onClick = { activeTab = 1 },
                        label = { Text("Sensors", fontSize = 11.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = EchoCyan,
                            selectedLabelColor = Color.Black,
                            containerColor = EchoCard,
                            labelColor = EchoText
                        ),
                        modifier = Modifier.weight(1f)
                    )
                    FilterChip(
                        selected = activeTab == 2,
                        onClick = { activeTab = 2 },
                        label = { Text("Log Console", fontSize = 11.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = EchoCyan,
                            selectedLabelColor = Color.Black,
                            containerColor = EchoCard,
                            labelColor = EchoText
                        ),
                        modifier = Modifier.weight(1f)
                    )
                }

                // Active Tab Content
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    when (activeTab) {
                        0 -> {
                            // DSP Waveform Screen
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(containerColor = EchoCard),
                                border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(EchoCardStroke))
                            ) {
                                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text("LIVE WAVEFORM OUTPUT", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = EchoCyan)
                                        Text(
                                            text = if (isAudioActive) "EMITTING AUDIO" else "STANDBY",
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (isAudioActive) EchoGreen else EchoMuted
                                        )
                                    }

                                    // Canvas Oscilloscope
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(130.dp)
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(Color(0xFF060A12))
                                            .border(1.dp, EchoCardStroke, RoundedCornerShape(8.dp))
                                    ) {
                                        Canvas(modifier = Modifier.fillMaxSize()) {
                                            val w = size.width
                                            val h = size.height
                                            val midY = h / 2f

                                            drawLine(Color(0xFF1E293B), Offset(0f, midY), Offset(w, midY), strokeWidth = 1f)

                                            val path = Path()
                                            val numPoints = 100
                                            val freqFactor = (testFrequencyHz / 20.0).toFloat()
                                            val amp = signalGain * (h * 0.4f)

                                            for (i in 0..numPoints) {
                                                val x = (i.toFloat() / numPoints) * w
                                                val norm = i.toFloat() / numPoints
                                                val y = midY + sin(norm * 2f * PI.toFloat() * freqFactor + wavePhase) * amp
                                                if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
                                            }

                                            drawPath(
                                                path = path,
                                                color = if (isAudioActive) EchoGreen else EchoCyan,
                                                style = Stroke(width = 2.5.dp.toPx(), cap = StrokeCap.Round)
                                            )
                                        }
                                    }

                                    // Controls
                                    Column {
                                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                            Text("Synthesizer Frequency: ${"%.1f".format(testFrequencyHz)} Hz", fontSize = 12.sp, color = EchoText)
                                        }
                                        Slider(
                                            value = testFrequencyHz.toFloat(),
                                            onValueChange = {
                                                testFrequencyHz = it.toDouble()
                                                addLog("Tuned frequency to ${"%.1f".format(it)} Hz")
                                            },
                                            valueRange = 7.83f..528f,
                                            colors = SliderDefaults.colors(thumbColor = EchoCyan, activeTrackColor = EchoCyan)
                                        )
                                    }

                                    Button(
                                        onClick = {
                                            isAudioActive = !isAudioActive
                                            addLog(if (isAudioActive) "Acoustic emission started" else "Acoustic emission paused")
                                        },
                                        modifier = Modifier.fillMaxWidth().height(42.dp),
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = if (isAudioActive) EchoGreen else EchoCyan,
                                            contentColor = Color.Black
                                        )
                                    ) {
                                        Icon(if (isAudioActive) Icons.Default.Pause else Icons.Default.PlayArrow, contentDescription = null)
                                        Spacer(Modifier.width(8.dp))
                                        Text(if (isAudioActive) "Stop Audio Synthesizer" else "Start Audio Synthesizer", fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                        1 -> {
                            // Sensors Telemetry Tab
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(containerColor = EchoCard)
                            ) {
                                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                    Text("DEVICE SENSORS & TELEMETRY", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = EchoCyan)

                                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                        Card(modifier = Modifier.weight(1f), colors = CardDefaults.cardColors(containerColor = EchoBgAlt)) {
                                            Column(modifier = Modifier.padding(10.dp)) {
                                                Text("ACCEL X", fontSize = 10.sp, color = EchoMuted)
                                                Text("+0.024 m/s²", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = EchoCyan)
                                            }
                                        }
                                        Card(modifier = Modifier.weight(1f), colors = CardDefaults.cardColors(containerColor = EchoBgAlt)) {
                                            Column(modifier = Modifier.padding(10.dp)) {
                                                Text("ACCEL Y", fontSize = 10.sp, color = EchoMuted)
                                                Text("-0.118 m/s²", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = EchoCyan)
                                            }
                                        }
                                        Card(modifier = Modifier.weight(1f), colors = CardDefaults.cardColors(containerColor = EchoBgAlt)) {
                                            Column(modifier = Modifier.padding(10.dp)) {
                                                Text("ACCEL Z", fontSize = 10.sp, color = EchoMuted)
                                                Text("+9.806 m/s²", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = EchoGreen)
                                            }
                                        }
                                    }

                                    Button(
                                        onClick = {
                                            addLog("Telemetry beacon packet emitted (UUID ${System.currentTimeMillis() % 10000})")
                                            Toast.makeText(context, "Telemetry broadcasted!", Toast.LENGTH_SHORT).show()
                                        },
                                        modifier = Modifier.fillMaxWidth(),
                                        colors = ButtonDefaults.buttonColors(containerColor = EchoCyan, contentColor = Color.Black)
                                    ) {
                                        Icon(Icons.Default.Bolt, contentDescription = null)
                                        Spacer(Modifier.width(6.dp))
                                        Text("Transmit Telemetry Beacon", fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                        2 -> {
                            // Console Log Screen
                            Card(
                                modifier = Modifier.fillMaxSize(),
                                colors = CardDefaults.cardColors(containerColor = Color(0xFF04070D))
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Text("APP RUNTIME DIAGNOSTIC CONSOLE", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = EchoMuted)
                                    Spacer(Modifier.height(8.dp))
                                    LazyColumn(
                                        modifier = Modifier.fillMaxSize(),
                                        verticalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        items(logs.size) { i ->
                                            Text(
                                                text = "> ${logs[i]}",
                                                fontFamily = FontFamily.Monospace,
                                                fontSize = 11.sp,
                                                color = EchoGreen,
                                                lineHeight = 15.sp
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
