package app.tally.platform

/** Local-only reminder scheduling — see AGENTS.md §13. Implemented in Phase 3. */
expect class Notifier {
    suspend fun schedule(reminder: PendingReminder)
    suspend fun cancel(reminderId: String)
    suspend fun cancelAll()
}

expect class NotificationPermissionController {
    suspend fun status(): PermissionStatus
    suspend fun request(): PermissionStatus
    fun openAppNotificationSettings()
    fun openExactAlarmSettings()
}

enum class PermissionStatus { GRANTED, DENIED, NOT_DETERMINED }

data class PendingReminder(
    val id: String,
    val subscriptionId: String,
    val fireAtEpochMillis: Long,
    val title: String,
    val body: String,
)
