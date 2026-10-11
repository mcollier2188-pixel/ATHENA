package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Entity representing an individual frequency tuning or resonance session entry.
 * Tracks user resonance sessions with frequency (Hz), timestamps, session duration, and notes.
 */
@Entity(tableName = "frequency_history")
data class FrequencyHistoryEntry(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val frequencyHz: Double,
    val timestamp: Long = System.currentTimeMillis(),
    val durationSeconds: Long = 0,
    val isResonanceLock: Boolean = false,
    val sessionNote: String? = null
)
