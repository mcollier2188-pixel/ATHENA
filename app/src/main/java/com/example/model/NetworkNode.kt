package com.example.model

data class NetworkNode(
    val id: String,
    val name: String,
    val baseRatio: Int,
    val latitude: Double,
    val longitude: Double,
    val dailyEnergyWh: Double,
    val batteryVoltage: Double,
    val batteryCapacityAh: Double,
    val setting: String,
    val frequency: Double = (baseRatio * 42.6)
) {
    val totalBatteryWh: Double
        get() = batteryVoltage * batteryCapacityAh

    val comparedToMaster: Double
        get() = frequency / 85.2

    companion object {
        const val BASE_FREQUENCY_HZ = 42.6
        const val MASTER_FREQUENCY_HZ = 85.2

        val ALL_NODES = listOf(
            NetworkNode("N1", "Portsmouth", 2, 50.80, -1.09, 21.9, 24.0, 15.0, "Master clock"),
            NetworkNode("N2", "Farnworth", 4, 53.55, -2.40, 28.2, 12.0, 39.0, "Buried site"),
            NetworkNode("N3", "Cornwall", 6, 50.20, -5.00, 25.0, 24.0, 17.0, "Coastal"),
            NetworkNode("N4", "Cairngorms", 8, 57.10, -3.70, 30.0, 25.6, 12.0, "Highlands"),
            NetworkNode("N5", "Solent Deep", 1, 50.50, -1.50, 9.0, 24.0, 4.0, "Seabed"),
            NetworkNode("N6", "Celtic Deep", 1, 50.40, -6.40, 49.9, 48.0, 11.0, "Open-sea buoy"),
            NetworkNode("N7", "Dorchester", 3, 50.71, -2.44, 20.5, 12.8, 16.5, "Chalk"),
            NetworkNode("N8", "Culver Down", 1, 50.62, -0.90, 17.7, 12.8, 14.2, "Chalk cliff"),
            NetworkNode("N9", "Thames Gate", 5, 51.45, 0.70, 26.2, 12.8, 21.0, "Estuary"),
            NetworkNode("N10", "Isle of Man", 4, 54.26, -4.46, 30.4, 25.6, 12.0, "Island summit"),
            NetworkNode("N11", "Bristol Channel", 2, 51.32, -3.02, 22.7, 25.6, 9.0, "Limestone ridge")
        )
    }
}
