package com.example.ui.components

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.PowerSettingsNew
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import com.example.ui.theme.EchoBgAlt
import com.example.ui.theme.EchoCard
import com.example.ui.theme.EchoCardStroke
import com.example.ui.theme.EchoCyan
import com.example.ui.theme.EchoGreen
import com.example.ui.theme.EchoMuted
import com.example.ui.theme.EchoOnCyan
import com.example.ui.theme.EchoRed
import com.example.ui.theme.EchoText

/**
 * Settings Dialog offering theme controls, cache reset,
 * app telemetry, and custom in-app exit.
 */
@Composable
fun SettingsDialog(
    isDarkTheme: Boolean,
    onToggleDarkTheme: () -> Unit,
    onClearData: () -> Unit = {},
    onRequestExitApp: () -> Unit,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .clip(RoundedCornerShape(16.dp))
                .border(1.dp, EchoCardStroke, RoundedCornerShape(16.dp))
                .testTag("settings_dialog"),
            colors = CardDefaults.cardColors(containerColor = EchoBgAlt)
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(EchoCyan),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Settings, contentDescription = null, tint = EchoOnCyan, modifier = Modifier.size(20.dp))
                        }
                        Column {
                            Text(
                                text = "ATHENA Settings",
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Bold,
                                color = EchoText
                            )
                            Text(
                                text = "Appearance & Application Controls",
                                fontSize = 11.sp,
                                color = EchoMuted
                            )
                        }
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.testTag("close_settings_button")
                    ) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = EchoText)
                    }
                }

                HorizontalDivider(color = EchoCardStroke, thickness = 1.dp)

                // 1. APPEARANCE: Dark / Light Mode Toggle
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = EchoCard),
                    border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(EchoCardStroke))
                ) {
                    Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Icon(
                                    imageVector = if (isDarkTheme) Icons.Default.DarkMode else Icons.Default.LightMode,
                                    contentDescription = null,
                                    tint = if (isDarkTheme) EchoCyan else EchoGreen,
                                    modifier = Modifier.size(20.dp)
                                )
                                Column {
                                    Text("Theme Appearance", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = EchoText)
                                    Text(if (isDarkTheme) "Dark Atmospheric Theme" else "Light Solar Theme", fontSize = 11.sp, color = EchoMuted)
                                }
                            }

                            Switch(
                                checked = !isDarkTheme,
                                onCheckedChange = { onToggleDarkTheme() },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = EchoGreen,
                                    checkedTrackColor = EchoGreen.copy(alpha = 0.3f),
                                    uncheckedThumbColor = EchoCyan,
                                    uncheckedTrackColor = EchoCardStroke
                                ),
                                modifier = Modifier.testTag("theme_switch")
                            )
                        }
                    }
                }

                // 2. STORAGE & DATA MANAGEMENT
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = EchoCard),
                    border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(EchoCardStroke))
                ) {
                    Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Icon(Icons.Default.DeleteSweep, contentDescription = null, tint = EchoMuted, modifier = Modifier.size(18.dp))
                            Text("Data Management", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = EchoText)
                        }
                        Text(
                            "Clear temporary audio buffers and refresh network cache.",
                            fontSize = 11.sp,
                            color = EchoMuted
                        )
                        OutlinedButton(
                            onClick = {
                                onClearData()
                                Toast.makeText(context, "Temporary session cache refreshed", Toast.LENGTH_SHORT).show()
                            },
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = EchoMuted),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Refresh Session Buffers", fontSize = 12.sp)
                        }
                    }
                }

                // 3. IN-APP EXIT ACTION
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = EchoRed.copy(alpha = 0.12f)),
                    border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(EchoRed.copy(alpha = 0.4f)))
                ) {
                    Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Icon(Icons.Default.PowerSettingsNew, contentDescription = null, tint = EchoRed, modifier = Modifier.size(22.dp))
                            Column {
                                Text("In-App Application Exit", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = EchoRed)
                                Text("Cleanly terminates app session without system back keys", fontSize = 11.sp, color = EchoText)
                            }
                        }

                        Button(
                            onClick = {
                                onDismiss()
                                onRequestExitApp()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = EchoRed, contentColor = Color.White),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(46.dp)
                                .testTag("settings_exit_app_button")
                        ) {
                            Icon(Icons.AutoMirrored.Filled.ExitToApp, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(Modifier.width(8.dp))
                            Text("Exit ATHENA App", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                // App Version Footer
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "ATHENA OS • 42.6 Hz Acoustic Intelligence Suite v1.5",
                        fontSize = 10.sp,
                        fontFamily = FontFamily.Monospace,
                        color = EchoMuted
                    )
                }
            }
        }
    }
}
