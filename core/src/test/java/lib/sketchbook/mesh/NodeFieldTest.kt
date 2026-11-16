package lib.sketchbook.mesh

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class NodeFieldTest {

    private val width = 400f
    private val height = 300f

    private fun field(seed: Int = 1, count: Int = 30, speed: Float = 40f) = DriftingNodeField(seed, count, speed)

    private fun NodeField.positions() = nodes.map { it.x to it.y }

    private fun NodeField.run(steps: Int, touch: Touch? = null) =
        repeat(steps) { step(16f, width, height, touch) }

    @Test
    fun `field holds the requested number of nodes`() {
        assertEquals(30, field(count = 30).nodes.size)
    }

    @Test
    fun `same seed gives the same positions`() {
        val a = field(seed = 7).also { it.step(0f, width, height, null) }
        val b = field(seed = 7).also { it.step(0f, width, height, null) }

        assertEquals(a.positions(), b.positions())
    }

    @Test
    fun `different seeds give different positions`() {
        val a = field(seed = 1).also { it.step(0f, width, height, null) }
        val b = field(seed = 2).also { it.step(0f, width, height, null) }

        assertTrue(a.positions() != b.positions())
    }

    @Test
    fun `nodes drift over time`() {
        val f = field().also { it.step(0f, width, height, null) }
        val before = f.positions()

        f.run(60)

        assertTrue(before != f.positions())
    }

    @Test
    fun `nodes stay within bounds while drifting`() {
        val f = field(speed = 300f)

        repeat(500) {
            f.run(1)
            assertTrue(f.nodes.all { it.x in 0f..width && it.y in 0f..height })
        }
    }

    @Test
    fun `zero speed keeps nodes still`() {
        val f = field(speed = 0f).also { it.step(0f, width, height, null) }
        val before = f.positions()

        f.run(60)

        assertEquals(before, f.positions())
    }

    @Test
    fun `touch pushes nearby nodes away from the touch point`() {
        val f = field(speed = 0f).also { it.step(0f, width, height, null) }
        val touch = Touch(200f, 150f, 120f)
        val before = f.nodes.filter { distance(it, touch) < 100f }.associateWith { distance(it, touch) }

        f.run(10, touch)

        assertTrue(before.isNotEmpty())
        assertTrue(before.all { (node, d) -> distance(node, touch) > d })
    }

    @Test
    fun `touch leaves nodes outside its radius untouched`() {
        val f = field(speed = 0f).also { it.step(0f, width, height, null) }
        val touch = Touch(200f, 150f, 60f)
        val far = f.nodes.filter { distance(it, touch) > 60f }
        val before = far.map { it.x to it.y }

        f.run(10, touch)

        assertEquals(before, far.map { it.x to it.y })
    }

    @Test
    fun `nodes resume drifting after touch is released`() {
        val f = field(speed = 40f).also { it.step(0f, width, height, null) }
        f.run(10, Touch(200f, 150f, 120f))
        f.run(200)
        val settled = f.positions()

        f.run(30)

        assertTrue(settled != f.positions())
    }

    private fun distance(node: Node, touch: Touch) =
        kotlin.math.hypot(node.x - touch.x, node.y - touch.y)
}
