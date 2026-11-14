package lib.sketchbook

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxColors
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import lib.sketchbook.theme.Theme

private const val DisabledAlpha = .38f

@Composable
internal fun checkboxColors(): CheckboxColors {
    val disabled = Theme.color.content.background.copy(DisabledAlpha)
    return CheckboxDefaults.colors(
        checkedColor = Theme.color.emphasis.primary,
        uncheckedColor = Theme.color.content.outline,
        checkmarkColor = Theme.color.overlay.primary,
        disabledCheckedColor = disabled,
        disabledUncheckedColor = disabled,
        disabledIndeterminateColor = disabled
    )
}

@Composable
fun SketchbookCheckbox(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    label: (@Composable () -> Unit)? = null,
    enabled: Boolean = true,
    colors: CheckboxColors = checkboxColors(),
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() }
) {
    if (label == null) {
        val control = modifier.pressControl(CircleShape, interactionSource)
        ThemePressHost {
            Checkbox(
                checked = checked,
                onCheckedChange = onCheckedChange,
                modifier = control,
                enabled = enabled,
                colors = colors,
                interactionSource = interactionSource
            )
        }
    } else {
        ToggleRow(
            checked = checked,
            onCheckedChange = onCheckedChange,
            enabled = enabled,
            interactionSource = interactionSource,
            modifier = modifier,
            control = {
                Checkbox(
                    checked = checked,
                    onCheckedChange = null,
                    enabled = enabled,
                    colors = colors,
                    interactionSource = interactionSource
                )
            },
            label = label
        )
    }
}

@SketchbookComponentPreview
@SketchbookValidation
@Composable
private fun SketchbookCheckboxPreview() = SketchbookPreviewLayout {
    Column {
        SketchbookCheckbox(true, {}, label = { Text("Checked") })
        SketchbookCheckbox(false, {}, label = { Text("Unchecked") })
        SketchbookCheckbox(true, {}, label = { Text("Disabled") }, enabled = false)
        SketchbookCheckbox(false, {})
    }
}
