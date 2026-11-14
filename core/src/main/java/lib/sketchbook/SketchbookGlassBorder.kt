package lib.sketchbook

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import lib.sketchbook.glass.glass
import lib.sketchbook.modifier.specularBorder
import lib.sketchbook.theme.Theme
import lib.sketchbook.theme.contentColorFor

private val GlassBorderElevation = 8.dp
private val DefaultBorderWidth = 1.5.dp

@Composable
fun SketchbookGlassBorder(
    modifier: Modifier = Modifier,
    shape: Shape = Theme.container.card,
    borderWidth: Dp = DefaultBorderWidth,
    borderColor: Color = Color.White,
    compositeColor: Color = Theme.color.emphasis.background,
    animated: Boolean = false,
    content: @Composable BoxScope.() -> Unit
) {
    Box(
        modifier = modifier
            .glass(shape, GlassBorderElevation, tint = compositeColor)
            .then(if (borderWidth > 0.dp) Modifier.specularBorder(shape, borderWidth, borderColor, animated = animated) else Modifier),
        content = content
    )
}

@SketchbookComponentPreview
@SketchbookValidation
@Composable
private fun SketchbookGlassBorderPreview() = SketchbookPreviewLayout {
    SketchbookGlassBorder(Modifier.padding(16.dp), animated = true) {
        Text("Glass border", Modifier.padding(24.dp), color = Theme.color.contentColorFor(Theme.color.emphasis.background))
    }
}
