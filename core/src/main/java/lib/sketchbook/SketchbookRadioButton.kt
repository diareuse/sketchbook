package lib.sketchbook

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonColors
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import lib.sketchbook.theme.Theme

private const val DisabledAlpha = .38f

@Composable
internal fun radioButtonColors(): RadioButtonColors {
    val disabled = Theme.color.content.background.copy(DisabledAlpha)
    return RadioButtonDefaults.colors(
        selectedColor = Theme.color.emphasis.primary,
        unselectedColor = Theme.color.content.outline,
        disabledSelectedColor = disabled,
        disabledUnselectedColor = disabled
    )
}

@Composable
fun SketchbookRadioButton(
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    label: (@Composable () -> Unit)? = null,
    enabled: Boolean = true,
    colors: RadioButtonColors = radioButtonColors(),
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() }
) {
    if (label == null) {
        val control = modifier.pressControl(CircleShape, interactionSource)
        ThemePressHost {
            RadioButton(
                selected = selected,
                onClick = onClick,
                modifier = control,
                enabled = enabled,
                colors = colors,
                interactionSource = interactionSource
            )
        }
    } else {
        SelectableRow(
            selected = selected,
            onClick = onClick,
            enabled = enabled,
            interactionSource = interactionSource,
            modifier = modifier,
            control = {
                RadioButton(
                    selected = selected,
                    onClick = null,
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
private fun SketchbookRadioButtonPreview() = SketchbookPreviewLayout {
    Column {
        SketchbookRadioButton(true, {}, label = { Text("Selected") })
        SketchbookRadioButton(false, {}, label = { Text("Unselected") })
        SketchbookRadioButton(true, {}, label = { Text("Disabled") }, enabled = false)
        SketchbookRadioButton(false, {})
    }
}
