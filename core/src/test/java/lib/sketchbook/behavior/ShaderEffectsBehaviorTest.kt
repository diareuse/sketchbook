package lib.sketchbook.behavior

import android.provider.Settings
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toPixelMap
import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.unit.dp
import lib.sketchbook.modifier.meshGradientBackground
import lib.sketchbook.modifier.shimmer
import lib.sketchbook.modifier.specularBorder
import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.robolectric.annotation.Config

abstract class ShaderEffectsBehavior : BehaviorTest() {

    private val shape = RoundedCornerShape(12.dp)

    @Test
    fun `shimmer moves a highlight across the content over time`() {
        show { Modifier.size(100.dp, 40.dp).background(Color.Black).shimmer(Color.White, 1000) }
        val first = frame()
        advance(400)
        val second = frame()
        assertFalse(first.contentEquals(second))
    }

    @Test
    fun `shimmer brightens the content it sweeps over`() {
        show { Modifier.size(100.dp, 40.dp).background(Color.Black).shimmer(Color.White, 1000) }
        var brightest = 0f
        repeat(10) {
            advance(100)
            brightest = maxOf(brightest, frame().max())
        }
        assertTrue(brightest > .3f)
    }

    @Test
    fun `disabled shimmer leaves the content untouched`() {
        show { Modifier.size(100.dp, 40.dp).background(Color.Black).shimmer(Color.White, 1000, enabled = false) }
        advance(400)
        assertEquals(0f, frame().max(), 0f)
    }

    @Test
    fun `shimmer stops when it is disabled`() {
        var enabled by mutableStateOf(true)
        show { Modifier.size(100.dp, 40.dp).background(Color.Black).shimmer(Color.White, 1000, enabled) }
        advance(300)
        enabled = false
        advance(100)
        val settled = frame()
        advance(400)
        assertArrayEquals(settled, frame(), 0f)
    }

    @Test
    fun `specular border draws a rim on the shape edge and leaves the interior alone`() {
        show {
            Modifier.size(100.dp).background(Color.Black, shape).specularBorder(shape, 3.dp, Color.White, animated = false)
        }
        val image = composeRule.onRoot().captureToImage().toPixelMap()
        var rim = 0f
        for (y in 2..8) for (x in 24..176) rim = maxOf(rim, image[x, y].let { maxOf(it.red, it.green, it.blue) })
        for (x in 24..176) rim = maxOf(rim, image[x, 198].let { maxOf(it.red, it.green, it.blue) })
        assertTrue("rim $rim", rim > .1f)
        assertEquals(0f, image[100, 100].red, 0f)
    }

    @Test
    fun `animated specular border moves its glint around the shape`() {
        show { Modifier.size(100.dp).background(Color.Black, shape).specularBorder(shape, 3.dp, Color.White, 2000) }
        val first = frame()
        advance(500)
        assertFalse(first.contentEquals(frame()))
    }

    @Test
    fun `mesh gradient fills the background with its base color`() {
        show { Modifier.size(100.dp).meshGradientBackground(listOf(Color.Red), animated = false, baseColor = Color.Red) }
        val center = composeRule.onRoot().captureToImage().toPixelMap()[100, 100]
        assertEquals(1f, center.red, .05f)
        assertEquals(0f, center.green, .05f)
        assertEquals(0f, center.blue, .05f)
    }

    @Test
    fun `animated mesh gradient shifts its colors over time`() {
        show {
            Modifier.size(100.dp).meshGradientBackground(
                listOf(Color.Red, Color.Green, Color.Blue, Color.Yellow),
                durationMillis = 2000
            )
        }
        val first = frame()
        advance(700)
        assertFalse(first.contentEquals(frame()))
    }

    @Test
    fun `mesh gradient rejects an empty palette`() = assertRejected(emptyList())

    @Test
    fun `mesh gradient rejects more colors than it can blend`() = assertRejected(List(5) { Color.Red })

    @Test
    fun `shimmer holds still when the system disabled animations`() {
        disableAnimations()
        show { Modifier.size(100.dp, 40.dp).background(Color.Black).shimmer(Color.White, 1000) }
        val first = frame()
        advance(400)
        assertArrayEquals(first, frame(), 0f)
    }

    @Test
    fun `animated specular border holds still when the system disabled animations`() {
        disableAnimations()
        show { Modifier.size(100.dp).background(Color.Black, shape).specularBorder(shape, 3.dp, Color.White, 2000) }
        val first = frame()
        advance(500)
        assertArrayEquals(first, frame(), 0f)
    }

    @Test
    fun `animated mesh gradient holds still when the system disabled animations`() {
        disableAnimations()
        show {
            Modifier.size(100.dp).meshGradientBackground(
                listOf(Color.Red, Color.Green, Color.Blue, Color.Yellow),
                durationMillis = 2000
            )
        }
        val first = frame()
        advance(700)
        assertArrayEquals(first, frame(), 0f)
    }

    private fun disableAnimations() {
        Settings.Global.putFloat(composeRule.activity.contentResolver, Settings.Global.ANIMATOR_DURATION_SCALE, 0f)
    }

    private fun assertRejected(palette: List<Color>) {
        val failure = runCatching { show { Modifier.size(10.dp).meshGradientBackground(palette) } }
        assertTrue(failure.exceptionOrNull() is IllegalArgumentException)
    }

    @Test
    fun `static specular border and mesh gradient hold a stable frame`() {
        show {
            Modifier.size(100.dp)
                .meshGradientBackground(listOf(Color.Red, Color.Blue), animated = false)
                .specularBorder(shape, 3.dp, Color.White, animated = false)
        }
        val first = frame()
        advance(700)
        assertArrayEquals(first, frame(), 0f)
    }

    private fun show(modifier: () -> Modifier) {
        composeRule.mainClock.autoAdvance = false
        composeRule.setContent { Box(modifier()) }
        advance(16)
    }

    private fun advance(millis: Long) {
        composeRule.mainClock.advanceTimeBy(millis)
    }

    private fun frame(): FloatArray {
        val map = composeRule.onRoot().captureToImage().toPixelMap()
        val out = FloatArray(map.width * map.height)
        for (y in 0 until map.height) for (x in 0 until map.width) {
            val c = map[x, y]
            out[y * map.width + x] = c.red + c.green + c.blue
        }
        return out
    }
}

@Config(sdk = [26])
class ShaderEffectsAndroid8BehaviorTest : ShaderEffectsBehavior()

@Config(sdk = [33])
class ShaderEffectsAndroid13BehaviorTest : ShaderEffectsBehavior()
