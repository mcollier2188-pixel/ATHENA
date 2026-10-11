package com.example.model

data class FieldDoc(
    val id: String,
    val title: String,
    val icon: String,
    val subtitle: String,
    val content: String
)

object FieldDocRepository {
    val documents = listOf(
        FieldDoc(
            id = "doc1",
            title = "ECHO-INTERFACE Schematic v4.1",
            icon = "📐",
            subtitle = "System Architecture & Transducer Coupling",
            content = """# ECHO-INTERFACE SCHEMATIC SPECIFICATION v4.1

1. SYSTEM TOPOLOGY:
- Master Node: N1 Portsmouth (85.200 Hz Carrier, 2x Harmonic).
- Peripheral Slave Nodes: N2 through N11 (Multipliers 1x, 2x, 3x, 4x, 5x, 6x, 8x).
- Coupling Medium: Upper Cretaceous Chalk, Serpentine Granite, and Devonian Limestone strata.

2. TRANSDUCER INTERFACE:
- Piezoelectric Stack: Lead Zirconate Titanate (PZT-8) high-power ceramic array.
- Pre-stress: 45 MPa axial mechanical compression inside titanium housing.
- Acoustic Matching Horn: Exponential aluminum-bronze acoustic transformer.
- Coupling Agent: Bentonite clay and acoustic silicone gel grout.

3. RECEIVER ARCHITECTURE:
- Low-noise seismic geophones (0.1 Hz to 500 Hz passband).
- 24-bit Delta-Sigma Analog-to-Digital Converter.
- FPGA-based digital phase lock loop (DPLL) with 0.001 Hz resolution.
"""
        ),
        FieldDoc(
            id = "doc2",
            title = "Master Power Budget & Sizing",
            icon = "⚡",
            subtitle = "Autonomous Solar-Battery Sizing Formulas",
            content = """# MASTER POWER BUDGET & SIZING GUIDE

FORMULAS:
1. Required Battery Capacity (Wh):
   C_bat = (Days_Autonomy * Daily_Energy_Wh) / (Max_DoD * System_Efficiency)
   
2. Battery Amp-Hours (Ah):
   Ah = C_bat / System_Voltage

3. Required Solar Panel Power (W):
   P_panel = Daily_Energy_Wh / (Winter_Sun_Hours * Temp_Factor * Charger_Efficiency)

NOMINAL CONSTANTS:
- Temp_Factor: 0.90 (Winter cold derating)
- Charger_Efficiency: 0.95 (MPPT controller)
- System_Efficiency: 85% (0.85)
- Standard Winter Sun Hours (UK): 0.8 hours/day
- Target Autonomy: 7.0 days continuous winter blackout
"""
        ),
        FieldDoc(
            id = "doc3",
            title = "Field Site Survey & Installation Guide",
            icon = "🗺️",
            subtitle = "Bedrock Anchoring & Geological Verification",
            content = """# FIELD SITE SURVEY & INSTALLATION PROCEDURES

CHECKLIST:
1. Bedrock Integrity:
   - Minimum rock compressive strength: 60 MPa.
   - Zero active water ingress across drill collar.
   - Core sample acoustic velocity > 2,800 m/s.

2. Borehole Preparation:
   - Drill diameter: 152 mm (6 inches).
   - Drill depth: 12 meters into unweathered competent bedrock.
   - Flush with pressurized nitrogen to remove drill fines.

3. Enclosure & Environmental Sealing:
   - IP68 hermetically sealed 316L stainless steel pressure vessel.
   - Nitrogen purge at 1.2 bar internal positive pressure.
   - Desiccant pack with humidity telemetry indicator.
"""
        ),
        FieldDoc(
            id = "doc4",
            title = "Seismic Simulation Report — Mw 4.5",
            icon = "🌋",
            subtitle = "Stress Testing & Phase Lock Verification",
            content = """# SEISMIC SIMULATION TEST REPORT

EVENT PARAMETERS:
- Simulated Event: Moment Magnitude Mw 4.5
- Epicenter: Bristol Channel (Fault strike 285°)
- Peak Ground Acceleration (PGA): 0.18 g
- Duration: 24.6 seconds

OBSERVATIONS:
- Node N11 (Brean Down): Received primary P-wave at T+0.8s, peak S-wave at T+3.2s.
- DPLL Behavior: Phase jitter peaked at ±14.2° during S-wave passage.
- Lock Recovery: Phase tracking returned to < 0.5° error within 3.4 seconds.
- Packet Integrity: 100% of telemetry frames successfully decoded via convolutional Viterbi FEC.
"""
        ),
        FieldDoc(
            id = "doc5",
            title = "Signal Test Data — All Nodes",
            icon = "📡",
            subtitle = "Frequency Multipliers & Calibration Metrics",
            content = """# SIGNAL TEST DATA SUMMARY (OCTOBER 2026)

| Node | Name | Ratio | Freq (Hz) | Signal-to-Noise (dB) | Status |
|---|---|---|---|---|---|
| N1 | Portsmouth | ×2 | 85.20 | +42.1 dB | MASTER LOCK |
| N2 | Farnworth | ×4 | 170.40 | +38.6 dB | ACTIVE |
| N3 | Cornwall | ×6 | 255.60 | +34.2 dB | ACTIVE |
| N4 | Cairngorms | ×8 | 340.80 | +31.5 dB | ACTIVE |
| N5 | Solent Deep | ×1 | 42.60 | +46.8 dB | ACTIVE |
| N6 | Celtic Deep | ×1 | 42.60 | +29.4 dB | BUOY SYNC |
| N7 | Dorchester | ×3 | 127.80 | +39.0 dB | ACTIVE |
| N8 | Culver Down | ×1 | 42.60 | +44.1 dB | ACTIVE |
| N9 | Thames Gate | ×5 | 213.00 | +36.7 dB | ACTIVE |
| N10 | Isle of Man | ×4 | 170.40 | +33.8 dB | ACTIVE |
| N11 | Bristol Channel | ×2 | 85.20 | +37.9 dB | ACTIVE |
"""
        ),
        FieldDoc(
            id = "doc6",
            title = "Master BOM & Wiring Guide — N1–N11",
            icon = "📋",
            subtitle = "Component Specifications & Interconnects",
            content = """# MASTER BILL OF MATERIALS & WIRING GUIDE

CORE SUB-ASSEMBLIES:
1. Signal Processing:
   - Cortex-M7 Core running embedded FreeRTOS audio DSP.
   - Dual TCXO precision crystal oscillator (0.5 ppm temperature stability).

2. Power Stage:
   - Victron SmartSolar MPPT 75/15 Charge Controller.
   - LiFePO4 battery pack with integrated active cell balancer.
   - Class-D high-efficiency acoustic transducer power amplifier (92% efficiency).

3. Interconnects:
   - Amphenol SubConn underwater connectors for subsea nodes N5 & N6.
   - Belden 9841 shielded twisted pair with double-braided ground for land telemetry.
"""
        )
    )
}
