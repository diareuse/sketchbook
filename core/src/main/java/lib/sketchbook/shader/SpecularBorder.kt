package lib.sketchbook.shader

import android.graphics.Matrix
import android.graphics.RuntimeShader
import android.graphics.SweepGradient
import androidx.annotation.RequiresApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.ShaderBrush
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.drawOutline
import androidx.compose.ui.graphics.drawscope.ContentDrawScope
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.node.DrawModifierNode
import androidx.compose.ui.node.ModifierNodeElement
import androidx.compose.ui.node.invalidateDraw
import androidx.compose.ui.unit.Dp
import lib.sketchbook.modifier.OutlineCache
import lib.sketchbook.util.Api
import lib.sketchbook.util.sdkAtLeast

internal fun interface RimPainter {
    fun DrawScope.paint(outline: Outline, width: Float, color: Color, phase: Float)
}

@RequiresApi(Api.Agsl)
internal class AgslRimPainter : RimPainter {
    private val shader = RuntimeShader(Agsl.SpecularRimSweep)
    private val brush = ShaderBrush(shader)

    override fun DrawScope.paint(outline: Outline, width: Float, color: Color, phase: Float) {
        shader.setFloatUniform("size", size.width, size.height)
        shader.setFloatUniform("angle", (phase * 2f - 1f) * Math.PI.toFloat())
        shader.setColor("color", color)
        drawOutline(outline, brush, style = Stroke(width))
    }
}

internal class SweepRimPainter : RimPainter {
    private var sweep: SweepGradient? = null
    private var brush: ShaderBrush? = null
    private var sweepSize = Size.Unspecified
    private var sweepColor = Color.Unspecified
    private val matrix = Matrix()

    override fun DrawScope.paint(outline: Outline, width: Float, color: Color, phase: Float) {
        if (sweep == null || sweepSize != size || sweepColor != color) {
            val gradient = SweepGradient(
                size.width / 2f,
                size.height / 2f,
                intArrayOf(
                    color.copy(alpha = color.alpha * .95f).toArgb(),
                    color.copy(alpha = color.alpha * .2f).toArgb(),
                    color.copy(alpha = color.alpha * .15f).toArgb(),
                    color.copy(alpha = color.alpha * .2f).toArgb(),
                    color.copy(alpha = color.alpha * .95f).toArgb()
                ),
                floatArrayOf(0f, .1f, .5f, .9f, 1f)
            )
            sweep = gradient
            brush = ShaderBrush(gradient)
            sweepSize = size
            sweepColor = color
        }
        matrix.setRotate(phase * 360f, size.width / 2f, size.height / 2f)
        sweep?.setLocalMatrix(matrix)
        drawOutline(outline, checkNotNull(brush), style = Stroke(width))
    }
}

internal fun rimPainter(): RimPainter =
    if (sdkAtLeast(Api.Agsl)) AgslRimPainter() else SweepRimPainter()

internal data class SpecularBorderElement(
    val shape: Shape,
    val width: Dp,
    val color: Color,
    val durationMillis: Int,
    val animated: Boolean
) : ModifierNodeElement<SpecularBorderNode>() {
    override fun create() = SpecularBorderNode(shape, width, color, durationMillis, animated)

    override fun update(node: SpecularBorderNode) = node.update(shape, width, color, durationMillis, animated)
}

internal class SpecularBorderNode(
    private var shape: Shape,
    private var width: Dp,
    private var color: Color,
    durationMillis: Int,
    private var animated: Boolean
) : Modifier.Node(), DrawModifierNode {

    private var phase = 0f
    private val cache = OutlineCache()
    private val painter by lazy(LazyThreadSafetyMode.NONE) { rimPainter() }
    private val driver = PhaseDriver(durationMillis) {
        phase = it
        invalidateDraw()
    }

    override fun onAttach() = sync()

    override fun onDetach() = driver.stop()

    fun update(shape: Shape, width: Dp, color: Color, durationMillis: Int, animated: Boolean) {
        if (this.shape != shape) cache.clear()
        this.shape = shape
        this.width = width
        this.color = color
        this.animated = animated
        driver.duration(durationMillis)
        sync()
        invalidateDraw()
    }

    private fun sync() {
        driver.run(animated && isAttached, coroutineScope)
    }

    override fun ContentDrawScope.draw() {
        drawContent()
        val outline = cache.outline(shape, size, layoutDirection, this)
        with(painter) { paint(outline, width.toPx(), color, phase) }
    }
}
