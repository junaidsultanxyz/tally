package app.tally.platform

/** GoogleSignIn SDK (auth) + incremental authorization (drive.appdata scope) — wired in Phase 5. */
actual class AuthProvider {
    actual suspend fun signIn(): AuthResult =
        AuthResult.Failure("Not implemented until Phase 5")

    actual suspend fun authorizeDrive(): AuthResult =
        AuthResult.Failure("Not implemented until Phase 5")

    actual suspend fun signOut() = Unit

    actual suspend fun revoke() = Unit
}
