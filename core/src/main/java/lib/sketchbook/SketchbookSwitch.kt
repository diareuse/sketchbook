package lib.sketchbook

import androidx.compose.foundation.interaction.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.unit.dp
import lib.sketchbook.haptic.rememberConfirmedChange
import lib.sketchbook.modifier.specularBorder
import lib.sketchbook.theme.Theme

private val SwitchOnTrack = Color(0xFF34C759).copy(alpha = .85f)
private val SwitchOffTrack = Color(0xFFFF3B30).copy(alpha = .85f)
private const val ThumbRimAlpha = .9f

@Composable
private fun ThumbRim(content: @Composable () -> Unit) = Box(
    modifier = Modifier
        .fillMaxSize()
        .specularBorder(
            shape = CircleShape,
            width = 1.dp,
            color = Color.White.copy(alpha = ThumbRimAlpha),
            animated = false
        ),
    contentAlignment = Alignment.Center
) {
    content()
}

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
        checkedTrackColor = SwitchOnTrack,
        disabledCheckedTrackColor = SwitchOnTrack,
        disabledUncheckedTrackColor = SwitchOffTrack,
        uncheckedTrackColor = SwitchOffTrack,
        checkedThumbColor = Color.White,
        uncheckedThumbColor = Color.White,
        disabledCheckedThumbColor = Color.White,
        disabledUncheckedThumbColor = Color.White
    ),
    thumbContent: (@Composable () -> Unit)? = {},
    enabled: Boolean = true,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() }
) {
    val change = rememberConfirmedChange(onCheckedChange)
    val control = modifier.glassControl(CircleShape, enabled, interactionSource)
    ThemePressHost {
        Switch(
            modifier = control,
            checked = checked,
            onCheckedChange = change,
            colors = colors,
            thumbContent = thumbContent?.let { inner -> { ThumbRim(inner) } },
            enabled = enabled,
            interactionSource = interactionSource
        )
    }
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