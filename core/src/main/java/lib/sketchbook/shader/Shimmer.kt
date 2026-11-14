package lib.sketchbook.shader

import android.graphics.RuntimeShader
import androidx.annotation.RequiresApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ShaderBrush
import androidx.compose.ui.graphics.drawscope.ContentDrawScope
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.node.DrawModifierNode
import androidx.compose.ui.node.ModifierNodeElement
import androidx.compose.ui.node.invalidateDraw
import lib.sketchbook.util.Api
import lib.sketchbook.util.sdkAtLeast

internal fun interface ShimmerPainter {
    fun DrawScope.paint(color: Color, phase: Float)
}

@RequiresApi(Api.Agsl)
internal class AgslShimmerPainter : ShimmerPainter {
    private val shader = RuntimeShader(Agsl.Shimmer)
    private val brush = ShaderBrush(shader)

    override fun DrawScope.paint(color: Color, phase: Float) {
        shader.setFloatUniform("size", size.width, size.height)
        shader.setFloatUniform("phase", phase)
        shader.setColor("color", color)
        drawRect(brush)
    }
}

internal class GradientShimmerPainter : ShimmerPainter {
    override fun DrawScope.paint(color: Color, phase: Float) {
        val x = (phase * 1.7f - .35f) * size.width
        val half = size.width * .2f
        val brush = Brush.linearGradient(
            colors = listOf(Color.Transparent, color, Color.Transparent),
            start = Offset(x - half, 0f),
            end = Offset(x + half, size.height * .35f)
        )
        drawRect(brush)
    }
}

internal fun shimmerPainter(): ShimmerPainter =
    if (sdkAtLeast(Api.Agsl)) AgslShimmerPainter() else GradientShimmerPainter()

internal data class ShimmerElement(
    val color: Color,
    val durationMillis: Int,
    val enabled: Boolean
) : ModifierNodeElement<ShimmerNode>() {
    override fun create() = ShimmerNode(color, durationMillis, enabled)

    override fun update(node: ShimmerNode) = node.update(color, durationMillis, enabled)
}

internal class ShimmerNode(
    private var color: Color,
    private var durationMillis: Int,
    private var enabled: Boolean
) : Modifier.Node(), DrawModifierNode {

    private var phase = 0f
    private var clock = Phase(periodNanos(durationMillis))
    private val painter by lazy(LazyThreadSafetyMode.NONE) { shimmerPainter() }
    private val loop = FrameLoop {
        phase = clock.at(it)
        invalidateDraw()
        true
    }

    override fun onAttach() = sync()

    override fun onDetach() {
        loop.stop()
        clock.reset()
    }

    fun update(color: Color, durationMillis: Int, enabled: Boolean) {
        this.color = color
        this.enabled = enabled
        if (this.durationMillis != durationMillis) {
            this.durationMillis = durationMillis
            clock = Phase(periodNanos(durationMillis))
        }
        sync()
        invalidateDraw()
    }

    private fun sync() {
        if (enabled && isAttached) loop.start(coroutineScope) else loop.stop()
    }

    override fun ContentDrawScope.draw() {
        drawContent()
        if (enabled) with(painter) { paint(color, phase) }
    }
}

internal fun periodNanos(durationMillis: Int): Long = durationMillis.coerceAtLeast(1) * 1_000_000L
