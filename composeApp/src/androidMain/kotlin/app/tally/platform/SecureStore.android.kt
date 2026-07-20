package app.tally.platform

/** Android Keystore-backed encrypted storage — wired in Phase 5. */
actual class SecureStore {
    actual suspend fun put(key: String, value: String) = Unit

    actual suspend fun get(key: String): String? = null

    actual suspend fun remove(key: String) = Unit
}
