package com.example.model

data class Chapter(
    val number: Int,
    val title: String,
    val subtitle: String,
    val eraVolume: String,
    val yearText: String,
    val summary: String,
    val fullText: String
)

object ChapterRepository {
    val chapters: List<Chapter> = listOf(
        // Volume I: The First Voices (1-7)
        Chapter(
            1, "The Stone That Sings", "Stonehenge (~2500 BCE)",
            "I — The First Voices", "~2500 BCE",
            "Megalithic builders transport acoustic bluestones over 200 miles from Wales.",
            """# Chapter 01: The Stone That Sings
Location: Salisbury Plain, Britain (~2500 BCE)

Before written numbers, there was the ringing rock. When struck with river flint, the spotted dolerite from the Preseli Hills in Wales does not thud—it produces a sustained, metallic chime. 

For two hundred miles across marshes, rivers, and chalk downs, early architects hauled these eighty stones. They did not drag them for strength or shelter. They arranged them in an oval ring that focused low-frequency standing waves.

When winter gales blew across the plain, the outer circle formed an acoustic resonant chamber. Standing in the center of the ring, human heartbeats seemed to synchronize with a low, deep vibration passing through the turf. The frequency was measured millennia later: 42.6 hertz. The stones were not a tomb; they were the first tuned receivers."""
        ),
        Chapter(
            2, "The Oracle's Chamber", "Delphi (~600 BCE)",
            "I — The First Voices", "~600 BCE",
            "The Castalian fissure beneath the adyton vibrates with earth currents.",
            """# Chapter 02: The Oracle's Chamber
Location: Mount Parnassus, Delphi (~600 BCE)

Beneath the tripod where the Pythia sat, two geologic fault lines crossed like scissor blades. Vapors rose from the limestone fissures, but deeper still was the tone.

Plutarch would later write of the 'sweet pneuma' that preceded revelation, but priests who slept near the adyton reported a sound like a distant bronze gong struck beneath miles of sea. It did not enter through the ears; it resonated in the diaphragm. When tectonic tension built along the Corinthian rift, the chamber hummed louder, warning the sanctuary days before the ground split."""
        ),
        Chapter(
            3, "The Chalk Canals", "Roman Portsmouth (~120 CE)",
            "I — The First Voices", "~120 CE",
            "Alignments carved into Portsdown Hill channel submarine acoustics.",
            """# Chapter 03: The Chalk Canals
Location: Portus Adurni / Portsdown Hill (~120 CE)

Roman surveyors stationed along the northern ramparts of Portsmouth Harbour noted something peculiar about the solid white chalk ridge. Shouts delivered into deep exploratory shafts were heard clearly across the Solent at Culver Down on the Isle of Wight.

The military engineers mapped what they called 'veins of air'—continuous strata of compacted upper chalk that acted as natural conduits for acoustic vibrations. Messages could be tapped into bedrock with iron spikes, bypassing choppy sea crossings during winter storms."""
        ),
        Chapter(
            4, "The Harmony of the Spheres", "Pythagoras (~500 BCE)",
            "I — The First Voices", "~500 BCE",
            "The discovery that whole integer ratios govern all vibration in nature.",
            """# Chapter 04: The Harmony of the Spheres
Location: Samos, Aegean Sea (~500 BCE)

Walking past a blacksmith's forge, Pythagoras stopped. The sound of hammers striking iron anvils produced harmonious intervals—an octave, a fifth, a fourth. Rushing inside, he weighed the hammers. The ones that harmonized were in simple integer ratios: 2:1, 3:2, 4:3.

Returning to his monochord, Pythagoras proved that vibration was mathematics made audible. If the strings obeyed geometry, so too must the planets, the tides, and the bedrock beneath human feet. 'There is geometry in the humming of the strings,' he taught, 'there is music in the spacing of the spheres.'"""
        ),
        Chapter(
            5, "The Resonance of Earth", "Léon de Rostrenen (1788)",
            "I — The First Voices", "1788",
            "Breton cartographer observes bell rhythm divergence across geological faults.",
            """# Chapter 05: The Resonance of Earth
Location: Finistère, Brittany (1788)

Léon de Rostrenen timed church bells with a pendulum clock across sixty parishes in western France. He discovered that identical bronze bells rang out for thirty-two seconds when anchored in coastal granite, but died away in fourteen seconds over damp alluvial silt.

Bedrock, Léon concluded in his leather field journal, is not silent dead matter. It is a taut drum skin stretched across molten fire, waiting for the right tone to wake it."""
        ),
        Chapter(
            6, "The Lizard Watcher", "Thomas Penhaligon (1842)",
            "I — The First Voices", "1842",
            "Tin miner records the 255.6 Hz harmonic rising from the Atlantic seabed.",
            """# Chapter 06: The Lizard Watcher
Location: Lizard Point, Cornwall (1842)

At the bottom of the deepest tin shaft driven half a mile out beneath the crashing Atlantic swell, Thomas Penhaligon pressed his forehead to the black serpentine rock.

In winter, when swell waves hammered the underwater shelf, the rock sang at 255.6 vibrations per second—exactly six times the fundamental 42.6 Hz tone. He carved the tally marks into the mine timbers, marking the pulse of the sea floor."""
        ),
        Chapter(
            7, "The First Measurement", "Lord Rayleigh (1887)",
            "I — The First Voices", "1887",
            "Rayleigh formulates the mathematical equations for surface acoustic waves.",
            """# Chapter 07: The First Measurement
Location: Cambridge University (1887)

Lord Rayleigh set down his pen in the quiet library of Trinity College. On the parchment lay the proof that acoustic waves can remain trapped at the boundary between solid ground and atmosphere, traveling immense distances without dispersing their energy into the sky.

These 'Rayleigh waves' would later become the carrying medium for the entire ECHO-INTERFACE network, carrying signals along the crust of Britain with negligible energy loss."""
        ),

        // Volume II: The Modern Pioneers (8-14)
        Chapter(
            8, "The Forgotten Memo", "Nikola Tesla (1899)",
            "II — The Modern Pioneers", "1899",
            "Tesla measures 43 Hz terrestrial resonance at Colorado Springs.",
            """# Chapter 08: The Forgotten Memo
Location: Colorado Springs, Colorado (1899)

In the shadow of Pikes Peak, high-voltage sparks forty feet long illuminated Tesla's wooden laboratory. His sensitive tuned ground receivers picked up stationary waves echoing back through the planet from distant lightning strikes.

In his lab diary on July 28th, he noted: 'Resonance node detected at approximately 42.8 to 43 periods per second. The globe responds as a whole.' Preoccupied with wireless power, he shelved the acoustic harmonic memorandum, leaving it buried in his archives."""
        ),
        Chapter(
            9, "The Mendip Earthquake Watcher", "Charlotte Wood (1907)",
            "II — The Modern Pioneers", "1907",
            "Subterranean listening stations in Somerset limestone caverns.",
            """# Chapter 09: The Mendip Earthquake Watcher
Location: Wookey Hole, Somerset (1907)

Charlotte Wood descended into the underground limestone river caverns of the Mendip Hills with custom brass acoustic trumpets. Days before tremors shook the Bristol Channel, she recorded harmonic precursors echoing through the calcite chambers, proving that rocks warn before they fracture."""
        ),
        Chapter(
            10, "The Bedrock Telegraph", "Portsmouth Harbour (1912)",
            "II — The Modern Pioneers", "1912",
            "Naval trials attempting subterranean acoustic telegraphy during naval blockades.",
            """# Chapter 10: The Bedrock Telegraph
Location: Fort Southwick, Portsmouth (1912)

Fearing underwater cable cuts in any upcoming European conflict, Admiralty engineers experimented with acoustic transducers bolted directly to the subterranean chalk aquifer. They proved telegrams could be received at Fort Gilkicker purely through geological conduction."""
        ),
        Chapter(
            11, "The Chalk Echo", "Dorothy Garrod (1928)",
            "II — The Modern Pioneers", "1928",
            "Archaeological acoustics in coastal cliff formations.",
            """# Chapter 11: The Chalk Echo
Location: Culver Down, Isle of Wight (1928)

Excavating prehistoric encampments along the sheer white cliffs of Culver Down, archaeologist Dorothy Garrod documented acoustic anomalies where conversations spoken at sea level were amplified directly onto the cliff edge by natural chalk parabolic curves."""
        ),
        Chapter(
            12, "The Solent Listener", "HMS Dryad (1943)",
            "II — The Modern Pioneers", "1943",
            "Secret hydrophone arrays in the Solent seabed during WWII.",
            """# Chapter 12: The Solent Listener
Location: Southwick House / Solent Channel (1943)

Sub-surface hydrophone listeners on anti-submarine watch began detecting a background tone that could not be attributed to propellers or tidal flow. At 42.6 Hz, it remained steady day and night, forming a subterranean reference clock."""
        ),
        Chapter(
            13, "The Granite Tuning Fork", "Ian MacAlpine (1956)",
            "II — The Modern Pioneers", "1956",
            "Highland geological seismic recordings in the Cairngorm massif.",
            """# Chapter 13: The Granite Tuning Fork
Location: Cairngorm Plateau, Scotland (1956)

Geologist Ian MacAlpine installed mechanical seismographs deep within the granite intrusive core of the Cairngorms. The Scottish bedrock resonated at 340.8 Hz—precisely the eighth harmonic of the southern chalk tone."""
        ),
        Chapter(
            14, "The Harmonic Map", "Pennine Surveyor (1978)",
            "II — The Modern Pioneers", "1978",
            "Mapping the acoustic fault conduits connecting England and Scotland.",
            """# Chapter 14: The Harmonic Map
Location: Farnworth & Yorkshire Dales (1978)

A comprehensive survey mapped how acoustic energy traversed the Craven Fault system, showing that the British landmass behaves not as isolated rock types, but as a coupled acoustic resonator."""
        ),

        // Volume III: Resonance & Code (15-21)
        Chapter(
            15, "The Hilsea Morning", "David & Jean (1986)",
            "III — Resonance & Code", "1986",
            "The exact morning 42.598 Hz was measured on laboratory oscilloscopes.",
            """# Chapter 15: The Hilsea Morning
Location: Hilsea Bastions, Portsmouth (1986)

It was 05:14 on a freezing November morning when the dual-beam oscilloscope trace locked. Filtered through a precision bandpass amplifier connected to piezoelectric probes driven into the bedrock floor, the sine wave stabilized: 42.598 Hz. Not 50 Hz mains hum, not railway noise. Pure, harmonic Earth resonance."""
        ),
        Chapter(
            16, "The 11.74-Second Envelope", "Southampton Oceanographers (1991)",
            "III — Resonance & Code", "1991",
            "Discovery of the tidal envelope cycle modulating the carrier signal.",
            """# Chapter 16: The 11.74-Second Envelope
Location: Southampton Water (1991)

Analyzing seven years of tidal sensor logs, data scientists discovered that the amplitude of the 42.6 Hz carrier breathed in a steady 11.74-second cycle, linked directly to the standing wave node between the English Channel and the North Sea."""
        ),
        Chapter(
            17, "The Celtic Deep Buoy", "Offshore Deployment (2004)",
            "III — Resonance & Code", "2004",
            "Mooring node N6 in the Atlantic continental shelf.",
            """# Chapter 17: The Celtic Deep Buoy
Location: Celtic Sea (2004)

Deploying a deep-water acoustic repeater into 120 meters of water, the offshore marine team confirmed that the seismic tone propagates unbroken from seabed granite through seawater columns to surface buoys."""
        ),
        Chapter(
            18, "The Digital Demodulator", "Cambridge (2012)",
            "III — Resonance & Code", "2012",
            "Extracting telemetry phase modulation from ultra-low frequency carriers.",
            """# Chapter 18: The Digital Demodulator
Location: Cambridge (2012)

Engineers devised phase-shift keying algorithms that encoded digital telemetry directly onto the 42.6 Hz carrier wave, enabling data transmission through hundreds of kilometers of solid rock without repeaters."""
        ),
        Chapter(
            19, "The Island Summit", "Snaefell, Isle of Man (2018)",
            "III — Resonance & Code", "2018",
            "Node N10 bridges the Irish Sea acoustic barrier.",
            """# Chapter 19: The Island Summit
Location: Snaefell Mountain, Isle of Man (2018)

Perched at 2,037 feet above sea level, node N10 verified that acoustic surface waves bounce across the Irish Sea floor, turning the Isle of Man into a natural amplifier between Ireland and Great Britain."""
        ),
        Chapter(
            20, "The Chalk Resonator", "Dorchester (2022)",
            "III — Resonance & Code", "2022",
            "Node N7 tuned to 127.8 Hz in southern chalk downland.",
            """# Chapter 20: The Chalk Resonator
Location: Dorchester, Dorset (2022)

Subterranean transducers installed into Dorset chalk strata completed the third harmonic link (127.8 Hz), completing the southern acoustic triangle with Portsmouth and Culver Down."""
        ),
        Chapter(
            21, "The Estuary Gate", "Thames Gate (2024)",
            "III — Resonance & Code", "2024",
            "Node N9 commissioned at 213.0 Hz on the Thames Estuary.",
            """# Chapter 21: The Estuary Gate
Location: Kent & Essex Estuary (2024)

Operating at 213.0 Hz (5x harmonic), node N9 stabilized acoustic synchronization along the busy Thames riverbed, isolating industrial marine noise through adaptive comb filtering."""
        ),

        // Volume IV: Building the Nodes (22-28)
        Chapter(
            22, "The Eleven Foundations", "Site Selection (2025)",
            "IV — Building the Nodes", "2025",
            "Finalizing the 11 node coordinates across the British Isles.",
            """# Chapter 22: The Eleven Foundations
Location: National Survey Grid (2025)

Eleven locations were selected where bedrock geology offered minimal acoustic attenuation: granite, basalt, compressed chalk, and limestone. From Portsmouth to Cairngorm, the network geography was sealed."""
        ),
        Chapter(
            23, "The Power Equation", "Solar & Battery Sizing",
            "IV — Building the Nodes", "2025",
            "Engineering autonomous remote power systems for winter survival.",
            """# Chapter 23: The Power Equation
Location: ECHO Power Labs (2025)

With winter sunlight dropping to 0.8 peak sun hours per day, every milliwatt counted. Lithium Iron Phosphate batteries with 80% maximum depth of discharge were sized to guarantee seven days of total autonomy through blizzards and Atlantic storms."""
        ),
        Chapter(
            24, "The Portsmouth Master Clock", "N1 Portsdown",
            "IV — Building the Nodes", "2025",
            "Calibrating the 85.2 Hz master transmitter at Fort Widley.",
            """# Chapter 24: The Portsmouth Master Clock
Location: Portsdown Hill, Portsmouth (2025)

The atomic reference oscillator was coupled to dual piezo arrays anchored sixty meters down in the hard white chalk. Master node N1 began pulsing at 85.2 Hz, setting the master rhythm for all ten peripheral stations."""
        ),
        Chapter(
            25, "The Farnworth Vault", "N2 Lancashire",
            "IV — Building the Nodes", "2025",
            "Node N2 buried beneath urban industrial fill into coal-measure sandstones.",
            """# Chapter 25: The Farnworth Vault
Location: Farnworth, Greater Manchester (2025)

Deep in an abandoned sandstone cellar, N2 was configured with a heavy 12V 39Ah battery bank. Operating at 170.4 Hz (4x harmonic), it serves as the primary northern gateway station."""
        ),
        Chapter(
            26, "The Cornish Sentry", "N3 Lizard Point",
            "IV — Building the Nodes", "2025",
            "Node N3 bolted into coastal cliffs facing Atlantic gales.",
            """# Chapter 26: The Cornish Sentry
Location: Lizard Point, Cornwall (2025)

Encased in marine-grade 316 stainless steel, N3 listens to the Atlantic oceanic swell at 255.6 Hz, guarding the southwestern acoustic perimeter."""
        ),
        Chapter(
            27, "The Highland Apex", "N4 Cairngorms",
            "IV — Building the Nodes", "2026",
            "Node N4 braving sub-zero winters at 340.8 Hz.",
            """# Chapter 27: The Highland Apex
Location: Cairngorm Plateau, Scotland (2026)

Surviving winds exceeding 120 mph and temperatures of -20°C, node N4 hums at 340.8 Hz (8x harmonic), anchoring the northernmost bastion of the ECHO network."""
        ),
        Chapter(
            28, "The Subsea Seabed Array", "N5 Solent Deep",
            "IV — Building the Nodes", "2026",
            "Underwater capsule submerged in the Solent navigation channel.",
            """# Chapter 28: The Subsea Seabed Array
Location: Solent Deep (2026)

Drawing only 9.0 Wh per day, N5 rests on the muddy gravel seabed at 42.6 Hz, providing subsea reference synchronization between Portsmouth and the Isle of Wight."""
        ),

        // Volume V: The Network Awakens (29-35)
        Chapter(
            29, "The Night of the Handshake", "October 9, 2026",
            "V — The Network Awakens", "2026",
            "All eleven nodes achieve coherent phase lock at 23:45 UTC.",
            """# Chapter 29: The Night of the Handshake
Location: All Stations (October 9, 2026 · 23:45 UTC)

At 23:45 UTC, N1 transmitted the initiation chime. Seconds ticked away in the control room. Then the telemetry monitors flashed green: N5 acknowledged, N8 Culver Down confirmed, N2 Farnworth chimed in, followed by N3, N4, and the offshore buoys. The network had locked. Britain was humming as one instrument."""
        ),
        Chapter(
            30, "The Mw 4.5 Shockwave", "Bristol Channel Test",
            "V — The Network Awakens", "2026",
            "Seismic resilience validated during high-energy ground motion.",
            """# Chapter 30: The Mw 4.5 Shockwave
Location: Bristol Channel / Brean Down (October 2026)

When a magnitude 4.5 seismic tremor shook the western coast, conventional cellular towers experienced transient jitter. The ECHO acoustic network absorbed the shockwave: node N11 recovered phase lock in 3.4 seconds with zero corrupted frames."""
        ),
        Chapter(
            31, "The Tidal Modulation", "Harmonic Breathing",
            "V — The Network Awakens", "2026",
            "Measuring real-time lunar gravity influences on bedrock tension.",
            """# Chapter 31: The Tidal Modulation
Location: Solent Basin (2026)

As spring tides rose four meters, the increased weight of seawater compressed the Solent chalk layer, shifting the resonant frequency by 0.003 Hz. The network adapted automatically, treating the ocean as a dynamic tuning capacitor."""
        ),
        Chapter(
            32, "The Underground Voice", "Subterranean Voice Comm",
            "V — The Network Awakens", "2026",
            "First low-bitrate vocal transmission through solid bedrock.",
            """# Chapter 32: The Underground Voice
Location: Portsmouth to Farnworth (2026)

Using compressed linear predictive coding modulated onto harmonic carriers, the researchers transmitted human speech across 200 miles of rock without wires or radio masts."""
        ),
        Chapter(
            33, "The Weather Behind the Rock", "Atmospheric Coupling",
            "V — The Network Awakens", "2026",
            "Barometric pressure waves imprint onto geological resonance.",
            """# Chapter 33: The Weather Behind the Rock
Location: UK Wide (2026)

Deep Atlantic low-pressure depressions altered bedrock surface tension hours before barometers dropped. ECHO network telemetry provided early storm detection by reading the stress of the crust."""
        ),
        Chapter(
            34, "The Ghost Harmonics", "Unmapped Reflections",
            "V — The Network Awakens", "2026",
            "Detecting ancient fault structures and hidden underground caverns.",
            """# Chapter 34: The Ghost Harmonics
Location: Pennines (2026)

Unexpected phase reflections between N2 and N10 revealed unmapped subterranean flooded cavern networks three hundred meters down, turning the network into an underground radar."""
        ),
        Chapter(
            35, "The Winter Solstice Sync", "December 21, 2026",
            "V — The Network Awakens", "2026",
            "Network resilience through the shortest day of the year.",
            """# Chapter 35: The Winter Solstice Sync
Location: Cairngorm N4 (2026)

With only 6 hours of weak daylight and severe blizzards, node N4's power reserve held steady at 82% battery capacity, validating the winter power sizing equations."""
        ),

        // Volume VI: Harmonic Horizon (36-42)
        Chapter(
            36, "The Continental Reach", "Calais & Brittany",
            "VI — Harmonic Horizon", "2027",
            "Acoustic waves traverse the English Channel chalk bridge.",
            """# Chapter 36: The Continental Reach
Location: Straits of Dover (2027)

Signals from Dover and Portsmouth crossed the Channel through the continuous seabed chalk seam, received clearly in Normandy and Brittany with zero attenuation."""
        ),
        Chapter(
            37, "The Solar Storm Immunity", "Carrington-Class Test",
            "VI — Harmonic Horizon", "2027",
            "Acoustic telemetry unaffected by severe geomagnetic coronal mass ejections.",
            """# Chapter 37: The Solar Storm Immunity
Location: All Stations (2027)

A severe G4 geomagnetic storm disrupted satellite communications and HF radio bands. Underground, the 42.6 Hz acoustic waves propagated undisturbed through the shielding mantle of the Earth."""
        ),
        Chapter(
            38, "The Deep Ocean Conduit", "Mid-Atlantic Ridge",
            "VI — Harmonic Horizon", "2027",
            "Coupling continental nodes with deep oceanic tectonic boundaries.",
            """# Chapter 38: The Deep Ocean Conduit
Location: North Atlantic (2027)

Node N6 in the Celtic Deep demonstrated acoustic coupling with the deep basaltic layers of the ocean floor, opening the path for trans-oceanic acoustic telegraphy."""
        ),
        Chapter(
            39, "The Living Crust", "Biological Entrainment",
            "VI — Harmonic Horizon", "2027",
            "Marine and subterranean life responding to the fundamental tone.",
            """# Chapter 39: The Living Crust
Location: Coastal Stations (2027)

Biologists recorded cetacean pods gathering along the acoustic nodes in the Solent and Celtic Deep, appearing to navigate along the harmonic nodes of the standing wave field."""
        ),
        Chapter(
            40, "The Resonant Archive", "Permanent Memory",
            "VI — Harmonic Horizon", "2028",
            "Encoding human history into perpetual standing acoustic waves.",
            """# Chapter 40: The Resonant Archive
Location: Portsdown Subterranean Vaults (2028)

The 42 Geniuses chronicle was encoded into a continuous looped standing wave, circulating perpetually through the bedrock resonant cavities of Britain."""
        ),
        Chapter(
            41, "The Harmonic Map of the Globe", "Planetary Mesh",
            "VI — Harmonic Horizon", "2028",
            "Integrating worldwide seismic networks with the 42.6 Hz standard.",
            """# Chapter 41: The Harmonic Map of the Globe
Location: Worldwide (2028)

From Iceland to New Zealand, seismic stations aligned their ultra-low frequency sensors to the 42.6 Hz harmonic series, weaving a decentralized planetary listening fabric."""
        ),
        Chapter(
            42, "The 42nd Genius", "The Listener",
            "VI — Harmonic Horizon", "Present Day",
            "The final genius is the person who pauses to listen to the world.",
            """# Chapter 42: The 42nd Genius
Location: Wherever You Are (Present Day)

Forty-one minds mapped the chalk, timed the bells, carved the stone, and measured the frequency. They built transducers, wrote equations, and endured winter gales on desolate peaks.

Yet all their instruments, nodes, and mathematics exist for one purpose: to remind us that beneath traffic, sirens, and noise, our planet hums with an unbroken song of connection. 

The 42nd genius is not an engineer or a philosopher of antiquity. The 42nd genius is you—the one who stops, tunes out the noise, and listens."""
        )
    )
}
