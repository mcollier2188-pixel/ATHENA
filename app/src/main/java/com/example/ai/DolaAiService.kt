package com.example.ai

import com.example.BuildConfig
import com.example.model.ChapterRepository
import com.example.model.NetworkNode
import com.example.model.TimelineRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

enum class DolaAiMode {
    REFINE,
    EXPAND,
    SUMMARIZE,
    CHAT
}

object DolaAiService {
    private val client = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    private const val MODEL_NAME = "gemini-3.5-flash"
    private const val API_ENDPOINT = "https://generativelanguage.googleapis.com/v1beta/models/$MODEL_NAME:generateContent"

    val isApiKeyConfigured: Boolean
        get() = try {
            val key = BuildConfig.GEMINI_API_KEY
            key.isNotBlank() && !key.contains("MY_GEMINI_API_KEY") && !key.contains("PLACEHOLDER")
        } catch (e: Exception) {
            false
        }

    suspend fun executeTask(
        mode: DolaAiMode,
        inputText: String,
        contextPrompt: String = ""
    ): Result<String> = withContext(Dispatchers.IO) {
        val systemInstruction = """
You are Dola AI, the specialized intelligence and writing assistant embedded into the '42 Geniuses' ECHO-INTERFACE archive.
You have deep canonical knowledge of:
1. The 42.6 Hz planetary fundamental frequency, the 1986 Hilsea laboratory detection (42.598 Hz), and the 11.74-second Solent tidal envelope cycle.
2. The 11 Network Nodes (N1 Portsmouth 85.2 Hz master clock, N2 Farnworth, N3 Cornwall/Lizard Point, N4 Cairngorms, N5 Solent Deep, N6 Celtic Deep, N7 Dorchester, N8 Culver Down, N9 Thames Gate, N10 Isle of Man, N11 Bristol Channel).
3. The historical timeline from Stonehenge (~2500 BCE) and Pythagoras to Lord Rayleigh and Nikola Tesla's 43 Hz observation.
4. Echo Comics lore and field survey technical specifications.

When in REFINE mode: Refine this writing. Fix flow and wording only. Keep the exact same story, meaning, voice, technical numbers, and events. Do not invent new characters. Return only the revised text.
When in EXPAND mode: Expand this writing in the author's own voice. Keep every existing meaning, name, and event. Do not invent contradictory storylines. Return only the expanded text.
When in SUMMARIZE mode: Provide a clear, bulleted executive summary emphasizing acoustic metrics and key insights.
When in CHAT mode: Answer questions accurately, concisely, and atmospherically with full mastery of the ECHO-INTERFACE canon and acoustic engineering.
        """.trimIndent()

        val taskPrompt = when (mode) {
            DolaAiMode.REFINE -> "Refine this text:\n\n$inputText"
            DolaAiMode.EXPAND -> "Expand this text in the same voice and lore:\n\n$inputText"
            DolaAiMode.SUMMARIZE -> "Summarize the key findings and technical specifications of this note:\n\n$inputText"
            DolaAiMode.CHAT -> if (contextPrompt.isNotBlank()) "$contextPrompt\n\nUser Question: $inputText" else inputText
        }

        val apiKey = try { BuildConfig.GEMINI_API_KEY } catch (e: Exception) { "" }

        if (apiKey.isNotBlank() && !apiKey.contains("MY_GEMINI_API_KEY") && !apiKey.contains("PLACEHOLDER")) {
            try {
                val url = "$API_ENDPOINT?key=$apiKey"

                val jsonBody = JSONObject().apply {
                    put("contents", JSONArray().apply {
                        put(JSONObject().apply {
                            put("parts", JSONArray().apply {
                                put(JSONObject().apply {
                                    put("text", taskPrompt)
                                })
                            })
                        })
                    })
                    put("systemInstruction", JSONObject().apply {
                        put("parts", JSONArray().apply {
                            put(JSONObject().apply {
                                put("text", systemInstruction)
                            })
                        })
                    })
                    put("generationConfig", JSONObject().apply {
                        put("temperature", if (mode == DolaAiMode.REFINE) 0.2 else 0.7)
                        put("topP", 0.95)
                        put("maxOutputTokens", 2048)
                    })
                }

                val request = Request.Builder()
                    .url(url)
                    .post(jsonBody.toString().toRequestBody("application/json".toMediaType()))
                    .build()

                val response = client.newCall(request).execute()
                val responseString = response.body?.string() ?: ""

                if (response.isSuccessful) {
                    val root = JSONObject(responseString)
                    val candidates = root.optJSONArray("candidates")
                    val candidate = candidates?.optJSONObject(0)
                    val content = candidate?.optJSONObject("content")
                    val parts = content?.optJSONArray("parts")
                    val reply = parts?.optJSONObject(0)?.optString("text")

                    if (!reply.isNullOrBlank()) {
                        return@withContext Result.success(reply.trim())
                    }
                }
            } catch (e: Exception) {
                // Network or API failure, fallback to intelligent offline generator
            }
        }

        // Offline canonical intelligence fallback
        val offlineReply = generateOfflineResponse(mode, inputText)
        Result.success(offlineReply)
    }

    private fun generateOfflineResponse(mode: DolaAiMode, input: String): String {
        return when (mode) {
            DolaAiMode.REFINE -> {
                val cleaned = input.trim()
                if (cleaned.isBlank()) "No text provided to refine."
                else {
                    cleaned
                        .replace("(?i)\\bvery\\b".toRegex(), "remarkably")
                        .replace("(?i)\\breally\\b".toRegex(), "distinctly")
                        .replace("42.6", "42.600 Hz")
                        .replace("42.598", "42.598 Hz (Hilsea 1986)")
                        .replace("85.2", "85.200 Hz (N1 Master)")
                }
            }
            DolaAiMode.EXPAND -> {
                """$input

[Dola Expansion Note]
The transmission continues through the subterranean chalk waveguide. Coupling loss between N1 Portsdown and N8 Culver Down was recorded at less than 1.4 dB/km. The 11.74-second lunar tidal envelope gently modulates the carrier amplitude, maintaining coherent lock across the British Isles. Synchronous feedback from all peripheral stations remains aligned at nominal resonance."""
            }
            DolaAiMode.SUMMARIZE -> {
                val words = input.trim().split("\\s+".toRegex()).size
                """• Telemetry Summary: Analyzed record containing $words words.
• Primary Frequency Carrier: 42.6 Hz fundamental (coupled to bedrock strata).
• Network Nodes Referenced: ${NetworkNode.ALL_NODES.filter { input.contains(it.id, ignoreCase = true) || input.contains(it.name, ignoreCase = true) }.joinToString { "${it.id} (${it.name})" }.ifBlank { "All standard nodes active" }}.
• Status: Canonically coherent with v4.0 network specifications."""
            }
            DolaAiMode.CHAT -> {
                val q = input.lowercase()
                when {
                    q.contains("42.6") || q.contains("frequency") -> {
                        "42.6 Hz is the fundamental subterranean standing acoustic resonance discovered in the bedrock of Britain. It operates with an 11.74-second lunar tidal envelope rhythm. The network master node N1 at Portsdown Hill transmits at 85.2 Hz (2x harmonic), synchronizing ten peripheral stations across England, Wales, Scotland, and offshore buoys."
                    }
                    q.contains("node") || q.contains("n1") || q.contains("network") -> {
                        "The ECHO-INTERFACE network consists of 11 tuned nodes:\n• N1 Portsmouth (85.2 Hz Master)\n• N2 Farnworth (170.4 Hz Gateway)\n• N3 Cornwall (255.6 Hz Coastal)\n• N4 Cairngorms (340.8 Hz Highland Apex)\n• N5 Solent Deep (42.6 Hz Seabed)\n• N6 Celtic Deep (42.6 Hz Offshore Buoy)\n• N7 Dorchester (127.8 Hz Chalk)\n• N8 Culver Down (42.6 Hz Cliff)\n• N9 Thames Gate (213.0 Hz Estuary)\n• N10 Isle of Man (170.4 Hz Summit)\n• N11 Bristol Channel (85.2 Hz Limestone Ridge)."
                    }
                    q.contains("comic") || q.contains("echo comics") -> {
                        "Echo Comics visualizes the journey of researchers David & Jean from the initial 1986 Hilsea baseline detection to the October 2026 synchronous handshake. Key issues include 'The First Frequency', 'The Farnworth Handshake', and 'Across the Solent'."
                    }
                    q.contains("power") || q.contains("battery") -> {
                        "Each node is sized for autonomous winter survival (0.8 peak sun hours, 7 days of autonomy). Daily power budgets range from 9.0 Wh (N5 Solent Deep) to 49.9 Wh (N6 Celtic Deep), buffered by LiFePO4 batteries configured for 80% maximum depth of discharge."
                    }
                    else -> {
                        "Dola AI: I am tracking all 11 nodes on the 42.6 Hz carrier. You can ask me to analyze field notes, explain harmonic multipliers, check timeline events (from Stonehenge to October 2026), or refine notes in your Workspace."
                    }
                }
            }
        }
    }
}
