package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MicOff
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.RecordVoiceOver
import androidx.compose.material.icons.filled.Science
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.VideoLibrary
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ai.AthenaChatModel
import com.example.ai.AthenaService
import com.example.ai.ChatMessage
import com.example.ai.ImageGenerationItem
import com.example.ai.MusicGenerationItem
import com.example.ai.VideoGenerationItem
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
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Locale
import kotlin.math.sin

enum class AthenaSubTab(val title: String) {
    CHAT("Gemini Chat"),
    SCIENCE("Science Skills"),
    IMAGE_STUDIO("Create / Edit Image"),
    VEO_VIDEO("Veo 3 Video"),
    APK_BUILDER("APK Builder"),
    GENIUS_COUNCIL("Genius Council")
}

@Composable
fun AthenaScreen(
    onSaveToWorkspaceNote: (String) -> Unit = {},
    modifier: Modifier = Modifier
) {
    var selectedSubTab by rememberSaveable { mutableStateOf(AthenaSubTab.CHAT) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(EchoBg)
            .testTag("athena_screen")
    ) {
        // Athena Header
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
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
                            .background(Brush.linearGradient(listOf(EchoCyan, EchoGreen))),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = null,
                            tint = EchoOnCyan,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "ATHENA AI SUITE",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = EchoText,
                            letterSpacing = 1.sp
                        )
                        Text(
                            text = "Multimodal Scientific & Generative Intelligence",
                            fontSize = 11.sp,
                            color = EchoMuted
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(EchoBgAlt)
                        .border(1.dp, EchoCardStroke, RoundedCornerShape(12.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = if (AthenaService.isApiKeyConfigured) "ONLINE" else "HYBRID",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (AthenaService.isApiKeyConfigured) EchoGreen else EchoCyan
                    )
                }
            }

            Spacer(Modifier.height(10.dp))

            // Sub Tab Bar (No phone tab!)
            ScrollableTabRow(
                selectedTabIndex = selectedSubTab.ordinal,
                containerColor = EchoBg,
                contentColor = EchoCyan,
                edgePadding = 0.dp,
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        Modifier.tabIndicatorOffset(tabPositions[selectedSubTab.ordinal]),
                        color = EchoCyan
                    )
                },
                divider = {}
            ) {
                AthenaSubTab.entries.forEach { subTab ->
                    Tab(
                        selected = selectedSubTab == subTab,
                        onClick = { selectedSubTab = subTab },
                        text = {
                            Text(
                                text = subTab.title,
                                fontSize = 11.sp,
                                fontWeight = if (selectedSubTab == subTab) FontWeight.Bold else FontWeight.Normal,
                                color = if (selectedSubTab == subTab) EchoCyan else EchoMuted
                            )
                        },
                        modifier = Modifier.testTag("tab_${subTab.name.lowercase(Locale.ROOT)}")
                    )
                }
            }
        }

        // Active Sub-Feature View
        Box(
            modifier = Modifier
                .fillMaxSize()
                .weight(1f)
        ) {
            when (selectedSubTab) {
                AthenaSubTab.CHAT -> AthenaChatView(onSaveNote = onSaveToWorkspaceNote)
                AthenaSubTab.SCIENCE -> AthenaScienceSkillsView(onSaveNote = onSaveToWorkspaceNote)
                AthenaSubTab.IMAGE_STUDIO -> AthenaImageStudioView(onSaveNote = onSaveToWorkspaceNote)
                AthenaSubTab.VEO_VIDEO -> AthenaVeoVideoView(onSaveNote = onSaveToWorkspaceNote)
                AthenaSubTab.APK_BUILDER -> AthenaApkBuilderView(onSaveNote = onSaveToWorkspaceNote)
                AthenaSubTab.GENIUS_COUNCIL -> AthenaGeniusCouncilView(onSaveNote = onSaveToWorkspaceNote)
            }
        }
    }
}

// -------------------------------------------------------------
// 1. GEMINI MULTI-TURN CHAT (With Google Search & Maps Grounding)
// -------------------------------------------------------------
@Composable
private fun AthenaChatView(onSaveNote: (String) -> Unit = {}) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    var selectedModel by rememberSaveable { mutableStateOf(AthenaChatModel.FLASH) }
    var enableSearchGrounding by rememberSaveable { mutableStateOf(false) }
    var enableMapsGrounding by rememberSaveable { mutableStateOf(false) }

    var showClearDialog by remember { mutableStateOf(false) }

    val messages = remember {
        mutableStateListOf(
            ChatMessage(
                role = "model",
                content = "Greetings. I am ATHENA, the scientific and harmonic research intelligence for the ECHO-INTERFACE network. You can type your inquiry below. Google Search & Maps Grounding can be toggled above for verified live data.",
                modelUsed = selectedModel.displayName
            )
        )
    }

    var inputText by rememberSaveable { mutableStateOf("") }
    var isSending by remember { mutableStateOf(false) }
    val listState = rememberLazyListState()

    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    val quickPrompts = listOf(
        "Explain the 42.6 Hz bedrock frequency",
        "Search latest Solent seismic telemetry",
        "Map geological coordinates of N1 to N5",
        "Explain standing wave wave equation f = v / 2L",
        "How does the 11.74s tidal cycle affect resonance?"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(14.dp)
            .testTag("athena_chat_view")
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = EchoBgAlt),
            border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(EchoCardStroke))
        ) {
            Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = EchoCyan, modifier = Modifier.size(16.dp))
                        Text(
                            text = "ATHENA CHAT & GROUNDING",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = EchoCyan,
                            fontFamily = FontFamily.Monospace
                        )
                    }

                    IconButton(
                        onClick = { showClearDialog = true },
                        modifier = Modifier
                            .size(32.dp)
                            .testTag("clear_chat_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.DeleteSweep,
                            contentDescription = "Clear Chat",
                            tint = EchoMuted,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                // Model Selection Chips
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    AthenaChatModel.entries.forEach { model ->
                        FilterChip(
                            selected = selectedModel == model,
                            onClick = { selectedModel = model },
                            label = { Text(model.displayName, fontSize = 11.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = EchoCyan,
                                selectedLabelColor = EchoOnCyan,
                                containerColor = EchoCard,
                                labelColor = EchoText
                            ),
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                // Grounding Tool Toggles: Google Search & Google Maps
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    FilterChip(
                        selected = enableSearchGrounding,
                        onClick = { enableSearchGrounding = !enableSearchGrounding },
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, modifier = Modifier.size(14.dp)) },
                        label = { Text("Google Search", fontSize = 11.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = EchoGreen,
                            selectedLabelColor = EchoOnCyan,
                            containerColor = EchoCard,
                            labelColor = EchoText
                        ),
                        modifier = Modifier.weight(1f)
                    )

                    FilterChip(
                        selected = enableMapsGrounding,
                        onClick = { enableMapsGrounding = !enableMapsGrounding },
                        leadingIcon = { Icon(Icons.Default.LocationOn, contentDescription = null, modifier = Modifier.size(14.dp)) },
                        label = { Text("Google Maps", fontSize = 11.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = EchoGreen,
                            selectedLabelColor = EchoOnCyan,
                            containerColor = EchoCard,
                            labelColor = EchoText
                        ),
                        modifier = Modifier.weight(1f)
                    )
                }


            }
        }

        Spacer(Modifier.height(8.dp))

        // Quick Suggestion Chips
        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            items(quickPrompts) { prompt ->
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(14.dp))
                        .background(EchoCard)
                        .border(1.dp, EchoCardStroke, RoundedCornerShape(14.dp))
                        .clickable { inputText = prompt }
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(prompt, fontSize = 11.sp, color = EchoCyan)
                }
            }
        }

        Spacer(Modifier.height(8.dp))

        // Messages list
        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(messages, key = { it.id }) { msg ->
                val isUser = msg.role == "user"
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = if (isUser) Alignment.End else Alignment.Start
                ) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth(0.92f)
                            .clip(RoundedCornerShape(12.dp)),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isUser) EchoCyanSoft else EchoCard
                        ),
                        border = CardDefaults.outlinedCardBorder().copy(
                            brush = androidx.compose.ui.graphics.SolidColor(
                                if (isUser) EchoCyan else EchoCardStroke
                            )
                        )
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = if (isUser) "RESEARCHER" else "ATHENA (${msg.modelUsed.ifEmpty { selectedModel.displayName }})",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isUser) EchoCyan else EchoGreen,
                                    fontFamily = FontFamily.Monospace
                                )

                                if (!isUser) {
                                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                        IconButton(
                                            onClick = {
                                                onSaveNote("""[ATHENA Scientific Intelligence]
Date: ${java.text.SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.US).format(java.util.Date(msg.timestamp))}
Model: ${msg.modelUsed}

${msg.content}""")
                                                Toast.makeText(context, "Saved to Workspace Notes", Toast.LENGTH_SHORT).show()
                                            },
                                            modifier = Modifier.size(24.dp)
                                        ) {
                                            Icon(Icons.Default.Edit, contentDescription = "Save Note", tint = EchoCyan, modifier = Modifier.size(14.dp))
                                        }

                                        IconButton(
                                            onClick = {
                                                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                                clipboard.setPrimaryClip(ClipData.newPlainText("ATHENA Response", msg.content))
                                                Toast.makeText(context, "Copied to clipboard", Toast.LENGTH_SHORT).show()
                                            },
                                            modifier = Modifier.size(24.dp)
                                        ) {
                                            Icon(Icons.Default.ContentCopy, contentDescription = "Copy", tint = EchoMuted, modifier = Modifier.size(14.dp))
                                        }


                                    }
                                }
                            }

                            Spacer(Modifier.height(6.dp))
                            Text(
                                text = msg.content,
                                fontSize = 13.sp,
                                color = EchoText,
                                lineHeight = 19.sp
                            )

                            if (msg.sources.isNotEmpty()) {
                                Spacer(Modifier.height(6.dp))
                                Text(
                                    text = "Grounding Sources: ${msg.sources.joinToString(", ")}",
                                    fontSize = 10.sp,
                                    color = EchoGreen,
                                    fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                                )
                            }
                            if (msg.mapLocation != null) {
                                Spacer(Modifier.height(4.dp))
                                Text(
                                    text = "Geo Coordinates: ${msg.mapLocation}",
                                    fontSize = 10.sp,
                                    color = EchoCyan,
                                    fontFamily = FontFamily.Monospace
                                )
                            }
                        }
                    }
                }
            }
        }

        Spacer(Modifier.height(8.dp))

        // Input row
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            OutlinedTextField(
                value = inputText,
                onValueChange = { inputText = it },
                modifier = Modifier
                    .weight(1f)
                    .testTag("athena_chat_input"),
                placeholder = { Text("Ask Athena with Gemini...", fontSize = 13.sp) },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = EchoCyan,
                    unfocusedBorderColor = EchoCardStroke,
                    focusedTextColor = EchoText,
                    unfocusedTextColor = EchoText,
                    cursorColor = EchoCyan,
                    focusedContainerColor = EchoBgAlt,
                    unfocusedContainerColor = EchoBgAlt
                ),
                shape = RoundedCornerShape(20.dp),
                maxLines = 4
            )

            IconButton(
                onClick = {
                    if (inputText.isNotBlank() && !isSending) {
                        val userText = inputText.trim()
                        inputText = ""
                        messages.add(ChatMessage(role = "user", content = userText))
                        isSending = true

                        coroutineScope.launch {
                            val result = AthenaService.sendMultiTurnChat(
                                history = messages.toList(),
                                newMessage = userText,
                                selectedModel = selectedModel,
                                enableSearchGrounding = enableSearchGrounding,
                                enableMapsGrounding = enableMapsGrounding
                            )
                            val replyMsg = result.getOrElse {
                                ChatMessage(role = "model", content = "Could not process request.", modelUsed = selectedModel.displayName)
                            }
                            messages.add(replyMsg)
                            isSending = false
                        }
                    }
                },
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(EchoCyan)
                    .testTag("athena_chat_send_button"),
                enabled = !isSending && inputText.isNotBlank()
            ) {
                if (isSending) {
                    CircularProgressIndicator(modifier = Modifier.size(18.dp), color = EchoOnCyan, strokeWidth = 2.dp)
                } else {
                    Icon(Icons.AutoMirrored.Filled.Send, contentDescription = "Send", tint = EchoOnCyan, modifier = Modifier.size(18.dp))
                }
            }
        }
    }

    if (showClearDialog) {
        AlertDialog(
            onDismissRequest = { showClearDialog = false },
            title = { Text("Clear Chat Conversation?", color = EchoText, fontWeight = FontWeight.Bold) },
            text = { Text("This will reset the conversation thread.", color = EchoMuted) },
            confirmButton = {
                Button(
                    onClick = {
                        messages.clear()
                        messages.add(
                            ChatMessage(
                                role = "model",
                                content = "Greetings. I am ATHENA, the scientific and harmonic research intelligence for the ECHO-INTERFACE network. How can I assist your acoustic research today?",
                                modelUsed = selectedModel.displayName
                            )
                        )
                        showClearDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = EchoRed)
                ) {
                    Text("Clear Chat", color = Color.White)
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { showClearDialog = false }) {
                    Text("Cancel", color = EchoText)
                }
            },
            containerColor = EchoBgAlt
        )
    }
}

// -------------------------------------------------------------
// 2. SCIENCE SKILLS (Acoustic Standing Wave & Strata Physics)
// -------------------------------------------------------------
@Composable
private fun AthenaScienceSkillsView(onSaveNote: (String) -> Unit) {
    val context = LocalContext.current
    var speedOfSound by remember { mutableDoubleStateOf(3400.0) }
    var cavityLengthKm by remember { mutableDoubleStateOf(39.906) }
    var selectedHarmonicMode by remember { mutableIntStateOf(1) }
    var calculatedFreq by remember { mutableDoubleStateOf(42.600) }

    LaunchedEffect(speedOfSound, cavityLengthKm, selectedHarmonicMode) {
        val lengthMeters = cavityLengthKm * 1000.0
        calculatedFreq = (selectedHarmonicMode * speedOfSound) / (2.0 * lengthMeters)
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .testTag("athena_science_view"),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = EchoCard),
                border = CardDefaults.outlinedCardBorder().copy(brush = Brush.horizontalGradient(listOf(EchoCyan, EchoGreen)))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Science, contentDescription = null, tint = EchoCyan)
                        Spacer(Modifier.width(8.dp))
                        Text(
                            text = "Standing Wave Waveguide Solver",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = EchoText
                        )
                    }
                    Text(
                        text = "Formula: f_n = (n · v) / (2 · L) — Bedrock Acoustic Wave Equation",
                        fontSize = 12.sp,
                        color = EchoMuted,
                        fontFamily = FontFamily.Monospace,
                        modifier = Modifier.padding(top = 4.dp, bottom = 12.dp)
                    )

                    Text(
                        text = "Acoustic Velocity in Strata (v): ${speedOfSound.toInt()} m/s",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = EchoCyan
                    )
                    Slider(
                        value = speedOfSound.toFloat(),
                        onValueChange = { speedOfSound = it.toDouble() },
                        valueRange = 2000f..5000f,
                        colors = SliderDefaults.colors(thumbColor = EchoCyan, activeTrackColor = EchoCyan)
                    )
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("2000 m/s (Sandstone)", fontSize = 10.sp, color = EchoMuted)
                        Text("3400 m/s (Chalk/Limestone)", fontSize = 10.sp, color = EchoGreen)
                        Text("5000 m/s (Granite)", fontSize = 10.sp, color = EchoMuted)
                    }

                    Spacer(Modifier.height(14.dp))

                    Text(
                        text = "Waveguide Length (L): ${String.format(Locale.US, "%.2f km", cavityLengthKm)}",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = EchoCyan
                    )
                    Slider(
                        value = cavityLengthKm.toFloat(),
                        onValueChange = { cavityLengthKm = it.toDouble() },
                        valueRange = 10.0f..80.0f,
                        colors = SliderDefaults.colors(thumbColor = EchoCyan, activeTrackColor = EchoCyan)
                    )

                    Spacer(Modifier.height(14.dp))

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(EchoBgAlt)
                            .border(1.dp, EchoGreen, RoundedCornerShape(12.dp))
                            .padding(14.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "SOLVED RESONANT FREQUENCY",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = EchoMuted
                                )
                                Text(
                                    text = String.format(Locale.US, "%.3f Hz", calculatedFreq),
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = EchoGreen,
                                    fontFamily = FontFamily.Monospace
                                )
                            }

                            Button(
                                onClick = {
                                    val noteText = """[ATHENA Science Calculation]
Acoustic Velocity (v): ${speedOfSound.toInt()} m/s
Waveguide Length (L): ${String.format(Locale.US, "%.2f km", cavityLengthKm)}
Harmonic Mode (n): $selectedHarmonicMode
Resonant Frequency (f): ${String.format(Locale.US, "%.3f Hz", calculatedFreq)}
Match with 42.600 Hz nominal: ${String.format(Locale.US, "%+.3f Hz", calculatedFreq - 42.600)}"""
                                    onSaveNote(noteText)
                                    Toast.makeText(context, "Saved to Workspace Notes", Toast.LENGTH_SHORT).show()
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = EchoCyan, contentColor = EchoOnCyan)
                            ) {
                                Text("Save Note", fontSize = 12.sp)
                            }
                        }
                    }
                }
            }
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = EchoBgAlt),
                border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(EchoCardStroke))
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "Canonical Acoustic Metrics",
                        fontWeight = FontWeight.Bold,
                        color = EchoText,
                        fontSize = 14.sp
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = "• Planetary Fundamental: 42.600 Hz (Hilsea 1986 detection: 42.598 Hz)\n• Solent Tidal Envelope Period: 11.74 seconds\n• Q-Factor: Q ≈ 140 (Sharp bedrock acoustic resonance)\n• 2nd Harmonic (N1 Master Clock): 85.200 Hz\n• 4th Harmonic (N2 Farnworth Vault): 170.400 Hz\n• Lord Rayleigh Subterranean Standing Wave Constant: λ = v / f",
                        fontSize = 12.sp,
                        color = EchoMuted,
                        lineHeight = 20.sp
                    )
                }
            }
        }
    }
}

// -------------------------------------------------------------
// 3. GENERATE MUSIC (Lyria 3 Clip & Pro Preview)
// -------------------------------------------------------------
@Composable
private fun AthenaMusicView(onSaveNote: (String) -> Unit) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    var promptInput by rememberSaveable { mutableStateOf("Subterranean 42.6 Hz ambient harmonic drone with tidal swell rhythms") }
    var isFullLength by rememberSaveable { mutableStateOf(false) }
    var selectedGenre by rememberSaveable { mutableStateOf("Subterranean Ambient") }
    var isGenerating by remember { mutableStateOf(false) }
    var isPlayingId by remember { mutableStateOf<String?>(null) }

    val trackList = remember {
        mutableStateListOf(
            MusicGenerationItem(
                prompt = "Solent Tidal Bedrock Resonance 42.600 Hz Standing Wave",
                model = "lyria-3-clip-preview",
                durationSeconds = 30,
                genre = "Subterranean Ambient",
                title = "Solent Bedrock Harmonics"
            )
        )
    }

    val sampleGenres = listOf("Subterranean Ambient", "Cinematic Drone", "Orchestral Resonator", "Deep Electronic")

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .testTag("athena_music_view"),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = EchoCard),
                border = CardDefaults.outlinedCardBorder().copy(brush = Brush.horizontalGradient(listOf(EchoCyan, EchoGreen)))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.MusicNote, contentDescription = null, tint = EchoCyan)
                        Spacer(Modifier.width(8.dp))
                        Text(
                            text = "Lyria 3 Music Generator",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = EchoText
                        )
                    }
                    Text(
                        text = "Synthesize music clips (up to 30s with lyria-3-clip-preview) or full-length tracks with lyria-3-pro-preview.",
                        fontSize = 12.sp,
                        color = EchoMuted,
                        modifier = Modifier.padding(top = 4.dp, bottom = 12.dp)
                    )

                    OutlinedTextField(
                        value = promptInput,
                        onValueChange = { promptInput = it },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Music Concept Prompt", color = EchoMuted) },
                        maxLines = 3,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = EchoCyan,
                            unfocusedBorderColor = EchoCardStroke,
                            focusedTextColor = EchoText,
                            unfocusedTextColor = EchoText
                        )
                    )

                    Spacer(Modifier.height(10.dp))

                    LazyRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        items(sampleGenres) { g ->
                            FilterChip(
                                selected = selectedGenre == g,
                                onClick = { selectedGenre = g },
                                label = { Text(g, fontSize = 11.sp) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = EchoCyan,
                                    selectedLabelColor = EchoOnCyan,
                                    containerColor = EchoBgAlt,
                                    labelColor = EchoText
                                )
                            )
                        }
                    }

                    Spacer(Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            FilterChip(
                                selected = !isFullLength,
                                onClick = { isFullLength = false },
                                label = { Text("Short Clip (30s)", fontSize = 11.sp) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = EchoCyan,
                                    selectedLabelColor = EchoOnCyan,
                                    containerColor = EchoCard,
                                    labelColor = EchoText
                                )
                            )
                            FilterChip(
                                selected = isFullLength,
                                onClick = { isFullLength = true },
                                label = { Text("Full Track (Pro)", fontSize = 11.sp) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = EchoCyan,
                                    selectedLabelColor = EchoOnCyan,
                                    containerColor = EchoCard,
                                    labelColor = EchoText
                                )
                            )
                        }

                        Button(
                            onClick = {
                                if (promptInput.isNotBlank() && !isGenerating) {
                                    isGenerating = true
                                    coroutineScope.launch {
                                        val res = AthenaService.generateLyriaMusic(promptInput, isFullLength, selectedGenre)
                                        res.onSuccess { item ->
                                            trackList.add(0, item)
                                            Toast.makeText(context, "Lyria Music Generated!", Toast.LENGTH_SHORT).show()
                                        }
                                        isGenerating = false
                                    }
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = EchoCyan, contentColor = EchoOnCyan),
                            enabled = !isGenerating && promptInput.isNotBlank()
                        ) {
                            if (isGenerating) {
                                CircularProgressIndicator(modifier = Modifier.size(16.dp), color = EchoOnCyan, strokeWidth = 2.dp)
                            } else {
                                Text("Generate Music", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }

        items(trackList, key = { it.id }) { item ->
            val isPlaying = isPlayingId == item.id
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
                        Column {
                            Text(text = item.title, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = EchoText)
                            Text(text = "${item.model} • ${item.genre} (${item.durationSeconds}s)", fontSize = 11.sp, color = EchoCyan)
                        }

                        IconButton(
                            onClick = { isPlayingId = if (isPlaying) null else item.id },
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(if (isPlaying) EchoGreen else EchoCyan)
                        ) {
                            Icon(
                                imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                                contentDescription = if (isPlaying) "Pause" else "Play",
                                tint = EchoOnCyan,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }

                    Spacer(Modifier.height(10.dp))

                    // Waveform visualizer
                    Canvas(modifier = Modifier.fillMaxWidth().height(36.dp).background(EchoBgAlt, RoundedCornerShape(6.dp))) {
                        val w = size.width
                        val h = size.height
                        val bars = 36
                        val barW = w / bars
                        for (i in 0 until bars) {
                            val barH = (sin((i.toDouble() + if (isPlaying) 1.2 else 0.0) * 0.5).toFloat() * 0.4f + 0.5f) * h
                            drawRect(
                                color = if (isPlaying) EchoGreen else EchoCyan,
                                topLeft = Offset(i * barW + 2, (h - barH) / 2),
                                size = androidx.compose.ui.geometry.Size(barW - 4, barH)
                            )
                        }
                    }

                    Spacer(Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (isPlaying) "Playing audio stream..." else "Ready",
                            fontSize = 11.sp,
                            color = if (isPlaying) EchoGreen else EchoMuted
                        )

                        OutlinedButton(
                            onClick = {
                                onSaveNote("""[ATHENA Lyria Music Track]
Title: ${item.title}
Prompt: ${item.prompt}
Model: ${item.model}
Genre: ${item.genre}
Duration: ${item.durationSeconds}s""")
                                Toast.makeText(context, "Saved to Workspace", Toast.LENGTH_SHORT).show()
                            },
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                            modifier = Modifier.height(30.dp)
                        ) {
                            Icon(Icons.Default.Edit, contentDescription = null, tint = EchoCyan, modifier = Modifier.size(14.dp))
                            Spacer(Modifier.width(4.dp))
                            Text("Save Note", fontSize = 11.sp, color = EchoCyan)
                        }
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------
// 4. CREATE & EDIT IMAGES (gemini-nano-banana-2.1)
// -------------------------------------------------------------
@Composable
private fun AthenaImageStudioView(onSaveNote: (String) -> Unit) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    var promptInput by rememberSaveable { mutableStateOf("Subterranean acoustic standing wave in British bedrock strata, 42.6 Hz") }
    var selectedRatio by rememberSaveable { mutableStateOf("1:1") }
    var isEditMode by rememberSaveable { mutableStateOf(false) }
    var isGenerating by remember { mutableStateOf(false) }

    val imageList = remember {
        mutableStateListOf(
            ImageGenerationItem(
                prompt = "Acoustic waveguide resonance in limestone layers, 42.6 Hz frequency simulation",
                aspectRatio = "1:1",
                title = "Bedrock Harmonic Waveguide",
                model = "gemini-nano-banana-2.1"
            )
        )
    }

    val presets = listOf(
        "Geological Bedrock Strata Cross-Section",
        "42.6 Hz Standing Wave Waveform Oscilloscope",
        "Hilsea Acoustic Research Vault 1986",
        "Lord Rayleigh Subterranean Resonator"
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .testTag("athena_image_view"),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = EchoCard),
                border = CardDefaults.outlinedCardBorder().copy(brush = Brush.horizontalGradient(listOf(EchoCyan, EchoGreen)))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Image, contentDescription = null, tint = EchoCyan)
                        Spacer(Modifier.width(8.dp))
                        Text(
                            text = "Create & Edit Images",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = EchoText
                        )
                    }
                    Text(
                        text = "Generate and edit images using text prompts with gemini-nano-banana-2.1.",
                        fontSize = 12.sp,
                        color = EchoMuted,
                        modifier = Modifier.padding(top = 4.dp, bottom = 12.dp)
                    )

                    OutlinedTextField(
                        value = promptInput,
                        onValueChange = { promptInput = it },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text(if (isEditMode) "Edit Instructions Prompt" else "Image Creation Prompt", color = EchoMuted) },
                        maxLines = 3,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = EchoCyan,
                            unfocusedBorderColor = EchoCardStroke,
                            focusedTextColor = EchoText,
                            unfocusedTextColor = EchoText
                        )
                    )

                    Spacer(Modifier.height(10.dp))

                    LazyRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        items(presets) { p ->
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(EchoBgAlt)
                                    .border(1.dp, EchoCardStroke, RoundedCornerShape(12.dp))
                                    .clickable { promptInput = p }
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(p, fontSize = 10.sp, color = EchoCyan)
                            }
                        }
                    }

                    Spacer(Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            listOf("1:1", "16:9", "9:16").forEach { ratio ->
                                FilterChip(
                                    selected = selectedRatio == ratio,
                                    onClick = { selectedRatio = ratio },
                                    label = { Text(ratio, fontSize = 11.sp) },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = EchoCyan,
                                        selectedLabelColor = EchoOnCyan,
                                        containerColor = EchoCard,
                                        labelColor = EchoText
                                    )
                                )
                            }

                            FilterChip(
                                selected = isEditMode,
                                onClick = { isEditMode = !isEditMode },
                                label = { Text(if (isEditMode) "Edit Mode" else "Create Mode", fontSize = 11.sp) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = EchoGreen,
                                    selectedLabelColor = EchoOnCyan,
                                    containerColor = EchoCard,
                                    labelColor = EchoText
                                )
                            )
                        }

                        Button(
                            onClick = {
                                if (promptInput.isNotBlank() && !isGenerating) {
                                    isGenerating = true
                                    coroutineScope.launch {
                                        val res = AthenaService.generateOrEditImage(promptInput, selectedRatio, isEditMode)
                                        res.onSuccess { item ->
                                            imageList.add(0, item)
                                            Toast.makeText(context, if (isEditMode) "Image Edited!" else "Image Created!", Toast.LENGTH_SHORT).show()
                                        }
                                        isGenerating = false
                                    }
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = EchoCyan, contentColor = EchoOnCyan),
                            enabled = !isGenerating && promptInput.isNotBlank()
                        ) {
                            if (isGenerating) {
                                CircularProgressIndicator(modifier = Modifier.size(16.dp), color = EchoOnCyan, strokeWidth = 2.dp)
                            } else {
                                Text(if (isEditMode) "Apply Edit" else "Create Image", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }

        items(imageList, key = { it.id }) { item ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = EchoCard),
                border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(EchoCardStroke))
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(text = item.title, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = EchoText)
                    Text(text = "Model: ${item.model} • Ratio: ${item.aspectRatio} ${if (item.isEdited) "• Edited" else ""}", fontSize = 11.sp, color = EchoMuted)

                    Spacer(Modifier.height(10.dp))

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(if (item.aspectRatio == "16:9") 16f / 9f else if (item.aspectRatio == "9:16") 9f / 16f else 1f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Brush.radialGradient(listOf(Color(0xFF163242), Color(0xFF07141E))))
                            .border(1.dp, EchoCyan, RoundedCornerShape(8.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            val w = size.width
                            val h = size.height
                            for (step in 1..6) {
                                drawCircle(
                                    color = EchoCyan.copy(alpha = 0.15f * step),
                                    radius = (w * 0.08f) * step,
                                    center = Offset(w / 2, h / 2),
                                    style = Stroke(width = 2.dp.toPx())
                                )
                            }
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = EchoCyan, modifier = Modifier.size(32.dp))
                            Spacer(Modifier.height(4.dp))
                            Text("Rendered with gemini-nano-banana-2.1", fontSize = 11.sp, color = EchoCyan, fontWeight = FontWeight.Bold)
                        }
                    }

                    Spacer(Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "Resolution: 1K HD", fontSize = 11.sp, color = EchoGreen)

                        OutlinedButton(
                            onClick = {
                                onSaveNote("""[ATHENA Image Studio Item]
Title: ${item.title}
Prompt: ${item.prompt}
Model: ${item.model}
Aspect Ratio: ${item.aspectRatio}""")
                                Toast.makeText(context, "Saved to Workspace", Toast.LENGTH_SHORT).show()
                            },
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                            modifier = Modifier.height(30.dp)
                        ) {
                            Icon(Icons.Default.Edit, contentDescription = null, tint = EchoCyan, modifier = Modifier.size(14.dp))
                            Spacer(Modifier.width(4.dp))
                            Text("Save Note", fontSize = 11.sp, color = EchoCyan)
                        }
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------
// 5. VEO 3 VIDEO (Text to Video & Animate Images to Video)
// -------------------------------------------------------------
@Composable
private fun AthenaVeoVideoView(onSaveNote: (String) -> Unit) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    var promptInput by rememberSaveable { mutableStateOf("Subterranean bedrock acoustic standing wave at 42.6 Hz propagating through limestone strata") }
    var selectedRatio by rememberSaveable { mutableStateOf("16:9") }
    var isAnimateImageMode by rememberSaveable { mutableStateOf(false) }
    var isGenerating by remember { mutableStateOf(false) }
    var isPlaying by remember { mutableStateOf(false) }

    val videoList = remember {
        mutableStateListOf(
            VideoGenerationItem(
                prompt = "Bedrock acoustic standing wave standing node pattern across Portsmouth geological basin",
                aspectRatio = "16:9",
                videoTitle = "Bedrock Harmonic Standing Wave 42.6 Hz"
            )
        )
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .testTag("athena_veo_view"),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = EchoCard),
                border = CardDefaults.outlinedCardBorder().copy(brush = Brush.horizontalGradient(listOf(EchoCyan, EchoGreen)))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.VideoLibrary, contentDescription = null, tint = EchoCyan)
                        Spacer(Modifier.width(8.dp))
                        Text(
                            text = "Veo 3 Video Generations",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = EchoText
                        )
                    }
                    Text(
                        text = "Generate videos from text prompts or animate photos into video using veo-3.1-fast-generate-preview (16:9 or 9:16).",
                        fontSize = 12.sp,
                        color = EchoMuted,
                        modifier = Modifier.padding(top = 4.dp, bottom = 12.dp)
                    )

                    OutlinedTextField(
                        value = promptInput,
                        onValueChange = { promptInput = it },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text(if (isAnimateImageMode) "Photo Animation Motion Prompt" else "Text-to-Video Prompt", color = EchoMuted) },
                        maxLines = 3,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = EchoCyan,
                            unfocusedBorderColor = EchoCardStroke,
                            focusedTextColor = EchoText,
                            unfocusedTextColor = EchoText
                        )
                    )

                    Spacer(Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            FilterChip(
                                selected = selectedRatio == "16:9",
                                onClick = { selectedRatio = "16:9" },
                                label = { Text("16:9 Landscape", fontSize = 11.sp) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = EchoCyan,
                                    selectedLabelColor = EchoOnCyan,
                                    containerColor = EchoCard,
                                    labelColor = EchoText
                                )
                            )
                            FilterChip(
                                selected = selectedRatio == "9:16",
                                onClick = { selectedRatio = "9:16" },
                                label = { Text("9:16 Portrait", fontSize = 11.sp) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = EchoCyan,
                                    selectedLabelColor = EchoOnCyan,
                                    containerColor = EchoCard,
                                    labelColor = EchoText
                                )
                            )
                            FilterChip(
                                selected = isAnimateImageMode,
                                onClick = { isAnimateImageMode = !isAnimateImageMode },
                                label = { Text(if (isAnimateImageMode) "Animate Image" else "From Text", fontSize = 11.sp) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = EchoGreen,
                                    selectedLabelColor = EchoOnCyan,
                                    containerColor = EchoCard,
                                    labelColor = EchoText
                                )
                            )
                        }

                        Button(
                            onClick = {
                                if (promptInput.isNotBlank() && !isGenerating) {
                                    isGenerating = true
                                    coroutineScope.launch {
                                        val res = AthenaService.generateVeoVideo(promptInput, selectedRatio, isAnimateImageMode)
                                        res.onSuccess { item ->
                                            videoList.add(0, item)
                                            Toast.makeText(context, "Veo 3 Video Generated!", Toast.LENGTH_SHORT).show()
                                        }
                                        isGenerating = false
                                    }
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = EchoCyan, contentColor = EchoOnCyan),
                            enabled = !isGenerating && promptInput.isNotBlank()
                        ) {
                            if (isGenerating) {
                                CircularProgressIndicator(modifier = Modifier.size(16.dp), color = EchoOnCyan, strokeWidth = 2.dp)
                            } else {
                                Text(if (isAnimateImageMode) "Animate" else "Generate", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }

        items(videoList, key = { it.id }) { item ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = EchoCard),
                border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(EchoCardStroke))
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(text = item.videoTitle, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = EchoText)
                    Text(text = "Model: ${item.model} • Aspect Ratio: ${item.aspectRatio} ${if (item.isImageAnimation) "• Photo Animation" else ""}", fontSize = 11.sp, color = EchoMuted)

                    Spacer(Modifier.height(10.dp))

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(if (item.aspectRatio == "16:9") 16f / 9f else 9f / 16f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFF03141F))
                            .border(1.dp, EchoCyan, RoundedCornerShape(8.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            val w = size.width
                            val h = size.height
                            val path = Path()
                            val points = 80
                            for (i in 0..points) {
                                val x = (i / points.toFloat()) * w
                                val y = (h / 2) + (sin((i / 8.0) + (if (isPlaying) 1.5 else 0.0)).toFloat() * (h * 0.25f))
                                if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
                            }
                            drawPath(path, color = EchoCyan, style = Stroke(width = 3.dp.toPx()))
                        }

                        IconButton(
                            onClick = { isPlaying = !isPlaying },
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(Color(0xAA0A0A0F))
                        ) {
                            Icon(
                                imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                                contentDescription = if (isPlaying) "Pause" else "Play",
                                tint = EchoCyan,
                                modifier = Modifier.size(28.dp)
                            )
                        }
                    }

                    Spacer(Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "Status: ${item.status} (${item.durationSeconds}s @ 1080p)", fontSize = 11.sp, color = EchoGreen, fontWeight = FontWeight.SemiBold)

                        OutlinedButton(
                            onClick = {
                                onSaveNote("""[ATHENA Veo 3 Video Export]
Title: ${item.videoTitle}
Prompt: ${item.prompt}
Model: ${item.model}
Aspect Ratio: ${item.aspectRatio}""")
                                Toast.makeText(context, "Saved to Workspace", Toast.LENGTH_SHORT).show()
                            },
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                            modifier = Modifier.height(30.dp)
                        ) {
                            Icon(Icons.Default.Edit, contentDescription = null, tint = EchoCyan, modifier = Modifier.size(14.dp))
                            Spacer(Modifier.width(4.dp))
                            Text("Save Note", fontSize = 11.sp, color = EchoCyan)
                        }
                    }
                }
            }
        }
    }
}


