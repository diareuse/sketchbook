package lib.sketchbook

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CheckboxColors
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.geometry.lerp
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawOutline
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import lib.sketchbook.haptic.deniedWhenDisabled
import lib.sketchbook.haptic.rememberConfirmedChange
import lib.sketchbook.theme.Theme

private const val DisabledAlpha = .38f
private const val UncheckedFillAlpha = .22f
private const val CheckDrawMillis = 180
private val CheckboxTarget = 48.dp
private val CheckboxBoxSize = 24.dp
private val CheckStroke = 3.dp

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
private fun CheckboxMark(checked: Boolean, enabled: Boolean) {
    val shape = Theme.container.buttonSmall
    val fill = Theme.color.emphasis.primary
    val check = Theme.color.overlay.primary
    val unchecked = Theme.color.overlay.background.copy(UncheckedFillAlpha)
    val dim = if (enabled) 1f else DisabledAlpha
    val progress = animateFloatAsState(if (checked) 1f else 0f, tween(CheckDrawMillis), label = "check")
    Spacer(
        Modifier
            .size(CheckboxBoxSize)
            .drawWithCache {
                val outline = shape.createOutline(size, layoutDirection, this)
                val stroke = Stroke(CheckStroke.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)
                val mark = CheckMark(size)
                val segment = Path()
                onDrawBehind {
                    val p = progress.value
                    drawOutline(outline, unchecked, alpha = dim)
                    drawOutline(outline, fill, alpha = p * dim)
                    mark.draw(p, segment)
                    drawPath(segment, check, alpha = dim, style = stroke)
                }
            }
    )
}

private class CheckMark(size: Size) {
    private val start = Offset(size.width * .26f, size.height * .53f)
    private val elbow = Offset(size.width * .43f, size.height * .70f)
    private val end = Offset(size.width * .75f, size.height * .33f)
    private val first = (elbow - start).getDistance()
    private val second = (end - elbow).getDistance()

    fun draw(progress: Float, into: Path) {
        into.rewind()
        if (progress <= 0f) return
        val reach = (first + second) * progress
        into.moveTo(start.x, start.y)
        if (reach <= first) {
            val point = lerp(start, elbow, reach / first)
            into.lineTo(point.x, point.y)
        } else {
            into.lineTo(elbow.x, elbow.y)
            val point = lerp(elbow, end, (reach - first) / second)
            into.lineTo(point.x, point.y)
        }
    }
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
    val change = rememberConfirmedChange(onCheckedChange)
    if (label == null) {
        Box(
            modifier = modifier
                .deniedWhenDisabled(enabled)
                .size(CheckboxTarget)
                .clip(CircleShape)
                .toggleable(
                    value = checked,
                    onValueChange = change,
                    enabled = enabled,
                    role = Role.Checkbox,
                    interactionSource = interactionSource,
                    indication = LocalIndication.current
                ),
            contentAlignment = Alignment.Center
        ) {
            CheckboxMark(checked, enabled)
        }
    } else {
        ToggleRow(
            checked = checked,
            onCheckedChange = change,
            enabled = enabled,
            interactionSource = interactionSource,
            modifier = modifier,
            control = { CheckboxMark(checked, enabled) },
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
