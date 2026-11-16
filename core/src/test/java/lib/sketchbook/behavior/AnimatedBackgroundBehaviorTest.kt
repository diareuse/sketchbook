package lib.sketchbook.behavior

import android.provider.Settings
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PixelMap
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.toPixelMap
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import androidx.lifecycle.compose.LocalLifecycleOwner
import lib.sketchbook.SketchbookAnimatedBackground
import lib.sketchbook.theme.Theme
import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
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

    private fun showOver(
        modifier: Modifier = Modifier.size(100.dp),
        backdrop: Color = Color.White,
        content: @Composable () -> Unit
    ) = show { Box(modifier.background(backdrop)) { content() } }

    private fun advance(millis: Long) = composeRule.mainClock.advanceTimeBy(millis)

    private fun frame(tag: String? = null): FloatArray {
        val node = if (tag == null) composeRule.onRoot() else composeRule.onNodeWithTag(tag)
        val map = node.captureToImage().toPixelMap()
        return FloatArray(map.width * map.height * 3) {
            val c = map[(it / 3) % map.width, (it / 3) / map.width]
            when (it % 3) {
                0 -> c.red
                1 -> c.green
                else -> c.blue
            }
        }
    }

    private fun PixelMap.pixels(x: IntRange, y: IntRange): List<Color> =
        y.flatMap { j -> x.map { i -> this[i, j] } }

    private fun disableAnimations() {
        Settings.Global.putFloat(composeRule.activity.contentResolver, Settings.Global.ANIMATOR_DURATION_SCALE, 0f)
    }

    private fun Color.isWhite() = red > .9f && green > .9f && blue > .9f

    @Test
    fun `background wraps its content`() {
        show { SketchbookAnimatedBackground(Modifier.size(100.dp)) { Text("Hello") } }

        composeRule.onNodeWithText("Hello").assertIsDisplayed()
    }

    @Test
    fun `background draws nothing behind its nodes`() {
        showOver(Modifier.size(100.dp), Color.White) {
            SketchbookAnimatedBackground(Modifier.fillMaxSize(), colors = listOf(Color.Red), nodeSize = 1.dp, density = .01f, animated = false) {}
        }

        val map = composeRule.onRoot().captureToImage().toPixelMap()

        assertTrue(map.pixels(0 until map.width, 0 until map.height).count { it.isWhite() } > map.width * map.height * .9)
    }

    @Test
    fun `background draws nodes in the given colors near its center`() {
        showOver(Modifier.size(150.dp)) {
            SketchbookAnimatedBackground(
                Modifier.fillMaxSize(),
                colors = listOf(Color.Red),
                nodeSize = 14.dp,
                density = 6f,
                animated = false
            ) {}
        }

        val map = composeRule.onRoot().captureToImage().toPixelMap()
        val q = map.width / 4
        val center = map.pixels(q..3 * q, q..3 * q)

        assertTrue(center.any { it.red > it.blue + .3f })
    }

    @Test
    fun `background fades to transparent at its corners`() {
        showOver(Modifier.size(150.dp)) {
            SketchbookAnimatedBackground(
                Modifier.fillMaxSize(),
                colors = listOf(Color.Red),
                nodeSize = 14.dp,
                density = 6f,
                animated = false
            ) {}
        }

        val map = composeRule.onRoot().captureToImage().toPixelMap()
        val near = 0..3
        val far = (map.width - 4) until map.width
        val corners = listOf(near to near, far to near, near to far, far to far)

        assertTrue(corners.all { (x, y) -> map.pixels(x, y).all { it.isWhite() } })
    }

    @Test
    fun `background without a vignette draws nodes at its edges`() {
        showOver(Modifier.size(150.dp)) {
            SketchbookAnimatedBackground(
                Modifier.fillMaxSize(),
                colors = listOf(Color.Red),
                nodeSize = 14.dp,
                density = 6f,
                vignette = 0f,
                animated = false
            ) {}
        }

        val map = composeRule.onRoot().captureToImage().toPixelMap()
        val edgeBand = map.pixels(0 until map.width, 0 until map.height).filterIndexed { i, _ ->
            val x = i % map.width
            val y = i / map.width
            x < map.width / 6 || y < map.height / 6 || x > map.width * 5 / 6 || y > map.height * 5 / 6
        }

        assertTrue(edgeBand.any { it.red > it.blue + .3f })
    }

    @Test
    fun `link distance connects nearby nodes with lines`() {
        showOver(Modifier.size(300.dp)) {
            Row {
                SketchbookAnimatedBackground(Modifier.testTag("linked").size(150.dp), colors = listOf(Color.Blue), nodeSize = 1.dp, density = 6f, linkDistance = 300.dp, vignette = 0f, animated = false) {}
                SketchbookAnimatedBackground(Modifier.testTag("bare").size(150.dp), colors = listOf(Color.Blue), nodeSize = 1.dp, density = 6f, linkDistance = 0.dp, vignette = 0f, animated = false) {}
            }
        }

        fun drawn(tag: String) = composeRule.onNodeWithTag(tag).captureToImage().toPixelMap().let { it.pixels(0 until it.width, 0 until it.height) }.count { !it.isWhite() }

        assertTrue(drawn("linked") > drawn("bare") * 3)
    }

    @Test
    fun `a custom node shape draws differently than the default circle`() {
        showOver(Modifier.size(200.dp)) {
            Row {
                SketchbookAnimatedBackground(Modifier.testTag("circle").size(100.dp), nodeSize = 14.dp, density = 6f, vignette = 0f, linkDistance = 0.dp, animated = false) {}
                SketchbookAnimatedBackground(Modifier.testTag("square").size(100.dp), nodeShape = RectangleShape, nodeSize = 14.dp, density = 6f, vignette = 0f, linkDistance = 0.dp, animated = false) {}
            }
        }

        assertFalse(frame("circle").contentEquals(frame("square")))
    }

    @Test
    fun `identical backgrounds render identical frames`() {
        showOver(Modifier.size(200.dp)) {
            Row {
                SketchbookAnimatedBackground(Modifier.testTag("a").size(100.dp), colors = palette) {}
                SketchbookAnimatedBackground(Modifier.testTag("b").size(100.dp), colors = palette) {}
            }
        }
        advance(1000)

        assertArrayEquals(frame("a"), frame("b"), 0f)
    }

    @Test
    fun `default background is not blank`() {
        showOver(Modifier.size(150.dp)) {
            SketchbookAnimatedBackground(Modifier.fillMaxSize(), density = 6f, nodeSize = 14.dp, animated = false) {}
        }

        val map = composeRule.onRoot().captureToImage().toPixelMap()

        assertTrue(map.pixels(0 until map.width, 0 until map.height).any { !it.isWhite() })
    }

    @Test
    fun `default colors differ between light and dark themes`() {
        composeRule.mainClock.autoAdvance = false
        composeRule.setContent {
            Row {
                Theme(darkTheme = false, dynamicColor = false) {
                    Box(Modifier.testTag("light").size(100.dp).background(Color.Gray)) {
                        SketchbookAnimatedBackground(Modifier.fillMaxSize(), nodeSize = 14.dp, density = 6f, vignette = 0f, animated = false) {}
                    }
                }
                Theme(darkTheme = true, dynamicColor = false) {
                    Box(Modifier.testTag("dark").size(100.dp).background(Color.Gray)) {
                        SketchbookAnimatedBackground(Modifier.fillMaxSize(), nodeSize = 14.dp, density = 6f, vignette = 0f, animated = false) {}
                    }
                }
            }
        }
        advance(16)

        assertFalse(frame("light").contentEquals(frame("dark")))
    }

    @Test
    fun `nodes drift over time without recomposing the content`() {
        showOver {
            SketchbookAnimatedBackground(Modifier.fillMaxSize(), colors = palette, nodeSize = 10.dp, density = 4f) {
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
        showOver { SketchbookAnimatedBackground(Modifier.fillMaxSize(), colors = palette, nodeSize = 10.dp, density = 4f, animated = false) {} }
        val first = frame()

        advance(2000)

        assertArrayEquals(first, frame(), 0f)
    }

    @Test
    fun `background with no speed holds a stable frame`() {
        showOver { SketchbookAnimatedBackground(Modifier.fillMaxSize(), colors = palette, nodeSize = 10.dp, density = 4f, speed = 0f) {} }
        val first = frame()

        advance(2000)

        assertArrayEquals(first, frame(), 0f)
    }

    @Test
    fun `background with a negative speed holds a stable frame`() {
        showOver { SketchbookAnimatedBackground(Modifier.fillMaxSize(), colors = palette, nodeSize = 10.dp, density = 4f, speed = -2f) {} }
        val first = frame()

        advance(2000)

        assertArrayEquals(first, frame(), 0f)
    }

    @Test
    fun `background holds still when the system disabled animations`() {
        disableAnimations()
        showOver { SketchbookAnimatedBackground(Modifier.fillMaxSize(), colors = palette, nodeSize = 10.dp, density = 4f) {} }
        val first = frame()

        advance(2000)

        assertArrayEquals(first, frame(), 0f)
    }

    @Test
    fun `background pauses while the screen is not started and resumes afterwards`() {
        val owner = TestLifecycleOwner()
        showOver {
            CompositionLocalProvider(LocalLifecycleOwner provides owner) {
                SketchbookAnimatedBackground(Modifier.fillMaxSize(), colors = palette, nodeSize = 10.dp, density = 4f) {}
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

    @Test
    fun `touching the background pushes nodes away`() {
        showOver(Modifier.size(200.dp)) {
            Row {
                SketchbookAnimatedBackground(Modifier.testTag("touched").size(100.dp), colors = palette, nodeSize = 10.dp, density = 6f, vignette = 0f) {}
                SketchbookAnimatedBackground(Modifier.testTag("control").size(100.dp), colors = palette, nodeSize = 10.dp, density = 6f, vignette = 0f) {}
            }
        }
        assertArrayEquals(frame("touched"), frame("control"), 0f)

        composeRule.onNodeWithTag("touched").performTouchInput { down(center) }
        advance(500)

        assertFalse(frame("touched").contentEquals(frame("control")))
    }

    @Test
    fun `touching content over the background still reaches the content`() {
        var clicks = 0
        show {
            SketchbookAnimatedBackground(Modifier.size(100.dp)) {
                Box(Modifier.testTag("target").size(50.dp).clickable { clicks++ })
            }
        }

        composeRule.onNodeWithTag("target").performClick()

        assertEquals(1, clicks)
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
