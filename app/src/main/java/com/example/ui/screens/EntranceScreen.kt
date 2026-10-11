package com.example.ui.screens

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.HeartbeatIndicator
import com.example.ui.theme.EchoBg
import com.example.ui.theme.EchoBgAlt
import com.example.ui.theme.EchoCard
import com.example.ui.theme.EchoCardStroke
import com.example.ui.theme.EchoCyan
import com.example.ui.theme.EchoGreen
import com.example.ui.theme.EchoMuted
import com.example.ui.theme.EchoOnCyan
import com.example.ui.theme.EchoText

/**
 * Welcome Entrance Screen that replaces sign-in requirements with a direct "ENTER" button.
 * Welcomes users directly into the ATHENA 42.6 Hz acoustic intelligence and 42 Geniuses chronicle.
 */
@Composable
fun EntranceScreen(
    onEnter: () -> Unit,
    isDarkTheme: Boolean = true,
    onToggleDarkTheme: () -> Unit = {}
) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse_transition")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.98f,
        targetValue = 1.04f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_scale"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(EchoBg)
            .statusBarsPadding()
            .navigationBarsPadding()
            .testTag("entrance_screen")
    ) {
        // Theme switch at top right
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Status badge
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(EchoBgAlt)
                    .border(1.dp, EchoCardStroke, RoundedCornerShape(20.dp))
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(EchoGreen)
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    text = "SYSTEM ONLINE • 42.6 Hz",
                    fontSize = 11.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.SemiBold,
                    color = EchoGreen
                )
            }

            IconButton(
                onClick = onToggleDarkTheme,
                modifier = Modifier.testTag("entrance_theme_toggle")
            ) {
                Icon(
                    imageVector = if (isDarkTheme) Icons.Default.LightMode else Icons.Default.DarkMode,
                    contentDescription = "Toggle Theme",
                    tint = if (isDarkTheme) EchoCyan else EchoGreen,
                    modifier = Modifier.size(22.dp)
                )
            }
        }

        // Main Center Content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Spacer(Modifier.height(24.dp))

            // Central Glowing Heartbeat Emblem
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .scale(pulseScale)
                    .clip(CircleShape)
                    .background(
                        Brush.radialGradient(
                            listOf(EchoCyan.copy(alpha = 0.25f), Color.Transparent)
                        )
                    )
                    .clickable { onEnter() },
                contentAlignment = Alignment.Center
            ) {
                HeartbeatIndicator(size = 72.dp)
            }

            Spacer(Modifier.height(18.dp))

            // Title & Branding
            Text(
                text = "ATHENA",
                fontSize = 38.sp,
                fontWeight = FontWeight.ExtraBold,
                color = EchoCyan,
                letterSpacing = 4.sp
            )

            Text(
                text = "42 GENIUSES • ECHO-INTERFACE",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace,
                color = EchoText,
                letterSpacing = 1.sp,
                modifier = Modifier.padding(top = 4.dp)
            )

            Text(
                text = "Standing bedrock wave acoustics, 42.6 Hz resonance frequency lab, and multimodal scientific intelligence.",
                fontSize = 13.sp,
                color = EchoMuted,
                textAlign = TextAlign.Center,
                lineHeight = 18.sp,
                modifier = Modifier.padding(top = 8.dp, start = 12.dp, end = 12.dp)
            )

            Spacer(Modifier.height(28.dp))

            // Telemetry Metric Chips Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = EchoCard),
                border = CardDefaults.outlinedCardBorder().copy(
                    brush = Brush.horizontalGradient(listOf(EchoCyan.copy(alpha = 0.5f), EchoGreen.copy(alpha = 0.5f)))
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp, horizontal = 16.dp),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("RESONANCE", fontSize = 10.sp, color = EchoMuted, fontFamily = FontFamily.Monospace)
                        Text("42.600 Hz", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = EchoCyan)
                    }
                    Box(modifier = Modifier.width(1.dp).height(24.dp).background(EchoCardStroke))
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("VELOCITY", fontSize = 10.sp, color = EchoMuted, fontFamily = FontFamily.Monospace)
                        Text("3,420 m/s", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = EchoGreen)
                    }
                    Box(modifier = Modifier.width(1.dp).height(24.dp).background(EchoCardStroke))
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("MESH NODES", fontSize = 10.sp, color = EchoMuted, fontFamily = FontFamily.Monospace)
                        Text("11 Nodes", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = EchoText)
                    }
                }
            }

            Spacer(Modifier.height(32.dp))

            // PRIMARY "ENTER" BUTTON (Replaces Sign In)
            Button(
                onClick = onEnter,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(58.dp)
                    .testTag("enter_button"),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = EchoCyan,
                    contentColor = EchoOnCyan
                ),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 6.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = null,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(Modifier.width(12.dp))
                    Text(
                        text = "ENTER",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 2.sp
                    )
                }
            }

            Spacer(Modifier.height(16.dp))

            // Direct access feature highlights (No account / No sign-in needed)
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = EchoGreen,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(Modifier.width(6.dp))
                Text(
                    text = "No Sign-In Required • Standalone Offline Access",
                    fontSize = 12.sp,
                    color = EchoMuted
                )
            }

            Spacer(Modifier.height(24.dp))
        }
    }
}
