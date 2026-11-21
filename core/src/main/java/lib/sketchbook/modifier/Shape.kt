package lib.sketchbook.modifier

import androidx.compose.material3.LocalContentColor
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.drawOutline
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.takeOrElse
import androidx.compose.ui.graphics.drawscope.ContentDrawScope
import androidx.compose.ui.node.CompositionLocalConsumerModifierNode
import androidx.compose.ui.node.DrawModifierNode
import androidx.compose.ui.node.ModifierNodeElement
import androidx.compose.ui.node.invalidateDraw
import androidx.compose.ui.node.currentValueOf
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

fun Modifier.glow(
    shape: Shape,
    color: Color = Color.Unspecified,
    lightSource: LightSource = LightSource.TopLeft,
    alpha: Float = .4f,
    fillAlpha: Float = alpha / 2f,
    width: Dp = 2.dp,
): Modifier = this then GlowElement(shape, color, lightSource, alpha, fillAlpha, width)

private data class GlowElement(
    val shape: Shape,
    val color: Color,
    val lightSource: LightSource,
    val alpha: Float,
    val fillAlpha: Float,
    val width: Dp
) : ModifierNodeElement<GlowNode>() {

    override fun create() = GlowNode(shape, color, lightSource, alpha, fillAlpha, width)

    override fun update(node: GlowNode) {
        node.update(shape, color, lightSource, alpha, fillAlpha, width)
    }
}

private class GlowNode(
    private var shape: Shape,
    private var color: Color,
    private var lightSource: LightSource,
    private var alpha: Float,
    private var fillAlpha: Float,
    private var width: Dp
) : Modifier.Node(), DrawModifierNode, CompositionLocalConsumerModifierNode {

    private val cache = OutlineCache()

    fun update(
        shape: Shape,
        color: Color,
        lightSource: LightSource,
        alpha: Float,
        fillAlpha: Float,
        width: Dp
    ) {
        this.shape = shape
        this.color = color
        this.lightSource = lightSource
        this.alpha = alpha
        this.fillAlpha = fillAlpha
        this.width = width
        cache.clear()
        invalidateDraw()
    }

    override fun ContentDrawScope.draw() {
        val outline = cache.outline(shape, size, layoutDirection, this)
        val strokeWidth = width.toPx()
        val tint = color.takeOrElse { currentValueOf(LocalContentColor) }
        val start = lightSource.start
        val end = lightSource.end
        drawContent()
        if (fillAlpha > 0) {
            val fillColors = listOf(tint.copy(alpha = fillAlpha), Color.Transparent)
            drawOutline(outline, Brush.linearGradient(fillColors, start, end), style = Fill)
        }
        if (strokeWidth > 0) {
            val strokeColors = listOf(tint.copy(alpha = alpha), Color.Transparent)
            drawOutline(outline, Brush.linearGradient(strokeColors, start, end), style = Stroke(strokeWidth))
        }
    }
}

enum class LightSource(val start: Offset, val end: Offset) {
    TopLeft(Offset.Zero, Offset.Infinite),
    Top(Offset.Zero, Offset(0f, Float.POSITIVE_INFINITY)),
    TopRight(Offset(Float.POSITIVE_INFINITY, 0f), Offset(0f, Float.POSITIVE_INFINITY)),
    Right(Offset(Float.POSITIVE_INFINITY, 0f), Offset.Zero),
    BottomRight(Offset.Infinite, Offset.Zero),
    Bottom(Offset(0f, Float.POSITIVE_INFINITY), Offset.Zero),
    BottomLeft(Offset(0f, Float.POSITIVE_INFINITY), Offset(Float.POSITIVE_INFINITY, 0f)),
    Left(Offset.Zero, Offset(Float.POSITIVE_INFINITY, 0f))
}