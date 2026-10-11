package com.example.model

enum class TimelineEra(val id: String, val title: String, val dateRange: String) {
    ANCIENT("ancient", "Ancient", "−2500 → −500"),
    PIONEERS("pioneers", "Pioneers", "−500 → 1890"),
    DISCOVERY("discovery", "Discovery", "1890 → 2025"),
    BUILDING("building", "Building", "2025 → 2026"),
    FUTURE("future", "Future", "2026+")
}

data class TimelineItem(
    val id: String,
    val era: TimelineEra,
    val dateText: String,
    val title: String,
    val description: String,
    val frequencyNote: String? = null
)

object TimelineRepository {
    val items = listOf(
        TimelineItem(
            id = "t1",
            era = TimelineEra.ANCIENT,
            dateText = "~2500 BCE · Salisbury Plain",
            title = "The Stones That Sang — Stonehenge",
            description = "The first known arrangement of megalithic bluestone and sarsen stones selected specifically for their resonant acoustic acoustic cavity properties.",
            frequencyNote = "Sub-harmonic acoustic focusing (~42.6 Hz cavity modes)"
        ),
        TimelineItem(
            id = "t2",
            era = TimelineEra.ANCIENT,
            dateText = "~600 BCE · Delphi, Greece",
            title = "The Oracle's Chamber",
            description = "A natural geological fault line beneath the temple hummed continuously with deep seismic signals, guiding ceremonial acoustic alignment.",
            frequencyNote = "Earth tremor harmonic baseline"
        ),
        TimelineItem(
            id = "t3",
            era = TimelineEra.ANCIENT,
            dateText = "~500 BCE · Samos",
            title = "Pythagoras — Harmonic Ratios",
            description = "The foundational discovery that integer ratios (1:2, 2:3, 3:4) govern all physical vibration across strings, air columns, and planetary bodies.",
            frequencyNote = "Fundamental ratios: 1x, 2x, 4x, 8x"
        ),
        TimelineItem(
            id = "t4",
            era = TimelineEra.ANCIENT,
            dateText = "~120 CE · Portsmouth",
            title = "Roman Chalk Canals — Portsdown Hill",
            description = "Engineered alignments carved into the southern chalk ridge following natural acoustic bedding planes to carry communication echoes across the Solent.",
            frequencyNote = "Chalk substrate waveguide"
        ),
        TimelineItem(
            id = "t5",
            era = TimelineEra.PIONEERS,
            dateText = "1788 · Brittany",
            title = "Léon de Rostrenen — The Bell Rhythm",
            description = "Systematically noted that church bell decay times varied dramatically depending on whether the bell tower was rooted in granite or alluvial sedimentary deposits.",
            frequencyNote = "Differential bedrock damping"
        ),
        TimelineItem(
            id = "t6",
            era = TimelineEra.PIONEERS,
            dateText = "1842 · Lizard Point",
            title = "Thomas Penhaligon — The Granite Hum",
            description = "Cornish tin miner who documented a persistent 255.6 Hz resonant signal rising up from the seabed shafts during Atlantic storm surges.",
            frequencyNote = "N3 Coastal precursor: 255.6 Hz (6x 42.6 Hz)"
        ),
        TimelineItem(
            id = "t7",
            era = TimelineEra.PIONEERS,
            dateText = "1887 · Cambridge",
            title = "Lord Rayleigh — Surface Wave Mathematics",
            description = "Published the exact mathematical equations defining how acoustic energy propagates along solid-fluid interfaces without dispersing.",
            frequencyNote = "Rayleigh acoustic wave propagation"
        ),
        TimelineItem(
            id = "t8",
            era = TimelineEra.DISCOVERY,
            dateText = "1899 · Colorado Springs",
            title = "Nikola Tesla — The 43 Hz Footnote",
            description = "During terrestrial stationary wave experiments, Tesla recorded persistent Earth acoustic-electrical resonance around 43 Hz, documenting it in research notes.",
            frequencyNote = "Early detection of ~42.6 Hz fundamental"
        ),
        TimelineItem(
            id = "t9",
            era = TimelineEra.DISCOVERY,
            dateText = "1986 · Hilsea, Portsmouth",
            title = "42.598 Hz — The Morning It Was Measured",
            description = "David and Jean isolated the exact fundamental tone emanating continuously through the laboratory bedrock slab beneath Portsmouth Harbour.",
            frequencyNote = "Calibrated at 42.598 Hz (nominal 42.6 Hz)"
        ),
        TimelineItem(
            id = "t10",
            era = TimelineEra.DISCOVERY,
            dateText = "1991 · Southampton",
            title = "The 11.74s Tidal Rhythm",
            description = "Solent oceanographers documented an ultra-low frequency envelope cycle pulsing every 11.74 seconds, matching the lunar tidal harmonic node.",
            frequencyNote = "Envelope periodicity: 11.74s"
        ),
        TimelineItem(
            id = "t11",
            era = TimelineEra.BUILDING,
            dateText = "October 9, 2026 · 23:45 UTC",
            title = "The First Handshake — All Nodes Online",
            description = "Master node N1 transmitted from Portsdown Hill. Return signals from all Season 1 nodes (N2-N6) confirmed phase lock across the British Isles within 42 milliseconds.",
            frequencyNote = "Synchronous 42.6 Hz network lock"
        ),
        TimelineItem(
            id = "t12",
            era = TimelineEra.BUILDING,
            dateText = "October 2026 · Bristol Channel",
            title = "Mw 4.5 Seismic Simulation — Network Held Lock",
            description = "Simulated seabed acoustic disturbance tested network resilience. Node N11 (Brean Down) absorbed peak ground acceleration and re-locked in 3.4 seconds.",
            frequencyNote = "Zero packet loss during shockwave"
        ),
        TimelineItem(
            id = "t13",
            era = TimelineEra.FUTURE,
            dateText = "2027+ · Global Extension",
            title = "The Planetary Acoustic Mesh",
            description = "Expansion of the 42.6 Hz frequency standard across continental tectonic plates, establishing an unjammable underground harmonic telemetry network.",
            frequencyNote = "Planetary resonance synchronization"
        )
    )
}
