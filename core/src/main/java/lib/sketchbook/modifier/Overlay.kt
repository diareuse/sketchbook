package lib.sketchbook.modifier

import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.graphics.drawscope.ContentDrawScope
import androidx.compose.ui.node.CompositionLocalConsumerModifierNode
import androidx.compose.ui.node.DrawModifierNode
import androidx.compose.ui.node.ModifierNodeElement
import androidx.compose.ui.node.currentValueOf
import lib.sketchbook.theme.LocalStyle

fun Modifier.overlay(): Modifier = this then ThemeOverlayElement

private data object ThemeOverlayElement : ModifierNodeElement<ThemeOverlayNode>() {

    override fun create() = ThemeOverlayNode()

    override fun update(node: ThemeOverlayNode) = Unit
}

private class ThemeOverlayNode : Modifier.Node(), DrawModifierNode, CompositionLocalConsumerModifierNode {

    override fun ContentDrawScope.draw() {
        val colors = listOf(Color.Transparent, currentValueOf(LocalStyle).container.background)
        drawContent()
        drawRect(Brush.verticalGradient(colors))
    }
}

fun Modifier.overlay(
    colorBottom: Color,
    colorTop: Color = Color.Transparent
) = let {
    val colors = listOf(colorTop, colorBottom)
    val brush = Brush.verticalGradient(colors)
    drawWithContent {
        drawContent()
        drawRect(brush)
    }
}

fun Modifier.verticalOverlay(
    height: Dp,
    gravity: VerticalGravity = VerticalGravity.Top
) = verticalOverlay(FadeColors, gravity) { height.toPx() }

fun Modifier.verticalOverlay(
    percent: Float,
    gravity: VerticalGravity = VerticalGravity.Top
) = verticalOverlay(FadeColors, gravity) { it.height * percent }

private val FadeColors = listOf(
    Color.White,
    Color.White.copy(alpha = .3f),
    Color.White.copy(alpha = .1f),
    Color.Transparent
)

fun Modifier.verticalOverlay(
    colors: List<Color>,
    gravity: VerticalGravity,
    height: Density.(Size) -> Float
) = graphicsLayer(compositingStrategy = CompositingStrategy.Offscreen).drawWithContent {
    val heightPx = height(size)
    val topLeft = gravity.getOffset(size, heightPx)
    val brushBounds = gravity.getBrushBounds(size, heightPx)
    val size = size.copy(height = heightPx)
    drawContent()
    val brush = Brush.verticalGradient(
        colors = colors,
        startY = brushBounds.start,
        endY = brushBounds.endInclusive
    )
    drawRect(brush, topLeft, size, blendMode = BlendMode.DstIn)
}

enum class VerticalGravity {
    Top, Bottom;

    fun getOffset(size: Size, height: Float) = when (this) {
        Top -> Offset.Zero
        Bottom -> Offset(0f, size.height - height)
    }

    fun getBrushBounds(size: Size, height: Float): ClosedFloatingPointRange<Float> = when (this) {
        Top -> getOffset(size, height).run {
            y + height..y
        }

        Bottom -> getOffset(size, height).run {
            y..y + height
        }
    }
}