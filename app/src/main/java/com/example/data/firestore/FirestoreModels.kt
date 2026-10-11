package com.example.data.firestore

import com.google.firebase.Timestamp
import com.google.firebase.firestore.IgnoreExtraProperties
import com.google.firebase.firestore.ServerTimestamp

@IgnoreExtraProperties
data class FirestoreNote(
    val id: String = "",
    val userId: String = "",
    val name: String = "",
    val text: String = "",
    val tags: String = "",
    val kind: String = "note", // "note", "folder", "file"
    val parentId: String? = null,
    val isPinned: Boolean = false,
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
)

@IgnoreExtraProperties
data class FirestoreAiCreation(
    val id: String = "",
    val userId: String = "",
    val type: String = "chat", // "image", "video_text", "video_image", "music", "transcript", "chat"
    val prompt: String = "",
    val resultData: String = "",
    val modelUsed: String = "gemini-3.5-flash",
    @ServerTimestamp val createdAt: Timestamp? = null
)

@IgnoreExtraProperties
data class FirestoreUserProfile(
    val id: String = "",
    val userId: String = "",
    val displayName: String = "",
    val email: String = "",
    val favoriteFrequency: Double = 42.6,
    @ServerTimestamp val updatedAt: Timestamp? = null
)
