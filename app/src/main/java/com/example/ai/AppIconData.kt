package com.example.ai

import androidx.compose.ui.graphics.Color

data class AppIconData(
    val glyph: String = "🌊",
    val glyphName: String = "Acoustic Wave",
    val bgStartHex: String = "#00E5FF",
    val bgEndHex: String = "#081A2D",
    val shape: IconShape = IconShape.SQUIRCLE
) {
    val startColor: Color get() = parseHexColor(bgStartHex)
    val endColor: Color get() = parseHexColor(bgEndHex)

    fun toSvg(appName: String): String {
        return """
            <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 512 512" width="512" height="512">
              <defs>
                <linearGradient id="bgGrad" x1="0%" y1="0%" x2="100%" y2="100%">
                  <stop offset="0%" stop-color="$bgStartHex" />
                  <stop offset="100%" stop-color="$bgEndHex" />
                </linearGradient>
              </defs>
              <rect width="512" height="512" rx="128" fill="url(#bgGrad)" />
              <text x="50%" y="54%" font-size="220" text-anchor="middle" dominant-baseline="central" font-family="system-ui, -apple-system, sans-serif">$glyph</text>
            </svg>
        """.trimIndent()
    }

    companion object {
        fun defaultFor(archetype: AppArchetype): AppIconData {
            return when (archetype) {
                AppArchetype.SCIENTIFIC_ACOUSTICS -> AppIconData(
                    glyph = "🌊",
                    glyphName = "Acoustic Wave",
                    bgStartHex = "#00E5FF",
                    bgEndHex = "#0A2540",
                    shape = IconShape.SQUIRCLE
                )
                AppArchetype.GEOLOGICAL_TELEMETRY -> AppIconData(
                    glyph = "📡",
                    glyphName = "Seismic Telemetry",
                    bgStartHex = "#06D6A0",
                    bgEndHex = "#05261F",
                    shape = IconShape.ROUNDED_RECT
                )
                AppArchetype.STANDALONE_ATHENA_AI -> AppIconData(
                    glyph = "🦉",
                    glyphName = "Athena Intelligence",
                    bgStartHex = "#B388FF",
                    bgEndHex = "#1E0E38",
                    shape = IconShape.CIRCLE
                )
                AppArchetype.CUSTOM_COMPOSE_APP -> AppIconData(
                    glyph = "⚡",
                    glyphName = "Quantum Pulse",
                    bgStartHex = "#FFD166",
                    bgEndHex = "#332200",
                    shape = IconShape.SQUIRCLE
                )
            }
        }
    }
}

enum class IconShape(val label: String) {
    SQUIRCLE("Adaptive Squircle"),
    CIRCLE("Circle"),
    ROUNDED_RECT("Rounded Square")
}

data class IconPalettePreset(
    val name: String,
    val startHex: String,
    val endHex: String
)

val PRESET_ICON_PALETTES = listOf(
    IconPalettePreset("Cyber Cyan", "#00E5FF", "#061A2B"),
    IconPalettePreset("Solar Gold", "#FFD166", "#3A2600"),
    IconPalettePreset("Emerald Matrix", "#06D6A0", "#06291E"),
    IconPalettePreset("Neon Violet", "#B388FF", "#1D0B33"),
    IconPalettePreset("Obsidian Ember", "#FF5252", "#2B0B0B"),
    IconPalettePreset("Deep Space", "#448AFF", "#09142B")
)

val PRESET_ICON_GLYPHS = listOf(
    "🌊" to "Acoustic Wave",
    "⚡" to "Harmonic Pulse",
    "⚛" to "Quantum Atom",
    "🏛" to "King's Chamber",
    "🦉" to "Athena Owl",
    "📡" to "Telemetry Sensor",
    "🎧" to "Binaural Audio",
    "💎" to "Resonant Crystal",
    "🧬" to "DNA Spiral",
    "📱" to "Mobile App",
    "🛡" to "Aegis Shield",
    "🎯" to "Frequency Target"
)

private fun parseHexColor(hex: String): Color {
    return try {
        val clean = hex.removePrefix("#")
        val colorInt = if (clean.length == 6) {
            (0xFF shl 24) or clean.toInt(16)
        } else {
            clean.toLong(16).toInt()
        }
        Color(colorInt)
    } catch (e: Exception) {
        Color(0xFF00E5FF)
    }
}
