@file:OptIn(ExperimentalMaterial3Api::class)

package lib.sketchbook

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.Text
import androidx.compose.material3.TooltipAnchorPosition
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipDefaults
import androidx.compose.material3.rememberTooltipState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp
import lib.sketchbook.glass.GlassDepthScope
import lib.sketchbook.glass.glass
import lib.sketchbook.theme.Theme
import lib.sketchbook.theme.contentColorFor

private val TooltipElevation = 20.dp

@Composable
internal fun TooltipSurface(
    shape: Shape,
    compositeColor: Color,
    content: @Composable () -> Unit
) = GlassDepthScope {
    Box(
        Modifier
            .glass(shape, TooltipElevation, tint = compositeColor)
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        CompositionLocalProvider(
            LocalContentColor provides Theme.color.contentColorFor(compositeColor),
            content = content
        )
    }
}

@Composable
fun SketchbookTooltip(
    tooltip: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    shape: Shape = Theme.container.buttonSmall,
    compositeColor: Color = Theme.color.emphasis.background,
    content: @Composable () -> Unit
) {
    TooltipBox(
        positionProvider = TooltipDefaults.rememberTooltipPositionProvider(TooltipAnchorPosition.Above),
        tooltip = { TooltipSurface(shape, compositeColor, tooltip) },
        state = rememberTooltipState(),
        modifier = modifier,
        content = content
    )
}

@Composable
fun SketchbookTooltip(
    text: String,
    modifier: Modifier = Modifier,
    shape: Shape = Theme.container.buttonSmall,
    compositeColor: Color = Theme.color.emphasis.background,
    content: @Composable () -> Unit
) = SketchbookTooltip(
    tooltip = { Text(text, style = Theme.textStyle.caption) },
    modifier = modifier,
    shape = shape,
    compositeColor = compositeColor,
    content = content
)

@SketchbookComponentPreview
@Composable
private fun SketchbookTooltipPreview() = SketchbookPreviewLayout {
    Box(Modifier.padding(48.dp)) {
        SketchbookTooltip("Add to favorites") {
            Icon(Icons.Default.Favorite, contentDescription = "Favorite", modifier = Modifier.size(24.dp))
        }
    }
}
