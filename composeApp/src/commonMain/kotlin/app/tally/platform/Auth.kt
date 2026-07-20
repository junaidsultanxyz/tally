package app.tally.platform

/** Split auth/authorization flow — see AGENTS.md §16. Implemented in Phase 5. */
expect class AuthProvider {
    suspend fun signIn(): AuthResult
    suspend fun authorizeDrive(): AuthResult
    suspend fun signOut()
    suspend fun revoke()
}

sealed interface AuthResult {
    data class Success(val accountEmail: String, val displayName: String?) : AuthResult
    data class Failure(val message: String) : AuthResult
    data object Cancelled : AuthResult
}
