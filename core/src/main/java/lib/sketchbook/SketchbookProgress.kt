package lib.sketchbook

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.semantics.ProgressBarRangeInfo
import androidx.compose.ui.semantics.progressBarRangeInfo
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import lib.sketchbook.shader.PhasePainter
import lib.sketchbook.shader.loopingDraw
import lib.sketchbook.theme.Theme
import kotlin.math.cos

private val LinearHeight = 6.dp
private val CircularSize = 40.dp
private val CircularStroke = 4.dp
private const val DefaultLinearDurationMillis = 1600
private const val DefaultCircularDurationMillis = 1800
private const val HighlightMix = .35f
private const val SegmentFraction = .4f
private const val InitialOffset = .3f
private const val FullTurn = 360f
private const val TwoPi = (2 * Math.PI).toFloat()

private fun Float.progressFraction(): Float = if (isNaN()) 0f else coerceIn(0f, 1f)

private fun highlightOf(color: Color): Color = lerp(color, Color.White, HighlightMix)

private fun Modifier.determinate(progress: () -> Float): Modifier = semantics {
    progressBarRangeInfo = ProgressBarRangeInfo(progress().progressFraction(), 0f..1f)
}

private fun Modifier.indeterminate(): Modifier = semantics {
    progressBarRangeInfo = ProgressBarRangeInfo.Indeterminate
}

private fun Modifier.linearFill(progress: () -> Float, color: Color): Modifier = drawWithCache {
    val rtl = layoutDirection == LayoutDirection.Rtl
    val brush = Brush.horizontalGradient(
        colors = listOf(color, highlightOf(color)),
        startX = if (rtl) size.width else 0f,
        endX = if (rtl) 0f else size.width
    )
    val radius = CornerRadius(size.height / 2f)
    onDrawBehind {
        val fraction = progress().progressFraction()
        if (fraction > 0f) {
            val width = size.width * fraction
            val left = if (rtl) size.width - width else 0f
            drawRoundRect(brush, Offset(left, 0f), Size(width, size.height), radius)
        }
    }
}

private class LinearSegmentPainter(private val color: Color) : PhasePainter {
    private var brush: Brush? = null
    private var brushWidth = -1f

    override fun DrawScope.paint(phase: Float) {
        val segment = size.width * SegmentFraction
        if (brush == null || brushWidth != segment) {
            brush = Brush.horizontalGradient(listOf(color, highlightOf(color)), 0f, segment)
            brushWidth = segment
        }
        val travelled = ((phase + InitialOffset) % 1f) * (size.width + segment) - segment
        val left = if (layoutDirection == LayoutDirection.Rtl) size.width - travelled - segment else travelled
        translate(left = left) {
            drawRoundRect(checkNotNull(brush), Offset.Zero, Size(segment, size.height), CornerRadius(size.height / 2f))
        }
    }

    override fun equals(other: Any?) = other is LinearSegmentPainter && other.color == color

    override fun hashCode() = color.hashCode()
}

private fun Modifier.circularFill(progress: () -> Float, color: Color): Modifier = drawWithCache {
    val stroke = CircularStroke.toPx()
    val arcSize = Size(size.width - stroke, size.height - stroke)
    val topLeft = Offset(stroke / 2f, stroke / 2f)
    val brush = Brush.sweepGradient(listOf(color, highlightOf(color), color), Offset(size.width / 2f, size.height / 2f))
    val style = Stroke(stroke, cap = StrokeCap.Round)
    onDrawBehind {
        val fraction = progress().progressFraction()
        if (fraction > 0f) {
            rotate(-90f) {
                drawArc(brush, 0f, FullTurn * fraction, false, topLeft, arcSize, style = style)
            }
        }
    }
}

private class CircularSweepPainter(private val color: Color) : PhasePainter {
    override fun DrawScope.paint(phase: Float) {
        val stroke = CircularStroke.toPx()
        val arcSize = Size(size.width - stroke, size.height - stroke)
        val topLeft = Offset(stroke / 2f, stroke / 2f)
        val sweep = 90f + 180f * (.5f - .5f * cos(phase * TwoPi))
        val style = Stroke(stroke, cap = StrokeCap.Round)
        rotate(phase * 2f * FullTurn - 90f) {
            drawArc(color, 0f, sweep, false, topLeft, arcSize, style = style)
        }
    }

    override fun equals(other: Any?) = other is CircularSweepPainter && other.color == color

    override fun hashCode() = color.hashCode()
}

@Composable
fun SketchbookLinearProgress(
    progress: () -> Float,
    modifier: Modifier = Modifier,
    color: Color = Theme.color.emphasis.primary,
    trackColor: Color = Theme.color.emphasis.background
) {
    Box(
        modifier
            .fillMaxWidth()
            .height(LinearHeight)
            .determinate(progress)
            .clip(CircleShape)
            .linearFill(progress, color)
    )
}

@Composable
fun SketchbookLinearProgress(
    modifier: Modifier = Modifier,
    color: Color = Theme.color.emphasis.primary,
    trackColor: Color = Theme.color.emphasis.background,
    durationMillis: Int = DefaultLinearDurationMillis
) {
    Box(
        modifier
            .fillMaxWidth()
            .height(LinearHeight)
            .indeterminate()
            .clip(CircleShape)
            .loopingDraw(LinearSegmentPainter(color), durationMillis)
    )
}

@Composable
fun SketchbookCircularProgress(
    progress: () -> Float,
    modifier: Modifier = Modifier,
    color: Color = Theme.color.emphasis.primary,
    trackColor: Color = Theme.color.emphasis.background
) {
    Box(
        modifier
            .size(CircularSize)
            .determinate(progress)
            .clip(CircleShape)
            .circularFill(progress, color)
    )
}

@Composable
fun SketchbookCircularProgress(
    modifier: Modifier = Modifier,
    color: Color = Theme.color.emphasis.primary,
    trackColor: Color = Theme.color.emphasis.background,
    durationMillis: Int = DefaultCircularDurationMillis
) {
    Box(
        modifier
            .size(CircularSize)
            .indeterminate()
            .clip(CircleShape)
            .loopingDraw(CircularSweepPainter(color), durationMillis)
    )
}

@SketchbookComponentPreview
@SketchbookValidation
@Composable
private fun SketchbookProgressPreview() = SketchbookPreviewLayout {
    Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        SketchbookLinearProgress(progress = { .4f })
        SketchbookLinearProgress()
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            SketchbookCircularProgress(progress = { .6f })
            SketchbookCircularProgress()
        }
    }
}
