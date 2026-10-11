package com.example.audio

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

enum class WaveformTimbre {
    PURE_SINE,
    BEDROCK_HARMONIC,
    TECTONIC_PULSE
}

/**
 * Frequency generator model. Physical audio streaming/hardware audio track
 * output has been removed to ensure silent operation and avoid device audio issues.
 */
class HarmonicToneGenerator {
    private val _isPlayingState = MutableStateFlow(false)
    val isPlayingState: StateFlow<Boolean> = _isPlayingState.asStateFlow()

    @Volatile
    var isPlaying: Boolean = false
        private set

    @Volatile
    var activeFrequencies: List<Double> = listOf(42.6)
        private set

    @Volatile
    var octaveMultiplier: Double = 8.0
        private set

    @Volatile
    var isTidalLfoEnabled: Boolean = false
        private set

    @Volatile
    var volume: Float = 0.85f
        private set

    @Volatile
    var timbre: WaveformTimbre = WaveformTimbre.PURE_SINE
        private set

    @Volatile
    var hardwareCalibrationOffsetHz: Double = 0.0
        private set

    fun setOctaveMultiplier(mult: Double) {
        octaveMultiplier = mult
    }

    fun setHardwareCalibrationOffset(offsetHz: Double) {
        hardwareCalibrationOffsetHz = offsetHz
    }

    fun setTidalLfo(enabled: Boolean) {
        isTidalLfoEnabled = enabled
    }

    fun setVolumeLevel(vol: Float) {
        volume = vol.coerceIn(0.0f, 1.0f)
    }

    fun setTimbreMode(mode: WaveformTimbre) {
        timbre = mode
    }

    fun updateFrequencies(frequencies: List<Double>) {
        activeFrequencies = if (frequencies.isEmpty()) listOf(42.6) else frequencies
    }

    fun start(
        frequencies: List<Double> = activeFrequencies,
        mult: Double = octaveMultiplier,
        tidalLfo: Boolean = isTidalLfoEnabled,
        timbreMode: WaveformTimbre = timbre
    ) {
        activeFrequencies = if (frequencies.isEmpty()) listOf(42.6) else frequencies
        octaveMultiplier = mult
        isTidalLfoEnabled = tidalLfo
        timbre = timbreMode

        isPlaying = true
        _isPlayingState.value = true
    }

    fun stop() {
        isPlaying = false
        _isPlayingState.value = false
    }

    companion object {
        val SHARED = HarmonicToneGenerator()
    }
}
