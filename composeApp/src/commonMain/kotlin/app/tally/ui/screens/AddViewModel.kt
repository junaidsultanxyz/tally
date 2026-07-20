package app.tally.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.tally.data.repository.SubscriptionRepository
import app.tally.data.settings.SettingsRepository
import app.tally.domain.model.BillingCycle
import app.tally.domain.model.Category
import app.tally.domain.model.Status
import app.tally.domain.model.Subscription
import app.tally.money.parseMoney
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate
import kotlin.time.Clock
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

/** User-edited fields only — reactive settings (currency, lead days) live in [AddUiState]. */
public data class AddFormState(
    val name: String = "",
    val priceInput: String = "",
    val billingCycle: BillingCycle = BillingCycle.MONTHLY,
    val category: Category = Category.ENTERTAINMENT, // kit's cat-grid default (index.html: `cat on` on Entertainment).
    val firstBillingDate: LocalDate? = null,
    val reminderEnabled: Boolean = true,
    val showNameError: Boolean = false,
    val showDateError: Boolean = false,
)

public data class AddUiState(
    val form: AddFormState,
    val activeCurrency: String,
    val reminderLeadDays: Int,
)

public class AddViewModel(
    private val subscriptionRepository: SubscriptionRepository,
    private val settingsRepository: SettingsRepository,
) : ViewModel() {

    private val form = MutableStateFlow(AddFormState())

    public val uiState: StateFlow<AddUiState> = combine(
        form,
        settingsRepository.activeCurrency,
        settingsRepository.defaultReminderLeadDays,
    ) { f, currency, leadDays -> AddUiState(f, currency, leadDays) }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            AddUiState(AddFormState(), activeCurrency = "USD", reminderLeadDays = 3),
        )

    public fun setName(name: String) {
        form.update { it.copy(name = name, showNameError = false) }
    }

    /** Strips `-` — prices are never negative in this domain (see [app.tally.domain.model.Money.divInto]'s doc). */
    public fun setPriceInput(input: String) {
        form.update { it.copy(priceInput = input.filter { c -> c != '-' }) }
    }

    public fun setBillingCycle(cycle: BillingCycle) {
        form.update { it.copy(billingCycle = cycle) }
    }

    public fun setCategory(category: Category) {
        form.update { it.copy(category = category) }
    }

    public fun setFirstBillingDate(date: LocalDate) {
        form.update { it.copy(firstBillingDate = date, showDateError = false) }
    }

    public fun setReminderEnabled(enabled: Boolean) {
        form.update { it.copy(reminderEnabled = enabled) }
    }

    /** Validates, persists, and invokes [onSaved] — only on success, so the caller only navigates away then. */
    @OptIn(ExperimentalUuidApi::class)
    public fun submit(onSaved: () -> Unit) {
        val current = form.value
        val nameValid = current.name.isNotBlank()
        val dateValid = current.firstBillingDate != null
        if (!nameValid || !dateValid) {
            form.update { it.copy(showNameError = !nameValid, showDateError = !dateValid) }
            return
        }

        val date = current.firstBillingDate
        viewModelScope.launch {
            val currency = settingsRepository.activeCurrency.first()
            val leadDays = settingsRepository.defaultReminderLeadDays.first()
            val reminderTime = settingsRepository.defaultReminderTime.first()
            subscriptionRepository.add(
                Subscription(
                    id = Uuid.random().toString(),
                    name = current.name.trim(),
                    category = current.category,
                    amountMinor = parseMoney(current.priceInput, currency),
                    currencyCode = currency,
                    billingCycle = current.billingCycle,
                    firstBillingDate = date,
                    nextBillingDate = date,
                    status = Status.ACTIVE, // Add form has no status field (kit parity) — Edit sheet is where status changes.
                    reminderEnabled = current.reminderEnabled,
                    reminderLeadDays = leadDays,
                    reminderTime = reminderTime,
                    updatedAt = Clock.System.now(),
                ),
            )
            // TODO(Phase 3): call ScheduleReminder(subscription) here once local notification
            // scheduling exists, so a reminder-enabled sub gets a WorkManager-backed alert
            // immediately instead of waiting for the next periodic reconciliation.
            form.value = AddFormState()
            onSaved()
        }
    }
}
