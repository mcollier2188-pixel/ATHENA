package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.WorkspaceDatabase
import com.example.data.WorkspaceItem
import com.example.data.firestore.FirestoreNote
import com.example.data.firestore.FirestoreWorkspaceRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.UUID

@OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
class WorkspaceViewModel(
    application: Application,
    private val firestoreRepository: FirestoreWorkspaceRepository? = null,
    private val currentUserId: String? = null
) : AndroidViewModel(application) {
    private val dao = WorkspaceDatabase.getDatabase(application).workspaceDao()

    val currentFolderId = MutableStateFlow<String?>(null)
    val searchQuery = MutableStateFlow("")
    val isTrashView = MutableStateFlow(false)

    // Selection mode state
    val isSelectionMode = MutableStateFlow(false)
    val selectedItemIds = MutableStateFlow<Set<String>>(emptySet())

    val allFolders: StateFlow<List<WorkspaceItem>> = dao.getAllLive()
        .combine(MutableStateFlow(Unit)) { items, _ ->
            items.filter { it.kind == "folder" }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val currentItems: StateFlow<List<WorkspaceItem>> = combine(
        currentFolderId,
        searchQuery,
        isTrashView
    ) { folderId, query, isTrash ->
        Triple(folderId, query, isTrash)
    }.flatMapLatest { (folderId, query, isTrash) ->
        when {
            isTrash -> dao.getTrashItems()
            query.isNotBlank() -> dao.searchItems(query.trim())
            else -> dao.getItemsInFolder(folderId)
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        // Real-time synchronization with Firestore when authenticated
        if (currentUserId != null && firestoreRepository != null) {
            viewModelScope.launch {
                try {
                    firestoreRepository.observeNotes(currentUserId).collect { remoteNotes ->
                        remoteNotes.forEach { remote ->
                            val local = dao.getItemById(remote.id)
                            if (local == null || local.modifiedTimestamp < System.currentTimeMillis() - 1000) {
                                dao.insertItem(
                                    WorkspaceItem(
                                        id = remote.id,
                                        parentId = remote.parentId,
                                        kind = remote.kind,
                                        name = remote.name,
                                        text = remote.text,
                                        tags = remote.tags,
                                        isPinned = remote.isPinned,
                                        modifiedTimestamp = System.currentTimeMillis(),
                                        size = remote.text.length.toLong()
                                    )
                                )
                            }
                        }
                    }
                } catch (e: Exception) {
                    // Handled gracefully offline
                }
            }
        }
    }

    fun navigateToFolder(folderId: String?) {
        currentFolderId.value = folderId
        searchQuery.value = ""
        isTrashView.value = false
        clearSelection()
    }

    fun toggleTrashView() {
        isTrashView.value = !isTrashView.value
        clearSelection()
    }

    fun setSearch(query: String) {
        searchQuery.value = query
    }

    fun toggleSelectionMode() {
        isSelectionMode.value = !isSelectionMode.value
        if (!isSelectionMode.value) {
            clearSelection()
        }
    }

    fun toggleSelectItem(id: String) {
        val current = selectedItemIds.value.toMutableSet()
        if (current.contains(id)) current.remove(id) else current.add(id)
        selectedItemIds.value = current
    }

    fun clearSelection() {
        selectedItemIds.value = emptySet()
    }

    fun createNote(name: String = "Untitled note", initialText: String = "", tags: String = ""): String {
        val newId = UUID.randomUUID().toString()
        val note = WorkspaceItem(
            id = newId,
            parentId = currentFolderId.value,
            kind = "note",
            name = name,
            text = initialText,
            tags = tags,
            modifiedTimestamp = System.currentTimeMillis(),
            size = initialText.length.toLong()
        )
        viewModelScope.launch {
            dao.insertItem(note)
            if (currentUserId != null && firestoreRepository != null) {
                try {
                    firestoreRepository.saveNote(
                        currentUserId,
                        FirestoreNote(
                            id = newId,
                            userId = currentUserId,
                            name = name,
                            text = initialText,
                            tags = tags,
                            kind = "note",
                            parentId = currentFolderId.value
                        )
                    )
                } catch (e: Exception) {
                    // Local saved, Firestore sync will retry or error handled
                }
            }
        }
        return newId
    }

    fun createFolder(name: String) {
        if (name.isBlank()) return
        val newId = UUID.randomUUID().toString()
        val folder = WorkspaceItem(
            id = newId,
            parentId = currentFolderId.value,
            kind = "folder",
            name = name.trim(),
            modifiedTimestamp = System.currentTimeMillis()
        )
        viewModelScope.launch {
            dao.insertItem(folder)
            if (currentUserId != null && firestoreRepository != null) {
                try {
                    firestoreRepository.saveNote(
                        currentUserId,
                        FirestoreNote(
                            id = newId,
                            userId = currentUserId,
                            name = name.trim(),
                            kind = "folder",
                            parentId = currentFolderId.value
                        )
                    )
                } catch (e: Exception) {
                    // Local saved
                }
            }
        }
    }

    fun updateItem(item: WorkspaceItem) {
        viewModelScope.launch {
            dao.updateItem(item.copy(modifiedTimestamp = System.currentTimeMillis(), size = item.text.length.toLong()))
            if (currentUserId != null && firestoreRepository != null) {
                try {
                    firestoreRepository.saveNote(
                        currentUserId,
                        FirestoreNote(
                            id = item.id,
                            userId = currentUserId,
                            name = item.name,
                            text = item.text,
                            tags = item.tags,
                            kind = item.kind,
                            parentId = item.parentId,
                            isPinned = item.isPinned
                        )
                    )
                } catch (e: Exception) {
                    // Handled
                }
            }
        }
    }

    fun togglePin(item: WorkspaceItem) {
        val updated = item.copy(isPinned = !item.isPinned)
        viewModelScope.launch {
            dao.updateItem(updated)
            if (currentUserId != null && firestoreRepository != null) {
                try {
                    firestoreRepository.saveNote(
                        currentUserId,
                        FirestoreNote(
                            id = updated.id,
                            userId = currentUserId,
                            name = updated.name,
                            text = updated.text,
                            tags = updated.tags,
                            kind = updated.kind,
                            parentId = updated.parentId,
                            isPinned = updated.isPinned
                        )
                    )
                } catch (e: Exception) {
                    // Handled
                }
            }
        }
    }

    fun moveToTrash(id: String) {
        viewModelScope.launch {
            dao.moveToTrash(id)
        }
    }

    fun restoreItem(id: String) {
        viewModelScope.launch {
            dao.restoreItem(id)
        }
    }

    fun deletePermanently(id: String) {
        viewModelScope.launch {
            dao.deletePermanently(id)
            if (currentUserId != null && firestoreRepository != null) {
                try {
                    firestoreRepository.deleteNote(currentUserId, id)
                } catch (e: Exception) {
                    // Handled
                }
            }
        }
    }

    fun deleteSelectedPermanently() {
        val ids = selectedItemIds.value.toList()
        viewModelScope.launch {
            dao.deletePermanently(ids)
            if (currentUserId != null && firestoreRepository != null) {
                ids.forEach { id ->
                    try {
                        firestoreRepository.deleteNote(currentUserId, id)
                    } catch (e: Exception) {
                        // Handled
                    }
                }
            }
            clearSelection()
        }
    }

    fun moveSelectedToTrash() {
        val ids = selectedItemIds.value.toList()
        viewModelScope.launch {
            ids.forEach { dao.moveToTrash(it) }
            clearSelection()
        }
    }

    fun moveSelectedToFolder(targetFolderId: String?) {
        val ids = selectedItemIds.value.toList()
        viewModelScope.launch {
            ids.forEach { id ->
                val item = dao.getItemById(id)
                if (item != null) {
                    val updated = item.copy(parentId = targetFolderId)
                    dao.updateItem(updated)
                    if (currentUserId != null && firestoreRepository != null) {
                        try {
                            firestoreRepository.saveNote(
                                currentUserId,
                                FirestoreNote(
                                    id = updated.id,
                                    userId = currentUserId,
                                    name = updated.name,
                                    text = updated.text,
                                    tags = updated.tags,
                                    kind = updated.kind,
                                    parentId = updated.parentId,
                                    isPinned = updated.isPinned
                                )
                            )
                        } catch (e: Exception) {
                            // Handled
                        }
                    }
                }
            }
            clearSelection()
        }
    }

    fun togglePinSelected() {
        val ids = selectedItemIds.value.toList()
        viewModelScope.launch {
            ids.forEach { id ->
                val item = dao.getItemById(id)
                if (item != null) {
                    val updated = item.copy(isPinned = !item.isPinned)
                    dao.updateItem(updated)
                    if (currentUserId != null && firestoreRepository != null) {
                        try {
                            firestoreRepository.saveNote(
                                currentUserId,
                                FirestoreNote(
                                    id = updated.id,
                                    userId = currentUserId,
                                    name = updated.name,
                                    text = updated.text,
                                    tags = updated.tags,
                                    kind = updated.kind,
                                    parentId = updated.parentId,
                                    isPinned = updated.isPinned
                                )
                            )
                        } catch (e: Exception) {
                            // Handled
                        }
                    }
                }
            }
            clearSelection()
        }
    }
}
