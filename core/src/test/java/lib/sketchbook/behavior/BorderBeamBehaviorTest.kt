package lib.sketchbook.behavior

import android.provider.Settings
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PixelMap
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.graphics.toPixelMap
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.unit.dp
import lib.sketchbook.modifier.BeamDirection
import lib.sketchbook.modifier.BeamPalette
import lib.sketchbook.modifier.BeamStyle
import lib.sketchbook.modifier.borderBeam
import lib.sketchbook.shape.SquircleShape
import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.robolectric.annotation.Config

abstract class BorderBeamBehavior : BehaviorTest() {

    private val squircle = SquircleShape(32.dp)

    @Test
    fun `the beam paints along the border of the shape`() {
        show(squircle) { Modifier.borderBeam(squircle) }

        assertTrue(painted(image()).isNotEmpty())
    }

    @Test
    fun `the beam never paints outside a squircle`() {
        show(squircle) { Modifier.borderBeam(squircle) }
        val image = image()

        assertTrue(painted(image).isNotEmpty())
        assertOutsideIsUntouched(image)
        assertSquircleCornersAreUntouched(image)
    }

    @Test
    fun `the beam never paints outside a circle`() {
        show(CircleShape) { Modifier.borderBeam(CircleShape) }
        val image = image()

        assertTrue(painted(image).isNotEmpty())
        assertOutsideIsUntouched(image)
        assertTrue(black(image, Margin + 4, Margin + 4))
        assertTrue(black(image, Margin + BoxWidthPx - 5, Margin + BoxHeightPx - 5))
    }

    @Test
    fun `the beam never paints outside rounded corners`() {
        val rounded = RoundedCornerShape(16.dp)
        show(rounded) { Modifier.borderBeam(rounded) }
        val image = image()

        assertTrue(painted(image).isNotEmpty())
        assertOutsideIsUntouched(image)
        assertTrue(black(image, Margin + 2, Margin + 2))
    }

    @Test
    fun `the beam travels around the border over time`() {
        show(squircle) { Modifier.borderBeam(squircle) }
        val first = frame()

        advance(600)

        assertFalse(first.contentEquals(frame()))
    }

    @Test
    fun `the beam carries more than one hue along the border`() {
        show(squircle) { Modifier.borderBeam(squircle, colors = BeamPalette.colorful) }

        val hues = painted(image())
            .filter { hsv(it)[1] > .35f && hsv(it)[2] > .35f }
            .map { (hsv(it)[0] / 30f).toInt() }
            .toSet()

        assertTrue("hue buckets $hues", hues.size >= 3)
    }

    @Test
    fun `an inactive beam draws nothing once it has faded out`() {
        var active by mutableStateOf(true)
        show(squircle) { Modifier.borderBeam(squircle, active = active) }
        assertTrue(painted(image()).isNotEmpty())

        active = false
        composeRule.waitForIdle()
        advance(1_500)

        assertTrue(painted(image()).isEmpty())
    }

    @Test
    fun `a beam that becomes active fades in`() {
        var active by mutableStateOf(false)
        show(squircle) { Modifier.borderBeam(squircle, active = active) }
        assertTrue(painted(image()).isEmpty())

        active = true
        composeRule.waitForIdle()
        advance(1_000)

        assertTrue(painted(image()).isNotEmpty())
    }

    @Test
    fun `a zero intensity beam draws nothing`() {
        show(squircle) { Modifier.borderBeam(squircle, intensity = 0f) }

        assertTrue(painted(image()).isEmpty())
    }

    @Test
    fun `animating the beam does not recompose its host`() {
        var compositions = 0
        composeRule.mainClock.autoAdvance = false
        composeRule.setContent {
            SideEffect { compositions++ }
            Beamed { Modifier.borderBeam(squircle) }
        }
        advance(16)
        val initial = compositions

        repeat(20) { advance(100) }

        assertEquals(initial, compositions)
    }

    @Test
    fun `the beam holds still when the system disabled animations`() {
        Settings.Global.putFloat(composeRule.activity.contentResolver, Settings.Global.ANIMATOR_DURATION_SCALE, 0f)
        show(squircle) { Modifier.borderBeam(squircle) }
        val first = frame()

        advance(700)

        assertArrayEquals(first, frame(), 0f)
        assertTrue(painted(image()).isNotEmpty())
    }

    @Test
    fun `clockwise and counter clockwise beams differ after the same time`() {
        composeRule.mainClock.autoAdvance = false
        composeRule.setContent {
            Row(Modifier.background(Color.Black)) {
                Box(Modifier.size(BoxWidth.dp, BoxHeight.dp).borderBeam(squircle, direction = BeamDirection.Clockwise))
                Box(Modifier.size(BoxWidth.dp, BoxHeight.dp).borderBeam(squircle, direction = BeamDirection.CounterClockwise))
            }
        }
        advance(700)
        val image = image()

        var differing = 0
        for (y in 0 until BoxHeightPx) for (x in 0 until BoxWidthPx) {
            if (image[x, y] != image[x + BoxWidthPx, y]) differing++
        }

        assertTrue("differing pixels $differing", differing > 100)
    }

    @Test
    fun `the small style draws a beam`() {
        show(squircle) { Modifier.borderBeam(squircle, style = BeamStyle.Small) }

        assertTrue(painted(image()).isNotEmpty())
    }

    @Test
    fun `the line style sweeps along the bottom edge only`() {
        show(squircle) { Modifier.borderBeam(squircle, style = BeamStyle.Line) }
        var found = false
        repeat(12) {
            advance(200)
            val image = image()
            for (y in 0 until image.height) for (x in 0 until image.width) {
                if (!black(image, x, y)) {
                    found = true
                    assertTrue("pixel ($x,$y) above the bottom edge", y > Margin + BoxHeightPx / 2)
                }
            }
        }

        assertTrue(found)
    }

    @Test
    fun `a beam that becomes active fades in gradually`() {
        var active by mutableStateOf(false)
        show(squircle) { Modifier.borderBeam(squircle, active = active) }

        active = true
        composeRule.waitForIdle()
        advance(150)
        val early = brightness()
        advance(1_000)
        val late = brightness()

        assertTrue("early $early late $late", early < late)
    }

    @Test
    fun `swapping the shape redraws the beam inside the new shape`() {
        var shape by mutableStateOf<Shape>(RoundedCornerShape(2.dp))
        show(squircle) { Modifier.borderBeam(shape) }
        assertTrue(black(image(), Margin + 10, Margin + 10).not() || painted(image()).isNotEmpty())

        shape = CircleShape
        composeRule.waitForIdle()
        advance(16)

        assertTrue(black(image(), Margin + 4, Margin + 4))
        assertOutsideIsUntouched(image())
    }

    @Test
    fun `the beam works on a column with children`() {
        composeRule.mainClock.autoAdvance = false
        composeRule.setContent {
            Frame {
                Column(
                    Modifier.align(Alignment.Center).size(BoxWidth.dp, BoxHeight.dp).borderBeam(squircle),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("Inside", color = Color.White)
                }
            }
        }
        advance(16)

        composeRule.onNodeWithText("Inside").assertIsDisplayed()
        assertTrue(painted(image()).isNotEmpty())
        assertOutsideIsUntouched(image())
    }

    private fun show(@Suppress("UNUSED_PARAMETER") shape: Shape, beam: () -> Modifier) {
        composeRule.mainClock.autoAdvance = false
        composeRule.setContent { Beamed(beam) }
        advance(16)
    }

    @Composable
    private fun Beamed(beam: () -> Modifier) = Frame {
        Box(Modifier.align(Alignment.Center).size(BoxWidth.dp, BoxHeight.dp).then(beam()))
    }

    @Composable
    private fun Frame(content: @Composable BoxScope.() -> Unit) =
        Box(Modifier.size(FrameWidth.dp, FrameHeight.dp).background(Color.Black), content = content)

    private fun brightness(): Float = frame().sum()

    private fun advance(millis: Long) = composeRule.mainClock.advanceTimeBy(millis)

    private fun image(): PixelMap = composeRule.onRoot().captureToImage().toPixelMap()

    private fun frame(): FloatArray {
        val map = image()
        return FloatArray(map.width * map.height) { map[it % map.width, it / map.width].let { c -> c.red + c.green + c.blue } }
    }

    private fun black(image: PixelMap, x: Int, y: Int): Boolean {
        val c = image[x, y]
        return c.red == 0f && c.green == 0f && c.blue == 0f
    }

    private fun painted(image: PixelMap): List<Color> {
        val out = mutableListOf<Color>()
        for (y in 0 until image.height) for (x in 0 until image.width) {
            if (!black(image, x, y)) out += image[x, y]
        }
        return out
    }

    private fun assertOutsideIsUntouched(image: PixelMap) {
        for (y in 0 until image.height) for (x in 0 until image.width) {
            val inside = x in Margin until Margin + BoxWidthPx && y in Margin until Margin + BoxHeightPx
            if (!inside) assertTrue("pixel ($x,$y) outside the shape is painted", black(image, x, y))
        }
    }

    private fun assertSquircleCornersAreUntouched(image: PixelMap) {
        listOf(
            Margin + 3 to Margin + 3,
            Margin + BoxWidthPx - 4 to Margin + 3,
            Margin + 3 to Margin + BoxHeightPx - 4,
            Margin + BoxWidthPx - 4 to Margin + BoxHeightPx - 4,
            Margin + 10 to Margin + 10
        ).forEach { (x, y) -> assertTrue("corner pixel ($x,$y) is painted", black(image, x, y)) }
    }

    private fun hsv(color: Color): FloatArray =
        FloatArray(3).also { android.graphics.Color.colorToHSV(color.toArgb(), it) }

    private companion object {
        const val BoxWidth = 120
        const val BoxHeight = 80
        const val FrameWidth = 136
        const val FrameHeight = 96
        const val Scale = 2
        const val Margin = 16
        const val BoxWidthPx = BoxWidth * Scale
        const val BoxHeightPx = BoxHeight * Scale
    }
}

@Config(sdk = [26])
class BorderBeamAndroid8BehaviorTest : BorderBeamBehavior()

@Config(sdk = [33])
class BorderBeamAndroid13BehaviorTest : BorderBeamBehavior()
