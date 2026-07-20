package app.tally.data.settings

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import okio.Path.Companion.toPath

/** Android actual uses `applicationContext.filesDir`, iOS actual uses `NSDocumentDirectory` — same split as `data/local/DatabaseBuilder.kt`. */
public expect fun dataStorePath(): String

public fun createSettingsDataStore(): DataStore<Preferences> =
    PreferenceDataStoreFactory.createWithPath(produceFile = { dataStorePath().toPath() })
