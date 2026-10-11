package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Entity representing a named frequency preset for quick snapping in the Resonance Synthesizer.
 * Allows users to customize labels and save their favorite resonance frequencies.
 */
@Entity(tableName = "frequency_presets")
data class FrequencyPreset(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val frequencyHz: Double,
    val label: String,
    val description: String = "",
    val isDefault: Boolean = false,
    val orderIndex: Int = 0
)
