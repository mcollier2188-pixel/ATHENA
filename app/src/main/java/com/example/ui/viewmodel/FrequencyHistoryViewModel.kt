package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.FrequencyHistoryEntry
import com.example.data.FrequencyHistoryRepository
import com.example.data.FrequencyPreset
import com.example.data.FrequencyPresetRepository
import com.example.data.WorkspaceDatabase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 * ViewModel managing frequency history tracking for resonance sessions using Room
 * and persistent frequency preset labels.
 */
class FrequencyHistoryViewModel(application: Application) : AndroidViewModel(application) {
    private val database = WorkspaceDatabase.getDatabase(application)
    private val repository = FrequencyHistoryRepository(database.frequencyHistoryDao())
    private val presetRepository = FrequencyPresetRepository(database.frequencyPresetDao())

    val presets: StateFlow<List<FrequencyPreset>> = presetRepository.allPresets
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = FrequencyPresetRepository.DEFAULT_PRESETS
        )

    init {
        viewModelScope.launch {
            presetRepository.ensureDefaultPresets()
            if (repository.getAllHistoryList().isEmpty()) {
                seedHistoricalSessions()
            }
        }
    }

    private suspend fun seedHistoricalSessions() {
        val now = System.currentTimeMillis()
        val seedEntries = listOf(
            // Bedrock Resonance Cluster (42.6 Hz)
            Triple(42.600, 180L, "Bedrock harmonic lock • Primary baseline"),
            Triple(42.600, 120L, "N5 Solent acoustic coupling"),
            Triple(42.598, 95L, "Fine millihertz drift calibration"),
            Triple(42.602, 110L, "Tuner verification session"),
            Triple(42.600, 240L, "Deep bedrock seismic monitor"),
            Triple(42.601, 80L, "Hilsea Node telemetry log"),
            // 85.2 Hz Octave Harmonic
            Triple(85.200, 90L, "N1 Portsmouth 2x harmonic verification"),
            Triple(85.200, 60L, "Octave doubling acoustic test"),
            Triple(85.195, 45L, "Second harmonic sweep"),
            // 127.8 Hz Harmonic
            Triple(127.800, 75L, "N7 Chalk 3x harmonic lock"),
            Triple(127.800, 50L, "Subterranean resonance probe"),
            // 170.4 Hz Harmonic
            Triple(170.400, 60L, "N2 Vault 4x harmonic sweep"),
            Triple(170.400, 40L, "High harmonic frequency test"),
            // Sub-harmonic & baseline test points across 0-190 Hz
            Triple(21.300, 30L, "Sub-harmonic half-frequency floor"),
            Triple(10.000, 20L, "Infrasound transducer sweep"),
            Triple(63.900, 25L, "Inter-harmonic probe"),
            Triple(106.500, 20L, "Upper mid-frequency scan"),
            Triple(149.100, 30L, "Sub-ceiling resonance test"),
            Triple(190.000, 45L, "Transducer 190 Hz ceiling limit test")
        )

        seedEntries.forEachIndexed { index, (freq, dur, note) ->
            repository.logSession(
                frequencyHz = freq,
                durationSeconds = dur,
                isResonanceLock = Math.abs(freq - 42.600) <= 0.02 || Math.abs(freq - 85.200) <= 0.02,
                sessionNote = note
            )
        }
    }

    val allHistory: StateFlow<List<FrequencyHistoryEntry>> = repository.allHistory
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val resonanceLocks: StateFlow<List<FrequencyHistoryEntry>> = repository.resonanceLocks
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    private val _filterOnlyLocks = MutableStateFlow(false)
    val filterOnlyLocks: StateFlow<Boolean> = _filterOnlyLocks.asStateFlow()

    val filteredHistory: StateFlow<List<FrequencyHistoryEntry>> = combine(
        allHistory,
        _filterOnlyLocks
    ) { history, onlyLocks ->
        if (onlyLocks) history.filter { it.isResonanceLock } else history
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    fun toggleFilterOnlyLocks() {
        _filterOnlyLocks.value = !_filterOnlyLocks.value
    }

    fun logSession(
        frequencyHz: Double,
        durationSeconds: Long = 0,
        note: String? = null
    ) {
        viewModelScope.launch {
            repository.logSession(
                frequencyHz = frequencyHz,
                durationSeconds = durationSeconds,
                sessionNote = note
            )
        }
    }

    fun deleteEntry(entry: FrequencyHistoryEntry) {
        viewModelScope.launch {
            repository.deleteEntry(entry)
        }
    }

    fun deleteById(id: Long) {
        viewModelScope.launch {
            repository.deleteById(id)
        }
    }

    private val _exportStatusMessage = MutableStateFlow<String?>(null)
    val exportStatusMessage: StateFlow<String?> = _exportStatusMessage.asStateFlow()

    private val _isExporting = MutableStateFlow(false)
    val isExporting: StateFlow<Boolean> = _isExporting.asStateFlow()

    fun dismissExportMessage() {
        _exportStatusMessage.value = null
    }

    /**
     * Exports the current frequency history to a user-selected file URI (via Storage Access Framework).
     */
    fun exportToUri(
        context: android.content.Context,
        uri: android.net.Uri,
        onComplete: (Boolean, String) -> Unit = { _, _ -> }
    ) {
        viewModelScope.launch {
            _isExporting.value = true
            try {
                val entries = repository.getAllHistoryList()
                if (entries.isEmpty()) {
                    val msg = "No frequency sessions to export."
                    _exportStatusMessage.value = msg
                    onComplete(false, msg)
                    return@launch
                }
                context.contentResolver.openOutputStream(uri)?.use { outputStream ->
                    com.example.util.FrequencyCsvExporter.writeCsvToOutputStream(outputStream, entries)
                }
                val msg = "Exported ${entries.size} resonance sessions to CSV file successfully!"
                _exportStatusMessage.value = msg
                onComplete(true, msg)
            } catch (e: Exception) {
                val errorMsg = "Failed to export CSV: ${e.localizedMessage ?: "Unknown error"}"
                _exportStatusMessage.value = errorMsg
                onComplete(false, errorMsg)
            } finally {
                _isExporting.value = false
            }
        }
    }

    /**
     * Creates a temporary CSV file and returns an Android Share Intent.
     */
    fun shareCsv(
        context: android.content.Context,
        onReady: (android.content.Intent) -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            _isExporting.value = true
            try {
                val entries = repository.getAllHistoryList()
                if (entries.isEmpty()) {
                    onError("No frequency sessions available to export.")
                    return@launch
                }
                val file = com.example.util.FrequencyCsvExporter.createTempCsvFile(context, entries)
                val intent = com.example.util.FrequencyCsvExporter.createShareIntent(context, file)
                onReady(intent)
            } catch (e: Exception) {
                onError("Failed to share CSV: ${e.localizedMessage ?: "Unknown error"}")
            } finally {
                _isExporting.value = false
            }
        }
    }

    /**
     * Copies CSV contents to system clipboard.
     */
    fun copyCsvToClipboard(
        context: android.content.Context,
        onCopied: (Int) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val entries = repository.getAllHistoryList()
                if (entries.isEmpty()) {
                    _exportStatusMessage.value = "No sessions to copy."
                    return@launch
                }
                com.example.util.FrequencyCsvExporter.copyCsvToClipboard(context, entries)
                val msg = "Copied ${entries.size} frequency records in CSV format to clipboard!"
                _exportStatusMessage.value = msg
                onCopied(entries.size)
            } catch (e: Exception) {
                _exportStatusMessage.value = "Failed to copy CSV: ${e.localizedMessage}"
            }
        }
    }

    fun clearAllHistory() {
        viewModelScope.launch {
            repository.clearAll()
        }
    }

    // ==========================================
    // Frequency Preset Label Management
    // ==========================================

    fun updatePresetLabel(id: Long, newLabel: String) {
        viewModelScope.launch {
            presetRepository.updateLabel(id, newLabel)
        }
    }

    fun updatePreset(preset: FrequencyPreset) {
        viewModelScope.launch {
            presetRepository.updatePreset(preset)
        }
    }

    fun addCustomPreset(frequencyHz: Double, label: String, description: String = "") {
        viewModelScope.launch {
            presetRepository.addPreset(frequencyHz, label, description)
        }
    }

    fun deletePreset(id: Long) {
        viewModelScope.launch {
            presetRepository.deletePreset(id)
        }
    }

    fun resetPresetsToDefault() {
        viewModelScope.launch {
            presetRepository.resetToDefaults()
        }
    }
}

