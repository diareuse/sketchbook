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

internal const val MeshColorCount = 4

internal fun interface MeshPainter {
    fun DrawScope.paint(base: Color, colors: List<Color>, phase: Float)
}

@RequiresApi(Api.Agsl)
internal class AgslMeshPainter : MeshPainter {
    private val shader = RuntimeShader(Agsl.MeshGradient)
    private val brush = ShaderBrush(shader)

    override fun DrawScope.paint(base: Color, colors: List<Color>, phase: Float) {
        shader.setFloatUniform("size", size.width, size.height)
        shader.setFloatUniform("time", phase * TwoPi)
        shader.setOklab("base", base)
        shader.setOklab("c0", colors[0])
        shader.setOklab("c1", colors[1])
        shader.setOklab("c2", colors[2])
        shader.setOklab("c3", colors[3])
        drawRect(brush)
    }

    private fun RuntimeShader.setOklab(name: String, color: Color) {
        val lab = color.toOklab()
        setFloatUniform(name, lab.l, lab.a, lab.b, color.alpha)
    }
}

internal class LayeredRadialMeshPainter : MeshPainter {
    override fun DrawScope.paint(base: Color, colors: List<Color>, phase: Float) {
        val t = phase * TwoPi
        val unit = Aurora.unit(size.width, size.height)
        drawRect(base)
        Aurora.blobs.forEachIndexed { i, blob ->
            val center = Aurora.center(blob, t).let { Offset(it.x * size.width, it.y * size.height) }
            val radius = blob.radius * unit * FalloffReach
            val stops = Array(FalloffStops) { step ->
                val x = step / (FalloffStops - 1f)
                val alpha = Aurora.alphaAt(Aurora.weightAt(x * FalloffReach * blob.radius, blob)) * colors[i].alpha
                x to colors[i].copy(alpha = alpha)
            }
            drawRect(Brush.radialGradient(colorStops = stops, center = center, radius = radius))
        }
    }
}

private const val FalloffStops = 9
private const val FalloffReach = 2f

private const val TwoPi = (2 * Math.PI).toFloat()

internal fun meshPainter(): MeshPainter =
    if (sdkAtLeast(Api.Agsl)) AgslMeshPainter() else LayeredRadialMeshPainter()

internal fun List<Color>.toMeshColors(): List<Color> {
    require(size in 1..MeshColorCount) { "colors must hold 1 to $MeshColorCount entries" }
    return List(MeshColorCount) { this[it % size] }
}

internal data class MeshGradientElement(
    val colors: List<Color>,
    val durationMillis: Int,
    val animated: Boolean,
    val base: Color
) : ModifierNodeElement<MeshGradientNode>() {
    override fun create() = MeshGradientNode(colors, durationMillis, animated, base)

    override fun update(node: MeshGradientNode) = node.update(colors, durationMillis, animated, base)
}

internal class MeshGradientNode(
    private var colors: List<Color>,
    durationMillis: Int,
    private var animated: Boolean,
    private var base: Color
) : Modifier.Node(), DrawModifierNode, CompositionLocalConsumerModifierNode {

    private var phase = 0f
    private val painter by lazy(LazyThreadSafetyMode.NONE) { meshPainter() }
    private val driver = PhaseDriver(durationMillis) {
        phase = it
        invalidateDraw()
    }

    override fun onAttach() = sync()

    override fun onDetach() = driver.stop()

    fun update(colors: List<Color>, durationMillis: Int, animated: Boolean, base: Color) {
        this.colors = colors
        this.base = base
        this.animated = animated
        driver.duration(durationMillis)
        sync()
        invalidateDraw()
    }

    private fun sync() {
        driver.run(animated && isAttached && systemAnimationsAllowed(), coroutineScope)
    }

    override fun ContentDrawScope.draw() {
        with(painter) { paint(base, colors, phase) }
        drawContent()
    }
}
