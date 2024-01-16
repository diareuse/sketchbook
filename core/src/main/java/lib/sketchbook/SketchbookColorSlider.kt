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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SketchbookColorSlider(
    modifier: Modifier = Modifier,
    value: Float,
    onValueChange: (Float) -> Unit
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
        valueRange = 0f..360f,
        thumb = {
            val interactionSource = remember { MutableInteractionSource() }
            Spacer(
                modifier
                    .size(32.dp)
                    .hoverable(interactionSource = interactionSource)
                    .surface(
                        color = Color.hsv(it.value, 1f, 1f),
                        shape = Theme.container.buttonSmall,
                        elevation = 16.dp,
                        shadowColor = Color.Black
                    )
            )
        },
        track = {
            val background = remember { List(360) { Color.hsv(it.toFloat(), 1f, .75f) } }
            val shape = Theme.container.button
            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
            ) {
                val outline = shape.createOutline(size, layoutDirection, this)

                clipPath(outline.toPath()) {
                    drawRect(Brush.horizontalGradient(background))
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
    var value by remember { mutableFloatStateOf(120f) }
    SketchbookColorSlider(value = value, onValueChange = { value = it })
}
