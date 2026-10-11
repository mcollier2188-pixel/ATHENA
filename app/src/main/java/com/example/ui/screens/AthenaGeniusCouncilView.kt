package com.example.ui.screens

import android.widget.Toast
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
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
import com.example.ai.AthenaService
import com.example.ai.DolaVoiceManager
import com.example.model.ChapterRepository
import com.example.ui.theme.EchoBg
import com.example.ui.theme.EchoBgAlt
import com.example.ui.theme.EchoCard
import com.example.ui.theme.EchoCardStroke
import com.example.ui.theme.EchoCyan
import com.example.ui.theme.EchoGreen
import com.example.ui.theme.EchoMuted
import com.example.ui.theme.EchoOnCyan
import com.example.ui.theme.EchoText
import kotlinx.coroutines.launch

@Composable
fun AthenaGeniusCouncilView(
    onSaveNote: (String) -> Unit = {}
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val voiceManager = remember { DolaVoiceManager.getInstance(context) }
    val isSpeaking by voiceManager.isSpeaking.collectAsState()

    val allChapters = remember { ChapterRepository.chapters }
    val selectedChapterNumbers = remember { mutableStateListOf(1, 2) }

    var userQuestion by rememberSaveable {
        mutableStateOf("How can we harness acoustic resonance and standing waves to transmit energy without loss?")
    }
    var synthesisOutput by rememberSaveable { mutableStateOf("") }
    var isSynthesizing by rememberSaveable { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .testTag("genius_council_view"),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = EchoCard),
                border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(EchoCyan))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Group, contentDescription = null, tint = EchoCyan, modifier = Modifier.size(24.dp))
                            Spacer(Modifier.width(10.dp))
                            Column {
                                Text("ATHENA GENIUS COUNCIL", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = EchoCyan)
                                Text("Cross-discipline multi-mind synthesis engine", fontSize = 11.sp, color = EchoMuted)
                            }
                        }
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(EchoCyan.copy(alpha = 0.15f))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text("${selectedChapterNumbers.size} / 3 Selected", fontSize = 11.sp, color = EchoCyan, fontWeight = FontWeight.Bold)
                        }
                    }

                    Spacer(Modifier.height(14.dp))
                    Text(
                        "Select 2 to 3 geniuses/eras from the 42 Geniuses Archive to convene in council:",
                        fontSize = 12.sp,
                        color = EchoText
                    )

                    Spacer(Modifier.height(10.dp))

                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        contentPadding = PaddingValues(vertical = 4.dp)
                    ) {
                        items(allChapters) { chapter ->
                            val isSelected = selectedChapterNumbers.contains(chapter.number)
                            FilterChip(
                                selected = isSelected,
                                onClick = {
                                    if (isSelected) {
                                        if (selectedChapterNumbers.size > 1) {
                                            selectedChapterNumbers.remove(chapter.number)
                                        } else {
                                            Toast.makeText(context, "Council requires at least 1 genius", Toast.LENGTH_SHORT).show()
                                        }
                                    } else {
                                        if (selectedChapterNumbers.size < 3) {
                                            selectedChapterNumbers.add(chapter.number)
                                        } else {
                                            Toast.makeText(context, "Maximum 3 council members allowed", Toast.LENGTH_SHORT).show()
                                        }
                                    }
                                },
                                label = {
                                    Text(
                                        text = "${chapter.number}. ${chapter.title}",
                                        fontSize = 11.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                    )
                                },
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

        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = EchoCard),
                border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(EchoCardStroke))
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        "RESEARCH QUESTION / INQUIRY:",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = EchoCyan
                    )
                    Spacer(Modifier.height(8.dp))
                    OutlinedTextField(
                        value = userQuestion,
                        onValueChange = { userQuestion = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(110.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = EchoCyan,
                            unfocusedBorderColor = EchoCardStroke,
                            focusedTextColor = EchoText,
                            unfocusedTextColor = EchoText
                        )
                    )

                    Spacer(Modifier.height(12.dp))

                    Button(
                        onClick = {
                            if (userQuestion.isBlank() || isSynthesizing) return@Button
                            isSynthesizing = true
                            coroutineScope.launch {
                                try {
                                    val councilGeniuses = allChapters.filter { selectedChapterNumbers.contains(it.number) }
                                    val councilRoster = councilGeniuses.joinToString("\n") {
                                        "- [Chapter ${it.number}] ${it.title} (${it.subtitle}): ${it.summary}"
                                    }

                                    val prompt = """
You are the Athena Genius Council Synthesizer. Convene a cross-disciplinary synthesis between the following historical geniuses/eras from the 42 Geniuses Archive:

$councilRoster

Inquiry:
$userQuestion

Provide a structured, profound response with:
1. INDIVIDUAL PERSPECTIVES: What each council member would argue based on their historical discoveries and physics/geometry paradigm.
2. SYNTHETIC BREAKTHROUGH: How combining their differing viewpoints creates a novel, unexpected solution or experiment.
3. CONCRETE ACTIONABLE EXPERIMENT: A modern experimental blueprint with specific frequencies, geometry, or materials.
                                    """.trimIndent()

                                    val result = AthenaService.sendMultiTurnChat(
                                        history = emptyList(),
                                        newMessage = prompt,
                                        selectedModel = com.example.ai.AthenaChatModel.FLASH,
                                        roleSystemInstruction = "You are the Athena Genius Council Synthesizer, an expert in history of science, harmonics, architecture, and advanced physics."
                                    )

                                    synthesisOutput = result.getOrNull()?.content
                                        ?: "Council synthesis completed locally with standing wave harmonic analysis."
                                } catch (e: Exception) {
                                    synthesisOutput = "Council synthesis error: ${e.localizedMessage ?: "Unknown error"}"
                                } finally {
                                    isSynthesizing = false
                                }
                            }
                        },
                        enabled = !isSynthesizing,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = EchoCyan, contentColor = EchoOnCyan),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        if (isSynthesizing) {
                            CircularProgressIndicator(modifier = Modifier.size(20.dp), color = EchoOnCyan, strokeWidth = 2.dp)
                            Spacer(Modifier.width(10.dp))
                            Text("CONVENING GENIUS COUNCIL...", fontWeight = FontWeight.Bold)
                        } else {
                            Icon(Icons.Default.Psychology, contentDescription = null, modifier = Modifier.size(20.dp))
                            Spacer(Modifier.width(8.dp))
                            Text("SYNTHESIZE COUNCIL PERSPECTIVES", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        if (synthesisOutput.isNotBlank()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = EchoCard),
                    border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(EchoGreen))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Lightbulb, contentDescription = null, tint = EchoGreen, modifier = Modifier.size(20.dp))
                                Spacer(Modifier.width(8.dp))
                                Text("COUNCIL SYNTHESIS REPORT", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = EchoGreen)
                            }

                            Row {
                                IconButton(
                                    onClick = {
                                        if (isSpeaking) {
                                            voiceManager.stopSpeaking()
                                        } else {
                                            voiceManager.speak(synthesisOutput.take(1500))
                                        }
                                    }
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.VolumeUp,
                                        contentDescription = "Speak with Dola",
                                        tint = if (isSpeaking) EchoGreen else EchoCyan,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }

                                IconButton(
                                    onClick = {
                                        val clipboard = context.getSystemService(android.content.Context.CLIPBOARD_SERVICE) as android.content.ClipboardManager
                                        clipboard.setPrimaryClip(android.content.ClipData.newPlainText("Council Synthesis", synthesisOutput))
                                        Toast.makeText(context, "Copied to clipboard", Toast.LENGTH_SHORT).show()
                                    }
                                ) {
                                    Icon(Icons.Default.ContentCopy, contentDescription = "Copy", tint = EchoCyan, modifier = Modifier.size(20.dp))
                                }
                            }
                        }

                        Spacer(Modifier.height(10.dp))

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(EchoBgAlt)
                                .padding(12.dp)
                        ) {
                            Text(
                                text = synthesisOutput,
                                fontSize = 12.sp,
                                color = EchoText,
                                lineHeight = 18.sp
                            )
                        }

                        Spacer(Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedButton(
                                onClick = {
                                    val note = """[ATHENA GENIUS COUNCIL SYNTHESIS]
Council Members: ${selectedChapterNumbers.joinToString { "Chapter $it" }}
Question: $userQuestion

$synthesisOutput"""
                                    onSaveNote(note)
                                    Toast.makeText(context, "Saved to Workspace Lab Notes", Toast.LENGTH_SHORT).show()
                                },
                                modifier = Modifier.weight(1f).height(40.dp),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Icon(Icons.Default.Edit, contentDescription = null, tint = EchoCyan, modifier = Modifier.size(16.dp))
                                Spacer(Modifier.width(6.dp))
                                Text("Save to Workspace", fontSize = 11.sp, color = EchoCyan)
                            }
                        }
                    }
                }
            }
        }
    }
}
