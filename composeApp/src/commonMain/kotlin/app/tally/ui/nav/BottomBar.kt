package app.tally.ui.nav

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import app.tally.ui.components.TallyIcons
import app.tally.ui.components.focusRing
import app.tally.ui.theme.AppTheme
import app.tally.ui.theme.TallyElevation
import app.tally.ui.theme.TallyShadowLevel
import app.tally.ui.theme.tallyShadow
import org.jetbrains.compose.resources.stringResource
import tally.composeapp.generated.resources.Res
import tally.composeapp.generated.resources.nav_add
import tally.composeapp.generated.resources.nav_alerts
import tally.composeapp.generated.resources.nav_home
import tally.composeapp.generated.resources.nav_settings
import tally.composeapp.generated.resources.nav_upcoming

private val fabSize = 56.dp
private val barHeight = 64.dp

/**
 * Sticky bottom bar: Home, Upcoming, center raised **+** FAB, Alerts, Settings
 * (AGENTS.md §3). The FAB visually floats above the bar's top edge rather
 * than sitting inline with the other four tabs.
 */
@Composable
internal fun TallyBottomBar(currentRoute: Route, onNavigate: (Route) -> Unit, modifier: Modifier = Modifier) {
    val colors = AppTheme.colors

    Box(modifier = modifier.fillMaxWidth()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .tallyShadow(TallyShadowLevel.MD, TallyElevation(isDark = colors.isDark))
                .background(colors.bgSurface)
                // No fixed height on this outer container — windowInsetsPadding below adds
                // bottom padding equal to the system nav bar inset (3-button nav: tall;
                // gesture nav: thin/zero), and the container grows to fit it. The inner
                // Row keeps a fixed barHeight regardless, so the tap targets are always the
                // same visible size on both navigation styles — only the padding beneath
                // them differs. (Previously windowInsetsPadding was applied to a box that
                // ALSO had a fixed .height(), which squeezed the inset out of the content
                // area instead of adding to it — badly cramped tabs on 3-button-nav devices.)
                .windowInsetsPadding(WindowInsets.navigationBars),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth().height(barHeight),
                horizontalArrangement = Arrangement.SpaceEvenly,
            ) {
                BottomBarTab(TallyIcons.home, stringResource(Res.string.nav_home), currentRoute == Route.Dashboard) {
                    onNavigate(Route.Dashboard)
                }
                BottomBarTab(TallyIcons.calendar, stringResource(Res.string.nav_upcoming), currentRoute == Route.Upcoming) {
                    onNavigate(Route.Upcoming)
                }
                // Empty slot the FAB floats above — keeps the other four tabs evenly spaced.
                Box(modifier = Modifier.sizeIn(minWidth = AppTheme.dimens.tapMin, minHeight = AppTheme.dimens.tapMin))
                BottomBarTab(TallyIcons.bell, stringResource(Res.string.nav_alerts), currentRoute == Route.Alerts) {
                    onNavigate(Route.Alerts)
                }
                BottomBarTab(TallyIcons.settings, stringResource(Res.string.nav_settings), currentRoute == Route.Settings) {
                    onNavigate(Route.Settings)
                }
            }
        }

        val fabLabel = stringResource(Res.string.nav_add)
        val fabInteractionSource = remember { MutableInteractionSource() }
        Box(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .offset(y = -(fabSize / 2))
                .size(fabSize)
                .tallyShadow(TallyShadowLevel.LG, TallyElevation(isDark = colors.isDark), CircleShape)
                .clip(CircleShape)
                .background(colors.accent, CircleShape)
                .clickable(
                    interactionSource = fabInteractionSource,
                    indication = null,
                    onClick = { onNavigate(Route.Add) },
                )
                .focusRing(cornerRadius = fabSize / 2),
            contentAlignment = Alignment.Center,
        ) {
            Icon(imageVector = TallyIcons.plus, contentDescription = fabLabel, tint = colors.textOnAccent)
        }
    }
}

@Composable
private fun BottomBarTab(icon: ImageVector, label: String, active: Boolean, onClick: () -> Unit) {
    val colors = AppTheme.colors
    val tint = if (active) colors.accent else colors.textTertiary
    val interactionSource = remember { MutableInteractionSource() }

    Column(
        modifier = Modifier
            .sizeIn(minWidth = AppTheme.dimens.tapMin, minHeight = AppTheme.dimens.tapMin)
            .clip(RoundedCornerShape(AppTheme.dimens.radii.sm))
            .clickable(interactionSource = interactionSource, indication = null, onClick = onClick)
            .focusRing(cornerRadius = AppTheme.dimens.radii.sm)
            .padding(vertical = 6.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(2.dp, Alignment.CenterVertically),
    ) {
        Icon(imageVector = icon, contentDescription = null, tint = tint)
        Text(
            text = label,
            color = tint,
            fontSize = AppTheme.typography.sizes.micro,
            fontWeight = if (active) AppTheme.typography.weights.semibold else AppTheme.typography.weights.regular,
            fontFamily = AppTheme.typography.fontFamily,
        )
    }
}
