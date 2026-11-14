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
import androidx.compose.ui.node.CompositionLocalConsumerModifierNode
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
    durationMillis: Int,
    private var enabled: Boolean
) : Modifier.Node(), DrawModifierNode, CompositionLocalConsumerModifierNode {

    private var phase = 0f
    private val painter by lazy(LazyThreadSafetyMode.NONE) { shimmerPainter() }
    private val driver = PhaseDriver(durationMillis) {
        phase = it
        invalidateDraw()
    }

    override fun onAttach() = sync()

    override fun onDetach() = driver.stop()

    fun update(color: Color, durationMillis: Int, enabled: Boolean) {
        this.color = color
        this.enabled = enabled
        driver.duration(durationMillis)
        sync()
        invalidateDraw()
    }

    private fun sync() {
        driver.run(enabled && isAttached && systemAnimationsAllowed(), coroutineScope)
    }

    override fun ContentDrawScope.draw() {
        drawContent()
        if (enabled) with(painter) { paint(color, phase) }
    }
}

internal fun periodNanos(durationMillis: Int): Long = durationMillis.coerceAtLeast(1) * 1_000_000L
