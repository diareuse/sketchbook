package lib.sketchbook

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CornerBasedShape
import androidx.compose.material3.MultiChoiceSegmentedButtonRow
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonColors
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import lib.sketchbook.glass.glass
import lib.sketchbook.theme.Theme
import lib.sketchbook.theme.contentColorFor

private val SegmentedGlassElevation = 12.dp
private const val ActiveAlpha = .28f
private const val InactiveBorderAlpha = .5f
private const val DisabledAlpha = .38f

@Composable
private fun segmentedColors(compositeColor: Color): SegmentedButtonColors {
    val content = Theme.color.contentColorFor(compositeColor)
    val primary = Theme.color.emphasis.primary
    val outline = Theme.color.content.outline.copy(InactiveBorderAlpha)
    val disabled = content.copy(DisabledAlpha)
    val active = primary.copy(ActiveAlpha)
    return SegmentedButtonDefaults.colors(
        activeContainerColor = active,
        activeContentColor = content,
        activeBorderColor = primary,
        inactiveContainerColor = Color.Transparent,
        inactiveContentColor = content,
        inactiveBorderColor = outline,
        disabledActiveContainerColor = active,
        disabledActiveContentColor = disabled,
        disabledActiveBorderColor = primary.copy(.12f),
        disabledInactiveContainerColor = Color.Transparent,
        disabledInactiveContentColor = disabled,
        disabledInactiveBorderColor = outline.copy(.12f)
    )
}

@Composable
fun SketchbookSegmentedButton(
    selectedIndex: Int,
    onSelectedIndexChange: (Int) -> Unit,
    count: Int,
    modifier: Modifier = Modifier,
    label: @Composable (index: Int) -> Unit,
    enabled: Boolean = true,
    shape: CornerBasedShape = Theme.container.button,
    compositeColor: Color = Theme.color.emphasis.background
) {
    val colors = segmentedColors(compositeColor)
    SingleChoiceSegmentedButtonRow(modifier = modifier.glass(shape, SegmentedGlassElevation, useGlow = enabled)) {
        repeat(count) { index ->
            val itemShape = SegmentedButtonDefaults.itemShape(index, count, shape)
            val interactionSource = remember { MutableInteractionSource() }
            val control = Modifier.pressControl(itemShape, interactionSource)
            ThemePressHost {
                SegmentedButton(
                    selected = index == selectedIndex,
                    onClick = { onSelectedIndexChange(index) },
                    shape = itemShape,
                    modifier = control,
                    enabled = enabled,
                    colors = colors,
                    interactionSource = interactionSource,
                    label = { label(index) }
                )
            }
        }
    }
}

@Composable
fun SketchbookMultiChoiceSegmentedButton(
    selectedIndices: Set<Int>,
    onSelectedIndicesChange: (Set<Int>) -> Unit,
    count: Int,
    modifier: Modifier = Modifier,
    label: @Composable (index: Int) -> Unit,
    enabled: Boolean = true,
    shape: CornerBasedShape = Theme.container.button,
    compositeColor: Color = Theme.color.emphasis.background
) {
    val colors = segmentedColors(compositeColor)
    MultiChoiceSegmentedButtonRow(modifier = modifier.glass(shape, SegmentedGlassElevation, useGlow = enabled)) {
        repeat(count) { index ->
            val itemShape = SegmentedButtonDefaults.itemShape(index, count, shape)
            val interactionSource = remember { MutableInteractionSource() }
            val control = Modifier.pressControl(itemShape, interactionSource)
            ThemePressHost {
                SegmentedButton(
                    checked = index in selectedIndices,
                    onCheckedChange = { checked ->
                        onSelectedIndicesChange(if (checked) selectedIndices + index else selectedIndices - index)
                    },
                    shape = itemShape,
                    modifier = control,
                    enabled = enabled,
                    colors = colors,
                    interactionSource = interactionSource,
                    label = { label(index) }
                )
            }
        }
    }
}

@SketchbookComponentPreview
@SketchbookValidation
@Composable
private fun SketchbookSegmentedButtonPreview() = SketchbookPreviewLayout {
    val options = listOf("Day", "Week", "Month")
    Column(modifier = Modifier.padding(16.dp)) {
        SketchbookSegmentedButton(1, {}, options.size, label = { Text(options[it]) })
        SketchbookMultiChoiceSegmentedButton(
            setOf(0, 2),
            {},
            options.size,
            Modifier.padding(top = 16.dp),
            label = { Text(options[it]) }
        )
    }
}
