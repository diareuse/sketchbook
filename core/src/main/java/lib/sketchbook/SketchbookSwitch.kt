package lib.sketchbook

import androidx.compose.foundation.interaction.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.hapticfeedback.*
import androidx.compose.ui.platform.*
import lib.sketchbook.theme.Theme
import lib.sketchbook.theme.contentColorFor

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
        checkedTrackColor = Theme.color.emphasis.primary.copy(.15f)
            .compositeOver(compositeColor),
        disabledCheckedTrackColor = Theme.color.overlay.background.copy(.05f)
            .compositeOver(compositeColor),
        disabledUncheckedTrackColor = Theme.color.overlay.background.copy(.05f)
            .compositeOver(compositeColor),
        uncheckedTrackColor = Theme.color.overlay.background.copy(.1f)
            .compositeOver(compositeColor),
        checkedThumbColor = Theme.color.emphasis.primary,
        uncheckedThumbColor = Theme.color.emphasis.background,
        disabledCheckedThumbColor = Theme.color.contentColorFor(compositeColor).copy(.15f),
        disabledUncheckedThumbColor = Theme.color.contentColorFor(compositeColor).copy(.15f)
    ),
    thumbContent: (@Composable () -> Unit)? = {},
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
        thumbContent = thumbContent,
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