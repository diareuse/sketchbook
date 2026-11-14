package lib.sketchbook.indication

import android.graphics.RuntimeShader
import androidx.annotation.RequiresApi
import androidx.compose.foundation.interaction.InteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.material3.LocalContentColor
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.isSpecified
import androidx.compose.ui.graphics.ShaderBrush
import androidx.compose.ui.graphics.drawscope.ContentDrawScope
import androidx.compose.ui.node.CompositionLocalConsumerModifierNode
import androidx.compose.ui.node.DrawModifierNode
import androidx.compose.ui.node.currentValueOf
import androidx.compose.ui.node.invalidateDraw
import kotlinx.coroutines.launch
import lib.sketchbook.shader.Agsl
import lib.sketchbook.shader.FrameLoop
import lib.sketchbook.shader.setColor
import lib.sketchbook.util.Api

@RequiresApi(Api.Agsl)
internal class PressShaderNode(
    private val interactionSource: InteractionSource,
    private val color: Color
) : Modifier.Node(), DrawModifierNode, CompositionLocalConsumerModifierNode {

    private val shader by lazy(LazyThreadSafetyMode.NONE) { RuntimeShader(Agsl.PressRipple) }
    private val brush by lazy(LazyThreadSafetyMode.NONE) { ShaderBrush(shader) }

    private var pressed = false
    private var center = Offset.Zero
    private var startNanos = -1L
    private var releaseNanos = -1L
    private var progress = 0f
    private var fade = 0f

    private val loop = FrameLoop(::onFrame)

    override fun onAttach() {
        coroutineScope.launch {
            interactionSource.interactions.collect { interaction ->
                when (interaction) {
                    is PressInteraction.Press -> press(interaction.pressPosition)
                    is PressInteraction.Release, is PressInteraction.Cancel -> release()
                }
            }
        }
    }

    override fun onDetach() {
        loop.stop()
        pressed = false
        fade = 0f
    }

    private fun press(position: Offset) {
        pressed = true
        center = position
        startNanos = -1L
        releaseNanos = -1L
        progress = 0f
        fade = 1f
        loop.start(coroutineScope)
    }

    private fun release() {
        if (!pressed) return
        pressed = false
        releaseNanos = -1L
        loop.start(coroutineScope)
    }

    private fun onFrame(nanos: Long): Boolean {
        if (startNanos < 0) startNanos = nanos
        if (!pressed && releaseNanos < 0) releaseNanos = nanos
        progress = ((nanos - startNanos).toFloat() / ExpandNanos).coerceIn(0f, 1f).let { 1f - (1f - it) * (1f - it) }
        fade = if (pressed) 1f else 1f - ((nanos - releaseNanos).toFloat() / FadeNanos).coerceIn(0f, 1f)
        invalidateDraw()
        return pressed || fade > 0f
    }

    override fun ContentDrawScope.draw() {
        drawContent()
        if (fade <= 0f) return
        val base = if (color.isSpecified) color else currentValueOf(LocalContentColor)
        shader.setFloatUniform("size", size.width, size.height)
        shader.setFloatUniform("center", center.x, center.y)
        shader.setFloatUniform("progress", progress)
        shader.setFloatUniform("fade", fade)
        shader.setColor("color", base.copy(alpha = .5f))
        drawRect(brush)
    }

    private companion object {
        const val ExpandNanos = 280_000_000L
        const val FadeNanos = 320_000_000L
    }
}
