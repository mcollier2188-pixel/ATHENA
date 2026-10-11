package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.widget.Toast
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import com.example.ai.DolaAiMode
import com.example.ui.components.DolaAiDialog
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.ContentPaste
import androidx.compose.material.icons.filled.CreateNewFolder
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.DriveFileMove
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Note
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material.icons.filled.Restore
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
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
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import com.example.data.WorkspaceItem
import com.example.ui.components.ReaderDialog
import com.example.ui.theme.EchoBg
import com.example.ui.theme.EchoBgAlt
import com.example.ui.theme.EchoCard
import com.example.ui.theme.EchoCardStroke
import com.example.ui.theme.EchoCyan
import com.example.ui.theme.EchoGreen
import com.example.ui.theme.EchoMuted
import com.example.ui.theme.EchoOnCyan
import com.example.ui.theme.EchoText
import com.example.ui.viewmodel.WorkspaceViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorkspaceScreen(
    viewModel: WorkspaceViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val currentFolderId by viewModel.currentFolderId.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val isTrashView by viewModel.isTrashView.collectAsState()
    val isSelectionMode by viewModel.isSelectionMode.collectAsState()
    val selectedItemIds by viewModel.selectedItemIds.collectAsState()
    val items by viewModel.currentItems.collectAsState()
    val allFolders by viewModel.allFolders.collectAsState()

    var showNewFolderDialog by remember { mutableStateOf(false) }
    var newFolderName by remember { mutableStateOf("") }
    var activeEditingNote by remember { mutableStateOf<WorkspaceItem?>(null) }
    var activeReaderItem by remember { mutableStateOf<Triple<String, String, String>?>(null) }
    var showDolaGeneralDialog by remember { mutableStateOf(false) }

    // Breadcrumb title
    val currentFolder = allFolders.find { it.id == currentFolderId }

    Box(modifier = modifier.fillMaxSize().background(EchoBg)) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Header
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = if (isTrashView) "Workspace — Trash" else "Workspace",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = EchoText
                        )
                        Text(
                            text = if (isTrashView) "Deleted items kept for restoration" else "Notes, folders and field logs kept private to you",
                            fontSize = 13.sp,
                            color = EchoMuted
                        )
                    }

                    if (currentFolderId != null && !isTrashView) {
                        IconButton(
                            onClick = {
                                val parent = allFolders.find { it.id == currentFolderId }?.parentId
                                viewModel.navigateToFolder(parent)
                            }
                        ) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Go up", tint = EchoCyan)
                        }
                    }
                }
            }

            // Breadcrumb path trail
            if (!isTrashView) {
                item {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "Home",
                            fontWeight = if (currentFolderId == null) FontWeight.Bold else FontWeight.Normal,
                            color = if (currentFolderId == null) EchoCyan else EchoMuted,
                            modifier = Modifier.clickable { viewModel.navigateToFolder(null) }
                        )
                        if (currentFolder != null) {
                            Text(" / ", color = EchoMuted)
                            Text(
                                text = currentFolder.name,
                                fontWeight = FontWeight.Bold,
                                color = EchoCyan
                            )
                        }
                    }
                }
            }

            // Action Buttons Toolbar
            item {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    if (!isTrashView) {
                        item {
                            Button(
                                onClick = {
                                    val noteId = viewModel.createNote()
                                    // Open editor immediately
                                    activeEditingNote = WorkspaceItem(
                                        id = noteId,
                                        parentId = currentFolderId,
                                        kind = "note",
                                        name = "Untitled note",
                                        text = "",
                                        modifiedTimestamp = System.currentTimeMillis()
                                    )
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = EchoCyan, contentColor = EchoOnCyan),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.testTag("new_note_button")
                            ) {
                                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(Modifier.width(4.dp))
                                Text("New note", fontWeight = FontWeight.Bold)
                            }
                        }
                        item {
                            OutlinedButton(
                                onClick = {
                                    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                    val clip = clipboard.primaryClip
                                    val text = clip?.getItemAt(0)?.text?.toString() ?: ""
                                    if (text.isNotBlank()) {
                                        val firstLine = text.trim().lines().firstOrNull()?.take(40) ?: "Pasted note"
                                        viewModel.createNote(name = firstLine, initialText = text)
                                        Toast.makeText(context, "Pasted as new note", Toast.LENGTH_SHORT).show()
                                    } else {
                                        Toast.makeText(context, "Clipboard is empty", Toast.LENGTH_SHORT).show()
                                    }
                                },
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Icon(Icons.Default.ContentPaste, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(Modifier.width(4.dp))
                                Text("Paste as note", color = EchoText)
                            }
                        }
                        item {
                            OutlinedButton(
                                onClick = { showNewFolderDialog = true },
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.testTag("new_folder_button")
                            ) {
                                Icon(Icons.Default.CreateNewFolder, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(Modifier.width(4.dp))
                                Text("New folder", color = EchoText)
                            }
                        }
                        item {
                            Button(
                                onClick = { showDolaGeneralDialog = true },
                                colors = ButtonDefaults.buttonColors(containerColor = EchoCard, contentColor = EchoCyan),
                                shape = RoundedCornerShape(8.dp),
                                border = androidx.compose.foundation.BorderStroke(1.dp, EchoCyan),
                                modifier = Modifier.testTag("workspace_dola_ai_button")
                            ) {
                                Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(16.dp), tint = EchoCyan)
                                Spacer(Modifier.width(4.dp))
                                Text("Dola AI", fontWeight = FontWeight.Bold)
                            }
                        }
                        item {
                            OutlinedButton(
                                onClick = { viewModel.toggleSelectionMode() },
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text(if (isSelectionMode) "Done" else "Select", color = EchoText)
                            }
                        }
                    }

                    item {
                        OutlinedButton(
                            onClick = { viewModel.toggleTrashView() },
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Icon(Icons.Default.Delete, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(Modifier.width(4.dp))
                            Text(if (isTrashView) "Back to Files" else "Trash", color = EchoText)
                        }
                    }

                    item {
                        OutlinedButton(
                            onClick = {
                                val sendIntent = Intent().apply {
                                    action = Intent.ACTION_SEND
                                    putExtra(Intent.EXTRA_SUBJECT, "ECHO-INTERFACE Workspace Backup")
                                    putExtra(Intent.EXTRA_TEXT, "42 Geniuses Workspace Export\nTotal items: ${items.size}\nCanon v4.0")
                                    type = "text/plain"
                                }
                                context.startActivity(Intent.createChooser(sendIntent, "Share Backup"))
                            },
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(Modifier.width(4.dp))
                            Text("Backup", color = EchoText)
                        }
                    }
                }
            }

            // Selection batch toolbar (when items are selected)
            if (isSelectionMode && selectedItemIds.isNotEmpty()) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = EchoCard),
                        border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(EchoCyan))
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "${selectedItemIds.size} selected",
                                fontWeight = FontWeight.Bold,
                                color = EchoCyan,
                                fontSize = 14.sp
                            )
                            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                Button(
                                    onClick = { viewModel.togglePinSelected() },
                                    colors = ButtonDefaults.buttonColors(containerColor = EchoBgAlt, contentColor = EchoText),
                                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Text("Pin", fontSize = 12.sp)
                                }
                                Button(
                                    onClick = {
                                        if (isTrashView) viewModel.deleteSelectedPermanently()
                                        else viewModel.moveSelectedToTrash()
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF4444), contentColor = Color.White),
                                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Text(if (isTrashView) "Delete Forever" else "Trash", fontSize = 12.sp)
                                }
                            }
                        }
                    }
                }
            }

            // Search Bar
            item {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { viewModel.setSearch(it) },
                    placeholder = { Text("Search names, tags and text", color = EchoMuted) },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = EchoMuted) },
                    trailingIcon = {
                        if (searchQuery.isNotBlank()) {
                            IconButton(onClick = { viewModel.setSearch("") }) {
                                Icon(Icons.Default.Close, contentDescription = "Clear search", tint = EchoMuted)
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("workspace_search_input"),
                    shape = RoundedCornerShape(10.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = EchoCyan,
                        unfocusedBorderColor = EchoCardStroke,
                        focusedTextColor = EchoText,
                        unfocusedTextColor = EchoText,
                        focusedContainerColor = EchoCard,
                        unfocusedContainerColor = EchoCard
                    )
                )
            }

            // Items List
            if (items.isEmpty()) {
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 40.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = if (isTrashView) "Trash is empty." else "Nothing here yet.\nTap 'New note' or 'New folder' to begin.",
                            fontSize = 14.sp,
                            color = EchoMuted,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                            lineHeight = 20.sp
                        )
                    }
                }
            } else {
                items(items, key = { it.id }) { item ->
                    WorkspaceItemRow(
                        item = item,
                        isSelectionMode = isSelectionMode,
                        isSelected = selectedItemIds.contains(item.id),
                        isTrashView = isTrashView,
                        onToggleSelect = { viewModel.toggleSelectItem(item.id) },
                        onClick = {
                            if (isSelectionMode) {
                                viewModel.toggleSelectItem(item.id)
                            } else if (item.kind == "folder") {
                                viewModel.navigateToFolder(item.id)
                            } else {
                                activeEditingNote = item
                            }
                        },
                        onRestore = { viewModel.restoreItem(item.id) },
                        onDeleteForever = { viewModel.deletePermanently(item.id) }
                    )
                }
            }
        }

        // New Folder Dialog
        if (showNewFolderDialog) {
            AlertDialog(
                onDismissRequest = { showNewFolderDialog = false },
                title = { Text("New Folder", color = EchoText) },
                text = {
                    OutlinedTextField(
                        value = newFolderName,
                        onValueChange = { newFolderName = it },
                        placeholder = { Text("Folder name", color = EchoMuted) },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = EchoCyan,
                            unfocusedBorderColor = EchoCardStroke,
                            focusedTextColor = EchoText,
                            unfocusedTextColor = EchoText
                        )
                    )
                },
                confirmButton = {
                    Button(
                        onClick = {
                            viewModel.createFolder(newFolderName)
                            newFolderName = ""
                            showNewFolderDialog = false
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = EchoCyan, contentColor = EchoOnCyan)
                    ) {
                        Text("Create")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showNewFolderDialog = false }) {
                        Text("Cancel", color = EchoMuted)
                    }
                },
                containerColor = EchoBgAlt
            )
        }

        // Note Editor / Viewer Dialog
        activeEditingNote?.let { note ->
            NoteEditorDialog(
                note = note,
                allFolders = allFolders,
                onSave = { updated ->
                    viewModel.updateItem(updated)
                    activeEditingNote = null
                },
                onDelete = {
                    viewModel.moveToTrash(note.id)
                    activeEditingNote = null
                },
                onOpenReader = {
                    activeReaderItem = Triple(note.name, note.tags, note.text)
                },
                onDismiss = {
                    activeEditingNote = null
                }
            )
        }

        // Reader overlay
        activeReaderItem?.let { (title, subtitle, content) ->
            ReaderDialog(
                title = title,
                subtitle = subtitle,
                content = content,
                onDismiss = { activeReaderItem = null }
            )
        }

        // Dola AI Copilot Dialog
        if (showDolaGeneralDialog) {
            DolaAiDialog(
                initialText = "",
                initialMode = DolaAiMode.CHAT,
                onSaveAsNewNote = { newText ->
                    val firstLine = newText.trim().lines().firstOrNull()?.take(40) ?: "Dola AI Note"
                    viewModel.createNote(name = firstLine, initialText = newText)
                },
                onDismiss = { showDolaGeneralDialog = false }
            )
        }
    }
}

@Composable
fun WorkspaceItemRow(
    item: WorkspaceItem,
    isSelectionMode: Boolean,
    isSelected: Boolean,
    isTrashView: Boolean,
    onToggleSelect: () -> Unit,
    onClick: () -> Unit,
    onRestore: () -> Unit,
    onDeleteForever: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .testTag("item_row_${item.id}"),
        colors = CardDefaults.cardColors(containerColor = EchoBgAlt),
        border = CardDefaults.outlinedCardBorder().copy(
            brush = androidx.compose.ui.graphics.SolidColor(
                if (isSelected) EchoCyan else EchoCardStroke
            )
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (isSelectionMode) {
                Checkbox(
                    checked = isSelected,
                    onCheckedChange = { onToggleSelect() },
                    colors = CheckboxDefaults.colors(checkedColor = EchoCyan, checkmarkColor = EchoOnCyan)
                )
                Spacer(Modifier.width(8.dp))
            } else {
                Text(
                    text = if (item.kind == "folder") "📁" else "📝",
                    fontSize = 20.sp
                )
                Spacer(Modifier.width(12.dp))
            }

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (item.isPinned) {
                        Text("📌 ", fontSize = 12.sp)
                    }
                    Text(
                        text = item.name,
                        fontWeight = FontWeight.SemiBold,
                        color = EchoText,
                        fontSize = 15.sp
                    )
                }
                Spacer(Modifier.height(2.dp))
                val wordCount = if (item.kind == "note") "${item.text.trim().split("\\s+".toRegex()).filter { it.isNotBlank() }.size} words" else "Folder"
                val tagStr = if (item.tags.isNotBlank()) " · ${item.tags}" else ""
                Text(
                    text = "$wordCount$tagStr",
                    fontSize = 12.sp,
                    color = EchoMuted
                )
            }

            if (isTrashView) {
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    IconButton(onClick = onRestore) {
                        Icon(Icons.Default.Restore, contentDescription = "Restore", tint = EchoGreen)
                    }
                    IconButton(onClick = onDeleteForever) {
                        Icon(Icons.Default.DeleteForever, contentDescription = "Delete Forever", tint = Color(0xFFEF4444))
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteEditorDialog(
    note: WorkspaceItem,
    allFolders: List<WorkspaceItem>,
    onSave: (WorkspaceItem) -> Unit,
    onDelete: () -> Unit,
    onOpenReader: () -> Unit,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    var title by remember { mutableStateOf(note.name) }
    var text by remember { mutableStateOf(note.text) }
    var tags by remember { mutableStateOf(note.tags) }
    var isPinned by remember { mutableStateOf(note.isPinned) }
    var parentId by remember { mutableStateOf(note.parentId) }
    var showFolderMenu by remember { mutableStateOf(false) }
    var showDolaEditorDialog by remember { mutableStateOf(false) }
    var dolaEditorMode by remember { mutableStateOf(DolaAiMode.REFINE) }

    val wordCount = remember(text) {
        if (text.isBlank()) 0 else text.trim().split("\\s+".toRegex()).size
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
                // Top control bar
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = {
                        onSave(note.copy(name = title, text = text, tags = tags, isPinned = isPinned, parentId = parentId))
                    }) {
                        Icon(Icons.Default.Check, contentDescription = "Save", tint = EchoCyan)
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(
                            onClick = {
                                dolaEditorMode = DolaAiMode.REFINE
                                showDolaEditorDialog = true
                            },
                            modifier = Modifier.testTag("note_dola_ai_button")
                        ) {
                            Icon(Icons.Default.AutoAwesome, contentDescription = "Dola AI Copilot", tint = EchoCyan)
                        }
                        IconButton(onClick = onOpenReader) {
                            Icon(Icons.AutoMirrored.Filled.MenuBook, contentDescription = "Read Mode", tint = EchoText)
                        }
                        IconButton(onClick = {
                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            clipboard.setPrimaryClip(ClipData.newPlainText("Note text", text))
                            Toast.makeText(context, "Copied to clipboard", Toast.LENGTH_SHORT).show()
                        }) {
                            Icon(Icons.Default.ContentCopy, contentDescription = "Copy", tint = EchoText)
                        }
                        IconButton(onClick = { isPinned = !isPinned }) {
                            Icon(Icons.Default.PushPin, contentDescription = "Pin", tint = if (isPinned) EchoCyan else EchoMuted)
                        }
                        IconButton(onClick = onDelete) {
                            Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color(0xFFEF4444))
                        }
                        IconButton(onClick = onDismiss) {
                            Icon(Icons.Default.Close, contentDescription = "Close", tint = EchoText)
                        }
                    }
                }

                HorizontalDivider(color = EchoCardStroke, thickness = 1.dp)
                Spacer(Modifier.height(10.dp))

                // Title input
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Title") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = EchoCyan,
                        unfocusedBorderColor = EchoCardStroke,
                        focusedTextColor = EchoText,
                        unfocusedTextColor = EchoText
                    )
                )

                Spacer(Modifier.height(8.dp))

                // Tags input
                OutlinedTextField(
                    value = tags,
                    onValueChange = { tags = it },
                    label = { Text("Tags (comma-separated)") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = EchoCyan,
                        unfocusedBorderColor = EchoCardStroke,
                        focusedTextColor = EchoText,
                        unfocusedTextColor = EchoText
                    )
                )

                Spacer(Modifier.height(8.dp))

                // Folder destination selector
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(EchoCard)
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val currentFolderTitle = allFolders.find { it.id == parentId }?.name ?: "Home"
                    Text("Folder: $currentFolderTitle", color = EchoMuted, fontSize = 13.sp)

                    Box {
                        TextButton(onClick = { showFolderMenu = true }) {
                            Text("Move to...", color = EchoCyan, fontSize = 12.sp)
                        }
                        DropdownMenu(
                            expanded = showFolderMenu,
                            onDismissRequest = { showFolderMenu = false },
                            modifier = Modifier.background(EchoBgAlt)
                        ) {
                            DropdownMenuItem(
                                text = { Text("Home (Root)", color = EchoText) },
                                onClick = {
                                    parentId = null
                                    showFolderMenu = false
                                }
                            )
                            allFolders.forEach { f ->
                                DropdownMenuItem(
                                    text = { Text(f.name, color = EchoText) },
                                    onClick = {
                                        parentId = f.id
                                        showFolderMenu = false
                                    }
                                )
                            }
                        }
                    }
                }

                // Word count & Dola Quick Actions banner
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "$wordCount words · ${text.length} chars",
                        fontSize = 11.sp,
                        color = EchoMuted
                    )

                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        TextButton(
                            onClick = {
                                dolaEditorMode = DolaAiMode.REFINE
                                showDolaEditorDialog = true
                            },
                            contentPadding = PaddingValues(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text("⚡ Refine", color = EchoCyan, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                        TextButton(
                            onClick = {
                                dolaEditorMode = DolaAiMode.EXPAND
                                showDolaEditorDialog = true
                            },
                            contentPadding = PaddingValues(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text("✨ Expand", color = EchoCyan, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                        TextButton(
                            onClick = {
                                dolaEditorMode = DolaAiMode.SUMMARIZE
                                showDolaEditorDialog = true
                            },
                            contentPadding = PaddingValues(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text("📋 Summary", color = EchoCyan, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                // Note Body Editor
                OutlinedTextField(
                    value = text,
                    onValueChange = { text = it },
                    placeholder = { Text("Write your field note, logs, or thoughts here...", color = EchoMuted) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = EchoCyan,
                        unfocusedBorderColor = EchoCardStroke,
                        focusedTextColor = EchoText,
                        unfocusedTextColor = EchoText
                    )
                )

                if (showDolaEditorDialog) {
                    DolaAiDialog(
                        initialText = text,
                        initialMode = dolaEditorMode,
                        onUseReplacement = { text = it },
                        onSaveAsNewNote = { newText ->
                            onSave(note.copy(name = "$title (Revised)", text = newText))
                        },
                        onDismiss = { showDolaEditorDialog = false }
                    )
                }
            }
        }
    }
}
