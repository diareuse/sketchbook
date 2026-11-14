package lib.sketchbook

import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.toggleable
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import lib.sketchbook.theme.Theme

private const val DisabledLabelAlpha = .38f

@Composable
internal fun ToggleRow(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    enabled: Boolean,
    interactionSource: MutableInteractionSource,
    modifier: Modifier,
    control: @Composable () -> Unit,
    label: @Composable () -> Unit
) = LabeledRow(
    modifier = modifier.toggleable(
        value = checked,
        onValueChange = onCheckedChange,
        enabled = enabled,
        role = Role.Checkbox,
        interactionSource = interactionSource,
        indication = LocalIndication.current
    ),
    enabled = enabled,
    control = control,
    label = label
)

@Composable
internal fun SelectableRow(
    selected: Boolean,
    onClick: () -> Unit,
    enabled: Boolean,
    interactionSource: MutableInteractionSource,
    modifier: Modifier,
    control: @Composable () -> Unit,
    label: @Composable () -> Unit
) = LabeledRow(
    modifier = modifier.selectable(
        selected = selected,
        onClick = onClick,
        enabled = enabled,
        role = Role.RadioButton,
        interactionSource = interactionSource,
        indication = LocalIndication.current
    ),
    enabled = enabled,
    control = control,
    label = label
)

@Composable
private fun LabeledRow(
    modifier: Modifier,
    enabled: Boolean,
    control: @Composable () -> Unit,
    label: @Composable () -> Unit
) {
    val content = Theme.color.content.background
    Row(
        modifier = modifier.minimumInteractiveComponentSize(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        control()
        Spacer(Modifier.width(4.dp))
        CompositionLocalProvider(
            LocalContentColor provides if (enabled) content else content.copy(DisabledLabelAlpha)
        ) {
            label()
        }
    }
}
