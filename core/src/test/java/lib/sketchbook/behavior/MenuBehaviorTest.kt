package lib.sketchbook.behavior

import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import lib.sketchbook.MenuState
import lib.sketchbook.OverlayState
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Test

class MenuBehaviorTest : BehaviorTest() {

    @Test
    fun `closing a menu while it is still opening leaves it fully closed`() {
        val overlay = OverlayState()
        val state = MenuState(overlay, RecordingHapticFeedback())
        lateinit var scope: CoroutineScope
        composeRule.setContent { scope = rememberCoroutineScope() }
        composeRule.waitForIdle()

        composeRule.mainClock.autoAdvance = false
        scope.launch { state.open() }
        composeRule.mainClock.advanceTimeBy(100)
        scope.launch { state.close() }
        composeRule.mainClock.advanceTimeBy(5_000)
        composeRule.waitForIdle()

        assertFalse(state.expanded)
        assertFalse(overlay.expanded)
        assertEquals(0f, state.expansionAlpha, 0f)
        assertEquals(1f, state.scale, 0f)
        assertEquals(1f, state.shadowAlpha, 0f)
    }
}
