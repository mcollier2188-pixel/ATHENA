package com.example.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for frequency preset entities.
 */
@Dao
interface FrequencyPresetDao {

    @Query("SELECT * FROM frequency_presets ORDER BY orderIndex ASC, frequencyHz ASC")
    fun getAllPresets(): Flow<List<FrequencyPreset>>

    @Query("SELECT * FROM frequency_presets ORDER BY orderIndex ASC, frequencyHz ASC")
    suspend fun getAllPresetsList(): List<FrequencyPreset>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPreset(preset: FrequencyPreset): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPresets(presets: List<FrequencyPreset>)

    @Update
    suspend fun updatePreset(preset: FrequencyPreset)

    @Query("UPDATE frequency_presets SET label = :label WHERE id = :id")
    suspend fun updateLabel(id: Long, label: String)

    @Delete
    suspend fun deletePreset(preset: FrequencyPreset)

    @Query("DELETE FROM frequency_presets WHERE id = :id")
    suspend fun deletePresetById(id: Long)

    @Query("SELECT COUNT(*) FROM frequency_presets")
    suspend fun getCount(): Int

    @Query("DELETE FROM frequency_presets")
    suspend fun clearAll()
}
