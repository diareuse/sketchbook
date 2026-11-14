package lib.sketchbook

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.background
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.semantics.invisibleToUser
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import lib.sketchbook.modifier.shimmer
import lib.sketchbook.theme.Theme

private const val BaseAlpha = .08f
private const val HighlightAlpha = .16f
private val DefaultLineHeight = 14.dp
private val DefaultLineSpacing = 8.dp
private const val DefaultLastLineFraction = .6f

@Composable
fun SketchbookSkeleton(
    modifier: Modifier = Modifier,
    shape: Shape = Theme.container.buttonSmall,
    color: Color = Theme.color.content.background.copy(BaseAlpha),
    highlight: Color = Theme.color.content.background.copy(HighlightAlpha),
    animated: Boolean = true
) {
    Box(
        modifier
            .semantics { invisibleToUser() }
            .clip(shape)
            .background(color)
            .shimmer(highlight, enabled = animated)
    )
}

@Composable
fun SketchbookSkeletonCircle(
    size: Dp,
    modifier: Modifier = Modifier,
    color: Color = Theme.color.content.background.copy(BaseAlpha),
    highlight: Color = Theme.color.content.background.copy(HighlightAlpha),
    animated: Boolean = true
) = SketchbookSkeleton(modifier.size(size), CircleShape, color, highlight, animated)

@Composable
fun SketchbookSkeletonText(
    modifier: Modifier = Modifier,
    lines: Int = 3,
    lineHeight: Dp = DefaultLineHeight,
    lineSpacing: Dp = DefaultLineSpacing,
    lastLineFraction: Float = DefaultLastLineFraction,
    shape: Shape = Theme.container.buttonSmall,
    color: Color = Theme.color.content.background.copy(BaseAlpha),
    highlight: Color = Theme.color.content.background.copy(HighlightAlpha),
    animated: Boolean = true
) {
    require(lines >= 1) { "lines must be at least 1" }
    require(lastLineFraction in 0f..1f) { "lastLineFraction must be within 0..1" }
    Column(modifier, verticalArrangement = Arrangement.spacedBy(lineSpacing)) {
        repeat(lines) { index ->
            val fraction = if (index == lines - 1 && lines > 1) lastLineFraction else 1f
            SketchbookSkeleton(
                modifier = Modifier.fillMaxWidth(fraction).height(lineHeight),
                shape = shape,
                color = color,
                highlight = highlight,
                animated = animated
            )
        }
    }
}

@SketchbookComponentPreview
@SketchbookValidation
@Composable
private fun SketchbookSkeletonPreview() = SketchbookPreviewLayout {
    Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        SketchbookSkeletonCircle(size = 48.dp)
        SketchbookSkeleton(Modifier.fillMaxWidth().height(96.dp))
        SketchbookSkeletonText()
    }
}
