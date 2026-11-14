package lib.sketchbook

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
import lib.sketchbook.glass.asGlassShape
import lib.sketchbook.haptic.rememberConfirmedChange
import lib.sketchbook.haptic.rememberConfirmedClick
import lib.sketchbook.theme.Theme
import lib.sketchbook.theme.contentColorFor

private const val SelectedAlpha = .28f
private const val DisabledAlpha = .38f

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
    val overlay = Color.Transparent
    val disabled = Theme.color.content.background.copy(DisabledAlpha)
    val glassShape = shape.asGlassShape()
    val confirmed = rememberConfirmedClick(onClick)
    val control = modifier.glassControl(glassShape, enabled, interactionSource)
    ThemePressHost {
        AssistChip(
            onClick = confirmed,
            label = label,
            modifier = control,
            enabled = enabled,
            leadingIcon = leadingIcon,
            trailingIcon = trailingIcon,
            shape = glassShape,
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
            border = null,
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
    val overlay = Color.Transparent
    val disabled = Theme.color.content.background.copy(DisabledAlpha)
    val selectedContainer = Theme.color.emphasis.primary.copy(SelectedAlpha)
    val glassShape = shape.asGlassShape()
    val confirm = rememberConfirmedChange { onClick() }
    val control = modifier.glassControl(glassShape, enabled, interactionSource)
    ThemePressHost {
        FilterChip(
            selected = selected,
            onClick = { confirm(!selected) },
            label = label,
            modifier = control,
            enabled = enabled,
            leadingIcon = leadingIcon,
            trailingIcon = trailingIcon,
            shape = glassShape,
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
            border = null,
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
