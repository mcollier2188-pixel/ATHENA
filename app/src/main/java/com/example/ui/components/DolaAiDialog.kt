package com.example.ui.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MicOff
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material.icons.filled.VolumeOff
import androidx.compose.foundation.clickable
import androidx.compose.runtime.collectAsState
import com.example.ai.DolaVoiceManager
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.ai.DolaAiMode
import com.example.ai.DolaAiService
import com.example.ui.theme.EchoBg
import com.example.ui.theme.EchoBgAlt
import com.example.ui.theme.EchoCard
import com.example.ui.theme.EchoCardStroke
import com.example.ui.theme.EchoCyan
import com.example.ui.theme.EchoGreen
import com.example.ui.theme.EchoMuted
import com.example.ui.theme.EchoOnCyan
import com.example.ui.theme.EchoText
import com.example.data.firestore.FirestoreAiCreation
import com.example.data.firestore.FirestoreWorkspaceRepository
import java.util.UUID
import kotlinx.coroutines.launch

@Composable
fun DolaAiDialog(
    initialText: String = "",
    initialMode: DolaAiMode = DolaAiMode.REFINE,
    firestoreRepository: FirestoreWorkspaceRepository? = null,
    currentUserId: String? = null,
    onUseReplacement: (String) -> Unit = {},
    onSaveAsNewNote: (String) -> Unit = {},
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val voiceManager = remember { DolaVoiceManager.getInstance(context) }
    val isSpeaking by voiceManager.isSpeaking.collectAsState()
    val isListening by voiceManager.isListening.collectAsState()

    var selectedMode by remember { mutableStateOf(initialMode) }
    var inputText by remember { mutableStateOf(initialText) }
    var resultText by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }

    val quickQuestions = listOf(
        "Explain the 42.6 Hz frequency",
        "How was 42.598 Hz detected at Hilsea?",
        "Explain the 11.74s tidal rhythm",
        "Calculate power budget for a coastal node",
        "Draft script for Echo Comics Issue 4"
    )

    fun runDola() {
        if (inputText.isBlank() && selectedMode != DolaAiMode.CHAT) return
        isLoading = true
        scope.launch {
            val res = DolaAiService.executeTask(
                mode = selectedMode,
                inputText = inputText
            )
            isLoading = false
            val text = res.getOrDefault("Dola was unable to process this request.")
            resultText = text
            if (res.isSuccess && currentUserId != null && firestoreRepository != null) {
                try {
                    firestoreRepository.saveAiCreation(
                        currentUserId,
                        FirestoreAiCreation(
                            id = UUID.randomUUID().toString(),
                            userId = currentUserId,
                            type = "chat",
                            prompt = inputText.ifBlank { "Prompt (${selectedMode.name})" },
                            resultData = text,
                            modelUsed = "gemini-3.5-flash"
                        )
                    )
                } catch (e: Exception) {
                    // Handled
                }
            }
        }
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = EchoBg
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
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(EchoCyan),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = EchoOnCyan, modifier = Modifier.size(20.dp))
                        }
                        Spacer(Modifier.width(10.dp))
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "Dola AI",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = EchoText
                                )
                                Spacer(Modifier.width(6.dp))
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(EchoCardStroke)
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = if (DolaAiService.isApiKeyConfigured) "Gemini 3.5 Flash" else "ECHO Copilot",
                                        fontSize = 10.sp,
                                        color = if (DolaAiService.isApiKeyConfigured) EchoGreen else EchoCyan,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                            Text(
                                text = "Acoustic reasoning & field writing assistant",
                                fontSize = 12.sp,
                                color = EchoMuted
                            )
                        }
                    }

                    IconButton(onClick = onDismiss, modifier = Modifier.testTag("dola_close_button")) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = EchoText)
                    }
                }

                HorizontalDivider(color = EchoCardStroke, thickness = 1.dp, modifier = Modifier.padding(vertical = 10.dp))

                // Mode Selector Chips
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilterChip(
                        selected = selectedMode == DolaAiMode.REFINE,
                        onClick = { selectedMode = DolaAiMode.REFINE },
                        label = { Text("Refine", fontSize = 12.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = EchoCyan,
                            selectedLabelColor = EchoOnCyan,
                            containerColor = EchoCard,
                            labelColor = EchoText
                        ),
                        modifier = Modifier.weight(1f)
                    )
                    FilterChip(
                        selected = selectedMode == DolaAiMode.EXPAND,
                        onClick = { selectedMode = DolaAiMode.EXPAND },
                        label = { Text("Expand", fontSize = 12.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = EchoCyan,
                            selectedLabelColor = EchoOnCyan,
                            containerColor = EchoCard,
                            labelColor = EchoText
                        ),
                        modifier = Modifier.weight(1f)
                    )
                    FilterChip(
                        selected = selectedMode == DolaAiMode.SUMMARIZE,
                        onClick = { selectedMode = DolaAiMode.SUMMARIZE },
                        label = { Text("Summarize", fontSize = 12.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = EchoCyan,
                            selectedLabelColor = EchoOnCyan,
                            containerColor = EchoCard,
                            labelColor = EchoText
                        ),
                        modifier = Modifier.weight(1.1f)
                    )
                    FilterChip(
                        selected = selectedMode == DolaAiMode.CHAT,
                        onClick = { selectedMode = DolaAiMode.CHAT },
                        label = { Text("Ask Dola", fontSize = 12.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = EchoCyan,
                            selectedLabelColor = EchoOnCyan,
                            containerColor = EchoCard,
                            labelColor = EchoText
                        ),
                        modifier = Modifier.weight(1.2f)
                    )
                }

                // Quick Prompt Suggestions if in Chat mode
                if (selectedMode == DolaAiMode.CHAT) {
                    Spacer(Modifier.height(8.dp))
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(quickQuestions) { q ->
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(EchoCard)
                                    .clickable {
                                        inputText = q
                                        runDola()
                                    }
                                    .padding(horizontal = 10.dp, vertical = 6.dp)
                            ) {
                                Text(q, fontSize = 11.sp, color = EchoCyan)
                            }
                        }
                    }
                }

                Spacer(Modifier.height(10.dp))

                // Scrollable workspace content
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (isListening) "Listening for speech..." else "Text Input",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isListening) EchoCyan else EchoMuted
                        )

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Dola Voice",
                                fontSize = 11.sp,
                                color = if (isListening) EchoCyan else EchoMuted
                            )
                            IconButton(
                                onClick = {
                                    if (isListening) {
                                        voiceManager.stopListening()
                                    } else {
                                        voiceManager.startListening(
                                            onResult = { spoken ->
                                                inputText = if (inputText.isBlank()) spoken else "$inputText $spoken"
                                                Toast.makeText(context, "Voice input received", Toast.LENGTH_SHORT).show()
                                            },
                                            onError = { err ->
                                                Toast.makeText(context, err, Toast.LENGTH_SHORT).show()
                                            }
                                        )
                                    }
                                }
                            ) {
                                Icon(
                                    imageVector = if (isListening) Icons.Default.MicOff else Icons.Default.Mic,
                                    contentDescription = "Voice input",
                                    tint = if (isListening) EchoCyan else EchoMuted,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }

                    OutlinedTextField(
                        value = inputText,
                        onValueChange = { inputText = it },
                        label = {
                            Text(
                                when (selectedMode) {
                                    DolaAiMode.REFINE -> "Note text to refine (fixes wording while preserving meaning)"
                                    DolaAiMode.EXPAND -> "Text to expand in author's voice"
                                    DolaAiMode.SUMMARIZE -> "Field note or document to summarize"
                                    DolaAiMode.CHAT -> "Ask Dola about the 42 Geniuses, 42.6 Hz frequency, or ECHO specs"
                                }
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(130.dp)
                            .testTag("dola_input_field"),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = EchoCyan,
                            unfocusedBorderColor = EchoCardStroke,
                            focusedTextColor = EchoText,
                            unfocusedTextColor = EchoText
                        )
                    )

                    Button(
                        onClick = { runDola() },
                        enabled = !isLoading,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("dola_submit_button"),
                        colors = ButtonDefaults.buttonColors(containerColor = EchoCyan, contentColor = EchoOnCyan),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(modifier = Modifier.size(20.dp), color = EchoOnCyan, strokeWidth = 2.dp)
                            Spacer(Modifier.width(8.dp))
                            Text("Dola is working...", fontWeight = FontWeight.Bold)
                        } else {
                            Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(Modifier.width(8.dp))
                            Text(
                                text = when (selectedMode) {
                                    DolaAiMode.REFINE -> "Refine Writing"
                                    DolaAiMode.EXPAND -> "Expand in Voice"
                                    DolaAiMode.SUMMARIZE -> "Generate Summary"
                                    DolaAiMode.CHAT -> "Ask Dola"
                                },
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    // Result Box
                    if (resultText.isNotBlank()) {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("dola_result_card"),
                            colors = CardDefaults.cardColors(containerColor = EchoBgAlt),
                            border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(EchoCyan))
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "Dola Output",
                                        fontWeight = FontWeight.Bold,
                                        color = EchoCyan,
                                        fontSize = 14.sp
                                    )
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        IconButton(
                                            onClick = {
                                                if (isSpeaking) {
                                                    voiceManager.stopSpeaking()
                                                } else {
                                                    voiceManager.speak(resultText)
                                                }
                                            }
                                        ) {
                                            Icon(
                                                imageVector = if (isSpeaking) Icons.Default.VolumeOff else Icons.Default.VolumeUp,
                                                contentDescription = "Speak with Dola",
                                                tint = if (isSpeaking) EchoGreen else EchoCyan,
                                                modifier = Modifier.size(18.dp)
                                            )
                                        }

                                        IconButton(
                                            onClick = {
                                                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                                clipboard.setPrimaryClip(ClipData.newPlainText("Dola text", resultText))
                                                Toast.makeText(context, "Copied to clipboard", Toast.LENGTH_SHORT).show()
                                            }
                                        ) {
                                            Icon(Icons.Default.ContentCopy, contentDescription = "Copy", tint = EchoMuted, modifier = Modifier.size(18.dp))
                                        }
                                    }
                                }

                                Spacer(Modifier.height(6.dp))
                                Text(
                                    text = resultText,
                                    fontSize = 14.sp,
                                    color = EchoText,
                                    lineHeight = 20.sp
                                )

                                Spacer(Modifier.height(14.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    if (selectedMode == DolaAiMode.REFINE || selectedMode == DolaAiMode.EXPAND) {
                                        Button(
                                            onClick = {
                                                onUseReplacement(resultText)
                                                onDismiss()
                                            },
                                            colors = ButtonDefaults.buttonColors(containerColor = EchoCyan, contentColor = EchoOnCyan),
                                            shape = RoundedCornerShape(8.dp),
                                            modifier = Modifier.weight(1f)
                                        ) {
                                            Text("Use This", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                    OutlinedButton(
                                        onClick = {
                                            onSaveAsNewNote(resultText)
                                            onDismiss()
                                        },
                                        shape = RoundedCornerShape(8.dp),
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Text("Save as New Note", fontSize = 11.sp, color = EchoText)
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
