package com.example.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface FrequencyHistoryDao {
    @Query("SELECT * FROM frequency_history ORDER BY timestamp DESC")
    fun getAllHistory(): Flow<List<FrequencyHistoryEntry>>

    @Query("SELECT * FROM frequency_history ORDER BY timestamp DESC")
    suspend fun getAllHistoryList(): List<FrequencyHistoryEntry>

    @Query("SELECT * FROM frequency_history ORDER BY timestamp DESC LIMIT :limit")
    fun getRecentHistory(limit: Int): Flow<List<FrequencyHistoryEntry>>

    @Query("SELECT * FROM frequency_history WHERE isResonanceLock = 1 ORDER BY timestamp DESC")
    fun getResonanceLocks(): Flow<List<FrequencyHistoryEntry>>

    @Query("SELECT * FROM frequency_history WHERE isResonanceLock = 1 ORDER BY timestamp DESC")
    suspend fun getResonanceLocksList(): List<FrequencyHistoryEntry>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEntry(entry: FrequencyHistoryEntry): Long

    @Delete
    suspend fun deleteEntry(entry: FrequencyHistoryEntry)

    @Query("DELETE FROM frequency_history WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("DELETE FROM frequency_history")
    suspend fun clearAll()
}
