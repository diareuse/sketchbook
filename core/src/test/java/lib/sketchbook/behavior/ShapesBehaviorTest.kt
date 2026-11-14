package lib.sketchbook.behavior

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import lib.sketchbook.theme.extendBy
import org.junit.Assert.assertEquals
import org.junit.Test

class ShapesBehaviorTest {

    private val density = Density(2f)

    @Test
    fun `extending a percent cornered shape adds the padding to every corner`() {
        val extended = RoundedCornerShape(50).extendBy(8.dp, density)

        val size = Size(100f, 100f)
        listOf(extended.topStart, extended.topEnd, extended.bottomEnd, extended.bottomStart)
            .forEach { assertEquals(16f, it.toPx(size, density), 0.001f) }
    }

    @Test
    fun `extending a dp cornered shape adds the padding to every corner`() {
        val extended = RoundedCornerShape(10.dp).extendBy(4.dp, density)

        assertEquals(28f, extended.topStart.toPx(Size(100f, 100f), density), 0.001f)
    }
}
