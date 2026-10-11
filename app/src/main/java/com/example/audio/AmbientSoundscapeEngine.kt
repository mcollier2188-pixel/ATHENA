package com.example.audio

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlin.math.sin
import kotlin.math.PI
import java.util.Random

enum class AmbientSoundLayer(val title: String, val subtitle: String, val iconLabel: String) {
    DEEP_EARTH("Deep Earth Infrasound", "Sub-bedrock resonant rumble (18-35 Hz)", "🌍"),
    GRANITE_WIND("Granite Wind", "Atmospheric resonant breeze across stones", "💨"),
    PINK_NOISE("Pink Acoustic Blanket", "Harmonious 1/f spectral balance", "🌊"),
    CAVE_WATERS("Sacred Cave Waters", "Gentle rhythmic drops in limestone caverns", "💧")
}

data class AmbientLayerState(
    val layer: AmbientSoundLayer,
    val isEnabled: Boolean = false,
    val volume: Float = 0.5f
)

/**
 * State and synthesizer model for multi-layer ambient soundscapes.
 * Provides real-time volume mixing and timer management.
 */
class AmbientSoundscapeEngine {
    private val _layers = MutableStateFlow(
        AmbientSoundLayer.values().map {
            AmbientLayerState(
                layer = it,
                isEnabled = it == AmbientSoundLayer.DEEP_EARTH,
                volume = 0.6f
            )
        }
    )
    val layers: StateFlow<List<AmbientLayerState>> = _layers.asStateFlow()

    private val _isEngineActive = MutableStateFlow(false)
    val isEngineActive: StateFlow<Boolean> = _isEngineActive.asStateFlow()

    // Sleep timer state in remaining seconds (0 = timer off)
    private val _timerRemainingSeconds = MutableStateFlow(0)
    val timerRemainingSeconds: StateFlow<Int> = _timerRemainingSeconds.asStateFlow()

    private val _timerTotalSeconds = MutableStateFlow(0)
    val timerTotalSeconds: StateFlow<Int> = _timerTotalSeconds.asStateFlow()

    fun toggleLayer(layer: AmbientSoundLayer) {
        val current = _layers.value.toMutableList()
        val index = current.indexOfFirst { it.layer == layer }
        if (index != -1) {
            val old = current[index]
            current[index] = old.copy(isEnabled = !old.isEnabled)
            _layers.value = current
        }
    }

    fun setLayerVolume(layer: AmbientSoundLayer, volume: Float) {
        val current = _layers.value.toMutableList()
        val index = current.indexOfFirst { it.layer == layer }
        if (index != -1) {
            val old = current[index]
            current[index] = old.copy(volume = volume.coerceIn(0f, 1f))
            _layers.value = current
        }
    }

    fun setTimer(minutes: Int) {
        val seconds = minutes * 60
        _timerTotalSeconds.value = seconds
        _timerRemainingSeconds.value = seconds
    }

    fun cancelTimer() {
        _timerTotalSeconds.value = 0
        _timerRemainingSeconds.value = 0
    }

    fun tickTimerOneSecond(): Boolean {
        val rem = _timerRemainingSeconds.value
        if (rem <= 0) return true
        val next = rem - 1
        _timerRemainingSeconds.value = next
        return next <= 0
    }

    fun startSoundscape() {
        _isEngineActive.value = true
    }

    fun stopSoundscape() {
        _isEngineActive.value = false
        _timerRemainingSeconds.value = 0
        _timerTotalSeconds.value = 0
    }

    companion object {
        val SHARED = AmbientSoundscapeEngine()
    }
}
