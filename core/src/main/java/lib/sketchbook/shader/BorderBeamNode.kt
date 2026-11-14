package lib.sketchbook.shader

import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.drawscope.ContentDrawScope
import androidx.compose.ui.node.CompositionLocalConsumerModifierNode
import androidx.compose.ui.node.DrawModifierNode
import androidx.compose.ui.node.ModifierNodeElement
import androidx.compose.ui.node.invalidateDraw
import lib.sketchbook.modifier.OutlineCache
import kotlin.math.max
import kotlin.math.min

internal data class BorderBeamElement(
    val shape: Shape,
    val look: BeamLook,
    val active: Boolean,
    val animated: Boolean,
    val durationMillis: Int
) : ModifierNodeElement<BorderBeamNode>() {
    override fun create() = BorderBeamNode(shape, look, active, animated, durationMillis)

    override fun update(node: BorderBeamNode) = node.update(shape, look, active, animated, durationMillis)
}

internal class BorderBeamNode(
    private var shape: Shape,
    private var look: BeamLook,
    private var active: Boolean,
    private var animated: Boolean,
    durationMillis: Int
) : Modifier.Node(), DrawModifierNode, CompositionLocalConsumerModifierNode {

    private var phase = StaticPhase
    private var fade = if (active) 1f else 0f
    private var fadeTarget = fade
    private var lastFadeNanos = -1L
    private val cache = OutlineCache()
    private var sampledOutline: Outline? = null
    private var geometry: BeamGeometry? = null
    private val painter by lazy(LazyThreadSafetyMode.NONE) { beamPainter() }
    private val driver = PhaseDriver(durationMillis) {
        phase = it
        invalidateDraw()
    }
    private val fader = FrameLoop(::fadeStep)

    override fun onAttach() = sync()

    override fun onDetach() {
        driver.stop()
        fader.stop()
        lastFadeNanos = -1L
    }

    fun update(shape: Shape, look: BeamLook, active: Boolean, animated: Boolean, durationMillis: Int) {
        if (this.shape != shape) {
            cache.clear()
            sampledOutline = null
            geometry = null
        }
        this.shape = shape
        this.look = look
        this.active = active
        this.animated = animated
        driver.duration(durationMillis)
        sync()
        invalidateDraw()
    }

    private fun sync() {
        if (!isAttached) return
        fadeTarget = if (active) 1f else 0f
        if (!animated || !systemAnimationsAllowed()) {
            driver.stop()
            fader.stop()
            phase = StaticPhase
            fade = fadeTarget
            invalidateDraw()
            return
        }
        driver.run(fade > 0f || fadeTarget > 0f, coroutineScope)
        if (fade != fadeTarget) fader.start(coroutineScope)
    }

    private fun fadeStep(nanos: Long): Boolean {
        val elapsed = if (lastFadeNanos < 0) 0f else (nanos - lastFadeNanos) / NanosPerSecond
        lastFadeNanos = nanos
        val delta = elapsed / FadeSeconds
        fade = if (fade < fadeTarget) min(fadeTarget, fade + delta) else max(fadeTarget, fade - delta)
        invalidateDraw()
        if (fade != fadeTarget) return true
        lastFadeNanos = -1L
        if (fade <= 0f) driver.stop()
        return false
    }

    override fun ContentDrawScope.draw() {
        drawContent()
        if (fade <= 0f || look.intensity <= 0f || size.minDimension <= 0f) return
        with(painter) { paint(geometry(this@draw), look, phase, fade) }
    }

    private fun ContentDrawScope.geometry(scope: ContentDrawScope): BeamGeometry {
        val outline = cache.outline(shape, size, layoutDirection, scope)
        val current = geometry
        if (current != null && sampledOutline === outline) return current
        sampledOutline = outline
        return BeamGeometry(cache.path(shape, size, layoutDirection, scope), sampleOutline(outline, density))
            .also { geometry = it }
    }

    private companion object {
        const val StaticPhase = .15f
        const val FadeSeconds = .6f
        const val NanosPerSecond = 1_000_000_000f
    }
}
