package app.tally.platform

/** WorkManager-backed daily reminder job + NotificationManager — wired in Phase 3. */
actual class Notifier {
    actual suspend fun schedule(reminder: PendingReminder) = Unit

    actual suspend fun cancel(reminderId: String) = Unit

    actual suspend fun cancelAll() = Unit
}

actual class NotificationPermissionController {
    actual suspend fun status(): PermissionStatus = PermissionStatus.NOT_DETERMINED

    actual suspend fun request(): PermissionStatus = PermissionStatus.NOT_DETERMINED

    actual fun openAppNotificationSettings() = Unit

    actual fun openExactAlarmSettings() = Unit
}
