package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Room Entity for storing calculated 42.6 Hz resonance intervals,
 * harmonic multipliers, acoustic wavelengths, and bedrock metrics.
 */
@Entity(tableName = "resonance_intervals")
data class ResonanceIntervalCalculation(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val intervalLabel: String,
    val multiplier: Double,
    val baseFrequencyHz: Double = 42.600,
    val calculatedFrequencyHz: Double,
    val wavelengthMeters: Double,
    val periodSeconds: Double,
    val acousticVelocityMps: Double = 3420.0,
    val couplingEfficiencyPercent: Double = 98.4,
    val note: String = "",
    val timestampEpochMs: Long = System.currentTimeMillis()
)
