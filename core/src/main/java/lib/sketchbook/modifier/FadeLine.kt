package lib.sketchbook.modifier

import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.ContentDrawScope
import androidx.compose.ui.node.DrawModifierNode
import androidx.compose.ui.node.ModifierNodeElement
import androidx.compose.ui.node.invalidateDraw
import androidx.compose.ui.unit.Dp

internal data class FadeLineElement(
    val vertical: Boolean,
    val color: Color,
    val fadeLength: Dp
) : ModifierNodeElement<FadeLineNode>() {
    override fun create() = FadeLineNode(vertical, color, fadeLength)

    override fun update(node: FadeLineNode) = node.update(vertical, color, fadeLength)
}

internal class FadeLineNode(
    private var vertical: Boolean,
    private var color: Color,
    private var fadeLength: Dp
) : Modifier.Node(), DrawModifierNode {

    private var brush: Brush? = null
    private var brushLength = -1f
    private var brushFade = -1f

    fun update(vertical: Boolean, color: Color, fadeLength: Dp) {
        this.vertical = vertical
        this.color = color
        this.fadeLength = fadeLength
        brush = null
        invalidateDraw()
    }

    override fun ContentDrawScope.draw() {
        val length = if (vertical) size.height else size.width
        if (length <= 0f) return
        val fade = (fadeLength.toPx() / length).coerceIn(0f, .5f)
        val cached = brush
        val current = if (cached != null && brushLength == length && brushFade == fade) cached else {
            val end = if (vertical) Offset(0f, length) else Offset(length, 0f)
            Brush.linearGradient(
                0f to Color.Transparent,
                fade to color,
                1f - fade to color,
                1f to Color.Transparent,
                start = Offset.Zero,
                end = end
            ).also {
                brush = it
                brushLength = length
                brushFade = fade
            }
        }
        drawRect(current)
    }
}
