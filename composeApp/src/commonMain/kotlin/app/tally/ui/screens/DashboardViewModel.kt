package app.tally.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.tally.data.local.MonthlySpendSnapshotDao
import app.tally.data.repository.ExchangeRateRepository
import app.tally.data.repository.SubscriptionRepository
import app.tally.data.settings.SettingsRepository
import app.tally.domain.model.Status
import app.tally.domain.model.Subscription
import app.tally.domain.usecase.MonthlyDelta
import app.tally.domain.usecase.captureMonthlySnapshot
import app.tally.domain.usecase.computeMonthlyDelta
import app.tally.domain.usecase.computeTotals
import app.tally.domain.usecase.daysUntil
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn
import kotlin.time.Clock

public enum class DashboardSortOrder { NEXT_CHARGE, PRICE_HIGH_TO_LOW, NAME_A_TO_Z }

public sealed interface DashboardUiState {
    public data object Loading : DashboardUiState
    public data object Empty : DashboardUiState
    public data class Content(
        val monthlyTotalMinor: Long,
        val annualTotalMinor: Long,
        val activeCount: Int,
        val upcomingCount: Int,
        val monthlyDelta: MonthlyDelta?,
        val renewingSoon: List<Subscription>,
        val allSubscriptions: List<Subscription>,
        val sortOrder: DashboardSortOrder,
        val activeCurrency: String,
    ) : DashboardUiState
}

public class DashboardViewModel(
    private val subscriptionRepository: SubscriptionRepository,
    private val settingsRepository: SettingsRepository,
    private val monthlySpendSnapshotDao: MonthlySpendSnapshotDao,
    private val exchangeRateRepository: ExchangeRateRepository,
) : ViewModel() {

    private val sortOrder = MutableStateFlow(DashboardSortOrder.NEXT_CHARGE)

    init {
        // Keep the current month's snapshot fresh on every Dashboard load — a lightweight
        // one-shot write, not part of the reactive uiState pipeline below (which stays read-only).
        viewModelScope.launch {
            val subscriptions = subscriptionRepository.observeAll().first()
            val currency = settingsRepository.activeCurrency.first()
            val today = Clock.System.todayIn(TimeZone.currentSystemDefault())
            val rates = resolveRates(subscriptions, currency)
            captureMonthlySnapshot(monthlySpendSnapshotDao, subscriptions, currency, today, rates)
        }
    }

    public val uiState: StateFlow<DashboardUiState> = combine(
        subscriptionRepository.observeAll(),
        settingsRepository.activeCurrency,
        sortOrder,
    ) { subscriptions, currency, sort -> Triple(subscriptions, currency, sort) }
        .map { (subscriptions, currency, sort) -> buildState(subscriptions, currency, sort) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), DashboardUiState.Loading)

    private suspend fun buildState(
        subscriptions: List<Subscription>,
        currency: String,
        sort: DashboardSortOrder,
    ): DashboardUiState {
        if (subscriptions.isEmpty()) return DashboardUiState.Empty

        val today = Clock.System.todayIn(TimeZone.currentSystemDefault())
        val rates = resolveRates(subscriptions, currency)
        val totals = computeTotals(subscriptions, currency, today, rates)
        val delta = computeMonthlyDelta(monthlySpendSnapshotDao, totals.monthlyTotalMinor, subscriptions, currency, today)

        return DashboardUiState.Content(
            monthlyTotalMinor = totals.monthlyTotalMinor,
            annualTotalMinor = totals.annualTotalMinor,
            activeCount = totals.activeCount,
            upcomingCount = totals.upcomingCount,
            monthlyDelta = delta,
            renewingSoon = totals.renewingSoon,
            allSubscriptions = sortSubscriptions(subscriptions, sort, today),
            sortOrder = sort,
            activeCurrency = currency,
        )
    }

    /**
     * One rate lookup per distinct foreign currency actually present — not the whole
     * 30-currency table — and [ExchangeRateRepository] itself collapses that into at most one
     * network call per base currency (TTL-cached). A currency [ExchangeRateRepository] can't
     * resolve (offline, never cached) is just absent from the returned map; `computeTotals`
     * already treats a missing rate as "exclude this currency from the total," the same
     * graceful degradation it had before conversion existed at all.
     */
    private suspend fun resolveRates(subscriptions: List<Subscription>, activeCurrency: String): Map<String, Double> {
        val foreignCurrencies = subscriptions
            .asSequence()
            .filter { it.status != Status.PAUSED && it.currencyCode != activeCurrency }
            .map { it.currencyCode }
            .distinct()
            .toList()
        if (foreignCurrencies.isEmpty()) return emptyMap()
        return foreignCurrencies.mapNotNull { currency ->
            exchangeRateRepository.getRate(currency, activeCurrency)?.let { rate -> currency to rate }
        }.toMap()
    }

    public fun setSortOrder(order: DashboardSortOrder) {
        sortOrder.value = order
    }

    public fun deleteSubscription(id: String) {
        viewModelScope.launch { subscriptionRepository.delete(id) }
    }
}

private fun sortSubscriptions(subscriptions: List<Subscription>, sort: DashboardSortOrder, today: LocalDate): List<Subscription> =
    when (sort) {
        DashboardSortOrder.NEXT_CHARGE -> subscriptions.sortedBy { daysUntil(it.nextBillingDate, it.status, today) }
        DashboardSortOrder.PRICE_HIGH_TO_LOW -> subscriptions.sortedByDescending { it.amountMinor }
        DashboardSortOrder.NAME_A_TO_Z -> subscriptions.sortedBy { it.name.lowercase() }
    }
