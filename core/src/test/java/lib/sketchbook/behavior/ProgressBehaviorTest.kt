package lib.sketchbook.behavior

import android.provider.Settings
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toPixelMap
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.ProgressBarRangeInfo
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import lib.sketchbook.SketchbookCircularProgress
import lib.sketchbook.SketchbookLinearProgress
import lib.sketchbook.distance
import lib.sketchbook.opaque
import lib.sketchbook.theme.Theme
import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.robolectric.annotation.Config

abstract class ProgressBehavior : BehaviorTest() {

    private var progress by mutableFloatStateOf(.5f)
    private var compositions = 0

    private fun show(direction: LayoutDirection = LayoutDirection.Ltr, content: @Composable () -> Unit) {
        composeRule.mainClock.autoAdvance = false
        composeRule.setContent {
            SideEffect { compositions++ }
            CompositionLocalProvider(LocalLayoutDirection provides direction) {
                Theme(darkTheme = false, dynamicColor = false) {
                    Box(Modifier.background(Color.White).size(200.dp, 48.dp)) { content() }
                }
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

    private fun disableAnimations() {
        Settings.Global.putFloat(composeRule.activity.contentResolver, Settings.Global.ANIMATOR_DURATION_SCALE, 0f)
    }

    private fun rangeInfo(value: Float) =
        SemanticsMatcher.expectValue(SemanticsProperties.ProgressBarRangeInfo, ProgressBarRangeInfo(value, 0f..1f))

    private val indeterminate =
        SemanticsMatcher.expectValue(SemanticsProperties.ProgressBarRangeInfo, ProgressBarRangeInfo.Indeterminate)

    @Composable
    private fun Linear(modifier: Modifier = Modifier.testTag("bar").fillMaxWidth().height(12.dp)) =
        SketchbookLinearProgress(progress = { progress }, modifier = modifier, color = Color.Red)

    @Test
    fun `linear progress reports its value to accessibility`() {
        progress = .4f
        show { Linear() }

        composeRule.onNodeWithTag("bar").assert(rangeInfo(.4f))
    }

    @Test
    fun `linear progress clamps out of range values`() {
        progress = 1.7f
        show { Linear() }

        composeRule.onNodeWithTag("bar").assert(rangeInfo(1f))
    }

    @Test
    fun `linear progress fills the track up to its value`() {
        progress = .5f
        show { Linear(Modifier.fillMaxWidth().height(12.dp)) }

        val image = pixels()
        assertTrue(distance(Color.Red, image.opaque(100, 12)) < 120)
        assertTrue(distance(Color.Red, image.opaque(300, 12)) > 250)
    }

    @Test
    fun `linear progress fills from the end side in a right to left layout`() {
        progress = .5f
        show(LayoutDirection.Rtl) { Linear(Modifier.fillMaxWidth().height(12.dp)) }

        val image = pixels()
        assertTrue(distance(Color.Red, image.opaque(300, 12)) < 120)
        assertTrue(distance(Color.Red, image.opaque(100, 12)) > 250)
    }

    @Test
    fun `linear progress follows state without recomposing`() {
        progress = .2f
        show { Linear(Modifier.fillMaxWidth().height(12.dp)) }
        val before = frame()
        val count = compositions

        progress = .9f
        advance(32)

        assertFalse(before.contentEquals(frame()))
        assertEquals(count, compositions)
    }

    @Test
    fun `indeterminate linear progress reports an unknown value`() {
        show { SketchbookLinearProgress(Modifier.testTag("bar").fillMaxWidth().height(12.dp), color = Color.Red) }

        composeRule.onNodeWithTag("bar").assert(indeterminate)
    }

    @Test
    fun `indeterminate linear progress animates without recomposing`() {
        show { SketchbookLinearProgress(Modifier.fillMaxWidth().height(12.dp), color = Color.Red) }
        val first = frame()
        val count = compositions

        advance(500)

        assertFalse(first.contentEquals(frame()))
        assertEquals(count, compositions)
    }

    @Test
    fun `indeterminate linear progress shows a segment while animations are disabled`() {
        disableAnimations()
        show { SketchbookLinearProgress(Modifier.fillMaxWidth().height(12.dp), color = Color.Red) }
        val first = frame()

        advance(500)

        assertArrayEquals(first, frame(), 0f)
        val image = pixels()
        assertTrue((0 until 400 step 4).any { distance(Color.Red, image.opaque(it, 12)) < 120 })
    }

    @Test
    fun `circular progress reports its value to accessibility`() {
        progress = .25f
        show { SketchbookCircularProgress(progress = { progress }, modifier = Modifier.testTag("ring")) }

        composeRule.onNodeWithTag("ring").assert(rangeInfo(.25f))
    }

    @Test
    fun `circular progress draws more of the ring as the value grows`() {
        progress = .1f
        show { SketchbookCircularProgress(progress = { progress }, color = Color.Red) }
        val low = ringCoverage()

        progress = .9f
        advance(32)

        assertTrue(ringCoverage() > low + 20)
    }

    private fun ringCoverage(): Int {
        val image = pixels()
        var hits = 0
        for (y in 0 until image.height step 2) for (x in 0 until image.width step 2) {
            if (distance(Color.Red, image.opaque(x, y)) < 120) hits++
        }
        return hits
    }

    @Test
    fun `indeterminate circular progress reports an unknown value and animates without recomposing`() {
        show { SketchbookCircularProgress(Modifier.testTag("ring"), color = Color.Red) }
        val first = frame()
        val count = compositions

        advance(400)

        composeRule.onNodeWithTag("ring").assert(indeterminate)
        assertFalse(first.contentEquals(frame()))
        assertEquals(count, compositions)
    }

    @Test
    fun `indeterminate circular progress holds still while animations are disabled`() {
        disableAnimations()
        show { SketchbookCircularProgress(color = Color.Red) }
        val first = frame()

        advance(400)

        assertArrayEquals(first, frame(), 0f)
        assertTrue(ringCoverage() > 0)
    }
}

@Config(sdk = [26])
class ProgressAndroid8BehaviorTest : ProgressBehavior()

@Config(sdk = [33])
class ProgressAndroid13BehaviorTest : ProgressBehavior()
