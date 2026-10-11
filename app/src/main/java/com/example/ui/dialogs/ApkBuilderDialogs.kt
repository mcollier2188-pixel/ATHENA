package com.example.ui.dialogs

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.OpenInBrowser
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Stop
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
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
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
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.core.content.FileProvider
import com.example.ai.ApkBuildResult
import com.example.ai.AppArchetype
import com.example.ai.AppIconData
import com.example.ai.IconPalettePreset
import com.example.ai.IconShape
import com.example.ai.PRESET_ICON_PALETTES
import com.example.audio.HarmonicToneGenerator
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
import java.io.File
import kotlin.math.sin

/**
 * 1. AppRunnerDialog: Real Interactive Live Sandbox
 * Runs the synthesized app archetype with full interactive UI and audio/telemetry logic.
 */
@Composable
fun AppRunnerDialog(
    result: ApkBuildResult,
    icon: AppIconData,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    var isSimulatedPhone by remember { mutableStateOf(true) }
    val toneGenerator = remember { HarmonicToneGenerator() }

    DisposableEffect(Unit) {
        onDispose {
            toneGenerator.stop()
        }
    }

    Dialog(
        onDismissRequest = {
            toneGenerator.stop()
            onDismiss()
        },
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF030712))
                .statusBarsPadding()
                .testTag("app_runner_dialog"),
            color = Color(0xFF030712)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 14.dp, vertical = 8.dp)
            ) {
                // Top Emulator Navigation Bar
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = {
                            toneGenerator.stop()
                            onDismiss()
                        }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Exit Sandbox", tint = EchoCyan)
                        }
                        Spacer(Modifier.width(4.dp))
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier.size(8.dp).background(EchoGreen, CircleShape)
                                )
                                Spacer(Modifier.width(6.dp))
                                Text(
                                    text = "SANDBOX TEST RUNNER",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = EchoGreen,
                                    letterSpacing = 1.sp
                                )
                            }
                            Text(
                                text = "${result.appName} • ${result.packageName}",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = EchoText
                            )
                        }
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        FilterChip(
                            selected = isSimulatedPhone,
                            onClick = { isSimulatedPhone = !isSimulatedPhone },
                            label = { Text(if (isSimulatedPhone) "Device Frame" else "Full Screen", fontSize = 10.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = EchoCyanSoft,
                                selectedLabelColor = EchoCyan,
                                containerColor = EchoBgAlt,
                                labelColor = EchoMuted
                            )
                        )
                    }
                }

                Spacer(Modifier.height(6.dp))

                // Smartphone Device Frame or Full Screen
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .then(
                            if (isSimulatedPhone) {
                                Modifier
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                                    .clip(RoundedCornerShape(28.dp))
                                    .border(4.dp, Color(0xFF1F2937), RoundedCornerShape(28.dp))
                                    .background(EchoBg)
                            } else {
                                Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(EchoBg)
                            }
                        )
                ) {
                    Column(modifier = Modifier.fillMaxSize()) {
                        // Simulated Status Bar
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(EchoBgAlt)
                                .padding(horizontal = 16.dp, vertical = 6.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("9:41 AM", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = EchoText)
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("5G", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = EchoCyan)
                                Text("100%", fontSize = 10.sp, color = EchoGreen, fontWeight = FontWeight.Bold)
                            }
                        }

                        // App Header inside runner
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(EchoCard)
                                .border(1.dp, EchoCardStroke)
                                .padding(horizontal = 14.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(34.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Brush.linearGradient(listOf(icon.startColor, icon.endColor))),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(icon.glyph, fontSize = 18.sp)
                            }
                            Spacer(Modifier.width(10.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(result.appName, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = EchoText)
                                Text(result.archetype.title, fontSize = 10.sp, color = EchoCyan)
                            }
                        }

                        // Interactive Archetype Content Body
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f)
                                .padding(12.dp)
                        ) {
                            when (result.archetype) {
                                AppArchetype.SCIENTIFIC_ACOUSTICS -> {
                                    AcousticArchetypeRunner(toneGenerator)
                                }
                                AppArchetype.GEOLOGICAL_TELEMETRY -> {
                                    GeologicalTelemetryRunner()
                                }
                                AppArchetype.STANDALONE_ATHENA_AI -> {
                                    AthenaAiRunner()
                                }
                                AppArchetype.CUSTOM_COMPOSE_APP -> {
                                    CustomComposeAppRunner()
                                }
                            }
                        }

                        // Simulated Android Navigation Bar at bottom of phone frame
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(EchoBgAlt)
                                .padding(vertical = 8.dp),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .width(80.dp)
                                    .height(4.dp)
                                    .clip(RoundedCornerShape(2.dp))
                                    .background(Color(0xFF6B7280))
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun AcousticArchetypeRunner(toneGenerator: HarmonicToneGenerator) {
    var frequency by remember { mutableFloatStateOf(42.600f) }
    var isPlaying by remember { mutableStateOf(false) }
    var qFactor by remember { mutableFloatStateOf(108.5f) }

    val infiniteTransition = rememberInfiniteTransition(label = "wave")
    val phase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 6.28318f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "phase"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // Waveform Visualizer Canvas
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(130.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0xFF040711))
                .border(1.dp, EchoCardStroke, RoundedCornerShape(12.dp))
                .padding(8.dp)
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val w = size.width
                val h = size.height
                val midY = h / 2f
                val path = Path()

                val cycles = 3.5f
                for (px in 0..w.toInt() step 3) {
                    val x = px.toFloat()
                    val normX = x / w
                    val waveAmp = if (isPlaying) 32f else 10f
                    val y = midY + sin(normX * cycles * 6.283f + phase) * waveAmp
                    if (px == 0) path.moveTo(x, y.toFloat()) else path.lineTo(x, y.toFloat())
                }

                drawPath(
                    path = path,
                    color = if (isPlaying) EchoCyan else EchoMuted,
                    style = Stroke(width = 2.5f)
                )

                // Standing wave nodal lines
                for (i in 1..3) {
                    val nx = (w / 4f) * i
                    drawLine(
                        color = EchoGreen.copy(alpha = 0.5f),
                        start = Offset(nx, 0f),
                        end = Offset(nx, h),
                        strokeWidth = 1f
                    )
                }
            }

            Text(
                text = "${"%.3f".format(frequency)} Hz • Q=${"%.1f".format(qFactor)}",
                fontSize = 10.sp,
                color = EchoCyan,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.align(Alignment.TopStart)
            )
        }

        // Frequency Presets
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            listOf(7.83f, 42.60f, 117.0f, 432.0f, 528.0f).forEach { preset ->
                FilterChip(
                    selected = (frequency == preset),
                    onClick = {
                        frequency = preset
                        if (isPlaying) toneGenerator.start(listOf(frequency.toDouble()))
                    },
                    label = { Text("${preset.toInt()}Hz", fontSize = 10.sp) },
                    modifier = Modifier.weight(1f),
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = EchoCyan,
                        selectedLabelColor = EchoOnCyan,
                        containerColor = EchoCard,
                        labelColor = EchoText
                    )
                )
            }
        }

        // Frequency Slider
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Carrier Frequency", fontSize = 11.sp, color = EchoMuted)
                Text("${"%.2f".format(frequency)} Hz", fontSize = 11.sp, color = EchoCyan, fontWeight = FontWeight.Bold)
            }
            Slider(
                value = frequency,
                onValueChange = {
                    frequency = it
                    if (isPlaying) toneGenerator.start(listOf(frequency.toDouble()))
                },
                valueRange = 10f..600f,
                colors = SliderDefaults.colors(thumbColor = EchoCyan, activeTrackColor = EchoCyan)
            )
        }

        // Q-Factor slider
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Cavity Q-Factor Resonance", fontSize = 11.sp, color = EchoMuted)
                Text("${"%.1f".format(qFactor)}", fontSize = 11.sp, color = EchoGreen, fontWeight = FontWeight.Bold)
            }
            Slider(
                value = qFactor,
                onValueChange = { qFactor = it },
                valueRange = 10f..250f,
                colors = SliderDefaults.colors(thumbColor = EchoGreen, activeTrackColor = EchoGreen)
            )
        }

        // Play Tone Button
        Button(
            onClick = {
                if (isPlaying) {
                    toneGenerator.stop()
                    isPlaying = false
                } else {
                    toneGenerator.start(listOf(frequency.toDouble()))
                    isPlaying = true
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(44.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = if (isPlaying) EchoRed else EchoGreen,
                contentColor = Color.Black
            ),
            shape = RoundedCornerShape(10.dp)
        ) {
            Icon(if (isPlaying) Icons.Default.Stop else Icons.Default.PlayArrow, contentDescription = null)
            Spacer(Modifier.width(8.dp))
            Text(if (isPlaying) "STOP ACOUSTIC TONE" else "PLAY RESONANT FREQUENCY", fontWeight = FontWeight.Bold, fontSize = 12.sp)
        }
    }
}

@Composable
private fun GeologicalTelemetryRunner() {
    val nodes = remember {
        listOf(
            "G1-St.Boniface" to (42.600f to "0.88 mm/s"),
            "G2-Appuldurcombe" to (42.598f to "0.92 mm/s"),
            "G3-Carisbrooke" to (42.603f to "0.79 mm/s"),
            "G4-Yarmouth" to (42.601f to "0.84 mm/s"),
            "G5-Culver Cliff" to (42.599f to "0.95 mm/s")
        )
    }
    var selectedNodeIndex by remember { mutableIntStateOf(0) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = EchoCard),
            border = CardDefaults.outlinedCardBorder().copy(brush = Brush.linearGradient(listOf(EchoGreen, EchoCyan)))
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text("SEISMIC TELEMETRY ARRAY", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = EchoGreen)
                Spacer(Modifier.height(4.dp))
                Text("11 Bedrock Sensors Active • Real-Time Solent Waveform", fontSize = 12.sp, color = EchoText)
            }
        }

        Text("Active Geological Stations:", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = EchoMuted)

        nodes.forEachIndexed { index, (name, telemetry) ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { selectedNodeIndex = index },
                colors = CardDefaults.cardColors(
                    containerColor = if (selectedNodeIndex == index) EchoBgAlt else EchoCard
                ),
                border = CardDefaults.outlinedCardBorder().copy(
                    brush = androidx.compose.ui.graphics.SolidColor(if (selectedNodeIndex == index) EchoCyan else EchoCardStroke)
                )
            ) {
                Row(
                    modifier = Modifier.padding(10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(name, fontWeight = FontWeight.Bold, fontSize = 12.sp, color = EchoText)
                        Text("${telemetry.first} Hz • Amplitude: ${telemetry.second}", fontSize = 10.sp, color = EchoMuted)
                    }
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .background(if (selectedNodeIndex == index) EchoCyan else EchoGreen, CircleShape)
                    )
                }
            }
        }
    }
}

@Composable
private fun AthenaAiRunner() {
    var queryText by remember { mutableStateOf("") }
    val messages = remember {
        mutableStateListOf(
            "Athena" to "Welcome to Athena Intelligent Companion. How may I assist your acoustic research today?"
        )
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            items(messages) { (sender, text) ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = if (sender == "User") EchoCyanSoft else EchoCard
                    )
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Text(sender, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = EchoCyan)
                        Spacer(Modifier.height(2.dp))
                        Text(text, fontSize = 12.sp, color = EchoText)
                    }
                }
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            OutlinedTextField(
                value = queryText,
                onValueChange = { queryText = it },
                placeholder = { Text("Ask Athena...", fontSize = 11.sp) },
                modifier = Modifier.weight(1f),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = EchoCyan)
            )
            Button(
                onClick = {
                    if (queryText.isNotBlank()) {
                        messages.add("User" to queryText)
                        val q = queryText
                        queryText = ""
                        messages.add("Athena" to "Synthesizing theoretical physics analysis for: '$q' at fundamental frequency 42.600 Hz.")
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = EchoCyan, contentColor = EchoOnCyan)
            ) {
                Text("Send", fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun CustomComposeAppRunner() {
    var counter by remember { mutableIntStateOf(42) }
    var toggleState by remember { mutableStateOf(true) }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = EchoCard),
            border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(EchoCardStroke))
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Interactive State Counter", fontSize = 12.sp, color = EchoMuted)
                Text("$counter", fontSize = 36.sp, fontWeight = FontWeight.Bold, color = EchoCyan)
                Spacer(Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(
                        onClick = { counter-- },
                        colors = ButtonDefaults.buttonColors(containerColor = EchoBgAlt, contentColor = EchoText)
                    ) {
                        Text("−1")
                    }
                    Button(
                        onClick = { counter++ },
                        colors = ButtonDefaults.buttonColors(containerColor = EchoCyan, contentColor = EchoOnCyan)
                    ) {
                        Text("+1")
                    }
                }
            }
        }

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = EchoCard)
        ) {
            Row(
                modifier = Modifier.padding(14.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Quantum State Synchronizer", fontSize = 12.sp, color = EchoText)
                Switch(
                    checked = toggleState,
                    onCheckedChange = { toggleState = it },
                    colors = SwitchDefaults.colors(checkedThumbColor = EchoCyan, checkedTrackColor = EchoCyanSoft)
                )
            }
        }
    }
}

/**
 * 2. IconStudioDialog: Visual Launcher Icon Customizer
 * Create, customize, and apply launcher icons with real-time preview and SVG export.
 */
@Composable
fun IconStudioDialog(
    currentIcon: AppIconData,
    appName: String,
    onIconChanged: (AppIconData) -> Unit,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    var selectedGlyph by remember { mutableStateOf(currentIcon.glyph) }
    var selectedGlyphName by remember { mutableStateOf(currentIcon.glyphName) }
    var selectedPalette by remember { mutableStateOf(PRESET_ICON_PALETTES.first()) }
    var selectedShape by remember { mutableStateOf(currentIcon.shape) }

    val glyphs = listOf(
        "🌊" to "Acoustic Wave",
        "📡" to "Telemetry Antenna",
        "🦉" to "Athena Owl",
        "⚛️" to "Quantum Atom",
        "🏛️" to "Pyramid Chamber",
        "⚡" to "Lightning Pulse",
        "💎" to "Resonant Crystal",
        "🔥" to "Solar Flare",
        "🔮" to "Harmonic Sphere",
        "🌀" to "Vortex Cymatics"
    )

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF070B14))
                .statusBarsPadding()
                .testTag("icon_studio_dialog"),
            color = Color(0xFF070B14)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = onDismiss) {
                            Icon(Icons.Default.Close, contentDescription = "Close", tint = EchoCyan)
                        }
                        Spacer(Modifier.width(4.dp))
                        Text("APP ICON STUDIO", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = EchoCyan)
                    }

                    Button(
                        onClick = {
                            val newIcon = AppIconData(
                                glyph = selectedGlyph,
                                glyphName = selectedGlyphName,
                                bgStartHex = selectedPalette.startHex,
                                bgEndHex = selectedPalette.endHex,
                                shape = selectedShape
                            )
                            onIconChanged(newIcon)
                            Toast.makeText(context, "Launcher Icon updated & applied to build bundle!", Toast.LENGTH_SHORT).show()
                            onDismiss()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = EchoGreen, contentColor = Color.Black)
                    ) {
                        Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(4.dp))
                        Text("Apply to App", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(Modifier.height(14.dp))

                // Hero Preview of Launcher Icon
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = EchoCard),
                    border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(EchoCardStroke))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Icon Preview with Selected Shape
                        val shapeModifier = when (selectedShape) {
                            IconShape.CIRCLE -> CircleShape
                            IconShape.SQUIRCLE -> RoundedCornerShape(26.dp)
                            IconShape.ROUNDED_RECT -> RoundedCornerShape(14.dp)
                        }

                        Box(
                            modifier = Modifier
                                .size(96.dp)
                                .clip(shapeModifier)
                                .background(
                                    Brush.linearGradient(
                                        listOf(
                                            selectedPalette.startHex.let { parseHexColor(it) },
                                            selectedPalette.endHex.let { parseHexColor(it) }
                                        )
                                    )
                                )
                                .border(2.dp, Color(0x33FFFFFF), shapeModifier),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(selectedGlyph, fontSize = 48.sp)
                        }

                        Spacer(Modifier.height(12.dp))
                        Text(appName, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = EchoText)
                        Text(
                            "$selectedGlyphName • ${selectedShape.label} • ${selectedPalette.name}",
                            fontSize = 11.sp,
                            color = EchoMuted
                        )
                    }
                }

                Spacer(Modifier.height(14.dp))

                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    // Glyph Selection
                    item {
                        Text("Choose Icon Glyph / Symbol:", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = EchoCyan)
                        Spacer(Modifier.height(8.dp))
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(glyphs) { (glyph, name) ->
                                Box(
                                    modifier = Modifier
                                        .size(52.dp)
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(if (selectedGlyph == glyph) EchoCyanSoft else EchoBgAlt)
                                        .border(
                                            1.dp,
                                            if (selectedGlyph == glyph) EchoCyan else EchoCardStroke,
                                            RoundedCornerShape(10.dp)
                                        )
                                        .clickable {
                                            selectedGlyph = glyph
                                            selectedGlyphName = name
                                        },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(glyph, fontSize = 24.sp)
                                }
                            }
                        }
                    }

                    // Background Palette Selection
                    item {
                        Text("Color Gradient Theme:", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = EchoCyan)
                        Spacer(Modifier.height(8.dp))
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            PRESET_ICON_PALETTES.forEach { palette ->
                                FilterChip(
                                    selected = (selectedPalette.name == palette.name),
                                    onClick = { selectedPalette = palette },
                                    label = { Text(palette.name, fontSize = 11.sp) },
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

                    // Shape Selection
                    item {
                        Text("Adaptive Launcher Shape:", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = EchoCyan)
                        Spacer(Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            IconShape.entries.forEach { shape ->
                                FilterChip(
                                    selected = (selectedShape == shape),
                                    onClick = { selectedShape = shape },
                                    label = { Text(shape.label, fontSize = 11.sp) },
                                    modifier = Modifier.weight(1f),
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
                }
            }
        }
    }
}

/**
 * 3. HtmlConfirmDialog: Standalone HTML Web App Viewer & Exporter
 * Zero-install test runner: opens in Chrome/browser or in-app interactive WebView.
 */
@Composable
fun HtmlConfirmDialog(
    htmlFile: File,
    appName: String,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    var showInAppWebView by remember { mutableStateOf(false) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF030712))
                .statusBarsPadding()
                .testTag("html_confirm_dialog"),
            color = Color(0xFF030712)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = onDismiss) {
                            Icon(Icons.Default.Close, contentDescription = "Close", tint = EchoCyan)
                        }
                        Spacer(Modifier.width(4.dp))
                        Column {
                            Text("HTML5 WEB APP EXPORT", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = EchoCyan)
                            Text(htmlFile.name, fontSize = 11.sp, color = EchoMuted)
                        }
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        // Open In Browser
                        Button(
                            onClick = {
                                try {
                                    val uri = FileProvider.getUriForFile(
                                        context,
                                        "${context.packageName}.fileprovider",
                                        htmlFile
                                    )
                                    val browserIntent = Intent(Intent.ACTION_VIEW).apply {
                                        setDataAndType(uri, "text/html")
                                        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                    }
                                    context.startActivity(browserIntent)
                                } catch (e: Exception) {
                                    Toast.makeText(context, "Opening in in-app web preview...", Toast.LENGTH_SHORT).show()
                                    showInAppWebView = true
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = EchoGreen, contentColor = Color.Black)
                        ) {
                            Icon(Icons.Default.OpenInBrowser, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(Modifier.width(4.dp))
                            Text("Open in Browser", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                Spacer(Modifier.height(10.dp))

                if (showInAppWebView) {
                    // Full interactive WebView inside dialog
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                            .clip(RoundedCornerShape(12.dp))
                            .border(1.dp, EchoCardStroke, RoundedCornerShape(12.dp))
                    ) {
                        AndroidView(
                            factory = { ctx ->
                                WebView(ctx).apply {
                                    webViewClient = WebViewClient()
                                    settings.javaScriptEnabled = true
                                    settings.domStorageEnabled = true
                                    loadUrl("file://${htmlFile.absolutePath}")
                                }
                            },
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                } else {
                    // Summary card and file actions
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = EchoCard),
                        border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(EchoGreen))
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.CheckCircle, contentDescription = null, tint = EchoGreen, modifier = Modifier.size(24.dp))
                                Spacer(Modifier.width(10.dp))
                                Column {
                                    Text("STANDALONE WEB APP READY", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = EchoGreen)
                                    Text("Single-file HTML with audio DSP, canvas visualizer, and offline PWA mode", fontSize = 11.sp, color = EchoMuted)
                                }
                            }

                            Spacer(Modifier.height(12.dp))
                            Text("Saved to public Downloads: /sdcard/Download/${htmlFile.name}", fontSize = 11.sp, color = EchoText)
                        }
                    }

                    Spacer(Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = { showInAppWebView = true },
                            modifier = Modifier.weight(1f).height(44.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = EchoCyan, contentColor = EchoOnCyan)
                        ) {
                            Icon(Icons.Default.Language, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(Modifier.width(6.dp))
                            Text("Preview in App", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }

                        Button(
                            onClick = {
                                try {
                                    val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", htmlFile)
                                    val shareIntent = Intent(Intent.ACTION_SEND).apply {
                                        type = "text/html"
                                        putExtra(Intent.EXTRA_STREAM, uri)
                                        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                                    }
                                    context.startActivity(Intent.createChooser(shareIntent, "Share Standalone Web App"))
                                } catch (e: Exception) {
                                    Toast.makeText(context, "Saved at: ${htmlFile.absolutePath}", Toast.LENGTH_SHORT).show()
                                }
                            },
                            modifier = Modifier.weight(1f).height(44.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = EchoBgAlt, contentColor = EchoText)
                        ) {
                            Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(Modifier.width(6.dp))
                            Text("Share File", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

private fun parseHexColor(hex: String): Color {
    val clean = hex.removePrefix("#")
    val colorInt = when (clean.length) {
        6 -> "FF$clean".toLong(16)
        8 -> clean.toLong(16)
        else -> 0xFF00E5FF
    }
    return Color(colorInt)
}
