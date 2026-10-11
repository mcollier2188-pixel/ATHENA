package com.example.ui.screens

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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Thermostat
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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import kotlin.math.PI
import kotlin.math.sin
import kotlin.math.sqrt

data class ChamberAcousticNode(
    val id: String,
    val name: String,
    val material: String,
    val lengthM: Double,
    val widthM: Double,
    val heightM: Double,
    val fundamentalBaseHz: Double,
    val keyHarmonicHz: Double,
    val acousticRole: String,
    val bookExcerpt: String,
    val xPercent: Float,
    val yPercent: Float
)

@Composable
fun KingsChamberWaveguideScreen(
    toneGenerator: HarmonicToneGenerator = HarmonicToneGenerator.SHARED,
    modifier: Modifier = Modifier
) {
    val chamberNodes = remember {
        listOf(
            ChamberAcousticNode(
                id = "kings_vault",
                name = "King's Chamber Vault",
                material = "Red Aswan Granite (High Quartz Content)",
                lengthM = 10.47,
                widthM = 5.23,
                heightM = 5.84,
                fundamentalBaseHz = 16.38,
                keyHarmonicHz = 121.2,
                acousticRole = "Primary Resonant Cavity (2:1:√5 Sacred Proportion). Operates as a standing wave acoustic amplifier tuned to 121 Hz harmonic mode.",
                bookExcerpt = "The red granite vault acts not merely as a tomb, but as a monolithic acoustic waveguide whose dimensions generate pure longitudinal standing waves.",
                xPercent = 0.58f,
                yPercent = 0.44f
            ),
            ChamberAcousticNode(
                id = "granite_coffer",
                name = "Aswan Granite Coffer",
                material = "Hollow Monolithic Rose Granite",
                lengthM = 2.28,
                widthM = 0.98,
                heightM = 1.05,
                fundamentalBaseHz = 117.0,
                keyHarmonicHz = 438.0,
                acousticRole = "Acoustic Sarcophagus Resonator. Hollow interior resonates at 117 Hz; structural stone rings at 438 Hz (near A432 sacred pitch).",
                bookExcerpt = "Struck lightly with a mallet, the coffer rings like a monumental bell, projecting acoustic overtone series that envelop the entire chamber.",
                xPercent = 0.54f,
                yPercent = 0.45f
            ),
            ChamberAcousticNode(
                id = "grand_gallery",
                name = "The Grand Gallery",
                material = "Corbelled Mokattam Limestone",
                lengthM = 46.7,
                widthM = 2.09,
                heightM = 8.60,
                fundamentalBaseHz = 3.67,
                keyHarmonicHz = 73.4,
                acousticRole = "Quarter-wave Exponential Stepped Horn Waveguide. 27 pairs of acoustic slots once held acoustic resonator bars.",
                bookExcerpt = "The ascending corbelled vault of the Grand Gallery funnels acoustic energy upward into the King's Chamber, stepping up acoustic velocity.",
                xPercent = 0.45f,
                yPercent = 0.58f
            ),
            ChamberAcousticNode(
                id = "subterranean_pit",
                name = "Subterranean Pit",
                material = "Living Bedrock Limestone",
                lengthM = 14.1,
                widthM = 8.3,
                heightM = 3.5,
                fundamentalBaseHz = 7.83,
                keyHarmonicHz = 14.3,
                acousticRole = "Infrasonic Helmholtz Bedrock Resonator. Coupled directly into the Earth's crust to resonate with the Schumann fundamental.",
                bookExcerpt = "Deep beneath the desert bedrock, the rough-hewn subterranean pit anchors the pyramid to tectonic crustal pulses.",
                xPercent = 0.50f,
                yPercent = 0.88f
            ),
            ChamberAcousticNode(
                id = "queens_chamber",
                name = "Queen's Chamber",
                material = "Fine White Limestone",
                lengthM = 5.75,
                widthM = 5.23,
                heightM = 6.22,
                fundamentalBaseHz = 29.8,
                keyHarmonicHz = 89.4,
                acousticRole = "Gabled Secondary Acoustic Chamber with niche resonator waveguide.",
                bookExcerpt = "The Queen's Chamber provides the stabilizing harmonic overtone anchor in the mid-tier acoustic network.",
                xPercent = 0.50f,
                yPercent = 0.62f
            )
        )
    }

    var selectedNode by remember { mutableStateOf(chamberNodes[0]) }
    var chamberTemperatureC by remember { mutableFloatStateOf(20.0f) }
    var harmonicMultiplier by remember { mutableIntStateOf(1) } // 1x Fundamental, 2x, 3x

    val isTonePlaying by toneGenerator.isPlayingState.collectAsState()

    // Speed of sound in air calculated for ambient temperature: v = 331.3 * sqrt(1 + T / 273.15)
    val speedOfSound = remember(chamberTemperatureC) {
        331.3 * sqrt(1.0 + chamberTemperatureC / 273.15)
    }

    // Dynamic standing wave frequency based on length and speed of sound: f = v / (2 * L)
    val calculatedFundamentalHz = remember(selectedNode, speedOfSound) {
        val baseL = selectedNode.lengthM
        (speedOfSound / (2.0 * baseL))
    }

    val activeAcousticHz = remember(calculatedFundamentalHz, harmonicMultiplier, selectedNode) {
        if (harmonicMultiplier == 1) calculatedFundamentalHz
        else if (harmonicMultiplier == 2) selectedNode.keyHarmonicHz
        else calculatedFundamentalHz * 3.0
    }

    val waveTransition = rememberInfiniteTransition(label = "standing_wave_anim")
    val wavePhase by waveTransition.animateFloat(
        initialValue = 0f,
        targetValue = 2f * PI.toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "wave_phase"
    )

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(EchoBg)
            .testTag("kings_chamber_waveguide_screen"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Hero Header Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = EchoCard),
                border = CardDefaults.outlinedCardBorder().copy(
                    brush = Brush.horizontalGradient(listOf(Color(0xFFE07A5F), EchoCyan))
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
                                text = "KING'S CHAMBER WAVEGUIDE",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFE07A5F),
                                letterSpacing = 1.sp
                            )
                            Text(
                                text = "Acoustic Architecture & Standing Wave Nodes",
                                fontSize = 11.sp,
                                color = EchoMuted
                            )
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFFE07A5F).copy(alpha = 0.15f))
                                .border(1.dp, Color(0xFFE07A5F).copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                                .padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "${"%.1f".format(activeAcousticHz)} Hz",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace,
                                color = Color(0xFFE07A5F)
                            )
                        }
                    }

                    Spacer(Modifier.height(10.dp))
                    Text(
                        text = "Explores the monumental acoustic engineering of the Great Pyramid of Giza. Tap any chamber node below to calculate standing wave frequencies f = v / (2L) and listen to the synthesized resonant signature.",
                        fontSize = 12.sp,
                        color = EchoText,
                        lineHeight = 16.sp
                    )
                }
            }
        }

        // Architectural Cross-Section Diagram
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = EchoBgAlt),
                border = CardDefaults.outlinedCardBorder().copy(brush = Brush.verticalGradient(listOf(EchoCardStroke, EchoCyan.copy(alpha = 0.4f))))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp)
                ) {
                    Text(
                        text = "GREAT PYRAMID CROSS-SECTION SCHEMATIC",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = EchoCyan
                    )
                    Spacer(Modifier.height(8.dp))

                    // Diagram Box
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(230.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFF090D14))
                            .border(1.dp, EchoCardStroke, RoundedCornerShape(12.dp))
                    ) {
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            val w = size.width
                            val h = size.height

                            // Draw Pyramid Triangular Profile
                            val apex = Offset(w * 0.5f, h * 0.12f)
                            val leftBase = Offset(w * 0.08f, h * 0.82f)
                            val rightBase = Offset(w * 0.92f, h * 0.82f)

                            val pyramidPath = Path().apply {
                                moveTo(apex.x, apex.y)
                                lineTo(leftBase.x, leftBase.y)
                                lineTo(rightBase.x, rightBase.y)
                                close()
                            }

                            drawPath(
                                path = pyramidPath,
                                color = Color(0xFF162130),
                                style = Stroke(width = 2.dp.toPx())
                            )

                            // Ground level bedrock line
                            drawLine(
                                color = Color(0xFF2C3E50),
                                start = Offset(0f, h * 0.82f),
                                end = Offset(w, h * 0.82f),
                                strokeWidth = 2.dp.toPx()
                            )

                            // Descending passage line
                            drawLine(
                                color = EchoCyan.copy(alpha = 0.4f),
                                start = Offset(w * 0.28f, h * 0.62f),
                                end = Offset(w * 0.50f, h * 0.88f),
                                strokeWidth = 3.dp.toPx()
                            )

                            // Ascending passage line
                            drawLine(
                                color = EchoCyan.copy(alpha = 0.4f),
                                start = Offset(w * 0.35f, h * 0.70f),
                                end = Offset(w * 0.58f, h * 0.44f),
                                strokeWidth = 3.5.dp.toPx()
                            )

                            // King's Chamber box
                            val kx = w * 0.58f
                            val ky = h * 0.44f
                            drawRect(
                                color = if (selectedNode.id == "kings_vault") Color(0xFFE07A5F) else Color(0xFF5D4037),
                                topLeft = Offset(kx - 16.dp.toPx(), ky - 10.dp.toPx()),
                                size = Size(32.dp.toPx(), 20.dp.toPx())
                            )

                            // Coffer marker inside King's chamber
                            drawRect(
                                color = if (selectedNode.id == "granite_coffer") Color(0xFFFFD166) else Color(0xFFBCAAA4),
                                topLeft = Offset(kx - 12.dp.toPx(), ky - 4.dp.toPx()),
                                size = Size(8.dp.toPx(), 8.dp.toPx())
                            )

                            // Queen's Chamber box
                            val qx = w * 0.50f
                            val qy = h * 0.62f
                            drawRect(
                                color = if (selectedNode.id == "queens_chamber") EchoCyan else Color(0xFF37474F),
                                topLeft = Offset(qx - 12.dp.toPx(), qy - 8.dp.toPx()),
                                size = Size(24.dp.toPx(), 16.dp.toPx())
                            )

                            // Subterranean pit box
                            val sx = w * 0.50f
                            val sy = h * 0.88f
                            drawRect(
                                color = if (selectedNode.id == "subterranean_pit") EchoGreen else Color(0xFF263238),
                                topLeft = Offset(sx - 18.dp.toPx(), sy - 8.dp.toPx()),
                                size = Size(36.dp.toPx(), 16.dp.toPx())
                            )

                            // Active node selection highlight ring
                            val activeX = w * selectedNode.xPercent
                            val activeY = h * selectedNode.yPercent

                            drawCircle(
                                color = Color.White.copy(alpha = 0.3f),
                                radius = 24.dp.toPx(),
                                center = Offset(activeX, activeY),
                                style = Stroke(width = 2.dp.toPx())
                            )

                            drawCircle(
                                color = Color(0xFFE07A5F),
                                radius = 6.dp.toPx(),
                                center = Offset(activeX, activeY)
                            )
                        }

                        // Floating badge
                        Box(
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(10.dp)
                                .clip(RoundedCornerShape(6.dp))
                                .background(Color.Black.copy(alpha = 0.7f))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text("Active: ${selectedNode.name}", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFFE07A5F))
                        }
                    }

                    Spacer(Modifier.height(10.dp))

                    // Chamber Node Selector Chips
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(chamberNodes) { node ->
                            val isSelected = selectedNode.id == node.id
                            FilterChip(
                                selected = isSelected,
                                onClick = { selectedNode = node },
                                label = { Text(node.name, fontSize = 11.sp, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = Color(0xFFE07A5F),
                                    selectedLabelColor = Color.White,
                                    containerColor = EchoCard,
                                    labelColor = EchoText
                                )
                            )
                        }
                    }
                }
            }
        }

        // Standing Wave Node Visualizer Canvas
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = EchoCard),
                border = CardDefaults.outlinedCardBorder().copy(brush = Brush.horizontalGradient(listOf(EchoCardStroke, Color(0xFFE07A5F).copy(alpha = 0.3f))))
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "STANDING WAVE NODAL PATTERN (λ/2 Mode)",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = EchoText
                        )
                        Text(
                            text = "v = ${"%.1f".format(speedOfSound)} m/s",
                            fontSize = 11.sp,
                            fontFamily = FontFamily.Monospace,
                            color = EchoCyan
                        )
                    }

                    // Wave Canvas (Height 80dp)
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(80.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFF070B12))
                            .border(1.dp, EchoCardStroke, RoundedCornerShape(8.dp))
                    ) {
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            val w = size.width
                            val h = size.height
                            val midY = h / 2f

                            // Center axis
                            drawLine(
                                color = Color.Gray.copy(alpha = 0.2f),
                                start = Offset(0f, midY),
                                end = Offset(w, midY),
                                strokeWidth = 1f
                            )

                            // Standing wave pressure curve
                            val wavePath = Path()
                            val numPoints = 80
                            val modes = harmonicMultiplier.toFloat()

                            for (i in 0..numPoints) {
                                val x = (i.toFloat() / numPoints) * w
                                val normX = i.toFloat() / numPoints
                                val amp = sin(normX * PI.toFloat() * modes) * (h * 0.35f) * sin(wavePhase)
                                val y = midY + amp
                                if (i == 0) wavePath.moveTo(x, y) else wavePath.lineTo(x, y)
                            }

                            drawPath(
                                path = wavePath,
                                color = Color(0xFFE07A5F),
                                style = Stroke(width = 2.5.dp.toPx(), cap = StrokeCap.Round)
                            )

                            // Antinode / Node markers
                            for (m in 1..harmonicMultiplier) {
                                val nodeX = (m.toFloat() / (harmonicMultiplier + 1)) * w
                                drawCircle(
                                    color = EchoCyan,
                                    radius = 4.dp.toPx(),
                                    center = Offset(nodeX, midY)
                                )
                            }
                        }
                    }

                    // Harmonic Mode Selector
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = { harmonicMultiplier = 1 },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.outlinedButtonColors(
                                containerColor = if (harmonicMultiplier == 1) Color(0xFFE07A5F).copy(alpha = 0.2f) else EchoBgAlt,
                                contentColor = if (harmonicMultiplier == 1) Color(0xFFE07A5F) else EchoText
                            )
                        ) {
                            Text("1x Fundamental", fontSize = 10.sp)
                        }
                        OutlinedButton(
                            onClick = { harmonicMultiplier = 2 },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.outlinedButtonColors(
                                containerColor = if (harmonicMultiplier == 2) Color(0xFFE07A5F).copy(alpha = 0.2f) else EchoBgAlt,
                                contentColor = if (harmonicMultiplier == 2) Color(0xFFE07A5F) else EchoText
                            )
                        ) {
                            Text("2x Key Mode", fontSize = 10.sp)
                        }
                        OutlinedButton(
                            onClick = { harmonicMultiplier = 3 },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.outlinedButtonColors(
                                containerColor = if (harmonicMultiplier == 3) Color(0xFFE07A5F).copy(alpha = 0.2f) else EchoBgAlt,
                                contentColor = if (harmonicMultiplier == 3) Color(0xFFE07A5F) else EchoText
                            )
                        ) {
                            Text("3x Overtone", fontSize = 10.sp)
                        }
                    }

                    // Play Resonant Sound Button
                    Button(
                        onClick = {
                            if (isTonePlaying) {
                                toneGenerator.stop()
                            } else {
                                toneGenerator.updateFrequencies(listOf(activeAcousticHz))
                                toneGenerator.start(frequencies = listOf(activeAcousticHz))
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isTonePlaying) Color(0xFFE07A5F) else EchoCyan,
                            contentColor = Color.Black
                        ),
                        modifier = Modifier.fillMaxWidth().testTag("play_chamber_frequency")
                    ) {
                        Icon(
                            if (isTonePlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(
                            text = if (isTonePlaying) "Stop Resonant Tone" else "Synthesize Chamber Resonance (${"%.1f".format(activeAcousticHz)} Hz)",
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }

        // Physical Dimensions & Acoustic Equation Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = EchoCard),
                border = CardDefaults.outlinedCardBorder().copy(brush = Brush.horizontalGradient(listOf(EchoCardStroke, EchoCardStroke)))
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "DIMENSIONS & EQUATIONS",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFE07A5F)
                    )

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Column {
                            Text("Length (L)", fontSize = 10.sp, color = EchoMuted)
                            Text("${selectedNode.lengthM} m", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = EchoText)
                        }
                        Column {
                            Text("Width (W)", fontSize = 10.sp, color = EchoMuted)
                            Text("${selectedNode.widthM} m", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = EchoText)
                        }
                        Column {
                            Text("Height (H)", fontSize = 10.sp, color = EchoMuted)
                            Text("${selectedNode.heightM} m", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = EchoText)
                        }
                        Column {
                            Text("Calculated f₁", fontSize = 10.sp, color = EchoMuted)
                            Text("${"%.1f".format(calculatedFundamentalHz)} Hz", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = EchoGreen)
                        }
                    }

                    HorizontalDivider(color = EchoCardStroke)

                    Text(
                        text = selectedNode.acousticRole,
                        fontSize = 12.sp,
                        color = EchoText,
                        lineHeight = 16.sp
                    )

                    Text(
                        text = "“${selectedNode.bookExcerpt}”",
                        fontSize = 11.sp,
                        fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                        color = EchoMuted,
                        lineHeight = 15.sp
                    )

                    HorizontalDivider(color = EchoCardStroke)

                    // Ambient Chamber Temperature Slider
                    Column {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Ambient Chamber Temperature: ${chamberTemperatureC.toInt()}°C", fontSize = 11.sp, color = EchoText)
                            Text("Speed of Sound: ${"%.1f".format(speedOfSound)} m/s", fontSize = 11.sp, color = EchoCyan)
                        }
                        Slider(
                            value = chamberTemperatureC,
                            onValueChange = { chamberTemperatureC = it },
                            valueRange = 15f..35f,
                            colors = SliderDefaults.colors(thumbColor = Color(0xFFE07A5F), activeTrackColor = Color(0xFFE07A5F))
                        )
                    }
                }
            }
        }
    }
}
