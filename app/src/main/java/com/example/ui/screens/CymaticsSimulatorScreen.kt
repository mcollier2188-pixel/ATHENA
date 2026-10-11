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
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
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
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.TouchApp
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
import java.util.Random
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sin

data class CymaticsPreset(
    val title: String,
    val frequencyHz: Double,
    val m: Int,
    val n: Int,
    val a: Double,
    val b: Double,
    val description: String
)

data class Particle(
    var x: Float,
    var y: Float,
    var vx: Float = 0f,
    var vy: Float = 0f
)

enum class ParticlePalette(val label: String, val primary: Color, val accent: Color) {
    SOLENT_CYAN("Cyan Biolum", EchoCyan, Color(0xFF80FFEA)),
    GOLD_STARLIGHT("Solar Gold", Color(0xFFFFD166), Color(0xFFFFE89E)),
    EMERALD_MATRIX("Emerald Glow", Color(0xFF06D6A0), Color(0xFF80FFD5)),
    AMETHYST_VORTEX("Sacred Violet", Color(0xFFB388FF), Color(0xFFE1BEE7))
}

@Composable
fun CymaticsSimulatorScreen(
    toneGenerator: HarmonicToneGenerator = HarmonicToneGenerator.SHARED,
    modifier: Modifier = Modifier
) {
    val presets = remember {
        listOf(
            CymaticsPreset(
                title = "432 Hz Sacred Mandala",
                frequencyHz = 432.0,
                m = 3,
                n = 3,
                a = 1.0,
                b = -1.0,
                description = "Verdi pitch harmonic resonance forming a 12-petaled floral mandala."
            ),
            CymaticsPreset(
                title = "528 Hz Solfeggio Repair",
                frequencyHz = 528.0,
                m = 5,
                n = 3,
                a = 1.0,
                b = 0.8,
                description = "Transformation Solfeggio frequency manifesting in an intricate 8-fold lattice."
            ),
            CymaticsPreset(
                title = "111 Hz Megalithic Vault",
                frequencyHz = 111.0,
                m = 2,
                n = 4,
                a = 1.0,
                b = -1.0,
                description = "Hal Saflieni Hypogeum resonant mode with cross-symmetric acoustic nodal nodes."
            ),
            CymaticsPreset(
                title = "121 Hz King's Chamber",
                frequencyHz = 121.2,
                m = 3,
                n = 5,
                a = 1.0,
                b = 1.0,
                description = "Acoustic standing wave of the red granite chamber of the Great Pyramid."
            ),
            CymaticsPreset(
                title = "42.6 Hz Solent Bedrock",
                frequencyHz = 42.6,
                m = 2,
                n = 2,
                a = 1.0,
                b = -1.0,
                description = "Fundamental acoustic carrier frequency of the Solent geological basin."
            ),
            CymaticsPreset(
                title = "7.83 Hz Schumann Cavity",
                frequencyHz = 7.83,
                m = 1,
                n = 3,
                a = 1.0,
                b = 1.0,
                description = "Earth-ionosphere cavity fundamental resonance forming circular nodal orbits."
            ),
            CymaticsPreset(
                title = "741 Hz Intuitive Awaken",
                frequencyHz = 741.0,
                m = 6,
                n = 4,
                a = 1.0,
                b = -1.0,
                description = "High-frequency Solfeggio mode generating ultra-fine cellular geometry."
            )
        )
    }

    var selectedPreset by remember { mutableStateOf(presets[0]) }
    var currentM by remember { mutableIntStateOf(presets[0].m) }
    var currentN by remember { mutableIntStateOf(presets[0].n) }
    var currentA by remember { mutableDoubleStateOf(presets[0].a) }
    var currentB by remember { mutableDoubleStateOf(presets[0].b) }
    var frequencyHz by remember { mutableDoubleStateOf(presets[0].frequencyHz) }

    var vibrationIntensity by remember { mutableFloatStateOf(0.85f) }
    var isSimulating by remember { mutableStateOf(true) }
    var syncAudioWithTone by remember { mutableStateOf(false) }
    var selectedPalette by remember { mutableStateOf(ParticlePalette.SOLENT_CYAN) }

    val isTonePlaying by toneGenerator.isPlayingState.collectAsState()

    // 1,400 particle array initialized randomly on square plate [-1, 1]
    val particleCount = 1400
    val particles = remember {
        val rand = Random(42)
        Array(particleCount) {
            Particle(
                x = (rand.nextFloat() * 2f - 1f) * 0.92f,
                y = (rand.nextFloat() * 2f - 1f) * 0.92f
            )
        }
    }

    var disturbancePoint by remember { mutableStateOf<Offset?>(null) }
    var frameTick by remember { mutableIntStateOf(0) }

    fun scatterParticles() {
        val rand = Random()
        for (p in particles) {
            p.x = (rand.nextFloat() * 2f - 1f) * 0.92f
            p.y = (rand.nextFloat() * 2f - 1f) * 0.92f
            p.vx = 0f
            p.vy = 0f
        }
        frameTick++
    }

    // Audio Sync Handler
    LaunchedEffect(syncAudioWithTone, frequencyHz) {
        if (syncAudioWithTone) {
            toneGenerator.updateFrequencies(listOf(frequencyHz))
            toneGenerator.start(frequencies = listOf(frequencyHz))
        } else if (isTonePlaying && !syncAudioWithTone) {
            toneGenerator.stop()
        }
    }

    // Physics Animation Loop
    LaunchedEffect(isSimulating, currentM, currentN, currentA, currentB, vibrationIntensity) {
        if (!isSimulating) return@LaunchedEffect
        val rand = Random()
        val pi = PI.toFloat()
        val intensity = vibrationIntensity

        while (isSimulating) {
            withFrameNanos { _ ->
                val mVal = currentM.toFloat()
                val nVal = currentN.toFloat()
                val aVal = currentA.toFloat()
                val bVal = currentB.toFloat()

                val step = 0.024f * intensity

                for (p in particles) {
                    // Chladni 2D Wave amplitude w(x, y):
                    // w = a*sin(n*pi*x)*sin(m*pi*y) + b*sin(m*pi*x)*sin(n*pi*y)
                    val sinNX = sin(nVal * pi * p.x)
                    val cosNX = cos(nVal * pi * p.x)
                    val sinMY = sin(mVal * pi * p.y)
                    val cosMY = cos(mVal * pi * p.y)

                    val sinMX = sin(mVal * pi * p.x)
                    val cosMX = cos(mVal * pi * p.x)
                    val sinNY = sin(nVal * pi * p.y)
                    val cosNY = cos(nVal * pi * p.y)

                    val w = aVal * sinNX * sinMY + bVal * sinMX * sinNY

                    // Spatial gradients dw/dx and dw/dy
                    val dwDx = aVal * nVal * pi * cosNX * sinMY + bVal * mVal * pi * cosMX * sinNY
                    val dwDy = aVal * mVal * pi * sinNX * cosMY + bVal * nVal * pi * sinMX * cosNY

                    // Sand migration force pushes particles away from antinodes toward nodal valleys (w = 0)
                    // F = -w * grad(w)
                    var fx = -w * dwDx * step
                    var fy = -w * dwDy * step

                    // Thermal acoustic agitation (Brownian shake proportional to |w|)
                    val absW = abs(w)
                    val jitter = absW * 0.018f * intensity
                    fx += (rand.nextFloat() * 2f - 1f) * jitter
                    fy += (rand.nextFloat() * 2f - 1f) * jitter

                    // Apply touch disturbance damping if finger is touching
                    disturbancePoint?.let { touch ->
                        val dx = p.x - touch.x
                        val dy = p.y - touch.y
                        val distSq = dx * dx + dy * dy
                        if (distSq < 0.08f) {
                            val push = (0.08f - distSq) * 0.2f
                            fx += dx * push
                            fy += dy * push
                        }
                    }

                    p.vx = (p.vx * 0.65f) + fx
                    p.vy = (p.vy * 0.65f) + fy

                    p.x += p.vx
                    p.y += p.vy

                    // Plate boundary elastic reflection
                    if (p.x < -0.92f) { p.x = -0.92f; p.vx = -p.vx * 0.5f }
                    if (p.x > 0.92f) { p.x = 0.92f; p.vx = -p.vx * 0.5f }
                    if (p.y < -0.92f) { p.y = -0.92f; p.vy = -p.vy * 0.5f }
                    if (p.y > 0.92f) { p.y = 0.92f; p.vy = -p.vy * 0.5f }
                }
                frameTick++
            }
        }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(EchoBg)
            .testTag("cymatics_simulator_screen"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Hero Header Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = EchoCard),
                border = CardDefaults.outlinedCardBorder().copy(
                    brush = Brush.horizontalGradient(listOf(selectedPalette.primary, selectedPalette.accent))
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
                                text = "CHLADNI CYMATICS",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = selectedPalette.primary,
                                letterSpacing = 1.sp
                            )
                            Text(
                                text = "Acoustic Geometry & Nodal Plate Physics",
                                fontSize = 11.sp,
                                color = EchoMuted
                            )
                        }
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(selectedPalette.primary.copy(alpha = 0.15f))
                                .border(1.dp, selectedPalette.primary.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                                .padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "${"%.1f".format(frequencyHz)} Hz",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace,
                                color = selectedPalette.primary
                            )
                        }
                    }

                    Spacer(Modifier.height(10.dp))
                    Text(
                        text = "Visualizes how harmonic sound frequencies organize matter. Over 1,400 sand particles assemble into nodal lines where plate displacement is zero: w(x,y) = a·sin(nπx)sin(mπy) + b·sin(mπx)sin(nπy) = 0.",
                        fontSize = 12.sp,
                        color = EchoText,
                        lineHeight = 16.sp
                    )
                }
            }
        }

        // The Chladni Plate Canvas
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = EchoBgAlt),
                border = CardDefaults.outlinedCardBorder().copy(brush = Brush.radialGradient(listOf(selectedPalette.primary.copy(alpha = 0.5f), EchoCardStroke)))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(0.92f)
                            .aspectRatio(1f)
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color(0xFF090D14))
                            .border(2.dp, selectedPalette.primary.copy(alpha = 0.6f), RoundedCornerShape(16.dp))
                            .pointerInput(Unit) {
                                detectTapGestures(
                                    onPress = { offset ->
                                        val normX = (offset.x / size.width) * 2f - 1f
                                        val normY = (offset.y / size.height) * 2f - 1f
                                        disturbancePoint = Offset(normX, normY)
                                        tryAwaitRelease()
                                        disturbancePoint = null
                                    }
                                )
                            }
                            .pointerInput(Unit) {
                                detectDragGestures(
                                    onDragStart = { offset ->
                                        val normX = (offset.x / size.width) * 2f - 1f
                                        val normY = (offset.y / size.height) * 2f - 1f
                                        disturbancePoint = Offset(normX, normY)
                                    },
                                    onDrag = { change, _ ->
                                        val normX = (change.position.x / size.width) * 2f - 1f
                                        val normY = (change.position.y / size.height) * 2f - 1f
                                        disturbancePoint = Offset(normX, normY)
                                    },
                                    onDragEnd = { disturbancePoint = null },
                                    onDragCancel = { disturbancePoint = null }
                                )
                            }
                            .testTag("chladni_plate_canvas")
                    ) {
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            // Touch disturbance indicator
                            disturbancePoint?.let { touch ->
                                val screenX = ((touch.x + 1f) / 2f) * size.width
                                val screenY = ((touch.y + 1f) / 2f) * size.height
                                drawCircle(
                                    color = Color.White.copy(alpha = 0.35f),
                                    radius = 28.dp.toPx(),
                                    center = Offset(screenX, screenY),
                                    style = Stroke(width = 2.dp.toPx())
                                )
                            }

                            // Circular boundary resonance ring
                            drawCircle(
                                color = selectedPalette.primary.copy(alpha = 0.12f),
                                radius = size.minDimension * 0.46f,
                                center = center,
                                style = Stroke(width = 1.5.dp.toPx())
                            )

                            // Crosshair subtle nodal grid
                            drawLine(
                                color = selectedPalette.primary.copy(alpha = 0.08f),
                                start = Offset(size.width * 0.08f, center.y),
                                end = Offset(size.width * 0.92f, center.y),
                                strokeWidth = 1.dp.toPx()
                            )
                            drawLine(
                                color = selectedPalette.primary.copy(alpha = 0.08f),
                                start = Offset(center.x, size.height * 0.08f),
                                end = Offset(center.x, size.height * 0.92f),
                                strokeWidth = 1.dp.toPx()
                            )

                            // Render sand particles
                            val primaryColor = selectedPalette.primary
                            val accentColor = selectedPalette.accent
                            val halfW = size.width / 2f
                            val halfH = size.height / 2f

                            for (i in 0 until particleCount) {
                                val p = particles[i]
                                val px = halfW + p.x * halfW
                                val py = halfH + p.y * halfH
                                val particleColor = if (i % 3 == 0) accentColor else primaryColor
                                drawCircle(
                                    color = particleColor,
                                    radius = 1.8.dp.toPx(),
                                    center = Offset(px, py)
                                )
                            }
                        }

                        // Overlay badge
                        Box(
                            modifier = Modifier
                                .align(Alignment.TopStart)
                                .padding(10.dp)
                                .clip(RoundedCornerShape(6.dp))
                                .background(Color.Black.copy(alpha = 0.7f))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.TouchApp, contentDescription = null, tint = selectedPalette.primary, modifier = Modifier.size(12.dp))
                                Spacer(Modifier.width(4.dp))
                                Text("Touch / Drag to Damp", fontSize = 10.sp, color = EchoText)
                            }
                        }

                        Box(
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(10.dp)
                                .clip(RoundedCornerShape(6.dp))
                                .background(Color.Black.copy(alpha = 0.7f))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text("Mode (m=$currentM, n=$currentN)", fontSize = 10.sp, fontFamily = FontFamily.Monospace, color = selectedPalette.accent)
                        }
                    }

                    Spacer(Modifier.height(12.dp))

                    // Plate Action Bar
                    Row(
                        modifier = Modifier.fillMaxWidth(0.92f),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedButton(
                            onClick = { scatterParticles() },
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = selectedPalette.primary),
                            modifier = Modifier.testTag("scatter_sand_button")
                        ) {
                            Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(Modifier.width(6.dp))
                            Text("Scatter Sand", fontSize = 12.sp)
                        }

                        Button(
                            onClick = { isSimulating = !isSimulating },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isSimulating) selectedPalette.primary else EchoCard,
                                contentColor = if (isSimulating) Color.Black else EchoText
                            ),
                            modifier = Modifier.testTag("toggle_vibration_button")
                        ) {
                            Icon(
                                if (isSimulating) Icons.Default.Pause else Icons.Default.PlayArrow,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(Modifier.width(6.dp))
                            Text(if (isSimulating) "Vibrating" else "Paused", fontSize = 12.sp)
                        }
                    }
                }
            }
        }

        // Harmonic Presets
        item {
            Text(
                text = "SACRED HARMONIC PRESETS",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = EchoCyan,
                letterSpacing = 1.sp
            )
            Spacer(Modifier.height(8.dp))
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(presets) { preset ->
                    val isSelected = selectedPreset.title == preset.title
                    FilterChip(
                        selected = isSelected,
                        onClick = {
                            selectedPreset = preset
                            currentM = preset.m
                            currentN = preset.n
                            currentA = preset.a
                            currentB = preset.b
                            frequencyHz = preset.frequencyHz
                            scatterParticles()
                        },
                        label = {
                            Text(
                                preset.title,
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = selectedPalette.primary,
                            selectedLabelColor = Color.Black,
                            containerColor = EchoCard,
                            labelColor = EchoText
                        ),
                        modifier = Modifier.testTag("preset_${preset.m}_${preset.n}")
                    )
                }
            }
        }

        // Controls Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = EchoCard),
                border = CardDefaults.outlinedCardBorder().copy(brush = Brush.horizontalGradient(listOf(EchoCardStroke, selectedPalette.primary.copy(alpha = 0.3f))))
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    Text(
                        text = "WAVE EQUATION PARAMETERS",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = selectedPalette.primary
                    )

                    // Audio Link Toggle
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("Sync with Audio Tone Generator", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = EchoText)
                            Text("Audibly synthesizes the ${"%.1f".format(frequencyHz)} Hz frequency", fontSize = 11.sp, color = EchoMuted)
                        }
                        Switch(
                            checked = syncAudioWithTone,
                            onCheckedChange = { syncAudioWithTone = it },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = selectedPalette.primary,
                                checkedTrackColor = selectedPalette.primary.copy(alpha = 0.4f)
                            ),
                            modifier = Modifier.testTag("sync_audio_switch")
                        )
                    }

                    HorizontalDivider(color = EchoCardStroke)

                    // Mode m slider
                    Column {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Harmonic Mode m: $currentM", fontSize = 12.sp, color = EchoText)
                            Text("Radial symmetry", fontSize = 11.sp, color = EchoMuted)
                        }
                        Slider(
                            value = currentM.toFloat(),
                            onValueChange = { currentM = it.toInt() },
                            valueRange = 1f..8f,
                            steps = 6,
                            colors = SliderDefaults.colors(thumbColor = selectedPalette.primary, activeTrackColor = selectedPalette.primary)
                        )
                    }

                    // Mode n slider
                    Column {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Harmonic Mode n: $currentN", fontSize = 12.sp, color = EchoText)
                            Text("Lattice density", fontSize = 11.sp, color = EchoMuted)
                        }
                        Slider(
                            value = currentN.toFloat(),
                            onValueChange = { currentN = it.toInt() },
                            valueRange = 1f..8f,
                            steps = 6,
                            colors = SliderDefaults.colors(thumbColor = selectedPalette.primary, activeTrackColor = selectedPalette.primary)
                        )
                    }

                    // Vibration Power slider
                    Column {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Acoustic Amplitude Power: ${(vibrationIntensity * 100).toInt()}%", fontSize = 12.sp, color = EchoText)
                            Text("Plate excitation", fontSize = 11.sp, color = EchoMuted)
                        }
                        Slider(
                            value = vibrationIntensity,
                            onValueChange = { vibrationIntensity = it },
                            valueRange = 0.1f..1.5f,
                            colors = SliderDefaults.colors(thumbColor = selectedPalette.accent, activeTrackColor = selectedPalette.accent)
                        )
                    }

                    HorizontalDivider(color = EchoCardStroke)

                    // Particle Palette Selector
                    Column {
                        Text("Particle Glow Palette", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = EchoText)
                        Spacer(Modifier.height(8.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            ParticlePalette.values().forEach { pal ->
                                FilterChip(
                                    selected = selectedPalette == pal,
                                    onClick = { selectedPalette = pal },
                                    label = { Text(pal.label, fontSize = 11.sp) },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = pal.primary,
                                        selectedLabelColor = Color.Black,
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
