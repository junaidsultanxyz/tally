package app.tally.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.tally.data.repository.SubscriptionRepository
import app.tally.data.settings.SettingsRepository
import app.tally.domain.model.Status
import app.tally.domain.model.Subscription
import app.tally.domain.usecase.daysUntil
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn
import kotlin.time.Clock

public sealed interface AlertsUiState {
    public data object Loading : AlertsUiState
    public data object Empty : AlertsUiState
    public data class Content(val reminders: List<Subscription>, val leadDays: Int) : AlertsUiState
}

/**
 * Derived from actual subscriptions, not a hardcoded array like the kit (IMPLEMENTATION_PLAN.md
 * 2.6). Every non-paused subscription appears here regardless of `reminderEnabled` — matching
 * the kit's own alerts array, which keeps an `on:false` entry (Disney+) in the list rather
 * than dropping it. Toggling a reminder off must only flip that row's switch, never remove
 * the row: an earlier version filtered to `reminderEnabled == true` here, which made a row
 * vanish the instant its switch was turned off — with no way to turn it back on, since the
 * only control for it had just disappeared. Paused subscriptions never renew, so they're
 * excluded the same way Upcoming excludes them (AGENTS.md §11).
 */
public class AlertsViewModel(
    private val subscriptionRepository: SubscriptionRepository,
    private val settingsRepository: SettingsRepository,
) : ViewModel() {

    public val uiState: StateFlow<AlertsUiState> = combine(
        subscriptionRepository.observeAll(),
        settingsRepository.defaultReminderLeadDays,
    ) { subscriptions, leadDays -> subscriptions to leadDays }
        .map { (subscriptions, leadDays) -> buildState(subscriptions, leadDays) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), AlertsUiState.Loading)

    private fun buildState(subscriptions: List<Subscription>, leadDays: Int): AlertsUiState {
        val today = Clock.System.todayIn(TimeZone.currentSystemDefault())
        val reminders = subscriptions
            .filter { it.status != Status.PAUSED }
            .sortedBy { daysUntil(it.nextBillingDate, it.status, today) }
        return if (reminders.isEmpty()) AlertsUiState.Empty else AlertsUiState.Content(reminders, leadDays)
    }

    public fun setReminderEnabled(id: String, enabled: Boolean) {
        viewModelScope.launch {
            val subscription = subscriptionRepository.observeById(id).first() ?: return@launch
            subscriptionRepository.update(subscription.copy(reminderEnabled = enabled))
        }
    }
}
