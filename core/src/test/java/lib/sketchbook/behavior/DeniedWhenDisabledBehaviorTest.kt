package lib.sketchbook.behavior

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.TouchInjectionScope
import androidx.compose.ui.test.center
import androidx.compose.ui.test.down
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.up
import androidx.compose.ui.unit.dp
import lib.sketchbook.haptic.deniedWhenDisabled
import lib.sketchbook.theme.Theme
import org.junit.Assert.assertEquals
import org.junit.Test

class DeniedWhenDisabledBehaviorTest : BehaviorTest() {

    private val haptics = RecordingHapticFeedback()
    private var enabled by mutableStateOf(false)
    private var clicks = 0

    @Test
    fun `a disabled target performs one denial per press`() {
        show(enabled = false)

        tap()
        tap()

        assertEquals(listOf(HapticFeedbackType.Reject, HapticFeedbackType.Reject), haptics.performed)
    }

    @Test
    fun `holding a disabled target does not repeat the denial`() {
        show(enabled = false)

        composeRule.onNodeWithTag(Target).performTouchInput { down(center) }
        composeRule.mainClock.advanceTimeBy(1_000)
        composeRule.onNodeWithTag(Target).performTouchInput { up() }

        assertEquals(listOf(HapticFeedbackType.Reject), haptics.performed)
    }

    @Test
    fun `a second finger on a disabled target does not add a denial`() {
        show(enabled = false)

        composeRule.onNodeWithTag(Target).performTouchInput {
            down(0, center)
            down(1, center)
            up(0)
            up(1)
        }

        assertEquals(listOf(HapticFeedbackType.Reject), haptics.performed)
    }

    @Test
    fun `an enabled target performs no denial`() {
        show(enabled = true)

        tap()

        assertEquals(emptyList<HapticFeedbackType>(), haptics.performed)
    }

    @Test
    fun `enabling a target stops the denials`() {
        show(enabled = false)
        tap()

        enabled = true
        composeRule.waitForIdle()
        tap()

        assertEquals(listOf(HapticFeedbackType.Reject), haptics.performed)
    }

    @Test
    fun `the denial does not consume the press from the parent`() {
        show(enabled = false)

        tap()

        assertEquals(1, clicks)
    }

    private fun show(enabled: Boolean) {
        this.enabled = enabled
        composeRule.setContent {
            Theme(darkTheme = false, dynamicColor = false) {
                CompositionLocalProvider(LocalHapticFeedback provides haptics) {
                    Box(
                        Modifier
                            .testTag(Target)
                            .size(48.dp)
                            .clickable { clicks++ }
                            .deniedWhenDisabled(this.enabled)
                    )
                }
            }
        }
        composeRule.waitForIdle()
    }

    private fun tap() {
        composeRule.onNodeWithTag(Target).performTouchInput { tapOnce() }
        composeRule.waitForIdle()
    }

    private fun TouchInjectionScope.tapOnce() {
        down(center)
        up()
    }

    private companion object {
        const val Target = "target"
    }
}
