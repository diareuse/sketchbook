package lib.sketchbook

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.RadioButtonColors
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import lib.sketchbook.haptic.deniedWhenDisabled
import lib.sketchbook.haptic.rememberConfirmedChange
import lib.sketchbook.theme.Theme

private const val DisabledAlpha = .38f
private const val UnselectedFillAlpha = .22f
private const val DotMillis = 180
private val RadioTarget = 48.dp
private val RadioDiscSize = 24.dp
private val RadioDotRadius = 5.dp

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
private fun RadioMark(selected: Boolean, enabled: Boolean) {
    val fill = Theme.color.emphasis.primary
    val dot = Theme.color.overlay.primary
    val unselected = Theme.color.overlay.background.copy(UnselectedFillAlpha)
    val dim = if (enabled) 1f else DisabledAlpha
    val progress = animateFloatAsState(if (selected) 1f else 0f, tween(DotMillis), label = "dot")
    Spacer(
        Modifier
            .size(RadioDiscSize)
            .drawWithCache {
                val dotRadius = RadioDotRadius.toPx()
                onDrawBehind {
                    val p = progress.value
                    drawCircle(unselected, alpha = dim)
                    drawCircle(fill, alpha = p * dim)
                    drawCircle(dot, radius = dotRadius * p, alpha = dim)
                }
            }
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
    val confirm = rememberConfirmedChange { onClick() }
    val select = remember(confirm) { { confirm(true) } }
    if (label == null) {
        Box(
            modifier = modifier
                .deniedWhenDisabled(enabled)
                .size(RadioTarget)
                .clip(CircleShape)
                .selectable(
                    selected = selected,
                    onClick = select,
                    enabled = enabled,
                    role = Role.RadioButton,
                    interactionSource = interactionSource,
                    indication = LocalIndication.current
                ),
            contentAlignment = Alignment.Center
        ) {
            RadioMark(selected, enabled)
        }
    } else {
        SelectableRow(
            selected = selected,
            onClick = select,
            enabled = enabled,
            interactionSource = interactionSource,
            modifier = modifier,
            control = { RadioMark(selected, enabled) },
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
