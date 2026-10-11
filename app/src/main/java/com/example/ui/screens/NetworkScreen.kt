package com.example.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.audio.HarmonicToneGenerator
import com.example.model.NetworkNode
import com.example.ui.components.NodeMapCanvas
import com.example.ui.components.OscilloscopeCanvas
import com.example.ui.theme.EchoBg
import com.example.ui.theme.EchoBgAlt
import com.example.ui.theme.EchoCard
import com.example.ui.theme.EchoCardStroke
import com.example.ui.theme.EchoCyan
import com.example.ui.theme.EchoGreen
import com.example.ui.theme.EchoMuted
import com.example.ui.theme.EchoOnCyan
import com.example.ui.theme.EchoText
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NetworkScreen(
    toneGenerator: HarmonicToneGenerator = HarmonicToneGenerator.SHARED,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    val nodes = NetworkNode.ALL_NODES
    val isAudioPlaying by toneGenerator.isPlayingState.collectAsState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(EchoBg)
    ) {
        // Top banner
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp)
        ) {
            Text(
                text = "ECHO Network Explorer",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = EchoText
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = "Eleven nodes tuned to one base tone of 42.6 Hz, with Portsmouth as the 85.2 Hz master.",
                fontSize = 13.sp,
                color = EchoMuted,
                lineHeight = 18.sp
            )
        }

        // Subtabs: Map, Listen, Power
        TabRow(
            selectedTabIndex = selectedTab,
            containerColor = EchoBgAlt,
            contentColor = EchoCyan,
            indicator = { tabPositions ->
                TabRowDefaults.SecondaryIndicator(
                    Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                    color = EchoCyan
                )
            }
        ) {
            Tab(
                selected = selectedTab == 0,
                onClick = { selectedTab = 0 },
                text = { Text("Map", fontWeight = FontWeight.SemiBold) },
                icon = { Icon(Icons.Default.Map, contentDescription = null, modifier = Modifier.size(18.dp)) },
                modifier = Modifier.testTag("tab_map")
            )
            Tab(
                selected = selectedTab == 1,
                onClick = { selectedTab = 1 },
                text = { Text("Listen", fontWeight = FontWeight.SemiBold) },
                icon = { Icon(Icons.Default.GraphicEq, contentDescription = null, modifier = Modifier.size(18.dp)) },
                modifier = Modifier.testTag("tab_listen")
            )
            Tab(
                selected = selectedTab == 2,
                onClick = { selectedTab = 2 },
                text = { Text("Power", fontWeight = FontWeight.SemiBold) },
                icon = { Icon(Icons.Default.Bolt, contentDescription = null, modifier = Modifier.size(18.dp)) },
                modifier = Modifier.testTag("tab_power")
            )
        }

        when (selectedTab) {
            0 -> MapSubTab(nodes = nodes)
            1 -> ListenSubTab(
                nodes = nodes,
                toneGenerator = toneGenerator,
                isPlaying = isAudioPlaying
            )
            2 -> PowerSubTab(nodes = nodes)
        }
    }
}

@Composable
fun MapSubTab(nodes: List<NetworkNode>) {
    var selectedNodeIndex by remember { mutableIntStateOf(0) }
    val selectedNode = nodes[selectedNodeIndex]

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            NodeMapCanvas(
                nodes = nodes,
                selectedNodeIndex = selectedNodeIndex,
                onSelectNode = { selectedNodeIndex = it }
            )
        }

        // Node quick chips selector
        item {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                itemsIndexed(nodes) { index, node ->
                    FilterChip(
                        selected = index == selectedNodeIndex,
                        onClick = { selectedNodeIndex = index },
                        label = { Text(node.id, fontWeight = FontWeight.Bold) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = EchoCyan,
                            selectedLabelColor = EchoOnCyan,
                            containerColor = EchoCard,
                            labelColor = EchoText
                        ),
                        modifier = Modifier.testTag("node_chip_${node.id}")
                    )
                }
            }
        }

        // Detailed Node Spec Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = EchoCard),
                border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(EchoCardStroke))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "${selectedNode.id} · ${selectedNode.name}",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = EchoText
                        )
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(if (selectedNode.id == "N1") EchoGreen else EchoCyan)
                                .padding(horizontal = 8.dp, vertical = 3.dp)
                        ) {
                            Text(
                                text = if (selectedNode.id == "N1") "MASTER" else "ACTIVE",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = EchoOnCyan
                            )
                        }
                    }

                    Spacer(Modifier.height(14.dp))
                    SpecRow(label = "Setting", value = selectedNode.setting)
                    SpecRow(label = "Frequency", value = String.format(Locale.US, "%.1f Hz", selectedNode.frequency))
                    SpecRow(label = "Times base tone (42.6 Hz)", value = "×${selectedNode.baseRatio}")
                    SpecRow(label = "Compared with master (85.2 Hz)", value = String.format(Locale.US, "×%.2f", selectedNode.comparedToMaster))
                    SpecRow(label = "Daily energy", value = "${selectedNode.dailyEnergyWh} Wh")
                    SpecRow(
                        label = "Battery specification",
                        value = "${selectedNode.batteryCapacityAh} Ah at ${selectedNode.batteryVoltage} V (${selectedNode.totalBatteryWh.toInt()} Wh)"
                    )
                    SpecRow(
                        label = "Coordinates",
                        value = String.format(Locale.US, "%.2f° N, %.2f° W", selectedNode.latitude, -selectedNode.longitude)
                    )
                }
            }
        }

        item {
            Text(
                text = "Positions and links are approximate. Tap any dot on the map or tap a chip to inspect telemetry.",
                fontSize = 12.sp,
                color = EchoMuted
            )
        }
    }
}

@Composable
fun ListenSubTab(
    nodes: List<NetworkNode>,
    toneGenerator: HarmonicToneGenerator,
    isPlaying: Boolean
) {
    val activeIndices = remember { mutableStateListOf(0) } // Default Portsmouth N1 active
    var liftOctaves by remember { mutableStateOf(true) }

    val activeFrequencies = remember(activeIndices.toList()) {
        activeIndices.map { nodes[it].frequency }.distinct()
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                text = "Layer nodes to hear and see how their frequencies combine.",
                fontSize = 14.sp,
                color = EchoMuted
            )
        }

        // Multi-select node chips
        item {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                itemsIndexed(nodes) { index, node ->
                    val isNodeActive = activeIndices.contains(index)
                    FilterChip(
                        selected = isNodeActive,
                        onClick = {
                            if (isNodeActive) {
                                if (activeIndices.size > 1) activeIndices.remove(index)
                            } else {
                                activeIndices.add(index)
                            }
                            if (isPlaying) {
                                toneGenerator.start(activeIndices.map { nodes[it].frequency }, if (liftOctaves) 8.0 else 1.0)
                            }
                        },
                        label = {
                            Text(
                                text = "${node.id} (${node.frequency.toInt()}Hz)",
                                fontWeight = if (isNodeActive) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = EchoCyan,
                            selectedLabelColor = EchoOnCyan,
                            containerColor = EchoCard,
                            labelColor = EchoText
                        ),
                        modifier = Modifier.testTag("listen_chip_${node.id}")
                    )
                }
            }
        }

        // Real-time animated Oscilloscope
        item {
            OscilloscopeCanvas(
                frequencies = activeFrequencies,
                isPlaying = isPlaying
            )
        }

        // Lift octaves toggle
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = EchoCard),
                border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(EchoCardStroke))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = liftOctaves,
                        onCheckedChange = {
                            liftOctaves = it
                            toneGenerator.setOctaveMultiplier(if (it) 8.0 else 1.0)
                            if (isPlaying) {
                                toneGenerator.start(activeFrequencies, if (liftOctaves) 8.0 else 1.0)
                            }
                        },
                        colors = CheckboxDefaults.colors(
                            checkedColor = EchoCyan,
                            checkmarkColor = EchoOnCyan
                        ),
                        modifier = Modifier.testTag("lift_octaves_checkbox")
                    )
                    Spacer(Modifier.width(8.dp))
                    Column {
                        Text(
                            text = "Lift 3 octaves (×8 multiplier)",
                            fontWeight = FontWeight.Medium,
                            color = EchoText,
                            fontSize = 14.sp
                        )
                        Text(
                            text = "Enables standard phone speakers to reproduce tones without sub-bass cutoffs.",
                            fontSize = 12.sp,
                            color = EchoMuted
                        )
                    }
                }
            }
        }

        // Play / Stop button
        item {
            Button(
                onClick = {
                    if (isPlaying) {
                        toneGenerator.stop()
                    } else {
                        toneGenerator.start(activeFrequencies, if (liftOctaves) 8.0 else 1.0)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .testTag("play_tone_button"),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isPlaying) Color(0xFFEF4444) else EchoCyan,
                    contentColor = if (isPlaying) Color.White else EchoOnCyan
                ),
                shape = RoundedCornerShape(10.dp)
            ) {
                Icon(
                    imageVector = if (isPlaying) Icons.Default.Stop else Icons.Default.PlayArrow,
                    contentDescription = null
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    text = if (isPlaying) "Stop Harmonic Audio" else "Play Synthesized Tone",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }
        }

        // Active harmonics summary
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = EchoBgAlt),
                border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(EchoCardStroke))
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "Active Waveform Components (${activeIndices.size} nodes)",
                        fontWeight = FontWeight.SemiBold,
                        color = EchoCyan,
                        fontSize = 14.sp
                    )
                    Spacer(Modifier.height(8.dp))
                    activeIndices.forEach { idx ->
                        val n = nodes[idx]
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("${n.id} ${n.name}", color = EchoText, fontSize = 13.sp)
                            Text(
                                text = "${n.frequency} Hz (×${n.baseRatio})",
                                color = EchoMuted,
                                fontSize = 13.sp
                            )
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PowerSubTab(nodes: List<NetworkNode>) {
    var selectedNodeIndex by remember { mutableIntStateOf(0) }
    var expandedDropdown by remember { mutableStateOf(false) }

    var dailyEnergyInput by remember { mutableStateOf(nodes[0].dailyEnergyWh.toString()) }
    var daysAutonomyInput by remember { mutableStateOf("7") }
    var sunHoursInput by remember { mutableStateOf("0.8") }
    var maxDodInput by remember { mutableStateOf("80") }
    var efficiencyInput by remember { mutableStateOf("85") }

    val currentNode = nodes[selectedNodeIndex]

    // Calculations based on the official ECHO power spec:
    // C = n * E / (dd * ef)
    // P = E / (ps * 0.9 * 0.95)
    val dailyEnergy = dailyEnergyInput.toDoubleOrNull() ?: 0.0
    val daysAutonomy = daysAutonomyInput.toDoubleOrNull() ?: 0.0
    val sunHours = sunHoursInput.toDoubleOrNull() ?: 0.0
    val dod = (maxDodInput.toDoubleOrNull() ?: 80.0) / 100.0
    val efficiency = (efficiencyInput.toDoubleOrNull() ?: 85.0) / 100.0
    val voltage = currentNode.batteryVoltage

    val batteryWhNeeded = if (dod > 0 && efficiency > 0) (daysAutonomy * dailyEnergy) / (dod * efficiency) else 0.0
    val batteryAhNeeded = if (voltage > 0) batteryWhNeeded / voltage else 0.0
    val solarWattsNeeded = if (sunHours > 0) dailyEnergy / (sunHours * 0.90 * 0.95) else 0.0

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = EchoCard),
                border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(EchoCardStroke))
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        text = "Site & Power Sizing Parameters",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = EchoCyan
                    )

                    // Node selector dropdown
                    ExposedDropdownMenuBox(
                        expanded = expandedDropdown,
                        onExpandedChange = { expandedDropdown = !expandedDropdown }
                    ) {
                        OutlinedTextField(
                            value = "${currentNode.id} · ${currentNode.name} (${currentNode.batteryVoltage}V)",
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Select Node Preset") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedDropdown) },
                            modifier = Modifier
                                .menuAnchor(androidx.compose.material3.MenuAnchorType.PrimaryNotEditable)
                                .fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = EchoCyan,
                                unfocusedBorderColor = EchoCardStroke,
                                focusedTextColor = EchoText,
                                unfocusedTextColor = EchoText
                            )
                        )
                        ExposedDropdownMenu(
                            expanded = expandedDropdown,
                            onDismissRequest = { expandedDropdown = false },
                            modifier = Modifier.background(EchoBgAlt)
                        ) {
                            nodes.forEachIndexed { i, n ->
                                DropdownMenuItem(
                                    text = { Text("${n.id} · ${n.name} (${n.dailyEnergyWh} Wh, ${n.batteryVoltage}V)", color = EchoText) },
                                    onClick = {
                                        selectedNodeIndex = i
                                        dailyEnergyInput = n.dailyEnergyWh.toString()
                                        expandedDropdown = false
                                    }
                                )
                            }
                        }
                    }

                    OutlinedTextField(
                        value = dailyEnergyInput,
                        onValueChange = { dailyEnergyInput = it },
                        label = { Text("Daily Energy Consumption (Wh)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = EchoCyan,
                            unfocusedBorderColor = EchoCardStroke,
                            focusedTextColor = EchoText,
                            unfocusedTextColor = EchoText
                        )
                    )

                    OutlinedTextField(
                        value = daysAutonomyInput,
                        onValueChange = { daysAutonomyInput = it },
                        label = { Text("Days of Autonomy") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = EchoCyan,
                            unfocusedBorderColor = EchoCardStroke,
                            focusedTextColor = EchoText,
                            unfocusedTextColor = EchoText
                        )
                    )

                    OutlinedTextField(
                        value = sunHoursInput,
                        onValueChange = { sunHoursInput = it },
                        label = { Text("Winter Peak Sun Hours per Day") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = EchoCyan,
                            unfocusedBorderColor = EchoCardStroke,
                            focusedTextColor = EchoText,
                            unfocusedTextColor = EchoText
                        )
                    )

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = maxDodInput,
                            onValueChange = { maxDodInput = it },
                            label = { Text("Max DoD (%)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = EchoCyan,
                                unfocusedBorderColor = EchoCardStroke,
                                focusedTextColor = EchoText,
                                unfocusedTextColor = EchoText
                            )
                        )
                        OutlinedTextField(
                            value = efficiencyInput,
                            onValueChange = { efficiencyInput = it },
                            label = { Text("Efficiency (%)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = EchoCyan,
                                unfocusedBorderColor = EchoCardStroke,
                                focusedTextColor = EchoText,
                                unfocusedTextColor = EchoText
                            )
                        )
                    }
                }
            }
        }

        // Calculation Output Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = EchoBgAlt),
                border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(EchoCyan))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Calculated Sizing Requirements",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = EchoCyan
                    )
                    Spacer(Modifier.height(14.dp))
                    SpecRow(
                        label = "Battery Needed",
                        value = "${batteryWhNeeded.toInt()} Wh"
                    )
                    SpecRow(
                        label = "At ${currentNode.batteryVoltage} V",
                        value = String.format(Locale.US, "%.1f Ah", batteryAhNeeded)
                    )
                    SpecRow(
                        label = "Solar Panel Needed",
                        value = String.format(Locale.US, "%.1f W", solarWattsNeeded)
                    )
                }
            }
        }

        item {
            Text(
                text = "Uses the formulas from the ECHO power specification, incorporating 90% winter temperature derating and 95% MPPT charger efficiency for panel yield.",
                fontSize = 12.sp,
                color = EchoMuted,
                lineHeight = 16.sp
            )
        }
    }
}

@Composable
fun SpecRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 5.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, color = EchoMuted, fontSize = 13.sp)
        Text(text = value, color = EchoText, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
    }
    HorizontalDivider(color = EchoCardStroke.copy(alpha = 0.5f), thickness = 0.8.dp)
}
