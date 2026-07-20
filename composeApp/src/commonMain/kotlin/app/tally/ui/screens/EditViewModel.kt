package app.tally.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.tally.data.repository.SubscriptionRepository
import app.tally.domain.model.BillingCycle
import app.tally.domain.model.Category
import app.tally.domain.model.Status
import app.tally.domain.model.Subscription
import app.tally.money.moneyInputString
import app.tally.money.parseMoney
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate

public data class EditFormState(
    val name: String,
    val priceInput: String,
    val category: Category,
    val billingCycle: BillingCycle,
    val nextBillingDate: LocalDate,
    val status: Status,
)

public sealed interface EditUiState {
    public data object Loading : EditUiState
    public data class Content(val form: EditFormState, val currencyCode: String) : EditUiState
}

private const val AUTOSAVE_DEBOUNCE_MS = 500L

/**
 * Not [androidx.lifecycle.viewmodel.compose.viewModel]-scoped per subscription — this is a
 * single shared instance (Koin default) reused across every Edit-sheet open. [load] always
 * re-reads from [subscriptionRepository] and replaces [form] wholesale, so switching which
 * subscription is being edited (or reopening the sheet after a discarded edit) never leaks
 * stale in-progress field values from a previous open.
 *
 * Every field edit autosaves — no explicit "Save" step is required (offline-first: writes
 * happen as they occur, AGENTS.md §4). Text fields ([setName]/[setPriceInput]) debounce so
 * every keystroke doesn't hit Room; picker selections ([setCategory]/[setNextBillingDate]/
 * [setStatus]) persist immediately since they're already one tap each. [flushAndClose]
 * cancels any pending debounce and writes synchronously before the sheet closes, so a
 * dismissal a moment after typing never loses the last few characters.
 */
public class EditViewModel(private val subscriptionRepository: SubscriptionRepository) : ViewModel() {

    private var original: Subscription? = null
    private val form = MutableStateFlow<EditFormState?>(null)
    private val currencyCode = MutableStateFlow("USD")
    private var debouncedSaveJob: Job? = null

    public val uiState: StateFlow<EditUiState> = form.map { f ->
        if (f == null) EditUiState.Loading else EditUiState.Content(f, currencyCode.value)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), EditUiState.Loading)

    public fun load(id: String) {
        debouncedSaveJob?.cancel()
        form.value = null
        viewModelScope.launch {
            val subscription = subscriptionRepository.observeById(id).first() ?: return@launch
            original = subscription
            currencyCode.value = subscription.currencyCode
            form.value = EditFormState(
                name = subscription.name,
                priceInput = moneyInputString(subscription.amountMinor, subscription.currencyCode),
                category = subscription.category,
                billingCycle = subscription.billingCycle,
                nextBillingDate = subscription.nextBillingDate,
                status = subscription.status,
            )
        }
    }

    public fun setName(name: String) {
        form.update { it?.copy(name = name) }
        scheduleAutosave()
    }

    /** Strips `-` — prices are never negative in this domain (see [app.tally.domain.model.Money.divInto]'s doc). */
    public fun setPriceInput(input: String) {
        form.update { it?.copy(priceInput = input.filter { c -> c != '-' }) }
        scheduleAutosave()
    }

    public fun setCategory(category: Category) {
        form.update { it?.copy(category = category) }
        persistNow()
    }

    public fun setBillingCycle(cycle: BillingCycle) {
        form.update { it?.copy(billingCycle = cycle) }
        persistNow()
    }

    public fun setNextBillingDate(date: LocalDate) {
        form.update { it?.copy(nextBillingDate = date) }
        persistNow()
    }

    public fun setStatus(status: Status) {
        form.update { it?.copy(status = status) }
        persistNow()
    }

    public fun delete(onDeleted: () -> Unit) {
        debouncedSaveJob?.cancel()
        val base = original ?: return
        viewModelScope.launch {
            subscriptionRepository.delete(base.id)
            onDeleted()
        }
    }

    /** Cancels any pending debounce and writes the current form state before the sheet closes. */
    public fun flushAndClose(onClosed: () -> Unit) {
        debouncedSaveJob?.cancel()
        val base = original
        val current = form.value
        if (base == null || current == null) {
            onClosed()
            return
        }
        viewModelScope.launch {
            persist(base, current)
            onClosed()
        }
    }

    private fun scheduleAutosave() {
        debouncedSaveJob?.cancel()
        debouncedSaveJob = viewModelScope.launch {
            delay(AUTOSAVE_DEBOUNCE_MS)
            val base = original ?: return@launch
            val current = form.value ?: return@launch
            persist(base, current)
        }
    }

    private fun persistNow() {
        debouncedSaveJob?.cancel()
        val base = original ?: return
        val current = form.value ?: return
        viewModelScope.launch { persist(base, current) }
    }

    private suspend fun persist(base: Subscription, current: EditFormState) {
        subscriptionRepository.update(
            base.copy(
                // Rename to empty keeps the previous name (FUNCTIONALITIES.md §14).
                name = current.name.trim().ifEmpty { base.name },
                amountMinor = parseMoney(current.priceInput, base.currencyCode),
                category = current.category,
                billingCycle = current.billingCycle,
                nextBillingDate = current.nextBillingDate,
                status = current.status,
            ),
        )
    }
}
