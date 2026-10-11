package com.example.ui.components

import android.content.Context
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.FormatQuote
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.RecordVoiceOver
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Tune
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
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.ui.theme.EchoCyan
import com.example.ui.theme.EchoGreen
import com.example.ui.theme.EchoMuted
import com.example.ui.theme.EchoText
import java.util.Locale

@Composable
fun ReaderDialog(
    title: String,
    subtitle: String,
    content: String,
    onSaveQuoteToFieldJournal: (quote: String, chapterTitle: String) -> Unit = { _, _ -> },
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    var isNightMode by remember { mutableStateOf(true) }
    var fontSizeSp by remember { mutableFloatStateOf(17f) }
    var isBookmarked by remember { mutableStateOf(false) }

    // Audio Narration State
    var isNarrating by remember { mutableStateOf(false) }
    var playbackSpeed by remember { mutableFloatStateOf(1.0f) } // 0.75x, 1.0x, 1.25x, 1.5x, 2.0x
    var voicePitch by remember { mutableFloatStateOf(1.0f) }   // 0.8f, 1.0f, 1.2f
    var showNarrationControls by remember { mutableStateOf(false) }
    var currentParagraphIndex by remember { mutableIntStateOf(0) }

    val paragraphs = remember(content) {
        content.split("\n\n").map { it.trim() }.filter { it.isNotBlank() }
    }

    var ttsInstance by remember { mutableStateOf<TextToSpeech?>(null) }

    // Initialize TTS
    DisposableEffect(Unit) {
        var tts: TextToSpeech? = null
        tts = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                tts?.language = Locale.UK
                tts?.setSpeechRate(playbackSpeed)
                tts?.setPitch(voicePitch)
                tts?.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                    override fun onStart(utteranceId: String?) {
                        isNarrating = true
                    }
                    override fun onDone(utteranceId: String?) {
                        val nextIdx = currentParagraphIndex + 1
                        if (nextIdx < paragraphs.size && isNarrating) {
                            currentParagraphIndex = nextIdx
                            tts?.speak(paragraphs[nextIdx], TextToSpeech.QUEUE_FLUSH, null, "p_$nextIdx")
                        } else {
                            isNarrating = false
                        }
                    }
                    override fun onError(utteranceId: String?) {
                        isNarrating = false
                    }
                })
                ttsInstance = tts
            }
        }

        onDispose {
            tts?.stop()
            tts?.shutdown()
        }
    }

    // Apply speed / pitch updates
    LaunchedEffect(playbackSpeed, voicePitch) {
        ttsInstance?.let { tts ->
            tts.setSpeechRate(playbackSpeed)
            tts.setPitch(voicePitch)
        }
    }

    fun startOrResumeNarration() {
        if (paragraphs.isEmpty()) return
        ttsInstance?.let { tts ->
            tts.setSpeechRate(playbackSpeed)
            tts.setPitch(voicePitch)
            val idx = currentParagraphIndex.coerceIn(0, paragraphs.size - 1)
            tts.speak(paragraphs[idx], TextToSpeech.QUEUE_FLUSH, null, "p_$idx")
            isNarrating = true
            Toast.makeText(context, "Resuming narration from paragraph ${idx + 1}", Toast.LENGTH_SHORT).show()
        }
    }

    fun pauseNarration() {
        ttsInstance?.stop()
        isNarrating = false
    }

    val bgColor = if (isNightMode) Color(0xFF0A0A0F) else Color(0xFFFAF8F3)
    val textColor = if (isNightMode) Color(0xFFD8D8DE) else Color(0xFF1A1A1A)
    val mutedColor = if (isNightMode) Color(0xFF88888F) else Color(0xFF6B7280)
    val barBg = if (isNightMode) Color(0xFF12121A) else Color(0xFFEFECE6)
    val cardBg = if (isNightMode) Color(0xFF141722) else Color(0xFFF3EFE6)

    Dialog(
        onDismissRequest = {
            ttsInstance?.stop()
            onDismiss()
        },
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .background(bgColor)
                .testTag("reader_dialog"),
            color = bgColor
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .statusBarsPadding()
            ) {
                // Header tool controls
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(barBg)
                        .padding(horizontal = 14.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        OutlinedButton(
                            onClick = { fontSizeSp = (fontSizeSp - 1f).coerceAtLeast(13f) },
                            modifier = Modifier.padding(end = 4.dp),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Text("A−", color = textColor, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                        OutlinedButton(
                            onClick = { fontSizeSp = (fontSizeSp + 1f).coerceAtMost(30f) },
                            modifier = Modifier.padding(end = 6.dp),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Text("A+", color = textColor, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }

                        // Narration Audio Toggle
                        Button(
                            onClick = {
                                showNarrationControls = !showNarrationControls
                                if (showNarrationControls && !isNarrating) {
                                    startOrResumeNarration()
                                }
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isNarrating) EchoGreen else if (showNarrationControls) EchoCyan else barBg,
                                contentColor = if (isNarrating || showNarrationControls) Color.Black else textColor
                            ),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                            modifier = Modifier.padding(end = 6.dp).testTag("toggle_narration_button")
                        ) {
                            Icon(
                                if (isNarrating) Icons.Default.RecordVoiceOver else Icons.Default.Tune,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(Modifier.width(4.dp))
                            Text(if (isNarrating) "Speaking" else "Audio", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }

                        // Bookmark Button
                        IconButton(
                            onClick = {
                                isBookmarked = !isBookmarked
                                Toast.makeText(
                                    context,
                                    if (isBookmarked) "Bookmarked '$title'!" else "Bookmark removed",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        ) {
                            Icon(
                                if (isBookmarked) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                                contentDescription = "Bookmark",
                                tint = if (isBookmarked) EchoCyan else textColor,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        // Day / Night Mode
                        IconButton(onClick = { isNightMode = !isNightMode }) {
                            Icon(
                                imageVector = if (isNightMode) Icons.Default.LightMode else Icons.Default.DarkMode,
                                contentDescription = "Toggle Theme",
                                tint = textColor,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }

                    IconButton(
                        onClick = {
                            ttsInstance?.stop()
                            onDismiss()
                        },
                        modifier = Modifier.testTag("close_reader")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close Reader",
                            tint = textColor
                        )
                    }
                }

                // Audio Narration Drawer & Speed Controls
                AnimatedVisibility(visible = showNarrationControls) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(cardBg)
                            .padding(horizontal = 16.dp, vertical = 10.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Button(
                                    onClick = {
                                        if (isNarrating) pauseNarration() else startOrResumeNarration()
                                    },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = if (isNarrating) EchoGreen else EchoCyan,
                                        contentColor = Color.Black
                                    ),
                                    modifier = Modifier.height(34.dp)
                                ) {
                                    Icon(
                                        if (isNarrating) Icons.Default.Pause else Icons.Default.PlayArrow,
                                        contentDescription = null,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(Modifier.width(6.dp))
                                    Text(if (isNarrating) "Pause" else "Play Narration", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }

                                Spacer(Modifier.width(10.dp))
                                Text(
                                    text = "Paragraph ${currentParagraphIndex + 1} of ${paragraphs.size}",
                                    fontSize = 11.sp,
                                    color = mutedColor
                                )
                            }
                        }

                        Spacer(Modifier.height(8.dp))

                        // Playback Speed Selector Chips
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Speed:", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = textColor)
                            val speeds = listOf(0.75f, 1.0f, 1.25f, 1.5f, 2.0f)
                            speeds.forEach { sp ->
                                val isSelected = playbackSpeed == sp
                                FilterChip(
                                    selected = isSelected,
                                    onClick = { playbackSpeed = sp },
                                    label = { Text("${sp}x", fontSize = 10.sp) },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = EchoCyan,
                                        selectedLabelColor = Color.Black,
                                        containerColor = barBg,
                                        labelColor = textColor
                                    ),
                                    modifier = Modifier.height(28.dp)
                                )
                            }
                        }

                        Spacer(Modifier.height(6.dp))

                        // Voice Pitch Selector Chips
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Tone Pitch:", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = textColor)
                            val pitches = listOf(0.85f to "Warm", 1.0f to "Standard", 1.2f to "Clear")
                            pitches.forEach { (p, label) ->
                                val isSelected = voicePitch == p
                                FilterChip(
                                    selected = isSelected,
                                    onClick = { voicePitch = p },
                                    label = { Text(label, fontSize = 10.sp) },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = EchoGreen,
                                        selectedLabelColor = Color.Black,
                                        containerColor = barBg,
                                        labelColor = textColor
                                    ),
                                    modifier = Modifier.height(28.dp)
                                )
                            }
                        }
                    }
                }

                // Scrollable text content & Paragraph cards with Quote Bookmarking
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 20.dp),
                    contentPadding = PaddingValues(vertical = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    item {
                        Text(
                            text = title,
                            fontSize = (fontSizeSp + 8f).sp,
                            fontWeight = FontWeight.Bold,
                            color = textColor,
                            lineHeight = (fontSizeSp + 14f).sp
                        )
                        if (subtitle.isNotBlank()) {
                            Spacer(Modifier.height(4.dp))
                            Text(
                                text = subtitle,
                                fontSize = (fontSizeSp - 2f).sp,
                                color = mutedColor,
                                fontWeight = FontWeight.Medium
                            )
                        }
                        Spacer(Modifier.height(8.dp))
                        HorizontalDivider(color = barBg)
                    }

                    // Render paragraphs with quote highlight & save action
                    itemsIndexed(paragraphs) { index, para ->
                        val isCurrentlySpeaking = isNarrating && currentParagraphIndex == index

                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp)),
                            colors = CardDefaults.cardColors(
                                containerColor = if (isCurrentlySpeaking) EchoCyan.copy(alpha = 0.12f) else Color.Transparent
                            ),
                            border = CardDefaults.outlinedCardBorder().copy(
                                brush = androidx.compose.ui.graphics.SolidColor(
                                    if (isCurrentlySpeaking) EchoCyan else Color.Transparent
                                )
                            )
                        ) {
                            Column(modifier = Modifier.padding(8.dp)) {
                                Text(
                                    text = para,
                                    fontSize = fontSizeSp.sp,
                                    color = textColor,
                                    lineHeight = (fontSizeSp * 1.65f).sp,
                                    fontFamily = FontFamily.Serif
                                )

                                Spacer(Modifier.height(6.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    if (isCurrentlySpeaking) {
                                        Text("▶ Narrating now", fontSize = 10.sp, color = EchoCyan, fontWeight = FontWeight.Bold)
                                    } else {
                                        Text("¶ ${index + 1}", fontSize = 10.sp, color = mutedColor)
                                    }

                                    // Save Quote to Field Journal Action
                                    OutlinedButton(
                                        onClick = {
                                            onSaveQuoteToFieldJournal(para, title)
                                            Toast.makeText(context, "Quote saved to Field Journal in Workspace!", Toast.LENGTH_SHORT).show()
                                        },
                                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                                        modifier = Modifier.height(28.dp).testTag("save_quote_${index}")
                                    ) {
                                        Icon(Icons.Default.FormatQuote, contentDescription = null, tint = EchoCyan, modifier = Modifier.size(14.dp))
                                        Spacer(Modifier.width(4.dp))
                                        Text("Save Quote to Journal", fontSize = 10.sp, color = EchoCyan)
                                    }
                                }
                            }
                        }
                    }

                    item {
                        Spacer(Modifier.height(60.dp))
                    }
                }
            }
        }
    }
}
