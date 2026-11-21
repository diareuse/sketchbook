package lib.sketchbook.behavior

import android.provider.Settings
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PixelMap
import androidx.compose.ui.graphics.toPixelMap
import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.unit.dp
import lib.sketchbook.SketchbookCircularProgress
import lib.sketchbook.SketchbookLinearProgress
import lib.sketchbook.distance
import lib.sketchbook.opaque
import lib.sketchbook.theme.Theme
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.robolectric.annotation.Config

private val Backdrop = Color(0xFF808080)

abstract class ProgressShapeBehavior : BehaviorTest() {

    private fun show(content: @Composable () -> Unit) {
        composeRule.mainClock.autoAdvance = false
        composeRule.setContent {
            Theme(darkTheme = false, dynamicColor = false) {
                Box(Modifier.background(Backdrop).size(200.dp, 48.dp)) { content() }
            }
        }
        composeRule.mainClock.advanceTimeBy(16)
    }

    private fun pixels(): PixelMap = composeRule.onRoot().captureToImage().toPixelMap()

    private fun isBackdrop(image: PixelMap, x: Int, y: Int) = distance(image.opaque(x, y), Backdrop) <= 3

    private fun isFilled(image: PixelMap, x: Int, y: Int) = !isBackdrop(image, x, y)

    @Test
    fun `determinate linear progress draws nothing beyond its filled part`() {
        show { SketchbookLinearProgress(progress = { .5f }, Modifier.fillMaxWidth().height(12.dp), color = Color.Red) }
        val image = pixels()

        val stray = (206 until image.width).flatMap { x -> (0 until 24).map { y -> x to y } }.filter { (x, y) -> !isBackdrop(image, x, y) }
        assertEquals(emptyList<Pair<Int, Int>>(), stray.take(8))
        assertEquals(emptyList<Pair<Int, Int>>(), (24 until image.height).flatMap { y -> (0 until image.width).map { it to y } }.filter { (x, y) -> !isBackdrop(image, x, y) }.take(8))
    }

    @Test
    fun `determinate linear progress has round caps at both ends`() {
        show { SketchbookLinearProgress(progress = { .5f }, Modifier.fillMaxWidth().height(12.dp), color = Color.Red) }
        val image = pixels()
        val end = (0 until image.width).last { isFilled(image, it, 12) }

        assertTrue(isFilled(image, 14, 12))
        assertTrue(isBackdrop(image, 0, 0))
        assertTrue(isBackdrop(image, 0, 23))
        assertTrue(isBackdrop(image, end, 0))
        assertTrue(isBackdrop(image, end, 23))
    }

    @Test
    fun `determinate circular progress draws no track ring or disc`() {
        show { SketchbookCircularProgress(progress = { .25f }, color = Color.Red) }
        val image = pixels()

        assertTrue(isBackdrop(image, 4, 40))
        assertTrue(isBackdrop(image, 40, 76))
        assertTrue(isBackdrop(image, 40, 40))
        assertTrue(isBackdrop(image, 10, 10))
    }

    @Test
    fun `determinate circular progress ends its arc with round caps`() {
        show { SketchbookCircularProgress(progress = { .25f }, color = Color.Red) }
        val image = pixels()

        assertTrue(distance(Color.Red, image.opaque(37, 4)) < 140)
        assertTrue(distance(Color.Red, image.opaque(76, 43)) < 140)
    }

    @Test
    fun `indeterminate linear progress draws only its round segment`() {
        Settings.Global.putFloat(composeRule.activity.contentResolver, Settings.Global.ANIMATOR_DURATION_SCALE, 0f)
        show { SketchbookLinearProgress(Modifier.fillMaxWidth().height(12.dp), color = Color.Red) }
        val image = pixels()
        val reds = (0 until image.width).filter { isFilled(image, it, 12) }
        val left = reds.first()
        val right = reds.last()

        assertTrue(reds.isNotEmpty())
        assertTrue(isBackdrop(image, left, 0))
        assertTrue(isBackdrop(image, right, 0))
        assertTrue(isBackdrop(image, left, 23))
        assertTrue(isBackdrop(image, right, 23))
        val stray = (0 until image.width).filter { it < left - 6 || it > right + 6 }.filter { !isBackdrop(image, it, 12) }
        assertEquals(emptyList<Int>(), stray.take(8))
    }

    @Test
    fun `indeterminate linear segment entering the track is cut by the round track end not a flat edge`() {
        show { SketchbookLinearProgress(Modifier.fillMaxWidth().height(12.dp), color = Color.Red) }
        composeRule.mainClock.advanceTimeBy(1_190)
        val image = pixels()

        assertTrue(isBackdrop(image, 0, 0))
        assertTrue(isBackdrop(image, 0, 23))
    }

    @Test
    fun `indeterminate circular progress draws no track ring`() {
        Settings.Global.putFloat(composeRule.activity.contentResolver, Settings.Global.ANIMATOR_DURATION_SCALE, 0f)
        show { SketchbookCircularProgress(color = Color.Red) }
        val image = pixels()

        assertTrue(isBackdrop(image, 40, 40))
        val ring = (0 until 360 step 5).map { Math.toRadians(it.toDouble()) }.map {
            (40 + 36 * Math.cos(it)).toInt() to (40 + 36 * Math.sin(it)).toInt()
        }
        assertTrue(ring.any { (x, y) -> isFilled(image, x, y) })
        assertTrue(ring.any { (x, y) -> isBackdrop(image, x, y) })
    }
}

@Config(sdk = [26])
class ProgressShapeAndroid8BehaviorTest : ProgressShapeBehavior()

@Config(sdk = [33])
class ProgressShapeAndroid13BehaviorTest : ProgressShapeBehavior()
