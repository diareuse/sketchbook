package lib.sketchbook

import androidx.compose.foundation.interaction.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.hapticfeedback.*
import androidx.compose.ui.platform.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.unit.dp
import lib.sketchbook.modifier.specularBorder
import lib.sketchbook.theme.Theme
import lib.sketchbook.theme.contentColorFor

private const val TrackTint = .45f
private const val DisabledTrackTint = .3f

private val DefaultThumbContent: @Composable () -> Unit = {}

private fun (@Composable () -> Unit)?.withRim(enabled: Boolean): (@Composable () -> Unit)? =
    if (this === DefaultThumbContent) {
        { ThumbRim(enabled) }
    } else {
        this
    }

@Composable
private fun ThumbRim(enabled: Boolean) = Box(
    Modifier
        .fillMaxSize()
        .specularBorder(
            shape = CircleShape,
            width = 1.dp,
            color = Color.White.copy(alpha = if (enabled) .9f else .35f),
            animated = false
        )
)

@Composable
fun SketchbookSwitch(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    compositeColor: Color = Theme.color.emphasis.background,
    colors: SwitchColors = SwitchDefaults.colors(
        checkedBorderColor = Color.Transparent,
        disabledCheckedBorderColor = Color.Transparent,
        disabledUncheckedBorderColor = Color.Transparent,
        uncheckedBorderColor = Color.Transparent,
        checkedTrackColor = Theme.color.emphasis.primary.copy(.3f)
            .compositeOver(compositeColor.copy(TrackTint)),
        disabledCheckedTrackColor = Theme.color.overlay.background.copy(.05f)
            .compositeOver(compositeColor.copy(DisabledTrackTint)),
        disabledUncheckedTrackColor = Theme.color.overlay.background.copy(.05f)
            .compositeOver(compositeColor.copy(DisabledTrackTint)),
        uncheckedTrackColor = Theme.color.overlay.background.copy(.1f)
            .compositeOver(compositeColor.copy(TrackTint)),
        checkedThumbColor = Theme.color.emphasis.primary,
        uncheckedThumbColor = Theme.color.emphasis.background,
        disabledCheckedThumbColor = Theme.color.contentColorFor(compositeColor).copy(.15f),
        disabledUncheckedThumbColor = Theme.color.contentColorFor(compositeColor).copy(.15f)
    ),
    thumbContent: (@Composable () -> Unit)? = DefaultThumbContent,
    enabled: Boolean = true,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() }
) {
    val haptics = LocalHapticFeedback.current
    Switch(
        modifier = modifier,
        checked = checked,
        onCheckedChange = {
            onCheckedChange(it)
            val feedback = if (it) HapticFeedbackType.ToggleOn else HapticFeedbackType.ToggleOff
            haptics.performHapticFeedback(feedback)
        },
        colors = colors,
        thumbContent = thumbContent.withRim(enabled),
        enabled = enabled,
        interactionSource = interactionSource
    )
}

@SketchbookComponentPreview
@Composable
private fun SketchbookSwitchPreview() = SketchbookPreviewLayout {
    Column {
        SketchbookSwitch(checked = true, onCheckedChange = {})
        SketchbookSwitch(checked = false, onCheckedChange = {})
        SketchbookSwitch(checked = true, onCheckedChange = {}, enabled = false)
        SketchbookSwitch(checked = false, onCheckedChange = {}, enabled = false)
    }
}