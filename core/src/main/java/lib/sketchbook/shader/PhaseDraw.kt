package lib.sketchbook.shader

import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.drawscope.ContentDrawScope
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.node.CompositionLocalConsumerModifierNode
import androidx.compose.ui.node.DrawModifierNode
import androidx.compose.ui.node.ModifierNodeElement
import androidx.compose.ui.node.invalidateDraw

internal fun interface PhasePainter {
    fun DrawScope.paint(phase: Float)
}

internal fun Modifier.loopingDraw(painter: PhasePainter, durationMillis: Int): Modifier =
    this then PhaseDrawElement(painter, durationMillis)

internal data class PhaseDrawElement(
    val painter: PhasePainter,
    val durationMillis: Int
) : ModifierNodeElement<PhaseDrawNode>() {
    override fun create() = PhaseDrawNode(painter, durationMillis)

    override fun update(node: PhaseDrawNode) = node.update(painter, durationMillis)
}

internal class PhaseDrawNode(
    private var painter: PhasePainter,
    durationMillis: Int
) : Modifier.Node(), DrawModifierNode, CompositionLocalConsumerModifierNode {

    private var phase = 0f
    private val driver = PhaseDriver(durationMillis) {
        phase = it
        invalidateDraw()
    }

    override fun onAttach() = sync()

    override fun onDetach() = driver.stop()

    fun update(painter: PhasePainter, durationMillis: Int) {
        this.painter = painter
        driver.duration(durationMillis)
        sync()
        invalidateDraw()
    }

    private fun sync() {
        driver.run(isAttached && systemAnimationsAllowed(), coroutineScope)
    }

    override fun ContentDrawScope.draw() {
        drawContent()
        with(painter) { paint(phase) }
    }
}
