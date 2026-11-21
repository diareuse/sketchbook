package lib.sketchbook

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import lib.sketchbook.modifier.FadeLineElement
import lib.sketchbook.theme.Theme

private val DefaultFadeLength = 24.dp
private const val DividerAlpha = .5f

@Composable
private fun hairlineAware(thickness: Dp): Dp {
    val density = LocalDensity.current
    return if (thickness == Dp.Hairline) (1f / density.density).dp else thickness
}

@Composable
fun SketchbookHorizontalDivider(
    modifier: Modifier = Modifier,
    thickness: Dp = Dp.Hairline,
    color: Color = Theme.color.content.outline.copy(DividerAlpha),
    fadeLength: Dp = DefaultFadeLength
) {
    val resolved = hairlineAware(thickness)
    Box(
        modifier
            .fillMaxWidth()
            .height(resolved)
            .then(FadeLineElement(vertical = false, color = color, fadeLength = fadeLength))
    )
}

@Composable
fun SketchbookVerticalDivider(
    modifier: Modifier = Modifier,
    thickness: Dp = Dp.Hairline,
    color: Color = Theme.color.content.outline.copy(DividerAlpha),
    fadeLength: Dp = DefaultFadeLength
) {
    val resolved = hairlineAware(thickness)
    Box(
        modifier
            .fillMaxHeight()
            .width(resolved)
            .then(FadeLineElement(vertical = true, color = color, fadeLength = fadeLength))
    )
}

@SketchbookComponentPreview
@SketchbookValidation
@Composable
private fun SketchbookDividerPreview() = SketchbookPreviewLayout {
    Column(modifier = Modifier.padding(16.dp)) {
        Text("Above")
        SketchbookHorizontalDivider(Modifier.padding(vertical = 8.dp))
        Row(modifier = Modifier.height(48.dp)) {
            Text("Left")
            SketchbookVerticalDivider(Modifier.padding(horizontal = 8.dp))
            Text("Right")
        }
    }
}
