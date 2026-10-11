package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.model.ChapterRepository
import com.example.model.NetworkNode
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleRobolectricTest {

    @Test
    fun `read string from context`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("ATHENA", appName)
    }

    @Test
    fun `verify network nodes specifications`() {
        assertEquals(11, NetworkNode.ALL_NODES.size)
        val n1 = NetworkNode.ALL_NODES.first()
        assertEquals("N1", n1.id)
        assertEquals("Portsmouth", n1.name)
        assertEquals(85.2, n1.frequency, 0.01)
        assertEquals(2, n1.baseRatio)
    }

    @Test
    fun `verify frequency csv generation`() {
        val entry1 = com.example.data.FrequencyHistoryEntry(
            id = 1,
            frequencyHz = 42.6,
            timestamp = 1700000000000L,
            durationSeconds = 120,
            isResonanceLock = true,
            sessionNote = "Bedrock resonance lock"
        )
        val entry2 = com.example.data.FrequencyHistoryEntry(
            id = 2,
            frequencyHz = 43.0,
            timestamp = 1700000060000L,
            durationSeconds = 60,
            isResonanceLock = false,
            sessionNote = "Test session, with comma"
        )

        val csv = com.example.util.FrequencyCsvExporter.generateCsv(listOf(entry1, entry2))
        assertTrue(csv.startsWith("id,frequency_hz,timestamp_epoch_ms,datetime_utc,duration_seconds,is_resonance_lock,session_note"))
        assertTrue(csv.contains("42.600"))
        assertTrue(csv.contains("Bedrock resonance lock"))
        assertTrue(csv.contains("\"Test session, with comma\""))
    }

    @Test
    fun `verify hardware audio calibrator profile detection`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val profile = com.example.audio.HardwareAudioCalibrator.detectHardwareProfile(context)
        assertTrue(profile.nativeSampleRate > 0)
        assertTrue(profile.spectrumCalibrationScore in 50..100)
        assertTrue(profile.estimatedLowCutoffHz in 10.0..150.0)
    }

    @Test
    fun `verify default frequency preset nodes within 0 to 190 Hz`() {
        val defaults = com.example.data.FrequencyPresetRepository.DEFAULT_PRESETS
        assertTrue(defaults.isNotEmpty())
        defaults.forEach { preset ->
            assertTrue("Preset ${preset.label} frequency ${preset.frequencyHz} should be in [0, 190]",
                preset.frequencyHz in 0.0..190.0)
            assertTrue(preset.label.isNotBlank())
        }
        val solent = defaults.firstOrNull { it.label.contains("Solent") }
        assertTrue(solent != null)
        assertEquals(42.600, solent!!.frequencyHz, 0.001)
    }

    @Test
    fun `verify frequency heatmap binning and intensity across 0-190 Hz`() {
        val testHistory = listOf(
            com.example.data.FrequencyHistoryEntry(frequencyHz = 42.600, isResonanceLock = true),
            com.example.data.FrequencyHistoryEntry(frequencyHz = 42.598, isResonanceLock = true),
            com.example.data.FrequencyHistoryEntry(frequencyHz = 42.602, isResonanceLock = true),
            com.example.data.FrequencyHistoryEntry(frequencyHz = 85.200, isResonanceLock = false),
            com.example.data.FrequencyHistoryEntry(frequencyHz = 170.400, isResonanceLock = false)
        )

        val bins = com.example.ui.components.FrequencyHeatmapAnalyzer.computeBins(
            history = testHistory,
            minHz = 0.0,
            maxHz = 190.0,
            binCount = 38
        )

        assertEquals(38, bins.size)
        val bedrockBin = bins.find { 42.600 >= it.minHz && 42.600 <= it.maxHz }
        assertTrue("Bedrock bin at 42.6 Hz should exist", bedrockBin != null)
        assertEquals(3, bedrockBin!!.count)
        assertEquals(3, bedrockBin.lockCount)
        assertTrue("Bedrock bin intensity should be peak", bedrockBin.intensity > 0.8f)

        val (intensityAt42_6, foundBin) = com.example.ui.components.FrequencyHeatmapAnalyzer.findIntensityAtFrequency(bins, 42.600)
        assertTrue(intensityAt42_6 > 0.8f)
        assertEquals(bedrockBin.binIndex, foundBin?.binIndex)

        val hotspots = com.example.ui.components.FrequencyHeatmapAnalyzer.getTopHotspots(bins, limit = 3)
        assertTrue(hotspots.isNotEmpty())
        assertEquals(bedrockBin.binIndex, hotspots.first().binIndex)
    }
}
