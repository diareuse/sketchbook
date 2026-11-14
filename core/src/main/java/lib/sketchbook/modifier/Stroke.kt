package lib.sketchbook.modifier

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.node.DrawModifierNode
import androidx.compose.ui.node.ModifierNodeElement
import androidx.compose.ui.node.invalidateDraw
import androidx.compose.ui.graphics.drawscope.ContentDrawScope
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawOutline
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import lib.sketchbook.theme.Theme

fun Modifier.stroke(
    color: Color,
    shape: Shape,
    width: Dp = 4.dp,
    stride: Dp = 4.dp
): Modifier = this then StrokeElement(color, shape, width, stride)

private data class StrokeElement(
    val color: Color,
    val shape: Shape,
    val width: Dp,
    val stride: Dp
) : ModifierNodeElement<StrokeNode>() {

    override fun create() = StrokeNode(color, shape, width, stride)

    override fun update(node: StrokeNode) {
        node.update(color, shape, width, stride)
    }
}

private class StrokeNode(
    private var color: Color,
    private var shape: Shape,
    private var width: Dp,
    private var stride: Dp
) : Modifier.Node(), DrawModifierNode {

    private val cache = OutlineCache()

    fun update(color: Color, shape: Shape, width: Dp, stride: Dp) {
        this.color = color
        this.shape = shape
        this.width = width
        this.stride = stride
        cache.clear()
        invalidateDraw()
    }

    override fun ContentDrawScope.draw() {
        val outline = cache.outline(shape, size, layoutDirection, this)
        val strokeWidth = width.toPx()
        val strideSize = stride.toPx()
        clipPath(cache.path(shape, size, layoutDirection, this)) {
            this@draw.drawContent()
        }
        drawOutline(
            outline = outline,
            color = color,
            style = Stroke(
                width = strokeWidth,
                miter = 0f,
                join = StrokeJoin.Round,
                cap = StrokeCap.Round,
                pathEffect = PathEffect.dashPathEffect(
                    floatArrayOf(strideSize - strokeWidth / 2, strideSize + strokeWidth),
                    0f
                )
            )
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun Preview() {
    Theme {
        Box(
            modifier = Modifier
                .size(128.dp)
                .padding(16.dp)
                .stroke(Color.Black, Theme.container.card)
        )
    }
}