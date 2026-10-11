package com.example.ui.components

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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Palette
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.ai.AppIconData
import com.example.ai.IconPalettePreset
import com.example.ai.IconShape
import com.example.ai.PRESET_ICON_GLYPHS
import com.example.ai.PRESET_ICON_PALETTES
import com.example.ui.theme.EchoBg
import com.example.ui.theme.EchoBgAlt
import com.example.ui.theme.EchoCard
import com.example.ui.theme.EchoCardStroke
import com.example.ui.theme.EchoCyan
import com.example.ui.theme.EchoGreen
import com.example.ui.theme.EchoMuted
import com.example.ui.theme.EchoText

@Composable
fun AppIconStudioDialog(
    appName: String,
    initialIcon: AppIconData,
    onSaveIcon: (AppIconData) -> Unit,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current

    var selectedGlyph by remember { mutableStateOf(initialIcon.glyph) }
    var selectedGlyphName by remember { mutableStateOf(initialIcon.glyphName) }
    var selectedPalette by remember {
        mutableStateOf(
            PRESET_ICON_PALETTES.find { it.startHex.equals(initialIcon.bgStartHex, ignoreCase = true) }
                ?: PRESET_ICON_PALETTES[0]
        )
    }
    var selectedShape by remember { mutableStateOf(initialIcon.shape) }

    val currentIcon = remember(selectedGlyph, selectedGlyphName, selectedPalette, selectedShape) {
        AppIconData(
            glyph = selectedGlyph,
            glyphName = selectedGlyphName,
            bgStartHex = selectedPalette.startHex,
            bgEndHex = selectedPalette.endHex,
            shape = selectedShape
        )
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .background(EchoBg)
                .testTag("app_icon_studio_dialog"),
            color = EchoBg
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                // Header Bar
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Palette, contentDescription = null, tint = EchoCyan, modifier = Modifier.size(24.dp))
                        Spacer(Modifier.width(10.dp))
                        Column {
                            Text("APP ICON STUDIO", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = EchoText)
                            Text("Adaptive launcher icon generator", fontSize = 11.sp, color = EchoMuted)
                        }
                    }

                    IconButton(onClick = onDismiss, modifier = Modifier.testTag("close_icon_studio")) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = EchoText)
                    }
                }

                Spacer(Modifier.height(12.dp))

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Home Screen Preview Mockup Card
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = EchoBgAlt),
                        border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(EchoCardStroke))
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(20.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "ANDROID LAUNCHER PREVIEW",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = EchoMuted,
                                letterSpacing = 1.sp
                            )
                            Spacer(Modifier.height(16.dp))

                            // The Big App Icon Preview
                            val cornerShape = when (selectedShape) {
                                IconShape.SQUIRCLE -> RoundedCornerShape(26.dp)
                                IconShape.CIRCLE -> CircleShape
                                IconShape.ROUNDED_RECT -> RoundedCornerShape(16.dp)
                            }

                            Box(
                                modifier = Modifier
                                    .size(96.dp)
                                    .clip(cornerShape)
                                    .background(
                                        Brush.linearGradient(
                                            listOf(currentIcon.startColor, currentIcon.endColor)
                                        )
                                    )
                                    .border(1.5.dp, Color.White.copy(alpha = 0.25f), cornerShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = currentIcon.glyph,
                                    fontSize = 46.sp
                                )
                            }

                            Spacer(Modifier.height(10.dp))
                            Text(
                                text = appName,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = EchoText
                            )
                            Text(
                                text = "${selectedGlyphName} • ${selectedPalette.name}",
                                fontSize = 11.sp,
                                color = EchoCyan
                            )
                        }
                    }

                    // Shape Selector
                    Column {
                        Text("ICON MASK SHAPE", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = EchoCyan)
                        Spacer(Modifier.height(8.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            IconShape.values().forEach { shape ->
                                val isSelected = selectedShape == shape
                                FilterChip(
                                    selected = isSelected,
                                    onClick = { selectedShape = shape },
                                    label = { Text(shape.label, fontSize = 11.sp) },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = EchoCyan,
                                        selectedLabelColor = Color.Black,
                                        containerColor = EchoCard,
                                        labelColor = EchoText
                                    )
                                )
                            }
                        }
                    }

                    // Glyph Selector
                    Column {
                        Text("ICON SYMBOL GLYPH", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = EchoCyan)
                        Spacer(Modifier.height(8.dp))
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            items(PRESET_ICON_GLYPHS) { (g, name) ->
                                val isSelected = selectedGlyph == g
                                Box(
                                    modifier = Modifier
                                        .size(56.dp)
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(if (isSelected) EchoCyan.copy(alpha = 0.2f) else EchoCard)
                                        .border(
                                            if (isSelected) 2.dp else 1.dp,
                                            if (isSelected) EchoCyan else EchoCardStroke,
                                            RoundedCornerShape(12.dp)
                                        )
                                        .clickable {
                                            selectedGlyph = g
                                            selectedGlyphName = name
                                        },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(g, fontSize = 26.sp)
                                }
                            }
                        }
                    }

                    // Color Palette Selector
                    Column {
                        Text("GRADIENT PALETTE", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = EchoCyan)
                        Spacer(Modifier.height(8.dp))
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            items(PRESET_ICON_PALETTES) { palette ->
                                val isSelected = selectedPalette.name == palette.name
                                Card(
                                    modifier = Modifier
                                        .width(110.dp)
                                        .clickable { selectedPalette = palette },
                                    colors = CardDefaults.cardColors(containerColor = EchoCard),
                                    border = CardDefaults.outlinedCardBorder().copy(
                                        brush = androidx.compose.ui.graphics.SolidColor(
                                            if (isSelected) EchoCyan else EchoCardStroke
                                        )
                                    )
                                ) {
                                    Column(
                                        modifier = Modifier.padding(8.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(28.dp)
                                                .clip(RoundedCornerShape(6.dp))
                                                .background(
                                                    Brush.horizontalGradient(
                                                        listOf(
                                                            currentIcon.startColor,
                                                            currentIcon.endColor
                                                        )
                                                    )
                                                )
                                        )
                                        Spacer(Modifier.height(6.dp))
                                        Text(
                                            text = palette.name,
                                            fontSize = 10.sp,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                            color = if (isSelected) EchoCyan else EchoText
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // SVG Code Export
                    OutlinedButton(
                        onClick = {
                            val svg = currentIcon.toSvg(appName)
                            clipboardManager.setText(AnnotatedString(svg))
                            Toast.makeText(context, "Vector SVG Icon code copied to clipboard!", Toast.LENGTH_SHORT).show()
                        },
                        modifier = Modifier.fillMaxWidth().height(40.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = EchoCyan)
                    ) {
                        Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(8.dp))
                        Text("Copy Vector SVG Icon Code", fontSize = 12.sp)
                    }
                }

                Spacer(Modifier.height(12.dp))

                // Bottom Save Action
                Button(
                    onClick = {
                        onSaveIcon(currentIcon)
                        Toast.makeText(context, "Icon applied to $appName!", Toast.LENGTH_SHORT).show()
                        onDismiss()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("save_custom_icon_button"),
                    colors = ButtonDefaults.buttonColors(containerColor = EchoGreen, contentColor = Color.Black),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("Apply Icon to APK & Web App", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
