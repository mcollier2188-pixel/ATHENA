package com.example.data

import kotlinx.coroutines.flow.Flow

/**
 * Repository abstracting data operations for FrequencyPresetDao.
 */
class FrequencyPresetRepository(private val dao: FrequencyPresetDao) {

    val allPresets: Flow<List<FrequencyPreset>> = dao.getAllPresets()

    companion object {
        val DEFAULT_PRESETS = listOf(
            FrequencyPreset(
                frequencyHz = 42.600,
                label = "N5 Solent (1x)",
                description = "42.600 Hz Fundamental Bedrock Peak",
                isDefault = true,
                orderIndex = 0
            ),
            FrequencyPreset(
                frequencyHz = 85.200,
                label = "N1 Master (2x)",
                description = "85.200 Hz Portsmouth Master Clock",
                isDefault = true,
                orderIndex = 1
            ),
            FrequencyPreset(
                frequencyHz = 127.800,
                label = "N7 Chalk (3x)",
                description = "127.800 Hz Dorchester Chalk Node",
                isDefault = true,
                orderIndex = 2
            ),
            FrequencyPreset(
                frequencyHz = 170.400,
                label = "N2 Vault (4x)",
                description = "170.400 Hz Farnworth Buried Vault",
                isDefault = true,
                orderIndex = 3
            ),
            FrequencyPreset(
                frequencyHz = 42.000,
                label = "Sub-Band Floor",
                description = "42.000 Hz Baseline Threshold",
                isDefault = true,
                orderIndex = 4
            ),
            FrequencyPreset(
                frequencyHz = 43.000,
                label = "Tesla 1899 Peak",
                description = "43.000 Hz Colorado Terrestrial Wave",
                isDefault = true,
                orderIndex = 5
            )
        )
    }

    suspend fun ensureDefaultPresets() {
        if (dao.getCount() == 0) {
            dao.insertPresets(DEFAULT_PRESETS)
        }
    }

    suspend fun updateLabel(id: Long, newLabel: String) {
        dao.updateLabel(id, newLabel.trim())
    }

    suspend fun updatePreset(preset: FrequencyPreset) {
        dao.updatePreset(preset)
    }

    suspend fun addPreset(frequencyHz: Double, label: String, description: String = ""): Long {
        val count = dao.getCount()
        val cleanedLabel = label.trim().ifEmpty { String.format(java.util.Locale.US, "%.3f Hz", frequencyHz) }
        return dao.insertPreset(
            FrequencyPreset(
                frequencyHz = frequencyHz,
                label = cleanedLabel,
                description = description.trim(),
                isDefault = false,
                orderIndex = count
            )
        )
    }

    suspend fun deletePreset(id: Long) {
        dao.deletePresetById(id)
    }

    suspend fun resetToDefaults() {
        dao.clearAll()
        dao.insertPresets(DEFAULT_PRESETS)
    }
}
