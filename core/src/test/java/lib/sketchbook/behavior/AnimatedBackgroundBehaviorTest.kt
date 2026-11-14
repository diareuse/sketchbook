package lib.sketchbook.behavior

import android.provider.Settings
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PixelMap
import androidx.compose.ui.graphics.toPixelMap
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import androidx.lifecycle.compose.LocalLifecycleOwner
import lib.sketchbook.SketchbookAnimatedBackground
import lib.sketchbook.theme.Theme
import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.robolectric.annotation.Config

abstract class AnimatedBackgroundBehavior : BehaviorTest() {

    private val palette = listOf(Color.Red, Color.Green, Color.Blue, Color.Yellow)
    private var compositions = 0

    private fun show(content: @Composable () -> Unit) {
        composeRule.mainClock.autoAdvance = false
        composeRule.setContent { Theme(darkTheme = false, dynamicColor = false) { content() } }
        advance(16)
    }

    private fun advance(millis: Long) = composeRule.mainClock.advanceTimeBy(millis)

    private fun frame(tag: String? = null): FloatArray {
        val node = if (tag == null) composeRule.onRoot() else composeRule.onNodeWithTag(tag)
        val map = node.captureToImage().toPixelMap()
        return FloatArray(map.width * map.height) { map[it % map.width, it / map.width].let { c -> c.red + c.green + c.blue } }
    }

    private fun PixelMap.pixels(): List<Color> =
        List(width * height) { this[it % width, it / width] }

    private fun disableAnimations() {
        Settings.Global.putFloat(composeRule.activity.contentResolver, Settings.Global.ANIMATOR_DURATION_SCALE, 0f)
    }

    @Test
    fun `background wraps its content`() {
        show { SketchbookAnimatedBackground(Modifier.size(100.dp)) { Text("Hello") } }

        composeRule.onNodeWithText("Hello").assertIsDisplayed()
    }

    @Test
    fun `background fills with its base color`() {
        show { SketchbookAnimatedBackground(Modifier.size(100.dp), colors = listOf(Color.Red), baseColor = Color.Red, animated = false) {} }

        val center = composeRule.onRoot().captureToImage().toPixelMap()[100, 100]
        assertEquals(1f, center.red, .05f)
        assertEquals(0f, center.green, .05f)
    }

    @Test
    fun `background shows its base color where no light reaches`() {
        show {
            SketchbookAnimatedBackground(
                Modifier.size(100.dp),
                colors = listOf(Color.Blue),
                baseColor = Color.Red,
                animated = false
            ) {}
        }

        val pixels = composeRule.onRoot().captureToImage().toPixelMap().pixels()

        assertTrue(pixels.any { it.red > it.blue + .3f })
    }

    @Test
    fun `background glows with the given colors over its base`() {
        show {
            SketchbookAnimatedBackground(
                Modifier.size(100.dp),
                colors = listOf(Color.Blue),
                baseColor = Color.Red,
                animated = false
            ) {}
        }

        val pixels = composeRule.onRoot().captureToImage().toPixelMap().pixels()

        assertTrue(pixels.any { it.blue > it.red + .15f })
    }

    @Test
    fun `default background is not a flat wash`() {
        show { SketchbookAnimatedBackground(Modifier.size(100.dp), animated = false) {} }

        val sums = composeRule.onRoot().captureToImage().toPixelMap().pixels().map { it.red + it.green + it.blue }

        assertTrue(sums.max() - sums.min() > .25f)
    }

    @Test
    fun `background repeats its frame after one full loop`() {
        show { SketchbookAnimatedBackground(Modifier.size(100.dp), colors = palette, baseColor = Color.Black) {} }
        val first = frame()

        advance(5_000)
        val middle = frame()
        advance(5_000)

        assertFalse(first.contentEquals(middle))
        assertArrayEquals(first, frame(), .15f)
    }

    @Test
    fun `default background is darker in a dark theme than in a light theme`() {
        composeRule.mainClock.autoAdvance = false
        composeRule.setContent {
            Row {
                Theme(darkTheme = false, dynamicColor = false) {
                    SketchbookAnimatedBackground(Modifier.testTag("light").size(100.dp), animated = false) {}
                }
                Theme(darkTheme = true, dynamicColor = false) {
                    SketchbookAnimatedBackground(Modifier.testTag("dark").size(100.dp), animated = false) {}
                }
            }
        }
        advance(16)

        assertTrue(frame("dark").average() < frame("light").average() - .5)
    }

    @Test
    fun `default light background glows peach at the bottom left`() {
        show { SketchbookAnimatedBackground(Modifier.size(100.dp), animated = false) {} }

        val pixel = composeRule.onRoot().captureToImage().toPixelMap()[40, 170]

        assertTrue(pixel.red > pixel.blue + .1f)
    }

    @Test
    fun `default dark background glows indigo at the top left`() {
        composeRule.mainClock.autoAdvance = false
        composeRule.setContent {
            Theme(darkTheme = true, dynamicColor = false) {
                SketchbookAnimatedBackground(Modifier.size(100.dp), animated = false) {}
            }
        }
        advance(16)

        val pixel = composeRule.onRoot().captureToImage().toPixelMap()[40, 30]

        assertTrue(pixel.blue > pixel.red + .3f && pixel.blue > pixel.green + .3f)
    }

    @Test
    fun `background drifts over time without recomposing its content`() {
        show {
            SketchbookAnimatedBackground(Modifier.size(100.dp), colors = palette) {
                SideEffect { compositions++ }
            }
        }
        val first = frame()
        val count = compositions

        advance(2000)

        assertFalse(first.contentEquals(frame()))
        assertEquals(count, compositions)
    }

    @Test
    fun `static background holds a stable frame`() {
        show { SketchbookAnimatedBackground(Modifier.size(100.dp), colors = palette, animated = false) {} }
        val first = frame()

        advance(2000)

        assertArrayEquals(first, frame(), 0f)
    }

    @Test
    fun `background with no speed holds a stable frame`() {
        show { SketchbookAnimatedBackground(Modifier.size(100.dp), colors = palette, speed = 0f) {} }
        val first = frame()

        advance(2000)

        assertArrayEquals(first, frame(), 0f)
    }

    @Test
    fun `background with a negative speed holds a stable frame`() {
        show { SketchbookAnimatedBackground(Modifier.size(100.dp), colors = palette, speed = -2f) {} }
        val first = frame()

        advance(2000)

        assertArrayEquals(first, frame(), 0f)
    }

    @Test
    fun `doubling the speed reaches in half the time what the base speed reaches later`() {
        show {
            Row {
                SketchbookAnimatedBackground(Modifier.testTag("slow").size(100.dp), colors = palette, speed = 1f) {}
                SketchbookAnimatedBackground(Modifier.testTag("fast").size(100.dp), colors = palette, speed = 2f) {}
            }
        }
        advance(1000)
        val fast = frame("fast")
        advance(1000)

        assertArrayEquals(fast, frame("slow"), .02f)
    }

    @Test
    fun `background holds still when the system disabled animations`() {
        disableAnimations()
        show { SketchbookAnimatedBackground(Modifier.size(100.dp), colors = palette) {} }
        val first = frame()

        advance(2000)

        assertArrayEquals(first, frame(), 0f)
    }

    @Test
    fun `background pauses while the screen is not started and resumes afterwards`() {
        val owner = TestLifecycleOwner()
        show {
            CompositionLocalProvider(LocalLifecycleOwner provides owner) {
                SketchbookAnimatedBackground(Modifier.size(100.dp), colors = palette) {}
            }
        }
        advance(500)

        owner.moveTo(Lifecycle.State.CREATED)
        advance(100)
        val paused = frame()
        advance(2000)
        assertArrayEquals(paused, frame(), 0f)

        owner.moveTo(Lifecycle.State.RESUMED)
        advance(100)
        advance(1500)

        assertFalse(paused.contentEquals(frame()))
    }
}

private class TestLifecycleOwner : LifecycleOwner {
    private val registry = LifecycleRegistry.createUnsafe(this).also { it.currentState = Lifecycle.State.RESUMED }

    override val lifecycle: Lifecycle get() = registry

    fun moveTo(state: Lifecycle.State) {
        registry.currentState = state
    }
}

@Config(sdk = [26])
class AnimatedBackgroundAndroid8BehaviorTest : AnimatedBackgroundBehavior()

@Config(sdk = [33])
class AnimatedBackgroundAndroid13BehaviorTest : AnimatedBackgroundBehavior()
