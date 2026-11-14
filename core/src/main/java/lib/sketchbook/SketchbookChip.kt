package lib.sketchbook

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp
import lib.sketchbook.theme.Theme
import lib.sketchbook.theme.contentColorFor

private const val ChipOverlayAlpha = .06f
private const val SelectedAlpha = .28f
private const val DisabledAlpha = .38f
private const val DisabledBorderAlpha = .12f

@Composable
private fun chipBorder(enabled: Boolean, selected: Boolean): BorderStroke {
    val outline = Theme.color.content.outline
    val primary = Theme.color.emphasis.primary
    return remember(enabled, selected, outline, primary) {
        val color = if (selected) primary else outline.copy(.5f)
        BorderStroke(1.dp, if (enabled) color else color.copy(DisabledBorderAlpha))
    }
}

@Composable
fun SketchbookChip(
    onClick: () -> Unit,
    label: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    leadingIcon: (@Composable () -> Unit)? = null,
    trailingIcon: (@Composable () -> Unit)? = null,
    enabled: Boolean = true,
    shape: Shape = Theme.container.buttonSmall,
    compositeColor: Color = Theme.color.emphasis.background,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() }
) {
    val content = Theme.color.contentColorFor(compositeColor)
    val overlay = Theme.color.overlay.background.copy(ChipOverlayAlpha)
    val disabled = Theme.color.content.background.copy(DisabledAlpha)
    val control = modifier.glassControl(shape, enabled, interactionSource)
    ThemePressHost {
        AssistChip(
            onClick = onClick,
            label = label,
            modifier = control,
            enabled = enabled,
            leadingIcon = leadingIcon,
            trailingIcon = trailingIcon,
            shape = shape,
            colors = AssistChipDefaults.assistChipColors(
                containerColor = overlay,
                labelColor = content,
                leadingIconContentColor = content,
                trailingIconContentColor = content,
                disabledContainerColor = overlay,
                disabledLabelColor = disabled,
                disabledLeadingIconContentColor = disabled,
                disabledTrailingIconContentColor = disabled
            ),
            elevation = null,
            border = chipBorder(enabled, selected = false),
            interactionSource = interactionSource
        )
    }
}

@Composable
fun SketchbookFilterChip(
    selected: Boolean,
    onClick: () -> Unit,
    label: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    leadingIcon: (@Composable () -> Unit)? = null,
    trailingIcon: (@Composable () -> Unit)? = null,
    enabled: Boolean = true,
    shape: Shape = Theme.container.buttonSmall,
    compositeColor: Color = Theme.color.emphasis.background,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() }
) {
    val content = Theme.color.contentColorFor(compositeColor)
    val overlay = Theme.color.overlay.background.copy(ChipOverlayAlpha)
    val disabled = Theme.color.content.background.copy(DisabledAlpha)
    val selectedContainer = Theme.color.emphasis.primary.copy(SelectedAlpha)
    val control = modifier.glassControl(shape, enabled, interactionSource)
    ThemePressHost {
        FilterChip(
            selected = selected,
            onClick = onClick,
            label = label,
            modifier = control,
            enabled = enabled,
            leadingIcon = leadingIcon,
            trailingIcon = trailingIcon,
            shape = shape,
            colors = FilterChipDefaults.filterChipColors(
                containerColor = overlay,
                labelColor = content,
                iconColor = content,
                disabledContainerColor = overlay,
                disabledLabelColor = disabled,
                disabledLeadingIconColor = disabled,
                disabledTrailingIconColor = disabled,
                selectedContainerColor = selectedContainer,
                disabledSelectedContainerColor = selectedContainer,
                selectedLabelColor = content,
                selectedLeadingIconColor = content,
                selectedTrailingIconColor = content
            ),
            elevation = null,
            border = chipBorder(enabled, selected),
            interactionSource = interactionSource
        )
    }
}

@SketchbookComponentPreview
@SketchbookValidation
@Composable
private fun SketchbookChipPreview() = SketchbookPreviewLayout {
    Row(modifier = Modifier.padding(16.dp)) {
        SketchbookChip({}, { Text("Chip") })
        SketchbookFilterChip(
            selected = true,
            onClick = {},
            label = { Text("Selected") },
            leadingIcon = { Icon(Icons.Default.Check, null) },
            modifier = Modifier.padding(start = 8.dp)
        )
        SketchbookFilterChip(false, {}, { Text("Off") }, Modifier.padding(start = 8.dp))
    }
}
