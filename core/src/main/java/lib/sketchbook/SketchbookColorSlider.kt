package lib.sketchbook

import android.content.res.Configuration.UI_MODE_NIGHT_NO
import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.*
import androidx.compose.ui.tooling.preview.*
import androidx.compose.ui.unit.*
import lib.sketchbook.modifier.surface
import lib.sketchbook.theme.Theme
import lib.sketchbook.util.toPath

@Composable
fun SketchbookColorSlider(
    hue: Float,
    onHueChange: (Float) -> Unit,
    saturation: Float,
    onSaturationChange: (Float) -> Unit,
    value: Float,
    onValueChange: (Float) -> Unit,
    modifier: Modifier = Modifier,
    thumbSize: Dp = 32.dp,
    trackHeight: Dp = 48.dp,
    verticalArrangement: Arrangement.Vertical = Arrangement.spacedBy(8.dp),
    thumbShape: Shape = Theme.container.buttonSmall,
    trackShape: Shape = Theme.container.button
) {
    Column(
        modifier = modifier,
        verticalArrangement = verticalArrangement
    ) {
        SketchbookColorSliderSimple(
            value = hue,
            onValueChange = onHueChange,
            valueRange = 0f..360f,
            background = { List(360) { Color.hsv(it.toFloat(), saturation, value) } },
            color = { Color.hsv(it, saturation, value) },
            trackHeight = trackHeight,
            thumbSize = thumbSize,
            thumbShape = thumbShape,
            trackShape = trackShape,
        )
        SketchbookColorSliderSimple(
            value = saturation,
            onValueChange = onSaturationChange,
            valueRange = 0f..1f,
            background = { List(10) { Color.hsv(hue, it / 10f, value) } },
            color = { Color.hsv(hue, it, value) },
            trackHeight = trackHeight,
            thumbSize = thumbSize,
            thumbShape = thumbShape,
            trackShape = trackShape,
        )
        SketchbookColorSliderSimple(
            value = value,
            onValueChange = onValueChange,
            valueRange = 0f..1f,
            background = { List(10) { Color.hsv(hue, saturation, it / 10f) } },
            color = { Color.hsv(hue, saturation, it) },
            trackHeight = trackHeight,
            thumbSize = thumbSize,
            thumbShape = thumbShape,
            trackShape = trackShape,
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SketchbookColorSliderSimple(
    value: Float,
    onValueChange: (Float) -> Unit,
    valueRange: ClosedFloatingPointRange<Float>,
    background: () -> List<Color>,
    color: (Float) -> Color,
    trackHeight: Dp,
    thumbSize: Dp,
    modifier: Modifier = Modifier,
    thumbShape: Shape = Theme.container.buttonSmall,
    trackShape: Shape = Theme.container.button
) {
    Slider(
        modifier = modifier,
        value = value,
        onValueChange = onValueChange,
        colors = SliderDefaults.colors(
            thumbColor = Color.Transparent,
            activeTrackColor = Color.Transparent,
            activeTickColor = Color.Transparent,
            inactiveTrackColor = Color.Transparent,
            inactiveTickColor = Color.Transparent,
            disabledThumbColor = Color.Transparent,
            disabledActiveTrackColor = Color.Transparent,
            disabledActiveTickColor = Color.Transparent,
            disabledInactiveTrackColor = Color.Transparent,
            disabledInactiveTickColor = Color.Transparent,
        ),
        valueRange = valueRange,
        thumb = {
            val interactionSource = remember { MutableInteractionSource() }
            Spacer(
                modifier
                    .size(thumbSize)
                    .hoverable(interactionSource = interactionSource)
                    .surface(
                        color = color(it.value),
                        shape = thumbShape,
                        elevation = 16.dp,
                        shadowColor = Color.Black
                    )
            )
        },
        track = {
            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(trackHeight)
            ) {
                val outline = trackShape.createOutline(size, layoutDirection, this)
                clipPath(outline.toPath()) {
                    drawRect(Brush.horizontalGradient(background()))
                }
            }
        }
    )
}

@Preview(showBackground = true, uiMode = UI_MODE_NIGHT_YES)
@Preview(showBackground = true, uiMode = UI_MODE_NIGHT_NO)
@Composable
private fun SketchbookColorSliderPreview(
) = SketchbookPreviewLayout {
    var hue by remember { mutableFloatStateOf(120f) }
    var saturation by remember { mutableFloatStateOf(1f) }
    var value by remember { mutableFloatStateOf(1f) }
    SketchbookColorSlider(hue, { hue = it }, saturation, { saturation = it }, value, { value = it })
}
