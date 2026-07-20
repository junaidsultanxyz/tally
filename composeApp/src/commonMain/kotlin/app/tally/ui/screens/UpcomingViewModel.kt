package app.tally.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.tally.data.repository.SubscriptionRepository
import app.tally.data.settings.SettingsRepository
import app.tally.domain.model.Status
import app.tally.domain.model.Subscription
import app.tally.domain.usecase.daysUntil
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn
import kotlin.time.Clock

public enum class UpcomingFilter { WEEK, MONTH, ALL }

public sealed interface UpcomingUiState {
    public data object Loading : UpcomingUiState
    public data class Content(
        val filter: UpcomingFilter,
        val thisWeek: List<Subscription>,
        val nextWeek: List<Subscription>,
        val laterThisMonth: List<Subscription>,
        val activeCurrency: String,
    ) : UpcomingUiState {
        val isEmpty: Boolean get() = thisWeek.isEmpty() && nextWeek.isEmpty() && laterThisMonth.isEmpty()
    }
}

public class UpcomingViewModel(
    private val subscriptionRepository: SubscriptionRepository,
    private val settingsRepository: SettingsRepository,
) : ViewModel() {

    private val filter = MutableStateFlow(UpcomingFilter.WEEK)

    public val uiState: StateFlow<UpcomingUiState> = combine(
        subscriptionRepository.observeAll(),
        settingsRepository.activeCurrency,
        filter,
    ) { subscriptions, currency, f -> Triple(subscriptions, currency, f) }
        .map { (subscriptions, currency, f) -> buildState(subscriptions, currency, f) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), UpcomingUiState.Loading)

    private fun buildState(subscriptions: List<Subscription>, currency: String, filter: UpcomingFilter): UpcomingUiState.Content {
        val today = Clock.System.todayIn(TimeZone.currentSystemDefault())
        fun days(sub: Subscription) = daysUntil(sub.nextBillingDate, sub.status, today)

        // Paused always excluded — in every filter (AGENTS.md §11).
        val nonPaused = subscriptions.filter { it.status != Status.PAUSED }
        val filtered = when (filter) {
            UpcomingFilter.WEEK -> nonPaused.filter { days(it) <= 7 }
            UpcomingFilter.MONTH -> nonPaused.filter { days(it) <= 30 }
            UpcomingFilter.ALL -> nonPaused
        }.sortedBy(::days)

        return UpcomingUiState.Content(
            filter = filter,
            thisWeek = filtered.filter { days(it) <= 7 },
            nextWeek = filtered.filter { days(it) in 8..14 },
            laterThisMonth = filtered.filter { days(it) > 14 },
            activeCurrency = currency,
        )
    }

    public fun setFilter(newFilter: UpcomingFilter) {
        filter.value = newFilter
    }

    public fun deleteSubscription(id: String) {
        viewModelScope.launch { subscriptionRepository.delete(id) }
    }
}
