package app.tally.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import app.tally.domain.model.BillingCycle
import app.tally.money.Currencies
import app.tally.ui.components.ButtonVariant
import app.tally.ui.components.CategoryGrid
import app.tally.ui.components.DateField
import app.tally.ui.components.TallyButton
import app.tally.ui.components.TallyIconButton
import app.tally.ui.components.TallyIcons
import app.tally.ui.components.TallySegmentedControl
import app.tally.ui.components.TallySwitch
import app.tally.ui.components.TallyTextField
import app.tally.ui.theme.AppTheme
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import tally.composeapp.generated.resources.Res
import tally.composeapp.generated.resources.add_billing_label
import tally.composeapp.generated.resources.add_cadence_monthly
import tally.composeapp.generated.resources.add_cadence_yearly
import tally.composeapp.generated.resources.add_category_label
import tally.composeapp.generated.resources.add_close
import tally.composeapp.generated.resources.add_first_billing_date_error_required
import tally.composeapp.generated.resources.add_first_billing_date_label
import tally.composeapp.generated.resources.add_first_billing_date_placeholder
import tally.composeapp.generated.resources.add_greeting
import tally.composeapp.generated.resources.add_name_error_required
import tally.composeapp.generated.resources.add_name_label
import tally.composeapp.generated.resources.add_name_placeholder
import tally.composeapp.generated.resources.add_price_label
import tally.composeapp.generated.resources.add_reminder_caption
import tally.composeapp.generated.resources.add_reminder_label
import tally.composeapp.generated.resources.add_submit
import tally.composeapp.generated.resources.add_title

/**
 * Add-subscription form (AGENTS.md §4 / FUNCTIONALITIES.md §4 / IMPLEMENTATION_PLAN.md 2.4).
 * [onClose] and [onSubmitted] both return to Dashboard — [onSubmitted] only fires after a
 * successful (validated + persisted) save, [onClose] discards whatever's in the form.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun AddScreen(
    onClose: () -> Unit = {},
    onSubmitted: () -> Unit = {},
    viewModel: AddViewModel = koinViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()
    val colors = AppTheme.colors
    val typography = AppTheme.typography
    val form = uiState.form
    val currencySymbol = Currencies.get(uiState.activeCurrency).symbol

    val nameError = if (form.showNameError) stringResource(Res.string.add_name_error_required) else null
    val dateError = if (form.showDateError) stringResource(Res.string.add_first_billing_date_error_required) else null
    val datePlaceholder = stringResource(Res.string.add_first_billing_date_placeholder)
    val dateFieldLabel = stringResource(Res.string.add_first_billing_date_label)
    val cadenceLabels = mapOf(
        BillingCycle.MONTHLY to stringResource(Res.string.add_cadence_monthly),
        BillingCycle.YEARLY to stringResource(Res.string.add_cadence_yearly),
    )
    val reminderCaption = stringResource(Res.string.add_reminder_caption, uiState.reminderLeadDays)

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(AppTheme.dimens.gutter),
        verticalArrangement = Arrangement.spacedBy(20.dp),
    ) {
        item {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = stringResource(Res.string.add_greeting),
                        color = colors.textTertiary,
                        fontSize = typography.sizes.callout,
                        fontFamily = typography.fontFamily,
                    )
                    Text(
                        text = stringResource(Res.string.add_title),
                        color = colors.textPrimary,
                        fontSize = typography.sizes.title,
                        fontWeight = typography.weights.bold,
                        fontFamily = typography.fontFamily,
                    )
                }
                TallyIconButton(
                    icon = TallyIcons.close,
                    label = stringResource(Res.string.add_close),
                    onClick = onClose,
                )
            }
        }

        item {
            TallyTextField(
                value = form.name,
                onValueChange = viewModel::setName,
                label = stringResource(Res.string.add_name_label),
                placeholder = stringResource(Res.string.add_name_placeholder),
                leadingIcon = TallyIcons.tag,
                error = nameError,
            )
        }

        item {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                TallyTextField(
                    value = form.priceInput,
                    onValueChange = viewModel::setPriceInput,
                    modifier = Modifier.weight(0.72f),
                    label = stringResource(Res.string.add_price_label),
                    placeholder = "0.00",
                    prefix = currencySymbol,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                )
                Column(modifier = Modifier.weight(1.28f), verticalArrangement = Arrangement.spacedBy(6.dp)) {
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
            }
        }

        item {
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
        }

        item {
            DateField(
                label = dateFieldLabel,
                value = form.firstBillingDate,
                placeholder = datePlaceholder,
                error = dateError,
                onDateSelected = viewModel::setFirstBillingDate,
            )
        }

        item {
            TallySwitch(
                checked = form.reminderEnabled,
                onCheckedChange = viewModel::setReminderEnabled,
                label = stringResource(Res.string.add_reminder_label),
                description = reminderCaption,
            )
        }

        item {
            TallyButton(
                text = stringResource(Res.string.add_submit),
                onClick = { viewModel.submit(onSaved = onSubmitted) },
                variant = ButtonVariant.PRIMARY,
                fullWidth = true,
                iconLeft = TallyIcons.check,
            )
        }
    }
}
