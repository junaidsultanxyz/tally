package app.tally.platform

/** Android Keystore / iOS Keychain-backed token storage. Never DataStore, never plain files. */
expect class SecureStore {
    suspend fun put(key: String, value: String)
    suspend fun get(key: String): String?
    suspend fun remove(key: String)
}
