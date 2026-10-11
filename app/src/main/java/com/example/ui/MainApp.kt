package com.example.ui

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.CellTower
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ai.DolaAiMode
import com.example.audio.HarmonicToneGenerator
import com.example.ui.components.DolaAiDialog
import com.example.ui.screens.ArchiveScreen
import com.example.ui.screens.FrequencyScreen
import com.example.ui.screens.NetworkScreen
import com.example.ui.screens.ProjectsScreen
import com.example.ui.screens.WorkspaceScreen
import com.example.ui.theme.EchoBg
import com.example.ui.theme.EchoBgAlt
import com.example.ui.theme.EchoCard
import com.example.ui.theme.EchoCardStroke
import com.example.ui.theme.EchoCyan
import com.example.ui.theme.EchoGreen
import com.example.ui.theme.EchoMuted
import com.example.ui.theme.EchoOnCyan
import com.example.ui.theme.EchoText
import com.example.ui.theme.EchoRed
import com.example.ui.viewmodel.WorkspaceViewModel

import android.app.Activity
import android.app.Application
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.PowerSettingsNew
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.saveable.rememberSaveableStateHolder
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.lifecycle.ViewModel
import androidx.compose.runtime.rememberCoroutineScope
import androidx.lifecycle.ViewModelProvider

import com.example.ui.screens.AthenaScreen
import com.example.ui.components.SettingsDialog

enum class MainTab(val index: Int, val title: String) {
    ARCHIVE(0, "Archive"),
    FREQUENCY(1, "Frequency"),
    ATHENA(2, "ATHENA"),
    WORKSPACE(3, "Workspace"),
    PROJECTS(4, "Projects")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainApp(
    currentUserId: String? = null,
    toneGenerator: HarmonicToneGenerator = HarmonicToneGenerator.SHARED,
    isDarkTheme: Boolean = true,
    onToggleDarkTheme: () -> Unit = {},
    onReturnToEntrance: () -> Unit = {}
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val app = context.applicationContext as Application

    val workspaceViewModel: WorkspaceViewModel = viewModel(
        factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return WorkspaceViewModel(app, null, null) as T
            }
        }
    )

    var selectedTab by rememberSaveable { mutableIntStateOf(MainTab.ARCHIVE.index) }
    val isTonePlaying by toneGenerator.isPlayingState.collectAsState()
    var showDolaAssistantDialog by remember { mutableStateOf(false) }
    var showSettingsDialog by remember { mutableStateOf(false) }
    var showExitConfirmationDialog by remember { mutableStateOf(false) }

    val saveableStateHolder = rememberSaveableStateHolder()
    val visitedTabs = remember { mutableStateListOf(MainTab.ARCHIVE.index) }
    LaunchedEffect(selectedTab) {
        if (!visitedTabs.contains(selectedTab)) {
            visitedTabs.add(selectedTab)
        }
    }

    // Standard APK Back Navigation: navigate back to Home tab or close open dialogs
    val canGoBackInApp = showExitConfirmationDialog || showSettingsDialog || showDolaAssistantDialog || selectedTab != MainTab.ARCHIVE.index
    BackHandler(enabled = canGoBackInApp) {
        when {
            showExitConfirmationDialog -> showExitConfirmationDialog = false
            showSettingsDialog -> showSettingsDialog = false
            showDolaAssistantDialog -> showDolaAssistantDialog = false
            selectedTab != MainTab.ARCHIVE.index -> selectedTab = MainTab.ARCHIVE.index
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = EchoBg,
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(10.dp)
                                .clip(CircleShape)
                                .background(EchoCyan)
                        )
                        Column {
                            Text(
                                text = "ATHENA",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = EchoText
                            )
                            Text(
                                text = "42 GENIUSES • ECHO-INTERFACE",
                                fontSize = 10.sp,
                                fontFamily = FontFamily.Monospace,
                                color = EchoCyan
                            )
                        }
                    }
                },
                actions = {
                    // Quick Dola AI Assistant Trigger
                    IconButton(
                        onClick = { showDolaAssistantDialog = true },
                        modifier = Modifier.testTag("top_dola_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = "Dola AI",
                            tint = EchoCyan,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    // Quick Dark / Light Mode Toggle
                    IconButton(
                        onClick = onToggleDarkTheme,
                        modifier = Modifier.testTag("top_theme_toggle_button")
                    ) {
                        Icon(
                            imageVector = if (isDarkTheme) Icons.Default.LightMode else Icons.Default.DarkMode,
                            contentDescription = "Toggle Theme",
                            tint = if (isDarkTheme) EchoCyan else EchoGreen,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    // Settings Dialog Trigger
                    IconButton(
                        onClick = { showSettingsDialog = true },
                        modifier = Modifier.testTag("top_settings_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Settings",
                            tint = EchoText,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    // In-App Custom "Exit App" Button
                    IconButton(
                        onClick = { showExitConfirmationDialog = true },
                        modifier = Modifier
                            .padding(end = 4.dp)
                            .testTag("top_exit_app_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.PowerSettingsNew,
                            contentDescription = "Exit App",
                            tint = EchoRed,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = EchoBgAlt)
            )
        },
        bottomBar = {
            // Split line at the bottom and safe navigationBarsPadding
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
            ) {
                // Crisp split divider separating content from bottom navigation
                HorizontalDivider(
                    color = EchoCardStroke,
                    thickness = 1.dp
                )

                // 5-Tab Material 3 Navigation Bar
                NavigationBar(
                    containerColor = EchoBgAlt,
                    contentColor = EchoCyan,
                    modifier = Modifier.testTag("main_bottom_nav")
                ) {
                    NavigationBarItem(
                        selected = selectedTab == MainTab.ARCHIVE.index,
                        onClick = { selectedTab = MainTab.ARCHIVE.index },
                        icon = { Icon(Icons.Default.Book, contentDescription = "Archive") },
                        label = { Text("Archive", fontSize = 11.sp) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = EchoOnCyan,
                            selectedTextColor = EchoCyan,
                            indicatorColor = EchoCyan,
                            unselectedIconColor = EchoMuted,
                            unselectedTextColor = EchoMuted
                        ),
                        modifier = Modifier.testTag("nav_archive")
                    )
                    NavigationBarItem(
                        selected = selectedTab == MainTab.FREQUENCY.index,
                        onClick = { selectedTab = MainTab.FREQUENCY.index },
                        icon = { Icon(Icons.Default.GraphicEq, contentDescription = "Frequency") },
                        label = { Text("Frequency", fontSize = 10.sp) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = EchoOnCyan,
                            selectedTextColor = EchoCyan,
                            indicatorColor = EchoCyan,
                            unselectedIconColor = EchoMuted,
                            unselectedTextColor = EchoMuted
                        ),
                        modifier = Modifier.testTag("nav_frequency")
                    )
                    NavigationBarItem(
                        selected = selectedTab == MainTab.ATHENA.index,
                        onClick = { selectedTab = MainTab.ATHENA.index },
                        icon = { Icon(Icons.Default.AutoAwesome, contentDescription = "ATHENA") },
                        label = { Text("ATHENA", fontSize = 10.sp, fontWeight = FontWeight.Bold) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = EchoOnCyan,
                            selectedTextColor = EchoCyan,
                            indicatorColor = EchoCyan,
                            unselectedIconColor = EchoMuted,
                            unselectedTextColor = EchoMuted
                        ),
                        modifier = Modifier.testTag("nav_athena")
                    )
                    NavigationBarItem(
                        selected = selectedTab == MainTab.WORKSPACE.index,
                        onClick = { selectedTab = MainTab.WORKSPACE.index },
                        icon = { Icon(Icons.Default.Folder, contentDescription = "Workspace") },
                        label = { Text("Workspace", fontSize = 11.sp) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = EchoOnCyan,
                            selectedTextColor = EchoCyan,
                            indicatorColor = EchoCyan,
                            unselectedIconColor = EchoMuted,
                            unselectedTextColor = EchoMuted
                        ),
                        modifier = Modifier.testTag("nav_workspace")
                    )

                    NavigationBarItem(
                        selected = selectedTab == MainTab.PROJECTS.index,
                        onClick = { selectedTab = MainTab.PROJECTS.index },
                        icon = { Icon(Icons.Default.Palette, contentDescription = "Projects") },
                        label = { Text("Projects", fontSize = 11.sp) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = EchoOnCyan,
                            selectedTextColor = EchoCyan,
                            indicatorColor = EchoCyan,
                            unselectedIconColor = EchoMuted,
                            unselectedTextColor = EchoMuted
                        ),
                        modifier = Modifier.testTag("nav_projects")
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Persistent Multi-Tab Host: Preserves exact state, scroll, and inputs across tab switches
            saveableStateHolder.SaveableStateProvider(key = selectedTab) {
                when (selectedTab) {
                    MainTab.ARCHIVE.index -> {
                        ArchiveScreen(
                            onNavigateToFrequency = { selectedTab = MainTab.FREQUENCY.index },
                            onNavigateToNetwork = { selectedTab = MainTab.FREQUENCY.index },
                            onNavigateToWorkspace = { selectedTab = MainTab.WORKSPACE.index },
                            onSaveQuoteToFieldJournal = { quote, chapterTitle ->
                                val firstLine = quote.trim().lines().firstOrNull()?.take(36) ?: "Archived Quote"
                                workspaceViewModel.createNote(
                                    name = "Quote: $firstLine...",
                                    initialText = "“$quote”\n\n— Source: $chapterTitle\n[Saved via Archive Reading Companion]",
                                    tags = "Quote, FieldJournal, Archive"
                                )
                            }
                        )
                    }
                    MainTab.FREQUENCY.index -> {
                        FrequencyScreen(toneGenerator = toneGenerator)
                    }
                    MainTab.ATHENA.index -> {
                        AthenaScreen(
                            onSaveToWorkspaceNote = { noteContent ->
                                val firstLine = noteContent.trim().lines().firstOrNull()?.take(40) ?: "Athena Note"
                                workspaceViewModel.createNote(name = firstLine, initialText = noteContent)
                                selectedTab = MainTab.WORKSPACE.index
                            }
                        )
                    }
                    MainTab.WORKSPACE.index -> {
                        WorkspaceScreen(viewModel = workspaceViewModel)
                    }
                    MainTab.PROJECTS.index -> {
                        ProjectsScreen(
                            viewModel = workspaceViewModel,
                            onNavigateToWorkspaceFolder = { folderId ->
                                workspaceViewModel.navigateToFolder(if (folderId.isBlank()) null else folderId)
                                selectedTab = MainTab.WORKSPACE.index
                            }
                        )
                    }
                }
            }

            // Global Dola AI Dialog
            if (showDolaAssistantDialog) {
                DolaAiDialog(
                    initialText = "",
                    initialMode = DolaAiMode.CHAT,
                    firestoreRepository = null,
                    currentUserId = null,
                    onSaveAsNewNote = { noteText ->
                        val firstLine = noteText.trim().lines().firstOrNull()?.take(40) ?: "Dola AI Note"
                        workspaceViewModel.createNote(name = firstLine, initialText = noteText)
                        selectedTab = MainTab.WORKSPACE.index
                    },
                    onDismiss = { showDolaAssistantDialog = false }
                )
            }

            // In-App Settings Dialog
            if (showSettingsDialog) {
                SettingsDialog(
                    isDarkTheme = isDarkTheme,
                    onToggleDarkTheme = onToggleDarkTheme,
                    onRequestExitApp = {
                        showSettingsDialog = false
                        showExitConfirmationDialog = true
                    },
                    onDismiss = { showSettingsDialog = false }
                )
            }

            // In-App Exit Confirmation Dialog (Non-native navigation termination)
            if (showExitConfirmationDialog) {
                AlertDialog(
                    onDismissRequest = { showExitConfirmationDialog = false },
                    title = {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Icon(Icons.Default.PowerSettingsNew, contentDescription = null, tint = EchoRed)
                            Text("Exit ATHENA Application?", fontWeight = FontWeight.Bold, color = EchoText)
                        }
                    },
                    text = {
                        Text(
                            "Are you sure you want to terminate the application? All active audio sessions, resonance presets, and workspace notes are securely preserved.",
                            color = EchoMuted,
                            lineHeight = 20.sp
                        )
                    },
                    confirmButton = {
                        Button(
                            onClick = {
                                showExitConfirmationDialog = false
                                toneGenerator.stop()
                                (context as? Activity)?.finishAffinity()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = EchoRed, contentColor = Color.White),
                            modifier = Modifier.testTag("confirm_exit_app_button")
                        ) {
                            Icon(Icons.AutoMirrored.Filled.ExitToApp, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(Modifier.width(6.dp))
                            Text("Exit App", fontWeight = FontWeight.Bold)
                        }
                    },
                    dismissButton = {
                        OutlinedButton(
                            onClick = { showExitConfirmationDialog = false },
                            modifier = Modifier.testTag("cancel_exit_app_button")
                        ) {
                            Text("Cancel", color = EchoText)
                        }
                    },
                    containerColor = EchoCard,
                    modifier = Modifier.testTag("exit_app_dialog")
                )
            }
        }
    }
}
