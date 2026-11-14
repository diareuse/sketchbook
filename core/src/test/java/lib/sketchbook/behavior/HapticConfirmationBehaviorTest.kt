package lib.sketchbook.behavior

import android.view.HapticFeedbackConstants
import android.view.View
import androidx.test.core.app.ApplicationProvider
import lib.sketchbook.haptic.PlatformHapticFeedback
import lib.sketchbook.haptic.confirmChange
import lib.sketchbook.haptic.denial
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
class HapticConfirmationBehaviorTest {

    @Test
    @Config(sdk = [34])
    fun `turning something on performs the toggle on haptic on android 14`() =
        assertEquals(listOf(HapticFeedbackConstants.TOGGLE_ON), performed { confirmChange(true) })

    @Test
    @Config(sdk = [34])
    fun `turning something off performs the toggle off haptic on android 14`() =
        assertEquals(listOf(HapticFeedbackConstants.TOGGLE_OFF), performed { confirmChange(false) })

    @Test
    @Config(sdk = [33])
    fun `turning something on performs a context click before android 14`() =
        assertEquals(listOf(HapticFeedbackConstants.CONTEXT_CLICK), performed { confirmChange(true) })

    @Test
    @Config(sdk = [33])
    fun `turning something off performs a clock tick before android 14`() =
        assertEquals(listOf(HapticFeedbackConstants.CLOCK_TICK), performed { confirmChange(false) })

    @Test
    @Config(sdk = [26])
    fun `a change without a direction performs a context click`() =
        assertEquals(listOf(HapticFeedbackConstants.CONTEXT_CLICK), performed { confirmChange(null) })

    @Test
    @Config(sdk = [30])
    fun `a denial performs the reject haptic on android 11`() =
        assertEquals(listOf(HapticFeedbackConstants.REJECT), performed { denial() })

    @Test
    @Config(sdk = [34])
    fun `a denial performs the reject haptic on android 14`() =
        assertEquals(listOf(HapticFeedbackConstants.REJECT), performed { denial() })

    @Test
    @Config(sdk = [29])
    fun `a denial performs a long press before android 11`() =
        assertEquals(listOf(HapticFeedbackConstants.LONG_PRESS), performed { denial() })

    private fun performed(block: PlatformHapticFeedback.() -> Unit): List<Int> {
        val view = RecordingView()
        PlatformHapticFeedback(view).block()
        return view.performed
    }

    private class RecordingView : View(ApplicationProvider.getApplicationContext()) {
        val performed = mutableListOf<Int>()

        override fun performHapticFeedback(feedbackConstant: Int): Boolean {
            performed += feedbackConstant
            return true
        }
    }
}
