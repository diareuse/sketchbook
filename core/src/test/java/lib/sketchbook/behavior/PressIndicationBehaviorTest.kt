package lib.sketchbook.behavior

import android.view.HapticFeedbackConstants
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toPixelMap
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.cancel
import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.down
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.center
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.up
import androidx.compose.ui.unit.dp
import lib.sketchbook.theme.Theme
import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Test
import org.robolectric.annotation.Config

abstract class PressIndicationBehavior : BehaviorTest() {

    private val tick = HapticFeedbackType(HapticFeedbackConstants.CLOCK_TICK)
    private val haptics = RecordingHapticFeedback()
    private var clicks = 0

    @Test
    fun `pressing a themed control ticks exactly once`() {
        show()
        press()
        advance(100)
        assertEquals(listOf(tick), haptics.performed)
    }

    @Test
    fun `releasing a themed control adds no further tick`() {
        show()
        press()
        advance(100)
        release()
        advance(600)
        assertEquals(listOf(tick), haptics.performed)
    }

    @Test
    fun `cancelling a press adds no further tick`() {
        show()
        press()
        advance(100)
        composeRule.onNodeWithTag(Target).performTouchInput { cancel() }
        advance(600)
        assertEquals(listOf(tick), haptics.performed)
    }

    @Test
    fun `an untouched control never ticks`() {
        show()
        advance(600)
        assertEquals(emptyList<HapticFeedbackType>(), haptics.performed)
    }

    @Test
    fun `a pressed control shows a press highlight`() {
        show()
        val idle = frame()
        press()
        advance(150)
        assertFalse(idle.contentEquals(frame()))
    }

    @Test
    fun `the press highlight fades after release`() {
        show()
        val idle = frame()
        press()
        advance(150)
        release()
        advance(1500)
        assertArrayEquals(idle, frame(), 0f)
    }

    @Test
    fun `a themed control still reports clicks`() {
        show()
        press()
        release()
        advance(100)
        assertEquals(1, clicks)
    }

    private fun show() {
        composeRule.mainClock.autoAdvance = false
        composeRule.setContent {
            CompositionLocalProvider(LocalHapticFeedback provides haptics) {
                Theme(darkTheme = false, dynamicColor = false) {
                    Box(
                        Modifier
                            .testTag(Target)
                            .size(80.dp)
                            .background(Color.Black)
                            .clickable { clicks++ }
                    )
                }
            }
        }
        advance(16)
    }

    private fun press() {
        composeRule.onNodeWithTag(Target).performTouchInput { down(center) }
        advance(16)
    }

    private fun release() {
        composeRule.onNodeWithTag(Target).performTouchInput { up() }
        advance(16)
    }

    private fun advance(millis: Long) = composeRule.mainClock.advanceTimeBy(millis)

    private fun frame(): FloatArray {
        val map = composeRule.onRoot().captureToImage().toPixelMap()
        return FloatArray(map.width * map.height) { map[it % map.width, it / map.width].let { c -> c.red + c.green + c.blue } }
    }

    private companion object {
        const val Target = "target"
    }
}

@Config(sdk = [26])
class PressIndicationAndroid8BehaviorTest : PressIndicationBehavior()

@Config(sdk = [33])
class PressIndicationAndroid13BehaviorTest : PressIndicationBehavior()
