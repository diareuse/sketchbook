package lib.sketchbook.behavior

import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.PathMeasure
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import lib.sketchbook.shader.BeamSampleCount
import lib.sketchbook.shader.OutlineSamples
import lib.sketchbook.shader.sampleOutline
import lib.sketchbook.shape.SquircleShape
import lib.sketchbook.util.toPath
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode
import kotlin.math.PI
import kotlin.math.hypot
import kotlin.math.max
import kotlin.math.min

@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(sdk = [34])
class OutlineSamplerBehaviorTest {

    private val density = Density(2f)
    private val shapes: List<Pair<String, Shape>> = listOf(
        "squircle" to SquircleShape(24.dp),
        "circle" to CircleShape,
        "rounded rectangle" to RoundedCornerShape(16.dp)
    )

    @Test
    fun `every outline is sampled into the beam sample count of points`() = forEachShape { name, samples, _ ->
        assertEquals(name, BeamSampleCount, samples.points.size / 2)
    }

    @Test
    fun `every sample lies within half a dp of the outline`() = forEachShape { name, samples, reference ->
        for (i in 0 until BeamSampleCount) {
            val p = Offset(samples.points[2 * i], samples.points[2 * i + 1])
            assertTrue("$name sample $i", reference.distanceTo(p) < .5f)
        }
    }

    @Test
    fun `samples are evenly spaced by arc length`() = forEachShape { name, samples, reference ->
        val step = reference.perimeter / BeamSampleCount.toFloat()
        for (i in 0 until BeamSampleCount) {
            val a = reference.arcOf(Offset(samples.points[2 * i], samples.points[2 * i + 1]))
            val b = reference.arcOf(
                Offset(samples.points[2 * ((i + 1) % BeamSampleCount)], samples.points[2 * ((i + 1) % BeamSampleCount) + 1])
            )
            val gap = (b - a + reference.perimeter) % reference.perimeter
            assertEquals("$name gap $i", step, min(gap, reference.perimeter - gap), step * .05f)
        }
    }

    @Test
    fun `the perimeter is the closed outline length in dp`() = forEachShape { name, samples, reference ->
        assertEquals(name, reference.perimeter, samples.perimeter, reference.perimeter * .01f)
    }

    @Test
    fun `a circle of 80dp measures its circumference in dp`() {
        val size = Size(160f, 160f)
        val samples = sampleOutline(CircleShape.createOutline(size, LayoutDirection.Ltr, density), 2f)

        assertEquals(PI.toFloat() * 80f, samples.perimeter, 1.5f)
    }

    @Test
    fun `samples run clockwise on screen`() = forEachShape { name, samples, _ ->
        var area = 0f
        for (i in 0 until BeamSampleCount) {
            val j = (i + 1) % BeamSampleCount
            area += samples.points[2 * i] * samples.points[2 * j + 1] - samples.points[2 * j] * samples.points[2 * i + 1]
        }
        assertTrue("$name area $area", area > 0f)
    }

    @Test
    fun `sampling is independent of the density`() {
        val shape = RoundedCornerShape(16.dp)
        val a = sampleOutline(shape.createOutline(Size(200f, 100f), LayoutDirection.Ltr, Density(1f)), 1f)
        val b = sampleOutline(shape.createOutline(Size(400f, 200f), LayoutDirection.Ltr, Density(2f)), 2f)

        assertEquals(a.perimeter, b.perimeter, 1f)
        assertEquals(a.points[10], b.points[10], 1f)
        assertEquals(a.points[11], b.points[11], 1f)
    }

    @Test
    fun `a position along the border interpolates between neighbouring samples and wraps`() {
        val outline = RoundedCornerShape(16.dp).createOutline(Size(400f, 160f), LayoutDirection.Ltr, density)
        val samples = sampleOutline(outline, density.density)
        val step = samples.perimeter / BeamSampleCount.toFloat()

        val start = samples.pointAt(0f)
        val wrapped = samples.pointAt(samples.perimeter)
        val between = samples.pointAt(step * 2.5f)

        assertEquals(samples.points[0], start.x, 1e-3f)
        assertEquals(samples.points[1], start.y, 1e-3f)
        assertEquals(start.x, wrapped.x, 1e-2f)
        assertEquals(start.y, wrapped.y, 1e-2f)
        assertEquals((samples.points[4] + samples.points[6]) / 2f, between.x, 1e-2f)
        assertEquals((samples.points[5] + samples.points[7]) / 2f, between.y, 1e-2f)
    }

    private fun forEachShape(block: (String, OutlineSamples, Reference) -> Unit) {
        val size = Size(400f, 160f)
        shapes.forEach { (name, shape) ->
            val outline = shape.createOutline(size, LayoutDirection.Ltr, density)
            block(name, sampleOutline(outline, density.density), Reference(outline, density.density))
        }
    }

    private class Reference(outline: Outline, density: Float) {
        private val dense = FloatArray(DenseCount * 2)
        val perimeter: Float

        init {
            val measure = PathMeasure()
            measure.setPath(outline.toPath(), true)
            val length = measure.length
            for (i in 0 until DenseCount) {
                val p = measure.getPosition(length * i / DenseCount)
                dense[2 * i] = p.x / density
                dense[2 * i + 1] = p.y / density
            }
            perimeter = length / density
        }

        fun distanceTo(p: Offset): Float {
            var best = Float.MAX_VALUE
            for (i in 0 until DenseCount) {
                val j = (i + 1) % DenseCount
                best = min(best, distanceToSegment(p, dense[2 * i], dense[2 * i + 1], dense[2 * j], dense[2 * j + 1]))
            }
            return best
        }

        fun arcOf(p: Offset): Float {
            var best = Float.MAX_VALUE
            var arc = 0f
            for (i in 0 until DenseCount) {
                val j = (i + 1) % DenseCount
                val ax = dense[2 * i]
                val ay = dense[2 * i + 1]
                val bx = dense[2 * j]
                val by = dense[2 * j + 1]
                val d = distanceToSegment(p, ax, ay, bx, by)
                if (d < best) {
                    best = d
                    val segment = hypot(bx - ax, by - ay)
                    val t = if (segment == 0f) 0f else
                        max(0f, min(1f, ((p.x - ax) * (bx - ax) + (p.y - ay) * (by - ay)) / (segment * segment)))
                    arc = (i + t) * perimeter / DenseCount
                }
            }
            return arc
        }

        private fun distanceToSegment(p: Offset, ax: Float, ay: Float, bx: Float, by: Float): Float {
            val dx = bx - ax
            val dy = by - ay
            val length2 = dx * dx + dy * dy
            val t = if (length2 == 0f) 0f else max(0f, min(1f, ((p.x - ax) * dx + (p.y - ay) * dy) / length2))
            return hypot(p.x - (ax + t * dx), p.y - (ay + t * dy))
        }

        private companion object {
            const val DenseCount = 2048
        }
    }
}
