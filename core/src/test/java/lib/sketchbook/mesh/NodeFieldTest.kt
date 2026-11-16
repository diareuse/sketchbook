package lib.sketchbook.mesh

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class NodeFieldTest {

    private val width = 400f
    private val height = 300f

    private fun field(seed: Int = 1, count: Int = 30) = DriftingNodeField(seed, count)

    private fun NodeField.positions() = nodes.map { it.x to it.y }

    private fun NodeField.run(steps: Int, touch: Touch? = null, speed: Float = 40f) =
        repeat(steps) { step(16f, width, height, touch, speed) }

    @Test
    fun `field holds the requested number of nodes`() {
        assertEquals(30, field(count = 30).nodes.size)
    }

    @Test
    fun `same seed gives the same positions`() {
        val a = field(seed = 7).also { it.step(0f, width, height, null, 40f) }
        val b = field(seed = 7).also { it.step(0f, width, height, null, 40f) }

        assertEquals(a.positions(), b.positions())
    }

    @Test
    fun `different seeds give different positions`() {
        val a = field(seed = 1).also { it.step(0f, width, height, null, 40f) }
        val b = field(seed = 2).also { it.step(0f, width, height, null, 40f) }

        assertTrue(a.positions() != b.positions())
    }

    @Test
    fun `nodes drift over time`() {
        val f = field().also { it.step(0f, width, height, null, 40f) }
        val before = f.positions()

        f.run(60)

        assertTrue(before != f.positions())
    }

    @Test
    fun `nodes stay within bounds while drifting`() {
        val f = field()

        repeat(500) {
            f.run(1, speed = 300f)
            assertTrue(f.nodes.all { it.x in 0f..width && it.y in 0f..height })
        }
    }

    @Test
    fun `zero speed keeps nodes still`() {
        val f = field().also { it.step(0f, width, height, null, 0f) }
        val before = f.positions()

        f.run(60, speed = 0f)

        assertEquals(before, f.positions())
    }

    @Test
    fun `touch pushes nearby nodes away from the touch point`() {
        val f = field().also { it.step(0f, width, height, null, 0f) }
        val touch = Touch(200f, 150f, 120f)
        val before = f.nodes.filter { distance(it, touch) < 100f }.associateWith { distance(it, touch) }

        f.run(10, touch, 0f)

        assertTrue(before.isNotEmpty())
        assertTrue(before.all { (node, d) -> distance(node, touch) > d })
    }

    @Test
    fun `touch leaves nodes outside its radius untouched`() {
        val f = field().also { it.step(0f, width, height, null, 0f) }
        val touch = Touch(200f, 150f, 60f)
        val far = f.nodes.filter { distance(it, touch) > 60f }
        val before = far.map { it.x to it.y }

        f.run(10, touch, 0f)

        assertEquals(before, far.map { it.x to it.y })
    }

    @Test
    fun `nodes resume drifting after touch is released`() {
        val f = field().also { it.step(0f, width, height, null, 40f) }
        f.run(10, Touch(200f, 150f, 120f), 0f)
        f.run(200)
        val settled = f.positions()

        f.run(30)

        assertTrue(settled != f.positions())
    }

    @Test
    fun `touch with an unusable radius leaves nodes alone`() {
        listOf(0f, -5f, Float.NaN, Float.POSITIVE_INFINITY).forEach { radius ->
            val f = field().also { it.step(0f, width, height, null, 0f) }
            val before = f.positions()

            f.run(10, Touch(200f, 150f, radius), 0f)

            assertEquals(before, f.positions())
        }
    }

    @Test
    fun `touch exactly on a node still pushes it to a finite position`() {
        val f = field().also { it.step(0f, width, height, null, 0f) }
        val node = f.nodes.first()
        val before = node.x to node.y

        f.run(5, Touch(node.x, node.y, 100f), 0f)

        assertTrue(node.x.isFinite() && node.y.isFinite())
        assertTrue(before != node.x to node.y)
    }

    @Test
    fun `node pinned against a wall by touch holds still`() {
        val f = field().also { it.step(0f, width, height, null, 0f) }
        val node = f.nodes.minBy { it.x }
        val touch = Touch(node.x + 40f, node.y, 400f)
        f.run(300, touch, 0f)

        val xs = List(20) { f.run(1, touch, 0f); node.x }

        assertEquals(setOf(0f), xs.toSet())
    }

    @Test
    fun `resizing keeps nodes in the same relative place`() {
        val f = field().also { it.step(0f, width, height, null, 0f) }
        val before = f.positions()

        f.step(0f, width * 2, height / 2, null, 0f)

        assertEquals(before.map { it.first * 2 to it.second / 2 }, f.positions())
    }

    @Test
    fun `node count follows area and density up to the cap`() {
        assertEquals(4, nodeCount(100f, 100f, 50f, 1f))
        assertEquals(MaxNodes, nodeCount(1000f, 1000f, 10f, 100f))
    }

    @Test
    fun `node count is zero for a density that is not a number`() {
        assertEquals(0, nodeCount(100f, 100f, 50f, Float.NaN))
        assertEquals(0, nodeCount(0f, 0f, 50f, Float.POSITIVE_INFINITY))
    }

    private fun distance(node: Node, touch: Touch) =
        kotlin.math.hypot(node.x - touch.x, node.y - touch.y)
}
