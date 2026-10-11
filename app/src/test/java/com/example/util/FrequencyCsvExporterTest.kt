package com.example.util

import com.example.data.FrequencyHistoryEntry
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class FrequencyCsvExporterTest {

    @Test
    fun testGenerateCsv_containsHeadersAndFormattedData() {
        val entries = listOf(
            FrequencyHistoryEntry(
                id = 1,
                frequencyHz = 42.600,
                timestamp = 1700000000000L,
                durationSeconds = 120,
                isResonanceLock = true,
                sessionNote = "Peak resonance test"
            ),
            FrequencyHistoryEntry(
                id = 2,
                frequencyHz = 42.000,
                timestamp = 1700000060000L,
                durationSeconds = 45,
                isResonanceLock = false,
                sessionNote = "Note with, comma and \"quotes\""
            )
        )

        val csv = FrequencyCsvExporter.generateCsv(entries)

        assertTrue(csv.startsWith("id,frequency_hz,timestamp_epoch_ms,datetime_utc,duration_seconds,is_resonance_lock,session_note"))
        assertTrue(csv.contains("42.600"))
        assertTrue(csv.contains("true"))
        assertTrue(csv.contains("\"Note with, comma and \"\"quotes\"\"\""))
    }
}
