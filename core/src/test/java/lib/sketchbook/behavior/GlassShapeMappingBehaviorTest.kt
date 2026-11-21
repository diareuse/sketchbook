package lib.sketchbook.behavior

import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.unit.dp
import lib.sketchbook.glass.asGlassShape
import lib.sketchbook.glass.glassShape
import lib.sketchbook.shape.SquircleShape
import org.junit.Assert.assertEquals
import org.junit.Assert.assertSame
import org.junit.Test

class GlassShapeMappingBehaviorTest {

    @Test
    fun `a squircle becomes rounded corners of the same sizes`() {
        val squircle = SquircleShape(
            topStartCorner = CornerSize(4.dp),
            topEndCorner = CornerSize(8.dp),
            bottomStartCorner = CornerSize(16.dp),
            bottomEndCorner = CornerSize(32.dp),
            cornerSmoothing = .8f
        )

        assertEquals(
            RoundedCornerShape(
                topStart = CornerSize(4.dp),
                topEnd = CornerSize(8.dp),
                bottomEnd = CornerSize(32.dp),
                bottomStart = CornerSize(16.dp)
            ),
            glassShape(squircle)
        )
    }

    @Test
    fun `percent corners keep their percentage`() {
        assertEquals(RoundedCornerShape(CornerSize(50)), glassShape(SquircleShape(50)))
    }

    @Test
    fun `rounded corners are kept as they are`() {
        val rounded = RoundedCornerShape(12.dp)

        assertSame(rounded, glassShape(rounded))
        assertSame(CircleShape, glassShape(CircleShape))
    }

    @Test
    fun `shapes that are not corner based are kept as they are`() {
        assertSame(RectangleShape, glassShape(RectangleShape))
    }

    @Test
    fun `the shape extension maps like the function`() {
        val squircle = SquircleShape(24.dp)

        assertEquals(glassShape(squircle), squircle.asGlassShape())
    }
}
