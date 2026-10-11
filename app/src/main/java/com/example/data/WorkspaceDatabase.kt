package com.example.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.UUID

import androidx.room.migration.Migration

@Database(
    entities = [
        WorkspaceItem::class,
        FrequencyHistoryEntry::class,
        FrequencyPreset::class,
        ResonanceIntervalCalculation::class
    ],
    version = 4,
    exportSchema = false
)
abstract class WorkspaceDatabase : RoomDatabase() {
    abstract fun workspaceDao(): WorkspaceDao
    abstract fun frequencyHistoryDao(): FrequencyHistoryDao
    abstract fun frequencyPresetDao(): FrequencyPresetDao
    abstract fun resonanceIntervalDao(): ResonanceIntervalDao

    companion object {
        @Volatile
        private var INSTANCE: WorkspaceDatabase? = null

        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("""
                    CREATE TABLE IF NOT EXISTS `frequency_history` (
                        `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        `frequencyHz` REAL NOT NULL,
                        `timestamp` INTEGER NOT NULL,
                        `durationSeconds` INTEGER NOT NULL,
                        `isResonanceLock` INTEGER NOT NULL,
                        `sessionNote` TEXT
                    )
                """.trimIndent())
            }
        }

        val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("""
                    CREATE TABLE IF NOT EXISTS `frequency_presets` (
                        `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        `frequencyHz` REAL NOT NULL,
                        `label` TEXT NOT NULL,
                        `description` TEXT NOT NULL,
                        `isDefault` INTEGER NOT NULL,
                        `orderIndex` INTEGER NOT NULL
                    )
                """.trimIndent())

                db.execSQL("INSERT INTO `frequency_presets` (`frequencyHz`, `label`, `description`, `isDefault`, `orderIndex`) VALUES (42.000, 'Sub-Band Floor', '42.000 Hz baseline threshold', 1, 0)")
                db.execSQL("INSERT INTO `frequency_presets` (`frequencyHz`, `label`, `description`, `isDefault`, `orderIndex`) VALUES (42.598, 'Hilsea 1986 Lab', '42.598 Hz chalk layer alignment', 1, 1)")
                db.execSQL("INSERT INTO `frequency_presets` (`frequencyHz`, `label`, `description`, `isDefault`, `orderIndex`) VALUES (42.600, 'Fundamental Peak', '42.600 Hz bedrock standing wave', 1, 2)")
                db.execSQL("INSERT INTO `frequency_presets` (`frequencyHz`, `label`, `description`, `isDefault`, `orderIndex`) VALUES (43.000, 'Tesla 1899 Peak', '43.000 Hz terrestrial wave peak', 1, 3)")
            }
        }

        val MIGRATION_1_3 = object : Migration(1, 3) {
            override fun migrate(db: SupportSQLiteDatabase) {
                MIGRATION_1_2.migrate(db)
                MIGRATION_2_3.migrate(db)
            }
        }

        val MIGRATION_3_4 = object : Migration(3, 4) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("""
                    CREATE TABLE IF NOT EXISTS `resonance_intervals` (
                        `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        `name` TEXT NOT NULL,
                        `intervalLabel` TEXT NOT NULL,
                        `multiplier` REAL NOT NULL,
                        `baseFrequencyHz` REAL NOT NULL,
                        `calculatedFrequencyHz` REAL NOT NULL,
                        `wavelengthMeters` REAL NOT NULL,
                        `periodSeconds` REAL NOT NULL,
                        `acousticVelocityMps` REAL NOT NULL,
                        `couplingEfficiencyPercent` REAL NOT NULL,
                        `note` TEXT NOT NULL,
                        `timestampEpochMs` INTEGER NOT NULL
                    )
                """.trimIndent())
            }
        }

        fun getDatabase(context: Context): WorkspaceDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    WorkspaceDatabase::class.java,
                    "echo_workspace_db"
                )
                    .addMigrations(MIGRATION_1_2, MIGRATION_2_3, MIGRATION_1_3, MIGRATION_3_4)
                    .fallbackToDestructiveMigration(true)
                    .fallbackToDestructiveMigrationOnDowngrade(true)
                    .addCallback(object : Callback() {
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)
                            CoroutineScope(Dispatchers.IO).launch {
                                populateInitialData(
                                    getDatabase(context).workspaceDao(),
                                    getDatabase(context).frequencyHistoryDao(),
                                    getDatabase(context).frequencyPresetDao(),
                                    getDatabase(context).resonanceIntervalDao()
                                )
                            }
                        }
                    })
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private suspend fun populateInitialData(
            dao: WorkspaceDao,
            historyDao: FrequencyHistoryDao,
            presetDao: FrequencyPresetDao,
            intervalDao: ResonanceIntervalDao
        ) {
            val echoComicsFolderId = UUID.randomUUID().toString()
            val myProjectsFolderId = UUID.randomUUID().toString()
            val fieldNotesFolderId = UUID.randomUUID().toString()

            val initialItems = listOf(
                WorkspaceItem(
                    id = echoComicsFolderId,
                    parentId = null,
                    kind = "folder",
                    name = "Echo Comics",
                    tags = "comics, visual, archive"
                ),
                WorkspaceItem(
                    id = myProjectsFolderId,
                    parentId = null,
                    kind = "folder",
                    name = "My Projects",
                    tags = "projects, active"
                ),
                WorkspaceItem(
                    id = fieldNotesFolderId,
                    parentId = null,
                    kind = "folder",
                    name = "Field Docs & Surveys",
                    tags = "nodes, echo-interface, telemetry"
                ),
                WorkspaceItem(
                    id = UUID.randomUUID().toString(),
                    parentId = echoComicsFolderId,
                    kind = "note",
                    name = "Issue 1 — The First Frequency",
                    text = """# ECHO COMICS: Issue 1 — The First Frequency
Setting: Portsmouth Harbour, Hilsea bastion, 1986.
Characters: David & Jean (Acoustic Researchers).

Page 1 (Splash):
Rain beating against reinforced subterranean bunker glass. An oscilloscope trace stabilizes into a perfect standing sinusoidal tone: 42.598 Hz.

David: "It's not ambient industrial noise, Jean. Look at the phase alignment across the Solent chalk layer."
Jean: "That's bedrock resonance. The entire island is humming."
""",
                    tags = "comic, script, issue1",
                    isPinned = true
                ),
                WorkspaceItem(
                    id = UUID.randomUUID().toString(),
                    parentId = myProjectsFolderId,
                    kind = "note",
                    name = "42 Geniuses Narrative Outline",
                    text = """The chronicle of 42 historical and contemporary observers who tuned into the Earth's low-frequency acoustic waveguide.

From the megalithic sarsen stones of Stonehenge (~2500 BCE) to Lord Rayleigh's mathematical formulation of surface waves in 1887, Nikola Tesla's 43 Hz terrestrial resonance note in 1899, and the modern 11-node ECHO-INTERFACE commissioning in 2026.

Key Themes:
1. Resonant harmonic ratios (Base: 42.6 Hz).
2. The 11.74-second tidal envelope cycle.
3. Network coherence during tectonic shifts.
""",
                    tags = "novel, lore, canon"
                ),
                WorkspaceItem(
                    id = UUID.randomUUID().toString(),
                    parentId = fieldNotesFolderId,
                    kind = "note",
                    name = "N1 Portsmouth Commissioning Telemetry",
                    text = """Site: Fort Widley / Portsdown Hill chalk alignment.
Transmitter: Piezo-acoustic array coupled to lower chalk strata.
Carrier Frequency: 85.200 Hz (Master 2x Harmonic).
Status: LOCKED (All 10 slave nodes synchronized).
Daily Energy Budget: 21.9 Wh.
Battery Buffer: 24V 15Ah LiFePO4.
Autonomy Margin: 16.4 days winter baseline.
""",
                    tags = "n1, telemetry, master",
                    isPinned = true
                )
            )
            dao.insertItems(initialItems)

            // Seed initial sample frequency resonance history entries
            val now = System.currentTimeMillis()
            val sampleHistory = listOf(
                FrequencyHistoryEntry(
                    frequencyHz = 42.600,
                    timestamp = now - (1000 * 60 * 12),
                    durationSeconds = 180,
                    isResonanceLock = true,
                    sessionNote = "Fundamental 42.6 Hz Bedrock lock. Max standing wave resonance."
                ),
                FrequencyHistoryEntry(
                    frequencyHz = 42.598,
                    timestamp = now - (1000 * 60 * 45),
                    durationSeconds = 95,
                    isResonanceLock = true,
                    sessionNote = "Hilsea 1986 chalk alignment verification session."
                ),
                FrequencyHistoryEntry(
                    frequencyHz = 42.000,
                    timestamp = now - (1000 * 60 * 120),
                    durationSeconds = 60,
                    isResonanceLock = false,
                    sessionNote = "Sub-band sweep baseline calibration."
                ),
                FrequencyHistoryEntry(
                    frequencyHz = 43.000,
                    timestamp = now - (1000 * 60 * 240),
                    durationSeconds = 110,
                    isResonanceLock = false,
                    sessionNote = "Tesla 1899 Colorado terrestrial resonance comparative note."
                )
            )
            sampleHistory.forEach { historyDao.insertEntry(it) }

            // Seed default frequency presets
            if (presetDao.getCount() == 0) {
                presetDao.insertPresets(FrequencyPresetRepository.DEFAULT_PRESETS)
            }
        }
    }
}
