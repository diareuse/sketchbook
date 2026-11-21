package lib.sketchbook

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.PagerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import lib.sketchbook.theme.Theme
import kotlin.math.abs
import kotlin.math.floor

enum class PagerIndicatorStyle { Dots, Worm }

private val DefaultDotSize = 8.dp
private val DefaultSpacing = 8.dp
private const val InactiveScale = .8f
private const val InactiveAlpha = .35f

private fun defaultPageDescription(page: Int, count: Int) = "Page $page of $count"

@Composable
fun SketchbookPagerIndicator(
    pagerState: PagerState,
    modifier: Modifier = Modifier,
    style: PagerIndicatorStyle = PagerIndicatorStyle.Dots,
    activeColor: Color = Theme.color.emphasis.primary,
    inactiveColor: Color = Theme.color.content.background.copy(InactiveAlpha),
    dotSize: Dp = DefaultDotSize,
    spacing: Dp = DefaultSpacing,
    pageDescription: (page: Int, count: Int) -> String = ::defaultPageDescription
) = PagerIndicator(
    count = pagerState.pageCount,
    position = { pagerState.currentPage + pagerState.currentPageOffsetFraction },
    modifier = modifier,
    style = style,
    activeColor = activeColor,
    inactiveColor = inactiveColor,
    dotSize = dotSize,
    spacing = spacing,
    pageDescription = pageDescription
)

@Composable
fun SketchbookPagerIndicator(
    pageCount: Int,
    currentPage: Int,
    modifier: Modifier = Modifier,
    currentPageOffsetFraction: () -> Float = { 0f },
    style: PagerIndicatorStyle = PagerIndicatorStyle.Dots,
    activeColor: Color = Theme.color.emphasis.primary,
    inactiveColor: Color = Theme.color.content.background.copy(InactiveAlpha),
    dotSize: Dp = DefaultDotSize,
    spacing: Dp = DefaultSpacing,
    pageDescription: (page: Int, count: Int) -> String = ::defaultPageDescription
) = PagerIndicator(
    count = pageCount,
    position = { currentPage + currentPageOffsetFraction() },
    modifier = modifier,
    style = style,
    activeColor = activeColor,
    inactiveColor = inactiveColor,
    dotSize = dotSize,
    spacing = spacing,
    pageDescription = pageDescription
)

@Composable
private fun PagerIndicator(
    count: Int,
    position: () -> Float,
    modifier: Modifier,
    style: PagerIndicatorStyle,
    activeColor: Color,
    inactiveColor: Color,
    dotSize: Dp,
    spacing: Dp,
    pageDescription: (Int, Int) -> String
) {
    val width = if (count <= 0) 0.dp else dotSize * count + spacing * (count - 1)
    Box(
        modifier
            .width(width)
            .height(dotSize)
            .semantics {
                if (count > 0) {
                    val page = position().roundToPage(count)
                    stateDescription = pageDescription(page + 1, count)
                }
            }
            .drawBehind {
                if (count > 0) {
                    paintIndicator(count, position().clampPosition(count), style, activeColor, inactiveColor, dotSize.toPx(), spacing.toPx())
                }
            }
    )
}

private fun Float.clampPosition(count: Int): Float = if (isNaN()) 0f else coerceIn(0f, (count - 1).toFloat())

private fun Float.roundToPage(count: Int): Int = clampPosition(count).let { floor(it + .5f).toInt() }

private fun DrawScope.paintIndicator(
    count: Int,
    position: Float,
    style: PagerIndicatorStyle,
    active: Color,
    inactive: Color,
    dot: Float,
    spacing: Float
) {
    val rtl = layoutDirection == LayoutDirection.Rtl
    val step = dot + spacing
    val radius = dot / 2f
    for (i in 0 until count) {
        val proximity = (1f - abs(position - i)).coerceIn(0f, 1f)
        val centerX = mirror(radius + i * step, rtl)
        when (style) {
            PagerIndicatorStyle.Dots -> drawCircle(
                color = lerp(inactive, active, proximity),
                radius = radius * (InactiveScale + (1f - InactiveScale) * proximity),
                center = Offset(centerX, radius)
            )

            PagerIndicatorStyle.Worm -> drawCircle(inactive, radius, Offset(centerX, radius))
        }
    }
    if (style == PagerIndicatorStyle.Worm) paintWorm(position, dot, step, active, rtl)
}

private fun DrawScope.mirror(x: Float, rtl: Boolean): Float =
    if (rtl) size.width - x else x

private fun DrawScope.paintWorm(position: Float, dot: Float, step: Float, color: Color, rtl: Boolean) {
    val base = floor(position)
    val fraction = position - base
    val tail = base * step + if (fraction > .5f) (fraction - .5f) * 2f * step else 0f
    val head = base * step + dot + if (fraction > .5f) step else fraction * 2f * step
    val left = if (rtl) size.width - head else tail
    val width = head - tail
    drawRoundRect(color, Offset(left, 0f), Size(width, dot), CornerRadius(dot / 2f))
}

@SketchbookComponentPreview
@Composable
private fun SketchbookPagerIndicatorPreview() = SketchbookPreviewLayout {
    Column(Modifier.padding(16.dp)) {
        SketchbookPagerIndicator(pageCount = 5, currentPage = 1, currentPageOffsetFraction = { .3f })
        SketchbookPagerIndicator(
            pageCount = 5,
            currentPage = 2,
            modifier = Modifier.padding(top = 16.dp),
            style = PagerIndicatorStyle.Worm
        )
    }
}
