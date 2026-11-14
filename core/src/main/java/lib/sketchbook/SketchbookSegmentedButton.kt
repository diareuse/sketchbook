package lib.sketchbook

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
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
import lib.sketchbook.glass.asGlassShape
import lib.sketchbook.glass.glass
import lib.sketchbook.haptic.deniedWhenDisabled
import lib.sketchbook.haptic.rememberConfirmation
import lib.sketchbook.theme.Theme
import lib.sketchbook.theme.contentColorFor

private val SegmentedGlassElevation = 12.dp
private val SegmentedHeight = 48.dp
private const val ActiveAlpha = .7f
private const val DisabledAlpha = .38f

@Composable
private fun segmentedColors(compositeColor: Color): SegmentedButtonColors {
    val content = Theme.color.contentColorFor(compositeColor)
    val active = Theme.color.container.primary.copy(ActiveAlpha)
    val disabled = content.copy(DisabledAlpha)
    val none = Color.Transparent
    return SegmentedButtonDefaults.colors(
        activeContainerColor = active,
        activeContentColor = content,
        activeBorderColor = none,
        inactiveContainerColor = none,
        inactiveContentColor = content,
        inactiveBorderColor = none,
        disabledActiveContainerColor = active,
        disabledActiveContentColor = disabled,
        disabledActiveBorderColor = none,
        disabledInactiveContainerColor = none,
        disabledInactiveContentColor = disabled,
        disabledInactiveBorderColor = none
    )
}

@Composable
private fun Modifier.segmentedRow(shape: Shape, enabled: Boolean): Modifier {
    val glassShape = shape.asGlassShape()
    return deniedWhenDisabled(enabled)
        .height(SegmentedHeight)
        .glass(glassShape, SegmentedGlassElevation, useGlow = enabled)
        .clip(glassShape)
}

@Composable
private fun Modifier.segment(interactionSource: MutableInteractionSource): Modifier =
    fillMaxHeight().pressIndicated(RectangleShape, interactionSource)

private val NoBorder = BorderStroke(0.dp, Color.Transparent)
private val NoPadding = PaddingValues(0.dp)

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
    val confirm = rememberConfirmation()
    SingleChoiceSegmentedButtonRow(modifier = modifier.segmentedRow(shape, enabled)) {
        repeat(count) { index ->
            val interactionSource = remember { MutableInteractionSource() }
            ThemePressHost {
                SegmentedButton(
                    selected = index == selectedIndex,
                    onClick = {
                        confirm(true)
                        onSelectedIndexChange(index)
                    },
                    shape = RectangleShape,
                    modifier = Modifier.segment(interactionSource),
                    enabled = enabled,
                    colors = colors,
                    border = NoBorder,
                    contentPadding = NoPadding,
                    interactionSource = interactionSource,
                    icon = {},
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
    val confirm = rememberConfirmation()
    MultiChoiceSegmentedButtonRow(modifier = modifier.segmentedRow(shape, enabled)) {
        repeat(count) { index ->
            val interactionSource = remember { MutableInteractionSource() }
            ThemePressHost {
                SegmentedButton(
                    checked = index in selectedIndices,
                    onCheckedChange = { checked ->
                        confirm(checked)
                        onSelectedIndicesChange(if (checked) selectedIndices + index else selectedIndices - index)
                    },
                    shape = RectangleShape,
                    modifier = Modifier.segment(interactionSource),
                    enabled = enabled,
                    colors = colors,
                    border = NoBorder,
                    contentPadding = NoPadding,
                    interactionSource = interactionSource,
                    icon = {},
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
