package lib.sketchbook.behavior

import android.graphics.Region
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.ui.Alignment
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.PathOperation
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.asAndroidPath
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import lib.sketchbook.shape.CompositeShape
import lib.sketchbook.shape.CutoutShape
import lib.sketchbook.util.toPath
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ShapeBehaviorTest : BehaviorTest() {

    private val density = Density(1f)

    private fun contains(shape: androidx.compose.ui.graphics.Shape, size: Size, x: Int, y: Int): Boolean {
        val path = shape.createOutline(size, LayoutDirection.Ltr, density).toPath().asAndroidPath()
        val region = Region()
        region.setPath(path, Region(0, 0, size.width.toInt(), size.height.toInt()))
        return region.contains(x, y)
    }

    @Test
    fun `cutout shapes with equal parameters are equal`() {
        val a = CutoutShape(CornerSize(16.dp), CutoutShape.Orientation.TopLeft)
        val b = CutoutShape(CornerSize(16.dp), CutoutShape.Orientation.TopLeft)

        assertEquals(a, b)
        assertEquals(a.hashCode(), b.hashCode())
        assertNotEquals(a, CutoutShape(CornerSize(16.dp), CutoutShape.Orientation.TopRight))
        assertNotEquals(a, CutoutShape(CornerSize(8.dp), CutoutShape.Orientation.TopLeft))
    }

    @Test
    fun `composite shapes built from equal definitions are equal`() {
        fun build(operation: PathOperation) = CompositeShape.build {
            setBaseline(RectangleShape)
            addShape(RectangleShape, DpSize(10.dp, 10.dp), Alignment.BottomEnd, operation)
        }

        assertEquals(build(PathOperation.Difference), build(PathOperation.Difference))
        assertEquals(build(PathOperation.Difference).hashCode(), build(PathOperation.Difference).hashCode())
        assertNotEquals(build(PathOperation.Difference), build(PathOperation.Union))
    }

    @Test
    fun `composite shape subtracts a child aligned to the bottom end`() {
        val shape = CompositeShape.build {
            setBaseline(RectangleShape)
            addShape(RectangleShape, DpSize(40.dp, 40.dp), Alignment.BottomEnd, PathOperation.Difference)
        }
        val size = Size(100f, 100f)

        assertTrue(contains(shape, size, 10, 10))
        assertTrue(contains(shape, size, 10, 90))
        assertFalse(contains(shape, size, 90, 90))
        assertFalse(contains(shape, size, 70, 70))
    }

    @Test
    fun `composite shape keeps the child shape unchanged after building outlines twice`() {
        val child = CutoutShape(CornerSize(16.dp), CutoutShape.Orientation.TopRight)
        val shape = CompositeShape.build {
            setBaseline(RectangleShape)
            addShape(child, DpSize(40.dp, 40.dp), Alignment.BottomEnd, PathOperation.Union)
        }
        val size = Size(100f, 100f)

        val first = contains(shape, size, 99, 99)
        val second = contains(shape, size, 99, 99)

        assertEquals(first, second)
    }
}
