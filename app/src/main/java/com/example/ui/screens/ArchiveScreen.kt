package com.example.ui.screens

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Hub
import androidx.compose.material.icons.filled.Radio
import androidx.compose.material.icons.filled.Timeline
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.Chapter
import com.example.model.ChapterRepository
import com.example.model.FieldDoc
import com.example.model.FieldDocRepository
import com.example.model.NetworkNode
import com.example.model.TimelineEra
import com.example.model.TimelineItem
import com.example.model.TimelineRepository
import com.example.ui.components.HeartbeatIndicator
import com.example.ui.components.ReaderDialog
import com.example.ui.theme.EchoBg
import com.example.ui.theme.EchoBgAlt
import com.example.ui.theme.EchoCard
import com.example.ui.theme.EchoCardStroke
import com.example.ui.theme.EchoCyan
import com.example.ui.theme.EchoCyanSoft
import com.example.ui.theme.EchoGreen
import com.example.ui.theme.EchoMuted
import com.example.ui.theme.EchoText

import com.example.audio.HarmonicToneGenerator
import androidx.compose.material.icons.filled.GraphicEq

@Composable
fun ArchiveScreen(
    onNavigateToFrequency: () -> Unit,
    onNavigateToNetwork: () -> Unit,
    onNavigateToWorkspace: () -> Unit,
    onSaveQuoteToFieldJournal: (quote: String, chapterTitle: String) -> Unit = { _, _ -> },
    modifier: Modifier = Modifier
) {
    var selectedEra by remember { mutableStateOf<TimelineEra?>(null) }
    var activeReaderItem by remember { mutableStateOf<Triple<String, String, String>?>(null) }

    val filteredTimeline = remember(selectedEra) {
        if (selectedEra == null) TimelineRepository.items
        else TimelineRepository.items.filter { it.era == selectedEra }
    }

    Box(modifier = modifier.fillMaxSize().background(EchoBg)) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 20.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Header with pulsing heartbeat
            item {
                Column(
                    modifier = Modifier.fillMaxWidth().padding(top = 16.dp, bottom = 8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .clip(CircleShape)
                            .clickable { onNavigateToFrequency() }
                            .padding(4.dp)
                            .testTag("archive_heartbeat_button")
                    ) {
                        HeartbeatIndicator(size = 72.dp)
                    }
                    Spacer(Modifier.height(14.dp))
                    Text(
                        text = "ATHENA",
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold,
                        color = EchoCyan,
                        letterSpacing = 2.sp
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = "42 Geniuses · ECHO-INTERFACE Archive",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = EchoText
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = "42.6 Hz — the frequency that connects the world.\nA chronicle of listening across time.",
                        fontSize = 14.sp,
                        color = EchoMuted,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                        lineHeight = 20.sp
                    )
                    Spacer(Modifier.height(10.dp))
                    OutlinedButton(
                        onClick = onNavigateToFrequency,
                        shape = RoundedCornerShape(20.dp),
                        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
                        modifier = Modifier.testTag("listen_42hz_button")
                    ) {
                        Icon(Icons.Default.GraphicEq, contentDescription = null, tint = EchoCyan, modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(6.dp))
                        Text("Explore 42.6 Hz Frequency Lab", fontSize = 12.sp, color = EchoCyan, fontWeight = FontWeight.Bold)
                    }
                }
            }

            // Offline Welcome Banner
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = EchoCard),
                    border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(EchoCardStroke))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = EchoGreen,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(Modifier.width(8.dp))
                            Text(
                                text = "Offline Archive & Network Mesh",
                                fontWeight = FontWeight.SemiBold,
                                color = EchoText,
                                fontSize = 15.sp
                            )
                        }
                        Spacer(Modifier.height(6.dp))
                        Text(
                            text = "This is the standalone offline archive for the ECHO-INTERFACE network and the 42 Geniuses chronicle. All nodes, chapters, waveforms, and field notes are available without an internet connection.",
                            fontSize = 13.sp,
                            color = EchoMuted,
                            lineHeight = 18.sp
                        )
                    }
                }
            }

            // Era Filter Row
            item {
                Column {
                    Text(
                        text = "Eras of Listening",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = EchoCyan
                    )
                    Spacer(Modifier.height(8.dp))
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        item {
                            FilterChip(
                                selected = selectedEra == null,
                                onClick = { selectedEra = null },
                                label = { Text("All Eras") },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = EchoCyan,
                                    selectedLabelColor = Color(0xFF062431),
                                    containerColor = EchoCard,
                                    labelColor = EchoText
                                )
                            )
                        }
                        items(TimelineEra.values()) { era ->
                            FilterChip(
                                selected = selectedEra == era,
                                onClick = { selectedEra = if (selectedEra == era) null else era },
                                label = {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Text(era.title, fontWeight = FontWeight.Medium)
                                        Text(era.dateRange, fontSize = 9.sp, color = EchoMuted)
                                    }
                                },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = EchoCyan,
                                    selectedLabelColor = Color(0xFF062431),
                                    containerColor = EchoCard,
                                    labelColor = EchoText
                                )
                            )
                        }
                    }
                }
            }

            // Chronological Timeline Section
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Chronological Timeline",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = EchoCyan
                    )
                    Text(
                        text = "${filteredTimeline.size} events",
                        fontSize = 12.sp,
                        color = EchoMuted
                    )
                }
            }

            items(filteredTimeline, key = { it.id }) { event ->
                TimelineEventCard(event = event)
            }

            // 42 Chapters Quick Access Section
            item {
                Spacer(Modifier.height(10.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "The 42 Chapters",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = EchoCyan
                    )
                    Text(
                        text = "Tap to read full text",
                        fontSize = 12.sp,
                        color = EchoMuted
                    )
                }
            }

            items(ChapterRepository.chapters, key = { "ch_${it.number}" }) { chapter ->
                ChapterCard(
                    chapter = chapter,
                    onClick = {
                        activeReaderItem = Triple(
                            "Ch ${chapter.number.toString().padStart(2, '0')} — ${chapter.title}",
                            "${chapter.subtitle} · ${chapter.eraVolume}",
                            chapter.fullText
                        )
                    }
                )
            }

            // Network Nodes Quick Status Reference
            item {
                Spacer(Modifier.height(10.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Network Nodes Quick Reference",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = EchoCyan
                    )
                    Text(
                        text = "Open Explorer →",
                        fontSize = 13.sp,
                        color = EchoCyan,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.clickable { onNavigateToNetwork() }
                    )
                }
            }

            item {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    val chunkedNodes = NetworkNode.ALL_NODES.chunked(2)
                    chunkedNodes.forEach { pair ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            pair.forEach { node ->
                                Box(modifier = Modifier.weight(1f)) {
                                    NodeSummaryCard(node = node, onClick = onNavigateToNetwork)
                                }
                            }
                            if (pair.size == 1) {
                                Spacer(modifier = Modifier.weight(1f))
                            }
                        }
                    }
                }
            }

            // Field Documents Section
            item {
                Spacer(Modifier.height(10.dp))
                Text(
                    text = "Field Documents — Offline Archive",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = EchoCyan
                )
            }

            items(FieldDocRepository.documents, key = { it.id }) { doc ->
                FieldDocCard(
                    doc = doc,
                    onClick = {
                        activeReaderItem = Triple(
                            doc.title,
                            doc.subtitle,
                            doc.content
                        )
                    }
                )
            }

            // Footer
            item {
                Column(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    HorizontalDivider(color = EchoCardStroke, thickness = 1.dp)
                    Spacer(Modifier.height(16.dp))
                    Text(
                        text = "42 Geniuses · ECHO-INTERFACE Archive · Canon v4.0",
                        fontSize = 12.sp,
                        color = EchoMuted
                    )
                    Text(
                        text = "Everything here works offline. Saved to device.",
                        fontSize = 11.sp,
                        color = EchoMuted.copy(alpha = 0.7f)
                    )
                }
            }
        }

        // Reader dialog when chapter or doc is tapped
        activeReaderItem?.let { (title, subtitle, content) ->
            ReaderDialog(
                title = title,
                subtitle = subtitle,
                content = content,
                onSaveQuoteToFieldJournal = onSaveQuoteToFieldJournal,
                onDismiss = { activeReaderItem = null }
            )
        }
    }
}

@Composable
fun TimelineEventCard(event: TimelineItem) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("timeline_item_${event.id}"),
        verticalAlignment = Alignment.Top
    ) {
        // Vertical timeline marker
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.width(24.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(12.dp)
                    .background(EchoCyan, CircleShape)
            )
            Box(
                modifier = Modifier
                    .width(2.dp)
                    .height(90.dp)
                    .background(EchoCardStroke)
            )
        }
        Spacer(Modifier.width(12.dp))
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = EchoCard),
            border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(EchoCardStroke))
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text(
                    text = event.dateText,
                    fontSize = 12.sp,
                    color = EchoMuted,
                    fontWeight = FontWeight.Medium
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = event.title,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = EchoText
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = event.description,
                    fontSize = 13.sp,
                    color = EchoText.copy(alpha = 0.85f),
                    lineHeight = 18.sp
                )
                if (event.frequencyNote != null) {
                    Spacer(Modifier.height(6.dp))
                    Text(
                        text = "⚡ ${event.frequencyNote}",
                        fontSize = 11.sp,
                        color = EchoCyan,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

@Composable
fun ChapterCard(chapter: Chapter, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .clickable(onClick = onClick)
            .testTag("chapter_card_${chapter.number}"),
        colors = CardDefaults.cardColors(containerColor = EchoBgAlt),
        border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(EchoCardStroke.copy(alpha = 0.5f)))
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Ch ${chapter.number.toString().padStart(2, '0')}",
                fontWeight = FontWeight.Bold,
                color = EchoCyan,
                fontSize = 14.sp
            )
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = chapter.title,
                    fontWeight = FontWeight.Medium,
                    color = EchoText,
                    fontSize = 14.sp
                )
                Text(
                    text = chapter.subtitle,
                    fontSize = 12.sp,
                    color = EchoMuted
                )
            }
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = "Read",
                tint = EchoMuted,
                modifier = Modifier.size(16.dp)
            )
        }
    }
}

@Composable
fun NodeSummaryCard(node: NetworkNode, onClick: () -> Unit) {
    val isMaster = (node.id == "N1")
    val accentColor = if (isMaster) EchoGreen else Color(0xFF60A5FA)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = EchoBgAlt),
        border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(EchoCardStroke))
    ) {
        Row(
            modifier = Modifier.padding(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .width(4.dp)
                    .height(36.dp)
                    .background(accentColor, RoundedCornerShape(2.dp))
            )
            Spacer(Modifier.width(8.dp))
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = node.id,
                        fontWeight = FontWeight.Bold,
                        color = accentColor,
                        fontSize = 13.sp
                    )
                    Spacer(Modifier.width(6.dp))
                    Text(
                        text = node.name,
                        fontWeight = FontWeight.Medium,
                        color = EchoText,
                        fontSize = 13.sp
                    )
                }
                Text(
                    text = "${node.frequency} Hz · ${node.setting}",
                    fontSize = 11.sp,
                    color = EchoMuted
                )
            }
        }
    }
}

@Composable
fun FieldDocCard(doc: FieldDoc, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .testTag("doc_card_${doc.id}"),
        colors = CardDefaults.cardColors(containerColor = EchoBgAlt),
        border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(EchoCardStroke))
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(doc.icon, fontSize = 20.sp)
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = doc.title,
                    fontWeight = FontWeight.Medium,
                    color = EchoText,
                    fontSize = 14.sp
                )
                Text(
                    text = doc.subtitle,
                    fontSize = 12.sp,
                    color = EchoMuted
                )
            }
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = "Open Document",
                tint = EchoCyan,
                modifier = Modifier.size(16.dp)
            )
        }
    }
}
