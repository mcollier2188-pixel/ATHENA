package com.example.util

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.content.FileProvider
import com.example.data.FrequencyHistoryEntry
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

/**
 * Utility for exporting frequency usage history from Room into RFC 4180 compliant CSV format.
 */
object FrequencyCsvExporter {

    private const val CSV_HEADER = "id,frequency_hz,timestamp_epoch_ms,datetime_utc,duration_seconds,is_resonance_lock,session_note"

    /**
     * Converts a list of [FrequencyHistoryEntry] objects into a well-formatted CSV string.
     */
    fun generateCsv(entries: List<FrequencyHistoryEntry>): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US).apply {
            timeZone = TimeZone.getTimeZone("UTC")
        }

        val sb = StringBuilder()
        sb.append(CSV_HEADER).append("\r\n")

        entries.forEach { entry ->
            val dateStr = dateFormat.format(Date(entry.timestamp))
            val escapedNote = escapeCsvField(entry.sessionNote ?: "")
            sb.append(entry.id).append(",")
                .append(String.format(Locale.US, "%.3f", entry.frequencyHz)).append(",")
                .append(entry.timestamp).append(",")
                .append(dateStr).append(",")
                .append(entry.durationSeconds).append(",")
                .append(entry.isResonanceLock).append(",")
                .append(escapedNote)
                .append("\r\n")
        }

        return sb.toString()
    }

    /**
     * Writes CSV string to the given [OutputStream] (e.g. from Storage Access Framework Uri).
     */
    fun writeCsvToOutputStream(outputStream: OutputStream, entries: List<FrequencyHistoryEntry>) {
        outputStream.bufferedWriter(Charsets.UTF_8).use { writer ->
            writer.write(generateCsv(entries))
            writer.flush()
        }
    }

    /**
     * Creates a temporary .csv file in cacheDir/exports for sharing via FileProvider.
     */
    fun createTempCsvFile(context: Context, entries: List<FrequencyHistoryEntry>): File {
        val exportDir = File(context.cacheDir, "exports").apply {
            if (!exists()) mkdirs()
        }
        val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
        val fileName = "resonance_frequency_history_$timestamp.csv"
        val file = File(exportDir, fileName)

        FileOutputStream(file).use { outputStream ->
            writeCsvToOutputStream(outputStream, entries)
        }
        return file
    }

    /**
     * Creates an Intent to share the exported CSV file with external applications.
     */
    fun createShareIntent(context: Context, csvFile: File): Intent {
        val contentUri: Uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            csvFile
        )

        return Intent(Intent.ACTION_SEND).apply {
            type = "text/csv"
            putExtra(Intent.EXTRA_STREAM, contentUri)
            putExtra(Intent.EXTRA_SUBJECT, "Resonance Frequency History Export (${csvFile.name})")
            putExtra(Intent.EXTRA_TEXT, "Attached is the exported frequency usage history from the Resonance Synthesizer.")
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
    }

    /**
     * Copies CSV contents directly to clipboard.
     */
    fun copyCsvToClipboard(context: Context, entries: List<FrequencyHistoryEntry>): String {
        val csvText = generateCsv(entries)
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("Frequency History CSV", csvText)
        clipboard.setPrimaryClip(clip)
        return csvText
    }

    private fun escapeCsvField(field: String): String {
        if (field.isEmpty()) return ""
        val containsSpecialChars = field.contains(',') || field.contains('"') || field.contains('\n') || field.contains('\r')
        return if (containsSpecialChars) {
            "\"" + field.replace("\"", "\"\"") + "\""
        } else {
            field
        }
    }
}
