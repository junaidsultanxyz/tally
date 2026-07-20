package app.tally.data.local

import androidx.room.RoomDatabase
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import kotlinx.coroutines.Dispatchers

/** Android actual uses `applicationContext.getDatabasePath`, iOS actual uses `NSDocumentDirectory` (AGENTS.md §17). */
public expect fun databaseBuilder(): RoomDatabase.Builder<TallyDatabase>

public fun createTallyDatabase(): TallyDatabase =
    databaseBuilder()
        .setDriver(BundledSQLiteDriver())
        // Dispatchers.IO is JVM-only (internal on Kotlin/Native) — Default is the
        // standard KMP-safe choice here; Room's own multiplatform samples use it too.
        .setQueryCoroutineContext(Dispatchers.Default)
        // No shipped user data yet (pre-release) — destructive fallback instead of
        // hand-written Migrations for now. Revisit before Phase 6 (AGENTS.md §17).
        .fallbackToDestructiveMigration(dropAllTables = true)
        .build()
