package lib.sketchbook.behavior

import android.view.HapticFeedbackConstants
import android.view.View
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.junit4.StateRestorationTester
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.cancel
import androidx.compose.ui.test.center
import androidx.compose.ui.test.down
import androidx.compose.ui.test.up
import androidx.compose.ui.unit.dp
import androidx.test.core.app.ApplicationProvider
import lib.sketchbook.haptic.PlatformHapticFeedback
import lib.sketchbook.haptic.TickOnChange
import lib.sketchbook.haptic.ClickOnChange
import lib.sketchbook.haptic.hapticIndication
import lib.sketchbook.haptic.rememberMutableInteractionSource
import lib.sketchbook.haptic.rememberVibrateIndication
import lib.sketchbook.haptic.tick
import org.junit.Assert.assertEquals
import org.junit.Test
import java.io.ByteArrayOutputStream
import java.io.PrintStream

class HapticsBehaviorTest : BehaviorTest() {

    private val tick = HapticFeedbackType(HapticFeedbackConstants.CLOCK_TICK)

    private fun pressable(haptics: RecordingHapticFeedback) {
        composeRule.setContent {
            CompositionLocalProvider(LocalHapticFeedback provides haptics) {
                Box(
                    Modifier
                        .testTag("target")
                        .size(48.dp)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = rememberVibrateIndication(),
                            onClick = {}
                        )
                )
            }
        }
    }

    private fun settle() {
        composeRule.waitForIdle()
        composeRule.mainClock.advanceTimeBy(200)
        composeRule.waitForIdle()
    }

    @Test
    fun `pressing an indicated element ticks once and releasing stays silent`() {
        val haptics = RecordingHapticFeedback()
        pressable(haptics)

        composeRule.onNodeWithTag("target").performTouchInput { down(center) }
        settle()
        assertEquals(listOf(tick), haptics.performed)

        composeRule.onNodeWithTag("target").performTouchInput { up() }
        settle()
        assertEquals(listOf(tick), haptics.performed)
    }

    @Test
    fun `cancelling a press does not trigger a reject`() {
        val haptics = RecordingHapticFeedback()
        pressable(haptics)

        composeRule.onNodeWithTag("target").performTouchInput { down(center) }
        settle()
        composeRule.onNodeWithTag("target").performTouchInput { cancel() }
        settle()

        assertEquals(listOf(tick), haptics.performed)
    }

    @Test
    fun `indication does not print interactions to standard output`() {
        val original = System.out
        val captured = ByteArrayOutputStream()
        System.setOut(PrintStream(captured, true))
        try {
            val haptics = RecordingHapticFeedback()
            pressable(haptics)
            composeRule.onNodeWithTag("target").performTouchInput { down(center) }
            settle()
        } finally {
            System.setOut(original)
        }

        assertEquals("", captured.toString())
    }

    @Test
    fun `haptic indication follows the interaction source it is given`() {
        val haptics = RecordingHapticFeedback()
        val first = MutableInteractionSource()
        val second = MutableInteractionSource()
        var source by mutableStateOf(first)
        composeRule.setContent {
            CompositionLocalProvider(LocalHapticFeedback provides haptics) {
                Box(Modifier.size(48.dp).hapticIndication(source))
            }
        }
        composeRule.waitForIdle()

        source = second
        composeRule.waitForIdle()
        first.tryEmit(PressInteraction.Press(Offset.Zero))
        settle()
        assertEquals(emptyList<HapticFeedbackType>(), haptics.performed)

        second.tryEmit(PressInteraction.Press(Offset.Zero))
        settle()
        assertEquals(listOf(tick), haptics.performed)
    }

    @Test
    fun `destructuring a mutable interaction source state composes once`() {
        var compositions = 0
        composeRule.setContent {
            val (a, b, c) = rememberMutableInteractionSource()
            SideEffect { compositions++ }
            a.hashCode(); b.hashCode(); c.hashCode()
        }
        composeRule.waitForIdle()

        assertEquals(1, compositions)
    }

    @Test
    fun `tick on change survives state restoration with a value that cannot be saved`() {
        class Unsaveable(val id: Int)

        val tester = StateRestorationTester(composeRule)
        tester.setContent {
            TickOnChange(value = Unsaveable(1))
            ClickOnChange(value = Unsaveable(2))
        }
        composeRule.waitForIdle()

        tester.emulateSavedInstanceStateRestore()
        composeRule.waitForIdle()
    }

    @Test
    fun `platform haptic feedback performs the platform constant for known types`() {
        val view = RecordingView()
        val feedback = PlatformHapticFeedback(view)

        feedback.performHapticFeedback(HapticFeedbackType.LongPress)
        feedback.performHapticFeedback(HapticFeedbackType.Reject)
        feedback.tick()

        assertEquals(
            listOf(
                HapticFeedbackConstants.LONG_PRESS,
                HapticFeedbackConstants.REJECT,
                HapticFeedbackConstants.CLOCK_TICK
            ),
            view.performed
        )
    }

    @Test
    fun `platform haptic feedback ignores unknown types`() {
        val view = RecordingView()

        PlatformHapticFeedback(view).performHapticFeedback(HapticFeedbackType(999))

        assertEquals(emptyList<Int>(), view.performed)
    }

    private class RecordingView : View(ApplicationProvider.getApplicationContext()) {
        val performed = mutableListOf<Int>()

        override fun performHapticFeedback(feedbackConstant: Int): Boolean {
            performed += feedbackConstant
            return true
        }
    }
}
