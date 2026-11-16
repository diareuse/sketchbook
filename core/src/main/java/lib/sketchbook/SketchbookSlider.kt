@file:OptIn(ExperimentalMaterial3Api::class)

package lib.sketchbook

import androidx.compose.foundation.interaction.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.unit.*
import lib.sketchbook.glass.glass
import lib.sketchbook.modifier.specularBorder
import lib.sketchbook.theme.Theme
import lib.sketchbook.theme.contentColorFor

private val TrackHeight = 12.dp
private val ThumbSize = 28.dp
private val TouchTarget = 48.dp
private val TrackGlassElevation = 8.dp
private val ThumbGlassElevation = 16.dp
private const val ActiveTrackAlpha = .85f
private const val InactiveTrackAlpha = .1f
private const val DisabledAlpha = .38f

@Composable
private fun sliderColors(compositeColor: Color): SliderColors {
    val content = Theme.color.contentColorFor(compositeColor)
    val primary = Theme.color.emphasis.primary
    val disabled = content.copy(DisabledAlpha)
    return SliderDefaults.colors(
        thumbColor = Color.Transparent,
        activeTrackColor = primary.copy(ActiveTrackAlpha),
        inactiveTrackColor = Theme.color.overlay.background.copy(InactiveTrackAlpha),
        activeTickColor = Theme.color.overlay.primary.copy(.6f),
        inactiveTickColor = content.copy(.4f),
        disabledThumbColor = Color.Transparent,
        disabledActiveTrackColor = disabled.copy(.25f),
        disabledInactiveTrackColor = disabled.copy(.1f),
        disabledActiveTickColor = Color.Transparent,
        disabledInactiveTickColor = Color.Transparent
    )
}

@Composable
private fun GlassThumb(enabled: Boolean) {
    Box(modifier = Modifier.size(ThumbSize, TouchTarget), contentAlignment = Alignment.Center) {
        Box(
            modifier = Modifier
                .size(ThumbSize)
                .glass(CircleShape, ThumbGlassElevation, useGlow = enabled)
                .specularBorder(
                    shape = CircleShape,
                    width = 0.dp,
                    color = Color.White.copy(alpha = if (enabled) .9f else .35f),
                    animated = false
                )
        )
    }
}

@Composable
private fun GlassTrack(state: SliderState, colors: SliderColors, enabled: Boolean) {
    SliderDefaults.Track(
        sliderState = state,
        modifier = Modifier
            .height(TrackHeight)
            .glass(CircleShape, TrackGlassElevation, useGlow = false),
        enabled = enabled,
        colors = colors,
        drawStopIndicator = null,
        thumbTrackGapSize = 0.dp,
        trackInsideCornerSize = 0.dp
    )
}

@Composable
fun SketchbookSlider(
    value: Float,
    onValueChange: (Float) -> Unit,
    modifier: Modifier = Modifier,
    valueRange: ClosedFloatingPointRange<Float> = 0f..1f,
    steps: Int = 0,
    onValueChangeFinished: (() -> Unit)? = null,
    enabled: Boolean = true,
    compositeColor: Color = Theme.color.emphasis.background,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() }
) {
    val colors = sliderColors(compositeColor)
    Slider(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier,
        enabled = enabled,
        valueRange = valueRange,
        steps = steps,
        onValueChangeFinished = onValueChangeFinished,
        colors = colors,
        interactionSource = interactionSource,
        thumb = { GlassThumb(enabled) },
        track = { state -> GlassTrack(state, colors, enabled) }
    )
}

@SketchbookComponentPreview
@Composable
private fun SketchbookSliderPreview() = SketchbookPreviewLayout {
    Column(modifier = Modifier.padding(16.dp)) {
        SketchbookSlider(.3f, {})
        SketchbookSlider(.6f, {}, steps = 4)
        SketchbookSlider(.5f, {}, enabled = false)
    }
}
