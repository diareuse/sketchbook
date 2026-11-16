package lib.sketchbook.mesh

import kotlin.math.cos
import kotlin.math.exp
import kotlin.math.hypot
import kotlin.math.sin
import kotlin.random.Random

internal class Touch(val x: Float, val y: Float, val radius: Float)

internal class Node(x: Float, y: Float, driftX: Float, driftY: Float) {
    var driftX = driftX
        internal set
    var driftY = driftY
        internal set
    var x = x
        internal set
    var y = y
        internal set
    internal var pushX = 0f
    internal var pushY = 0f
}

internal interface NodeField {
    val nodes: List<Node>

    fun step(dtMillis: Float, width: Float, height: Float, touch: Touch?)
}

internal class DriftingNodeField(seed: Int, count: Int, speed: Float) : NodeField {

    private val random = Random(seed)
    private val unit = List(count) { Triple(random.nextFloat(), random.nextFloat(), random.nextFloat() * TwoPi) }
    private var placed = false
    private var lastWidth = 0f
    private var lastHeight = 0f

    override val nodes: List<Node> = unit.map { (_, _, angle) ->
        val magnitude = speed * (0.5f + random.nextFloat())
        Node(0f, 0f, cos(angle) * magnitude, sin(angle) * magnitude)
    }

    override fun step(dtMillis: Float, width: Float, height: Float, touch: Touch?) {
        place(width, height)
        val dt = dtMillis / 1000f
        val decay = exp(-dt / PushDecaySeconds)
        nodes.forEach { node ->
            if (touch != null) repel(node, touch, dt)
            node.pushX *= decay
            node.pushY *= decay
            advance(node, dt, width, height)
        }
    }

    private fun place(width: Float, height: Float) {
        if (placed && width == lastWidth && height == lastHeight) return
        nodes.forEachIndexed { i, node ->
            node.x = unit[i].first * width
            node.y = unit[i].second * height
        }
        placed = true
        lastWidth = width
        lastHeight = height
    }

    private fun repel(node: Node, touch: Touch, dt: Float) {
        val dx = node.x - touch.x
        val dy = node.y - touch.y
        val distance = hypot(dx, dy)
        if (distance >= touch.radius || touch.radius <= 0f) return
        val falloff = 1f - distance / touch.radius
        val force = falloff * falloff * touch.radius * RepelGain * dt
        val nx = if (distance > 0f) dx / distance else 1f
        val ny = if (distance > 0f) dy / distance else 0f
        node.pushX += nx * force
        node.pushY += ny * force
    }

    private fun advance(node: Node, dt: Float, width: Float, height: Float) {
        node.x += (node.driftX + node.pushX) * dt
        node.y += (node.driftY + node.pushY) * dt
        if (node.x < 0f || node.x > width) {
            node.x = node.x.coerceIn(0f, width)
            node.pushX = -node.pushX
            node.driftX = -node.driftX
        }
        if (node.y < 0f || node.y > height) {
            node.y = node.y.coerceIn(0f, height)
            node.pushY = -node.pushY
            node.driftY = -node.driftY
        }
    }

    private companion object {
        const val TwoPi = (2 * Math.PI).toFloat()
        const val PushDecaySeconds = 0.6f
        const val RepelGain = 12f
    }
}
