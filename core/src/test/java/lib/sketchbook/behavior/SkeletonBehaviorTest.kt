package lib.sketchbook.behavior

import android.provider.Settings
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toPixelMap
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.assertHeightIsEqualTo
import androidx.compose.ui.test.assertWidthIsEqualTo
import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.unit.dp
import lib.sketchbook.SketchbookSkeleton
import lib.sketchbook.SketchbookSkeletonCircle
import lib.sketchbook.SketchbookSkeletonText
import lib.sketchbook.opaque
import lib.sketchbook.theme.Theme
import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.robolectric.annotation.Config

abstract class SkeletonBehavior : BehaviorTest() {

    private fun show(content: @Composable () -> Unit) {
        composeRule.mainClock.autoAdvance = false
        composeRule.setContent {
            Theme(darkTheme = false, dynamicColor = false) {
                Box(Modifier.background(Color.White)) { content() }
            }
        }
        composeRule.mainClock.advanceTimeBy(16)
    }

    private fun advance(millis: Long) = composeRule.mainClock.advanceTimeBy(millis)

    private fun pixels() = composeRule.onRoot().captureToImage().toPixelMap()

    private fun frame(): FloatArray {
        val map = pixels()
        return FloatArray(map.width * map.height) { map[it % map.width, it / map.width].let { c -> c.red + c.green + c.blue } }
    }

    @Test
    fun `skeleton fills its bounds with the placeholder color`() {
        show {
            Box(Modifier.size(200.dp, 40.dp)) {
                SketchbookSkeleton(Modifier.size(100.dp, 40.dp), color = Color.Black, animated = false)
            }
        }

        val image = pixels()
        assertEquals(Color.Black, image.opaque(100, 40))
        assertEquals(Color.White, image.opaque(300, 40))
    }

    @Test
    fun `skeleton respects the requested size`() {
        show { SketchbookSkeleton(Modifier.testTag("sk").size(100.dp, 40.dp), animated = false) }

        composeRule.onNodeWithTag("sk").assertWidthIsEqualTo(100.dp).assertHeightIsEqualTo(40.dp)
    }

    @Test
    fun `circular skeleton clips its corners`() {
        show { SketchbookSkeletonCircle(size = 60.dp, color = Color.Black, animated = false) }

        val image = pixels()
        assertEquals(Color.Black, image.opaque(60, 60))
        assertEquals(Color.White, image.opaque(3, 3))
    }

    @Test
    fun `skeleton lines shorten the last line`() {
        show {
            SketchbookSkeletonText(
                modifier = Modifier.size(200.dp, 80.dp),
                lines = 3,
                color = Color.Black,
                lineHeight = 16.dp,
                lineSpacing = 8.dp,
                lastLineFraction = .5f,
                animated = false
            )
        }

        val image = pixels()
        assertEquals(Color.Black, image.opaque(380, 16))
        assertEquals(Color.Black, image.opaque(380, 64))
        assertEquals(Color.White, image.opaque(380, 112))
        assertEquals(Color.Black, image.opaque(100, 112))
    }

    @Test
    fun `skeleton lines reject a count below one`() {
        val failure = runCatching { show { SketchbookSkeletonText(lines = 0) } }

        assertTrue(failure.exceptionOrNull() is IllegalArgumentException)
    }

    @Test
    fun `animated skeleton sweeps a highlight over time`() {
        show { SketchbookSkeleton(Modifier.size(100.dp, 40.dp), color = Color.Black) }

        val first = frame()
        advance(500)

        assertFalse(first.contentEquals(frame()))
    }

    @Test
    fun `static skeleton holds a stable frame`() {
        show { SketchbookSkeleton(Modifier.size(100.dp, 40.dp), color = Color.Black, animated = false) }

        val first = frame()
        advance(500)

        assertArrayEquals(first, frame(), 0f)
    }

    @Test
    fun `skeleton holds still when the system disabled animations`() {
        Settings.Global.putFloat(composeRule.activity.contentResolver, Settings.Global.ANIMATOR_DURATION_SCALE, 0f)
        show { SketchbookSkeleton(Modifier.size(100.dp, 40.dp), color = Color.Black) }

        val first = frame()
        advance(500)

        assertArrayEquals(first, frame(), 0f)
    }

    @Test
    fun `skeleton is hidden from accessibility services`() {
        show { SketchbookSkeleton(Modifier.testTag("sk").size(100.dp, 40.dp), animated = false) }

        composeRule.onNodeWithTag("sk")
            .assert(SemanticsMatcher.keyIsDefined(SemanticsProperties.InvisibleToUser))
    }
}

@Config(sdk = [26])
class SkeletonAndroid8BehaviorTest : SkeletonBehavior()

@Config(sdk = [33])
class SkeletonAndroid13BehaviorTest : SkeletonBehavior()
