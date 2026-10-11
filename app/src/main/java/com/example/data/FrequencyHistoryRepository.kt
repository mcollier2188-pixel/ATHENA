package com.example.data

import kotlinx.coroutines.flow.Flow

/**
 * Repository adhering to the Android Repository Pattern, abstracting access to FrequencyHistoryDao.
 */
class FrequencyHistoryRepository(private val dao: FrequencyHistoryDao) {
    val allHistory: Flow<List<FrequencyHistoryEntry>> = dao.getAllHistory()
    val resonanceLocks: Flow<List<FrequencyHistoryEntry>> = dao.getResonanceLocks()

    suspend fun getAllHistoryList(): List<FrequencyHistoryEntry> = dao.getAllHistoryList()

    suspend fun getResonanceLocksList(): List<FrequencyHistoryEntry> = dao.getResonanceLocksList()

    fun getRecentHistory(limit: Int = 20): Flow<List<FrequencyHistoryEntry>> =
        dao.getRecentHistory(limit)

    suspend fun logSession(
        frequencyHz: Double,
        durationSeconds: Long = 0,
        isResonanceLock: Boolean = Math.abs(frequencyHz - 42.600) <= 0.018,
        sessionNote: String? = null
    ): Long {
        return dao.insertEntry(
            FrequencyHistoryEntry(
                frequencyHz = frequencyHz,
                timestamp = System.currentTimeMillis(),
                durationSeconds = durationSeconds,
                isResonanceLock = isResonanceLock,
                sessionNote = sessionNote
            )
        )
    }

    suspend fun deleteEntry(entry: FrequencyHistoryEntry) {
        dao.deleteEntry(entry)
    }

    suspend fun deleteById(id: Long) {
        dao.deleteById(id)
    }

    suspend fun clearAll() {
        dao.clearAll()
    }
}
