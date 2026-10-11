package com.example.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for local Room persistence of 42.6 Hz resonance intervals.
 */
@Dao
interface ResonanceIntervalDao {

    @Query("SELECT * FROM resonance_intervals ORDER BY timestampEpochMs DESC")
    fun getAllIntervals(): Flow<List<ResonanceIntervalCalculation>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertInterval(interval: ResonanceIntervalCalculation): Long

    @Delete
    suspend fun deleteInterval(interval: ResonanceIntervalCalculation)

    @Query("DELETE FROM resonance_intervals WHERE id = :id")
    suspend fun deleteIntervalById(id: Long)

    @Query("SELECT COUNT(*) FROM resonance_intervals")
    suspend fun getCount(): Int

    @Query("DELETE FROM resonance_intervals")
    suspend fun clearAll()
}
