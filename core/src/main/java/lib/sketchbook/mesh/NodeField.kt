package lib.sketchbook.mesh

import kotlin.math.cos
import kotlin.math.exp
import kotlin.math.hypot
import kotlin.math.roundToInt
import kotlin.math.sin
import kotlin.random.Random

internal const val MaxNodes = 120

internal fun nodeCount(widthPx: Float, heightPx: Float, cellPx: Float, density: Float): Int {
    val raw = widthPx * heightPx / (cellPx * cellPx) * density
    return if (raw.isFinite()) raw.roundToInt().coerceIn(0, MaxNodes) else 0
}

internal class Touch(val x: Float, val y: Float, val radius: Float)

internal class Node(x: Float, y: Float, driftX: Float, driftY: Float) {
    var x = x
        internal set
    var y = y
        internal set
    var driftX = driftX
        internal set
    var driftY = driftY
        internal set
    internal var pushX = 0f
    internal var pushY = 0f
}

internal interface NodeField {
    val nodes: List<Node>

    fun step(dtMillis: Float, width: Float, height: Float, touch: Touch?, speed: Float)
}

internal class DriftingNodeField(seed: Int, count: Int) : NodeField {

    private val random = Random(seed)
    private val unit = List(count) { random.nextFloat() to random.nextFloat() }
    private var lastWidth = 0f
    private var lastHeight = 0f

    override val nodes: List<Node> = List(count) {
        val angle = random.nextFloat() * TwoPi
        val magnitude = 0.5f + random.nextFloat()
        Node(0f, 0f, cos(angle) * magnitude, sin(angle) * magnitude)
    }

    override fun step(dtMillis: Float, width: Float, height: Float, touch: Touch?, speed: Float) {
        place(width, height)
        val dt = dtMillis / 1000f
        val decay = exp(-dt / PushDecaySeconds)
        nodes.forEach { node ->
            if (touch != null) repel(node, touch, dt)
            node.pushX *= decay
            node.pushY *= decay
            advance(node, dt, width, height, speed)
        }
    }

    private fun place(width: Float, height: Float) {
        if (width == lastWidth && height == lastHeight) return
        if (lastWidth > 0f && lastHeight > 0f) {
            val sx = width / lastWidth
            val sy = height / lastHeight
            nodes.forEach { node ->
                node.x *= sx
                node.y *= sy
            }
        } else {
            nodes.forEachIndexed { i, node ->
                node.x = unit[i].first * width
                node.y = unit[i].second * height
            }
        }
        lastWidth = width
        lastHeight = height
    }

    private fun repel(node: Node, touch: Touch, dt: Float) {
        if (!(touch.radius > 0f && touch.radius.isFinite())) return
        val dx = node.x - touch.x
        val dy = node.y - touch.y
        val distance = hypot(dx, dy)
        if (distance >= touch.radius) return
        val falloff = 1f - distance / touch.radius
        val force = falloff * falloff * touch.radius * RepelGain * dt
        val nx = if (distance > 0f) dx / distance else 1f
        val ny = if (distance > 0f) dy / distance else 0f
        node.pushX += nx * force
        node.pushY += ny * force
    }

    private fun advance(node: Node, dt: Float, width: Float, height: Float, speed: Float) {
        node.x += (node.driftX * speed + node.pushX) * dt
        node.y += (node.driftY * speed + node.pushY) * dt
        if (node.x < 0f) {
            node.x = 0f
            if (node.driftX < 0f) node.driftX = -node.driftX
            if (node.pushX < 0f) node.pushX = 0f
        } else if (node.x > width) {
            node.x = width
            if (node.driftX > 0f) node.driftX = -node.driftX
            if (node.pushX > 0f) node.pushX = 0f
        }
        if (node.y < 0f) {
            node.y = 0f
            if (node.driftY < 0f) node.driftY = -node.driftY
            if (node.pushY < 0f) node.pushY = 0f
        } else if (node.y > height) {
            node.y = height
            if (node.driftY > 0f) node.driftY = -node.driftY
            if (node.pushY > 0f) node.pushY = 0f
        }
    }

    private companion object {
        const val TwoPi = (2 * Math.PI).toFloat()
        const val PushDecaySeconds = 0.6f
        const val RepelGain = 12f
    }
}
