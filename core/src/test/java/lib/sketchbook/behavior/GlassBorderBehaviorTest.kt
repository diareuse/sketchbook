package lib.sketchbook.behavior

import android.provider.Settings
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toPixelMap
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.unit.dp
import lib.sketchbook.SketchbookGlassBorder
import lib.sketchbook.distance
import lib.sketchbook.opaque
import lib.sketchbook.theme.Theme
import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.robolectric.annotation.Config

abstract class GlassBorderBehavior : BehaviorTest() {

    private var compositions = 0
    private val shape = RoundedCornerShape(16.dp)

    private fun show(content: @Composable () -> Unit) {
        composeRule.mainClock.autoAdvance = false
        composeRule.setContent {
            Theme(darkTheme = false, dynamicColor = false) {
                Box(Modifier.background(Color.Black)) { content() }
            }
        }
        advance(16)
    }

    private fun advance(millis: Long) = composeRule.mainClock.advanceTimeBy(millis)

    private fun pixels() = composeRule.onRoot().captureToImage().toPixelMap()

    private fun frame(): FloatArray {
        val map = pixels()
        return FloatArray(map.width * map.height) { map[it % map.width, it / map.width].let { c -> c.red + c.green + c.blue } }
    }

    @Test
    fun `glass border shows its content`() {
        show { SketchbookGlassBorder(Modifier.size(160.dp, 80.dp), animated = false) { Text("Inside") } }

        composeRule.onNodeWithText("Inside").assertIsDisplayed()
    }

    @Test
    fun `glass border draws a rim on the edge of its shape`() {
        show {
            Row {
                SketchbookGlassBorder(Modifier.size(100.dp), shape, borderWidth = 3.dp, animated = false) {}
                SketchbookGlassBorder(Modifier.size(100.dp), shape, borderWidth = 0.dp, animated = false) {}
            }
        }

        val image = pixels()
        var strongest = 0
        for (y in 2..20 step 2) for (x in 20..180 step 2) {
            strongest = maxOf(strongest, distance(image.opaque(x, y), image.opaque(x + 200, y)))
        }
        assertTrue("rim difference $strongest", strongest > 30)
    }

    @Test
    fun `glass border clips its glass to the given shape`() {
        show {
            SketchbookGlassBorder(Modifier.size(100.dp), CircleShape, borderWidth = 0.dp, animated = false) {}
        }

        assertEquals(Color.Black, pixels().opaque(2, 2))
    }

    @Test
    fun `animated glass border sweeps its glint without recomposing the content`() {
        show {
            SketchbookGlassBorder(Modifier.size(100.dp), shape, borderWidth = 3.dp, animated = true) {
                SideEffect { compositions++ }
            }
        }
        val first = frame()
        val count = compositions

        advance(1000)

        assertFalse(first.contentEquals(frame()))
        assertEquals(count, compositions)
    }

    @Test
    fun `static glass border holds a stable frame`() {
        show { SketchbookGlassBorder(Modifier.size(100.dp), shape, borderWidth = 3.dp, animated = false) {} }
        val first = frame()

        advance(1000)

        assertArrayEquals(first, frame(), 0f)
    }

    @Test
    fun `animated glass border holds still when the system disabled animations`() {
        Settings.Global.putFloat(composeRule.activity.contentResolver, Settings.Global.ANIMATOR_DURATION_SCALE, 0f)
        show { SketchbookGlassBorder(Modifier.size(100.dp), shape, borderWidth = 3.dp, animated = true) {} }
        val first = frame()

        advance(1000)

        assertArrayEquals(first, frame(), 0f)
    }
}

@Config(sdk = [26])
class GlassBorderAndroid8BehaviorTest : GlassBorderBehavior()

@Config(sdk = [33])
class GlassBorderAndroid13BehaviorTest : GlassBorderBehavior()
