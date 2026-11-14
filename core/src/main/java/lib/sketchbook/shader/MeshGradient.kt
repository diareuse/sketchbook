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
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.sin

internal const val MeshColorCount = 4

internal fun interface MeshPainter {
    fun DrawScope.paint(colors: List<Color>, phase: Float)
}

@RequiresApi(Api.Agsl)
internal class AgslMeshPainter : MeshPainter {
    private val shader = RuntimeShader(Agsl.MeshGradient)
    private val brush = ShaderBrush(shader)

    override fun DrawScope.paint(colors: List<Color>, phase: Float) {
        shader.setFloatUniform("size", size.width, size.height)
        shader.setFloatUniform("time", phase * TwoPi)
        shader.setColor("c0", colors[0])
        shader.setColor("c1", colors[1])
        shader.setColor("c2", colors[2])
        shader.setColor("c3", colors[3])
        drawRect(brush)
    }
}

internal class LayeredRadialMeshPainter : MeshPainter {
    override fun DrawScope.paint(colors: List<Color>, phase: Float) {
        val t = phase * TwoPi
        val radius = max(size.width, size.height) * .8f
        drawRect(colors[0])
        for (i in 1 until MeshColorCount) {
            val center = Offset(
                size.width * (.5f + .4f * cos(t + i * 2.1f)),
                size.height * (.5f + .4f * sin(t + i * 1.7f))
            )
            drawRect(Brush.radialGradient(listOf(colors[i], colors[i].copy(alpha = 0f)), center, radius))
        }
    }
}

private const val TwoPi = (2 * Math.PI).toFloat()

internal fun meshPainter(): MeshPainter =
    if (sdkAtLeast(Api.Agsl)) AgslMeshPainter() else LayeredRadialMeshPainter()

internal fun List<Color>.toMeshColors(): List<Color> {
    require(isNotEmpty()) { "colors must not be empty" }
    return List(MeshColorCount) { this[it % size] }
}

internal data class MeshGradientElement(
    val colors: List<Color>,
    val durationMillis: Int,
    val animated: Boolean
) : ModifierNodeElement<MeshGradientNode>() {
    override fun create() = MeshGradientNode(colors, durationMillis, animated)

    override fun update(node: MeshGradientNode) = node.update(colors, durationMillis, animated)
}

internal class MeshGradientNode(
    private var colors: List<Color>,
    private var durationMillis: Int,
    private var animated: Boolean
) : Modifier.Node(), DrawModifierNode {

    private var phase = 0f
    private var clock = Phase(periodNanos(durationMillis))
    private val painter by lazy(LazyThreadSafetyMode.NONE) { meshPainter() }
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

    fun update(colors: List<Color>, durationMillis: Int, animated: Boolean) {
        this.colors = colors
        this.animated = animated
        if (this.durationMillis != durationMillis) {
            this.durationMillis = durationMillis
            clock = Phase(periodNanos(durationMillis))
        }
        sync()
        invalidateDraw()
    }

    private fun sync() {
        if (animated && isAttached) loop.start(coroutineScope) else loop.stop()
    }

    override fun ContentDrawScope.draw() {
        with(painter) { paint(colors, phase) }
        drawContent()
    }
}
