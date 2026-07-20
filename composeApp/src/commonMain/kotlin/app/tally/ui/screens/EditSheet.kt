package app.tally.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.paneTitle
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import app.tally.domain.model.BillingCycle
import app.tally.money.Currencies
import app.tally.ui.components.ButtonVariant
import app.tally.ui.components.CategoryGrid
import app.tally.ui.components.DateField
import app.tally.ui.components.StatusGrid
import app.tally.ui.components.TallyButton
import app.tally.ui.components.TallyIconButton
import app.tally.ui.components.TallyIcons
import app.tally.ui.components.TallySegmentedControl
import app.tally.ui.components.TallyTextField
import app.tally.ui.theme.AppTheme
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import tally.composeapp.generated.resources.Res
import tally.composeapp.generated.resources.add_billing_label
import tally.composeapp.generated.resources.add_cadence_monthly
import tally.composeapp.generated.resources.add_cadence_yearly
import tally.composeapp.generated.resources.add_category_label
import tally.composeapp.generated.resources.add_close
import tally.composeapp.generated.resources.add_name_label
import tally.composeapp.generated.resources.add_name_placeholder
import tally.composeapp.generated.resources.add_price_label
import tally.composeapp.generated.resources.edit_delete
import tally.composeapp.generated.resources.edit_dialog_label
import tally.composeapp.generated.resources.edit_next_charge_label
import tally.composeapp.generated.resources.edit_save
import tally.composeapp.generated.resources.edit_status_label

/**
 * Modal bottom sheet for editing a subscription (FUNCTIONALITIES.md §5 / IMPLEMENTATION_PLAN.md
 * 2.5) — reachable by tapping any row on Dashboard or Upcoming. [subscriptionId] drives what's
 * loaded; [onDismiss] fires after save, after delete, or on scrim/X/back dismissal alike
 * (all three are just "close the sheet" — [EditViewModel.load] always starts from the
 * persisted record on next open, so a discarded in-progress edit never leaks forward).
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun EditSheet(
    subscriptionId: String,
    onDismiss: () -> Unit,
    viewModel: EditViewModel = koinViewModel(),
) {
    LaunchedEffect(subscriptionId) { viewModel.load(subscriptionId) }

    val uiState by viewModel.uiState.collectAsState()
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scope = rememberCoroutineScope()
    val colors = AppTheme.colors
    val typography = AppTheme.typography
    val dialogLabel = stringResource(Res.string.edit_dialog_label)
    val closeLabel = stringResource(Res.string.add_close)

    fun hideThenDismiss() {
        scope.launch { sheetState.hide() }.invokeOnCompletion { onDismiss() }
    }

    // Every field already autosaves (EditViewModel) — this only flushes a debounce still in
    // flight (e.g. dismissing a beat after typing) before the sheet actually closes, so
    // nothing typed is ever lost regardless of which of the three dismiss paths is used.
    fun closeSheet() {
        viewModel.flushAndClose(onClosed = ::hideThenDismiss)
    }

    ModalBottomSheet(
        onDismissRequest = ::closeSheet,
        sheetState = sheetState,
        containerColor = colors.bgApp,
        modifier = Modifier.semantics { paneTitle = dialogLabel },
    ) {
        when (val state = uiState) {
            EditUiState.Loading -> {
                Box(modifier = Modifier.fillMaxWidth().height(240.dp))
            }
            is EditUiState.Content -> {
                val form = state.form
                val currencySymbol = Currencies.get(state.currencyCode).symbol
                val monogramLetter = form.name.trim().firstOrNull()?.uppercase() ?: "?"

                val cadenceLabels = mapOf(
                    BillingCycle.MONTHLY to stringResource(Res.string.add_cadence_monthly),
                    BillingCycle.YEARLY to stringResource(Res.string.add_cadence_yearly),
                )

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = AppTheme.dimens.gutter)
                        .padding(bottom = 24.dp),
                    verticalArrangement = Arrangement.spacedBy(20.dp),
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .background(colors.accentSoft, RoundedCornerShape(AppTheme.dimens.radii.md)),
                            contentAlignment = Alignment.Center,
                        ) {
                            Text(
                                text = monogramLetter,
                                color = colors.accentSoftFg,
                                fontWeight = typography.weights.bold,
                                fontSize = typography.sizes.bodyLg,
                                fontFamily = typography.fontFamily,
                            )
                        }
                        Text(
                            text = form.name,
                            modifier = Modifier.weight(1f),
                            color = colors.textPrimary,
                            fontSize = typography.sizes.title,
                            fontWeight = typography.weights.bold,
                            fontFamily = typography.fontFamily,
                        )
                        TallyIconButton(
                            icon = TallyIcons.close,
                            label = closeLabel,
                            onClick = ::closeSheet,
                        )
                    }

                    TallyTextField(
                        value = form.name,
                        onValueChange = viewModel::setName,
                        label = stringResource(Res.string.add_name_label),
                        placeholder = stringResource(Res.string.add_name_placeholder),
                        leadingIcon = TallyIcons.tag,
                    )

                    TallyTextField(
                        value = form.priceInput,
                        onValueChange = viewModel::setPriceInput,
                        label = stringResource(Res.string.add_price_label),
                        placeholder = "0.00",
                        prefix = currencySymbol,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    )

                    // A full-width segmented control (not a cramped inline toggle) so the
                    // monthly/yearly choice reads as an obvious tap target on its own, not
                    // fine print next to price — most people don't read small labels closely.
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            text = stringResource(Res.string.add_billing_label),
                            color = colors.textSecondary,
                            fontSize = typography.sizes.callout,
                            fontWeight = typography.weights.semibold,
                            fontFamily = typography.fontFamily,
                        )
                        TallySegmentedControl(
                            options = listOf(BillingCycle.MONTHLY, BillingCycle.YEARLY),
                            selected = form.billingCycle,
                            onSelect = viewModel::setBillingCycle,
                            labelOf = { cadenceLabels.getValue(it) },
                        )
                    }

                    DateField(
                        label = stringResource(Res.string.edit_next_charge_label),
                        value = form.nextBillingDate,
                        placeholder = "",
                        onDateSelected = viewModel::setNextBillingDate,
                    )

                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            text = stringResource(Res.string.add_category_label),
                            color = colors.textSecondary,
                            fontSize = typography.sizes.callout,
                            fontWeight = typography.weights.semibold,
                            fontFamily = typography.fontFamily,
                        )
                        CategoryGrid(selected = form.category, onSelect = viewModel::setCategory)
                    }

                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            text = stringResource(Res.string.edit_status_label),
                            color = colors.textSecondary,
                            fontSize = typography.sizes.callout,
                            fontWeight = typography.weights.semibold,
                            fontFamily = typography.fontFamily,
                        )
                        StatusGrid(selected = form.status, onSelect = viewModel::setStatus)
                    }

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        TallyButton(
                            text = stringResource(Res.string.edit_delete),
                            onClick = { viewModel.delete(onDeleted = ::hideThenDismiss) },
                            variant = ButtonVariant.DANGER,
                            iconLeft = TallyIcons.trash2,
                        )
                        // Every field already autosaves — this just flushes+closes, same as X.
                        TallyButton(
                            text = stringResource(Res.string.edit_save),
                            onClick = ::closeSheet,
                            variant = ButtonVariant.PRIMARY,
                            fullWidth = true,
                            iconLeft = TallyIcons.check,
                            modifier = Modifier.weight(1f),
                        )
                    }
                }
            }
        }
    }
}
