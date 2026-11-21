package lib.sketchbook.haptic

import android.view.HapticFeedbackConstants
import android.view.View
import androidx.compose.runtime.Immutable
import androidx.compose.ui.hapticfeedback.HapticFeedback
import androidx.compose.ui.hapticfeedback.HapticFeedbackType

@Immutable
class PlatformHapticFeedback(
    private val view: View
) : HapticFeedback {

    override fun performHapticFeedback(hapticFeedbackType: HapticFeedbackType) {
        val constant = Supported[hapticFeedbackType] ?: return
        view.performHapticFeedback(constant)
    }

    private companion object {
        val Supported: Map<HapticFeedbackType, Int> = intArrayOf(
            HapticFeedbackConstants.LONG_PRESS,
            HapticFeedbackConstants.VIRTUAL_KEY,
            HapticFeedbackConstants.KEYBOARD_TAP,
            HapticFeedbackConstants.CLOCK_TICK,
            HapticFeedbackConstants.CONTEXT_CLICK,
            HapticFeedbackConstants.KEYBOARD_RELEASE,
            HapticFeedbackConstants.VIRTUAL_KEY_RELEASE,
            HapticFeedbackConstants.TEXT_HANDLE_MOVE,
            HapticFeedbackConstants.GESTURE_START,
            HapticFeedbackConstants.GESTURE_END,
            HapticFeedbackConstants.CONFIRM,
            HapticFeedbackConstants.REJECT,
            HapticFeedbackConstants.TOGGLE_ON,
            HapticFeedbackConstants.TOGGLE_OFF,
            HapticFeedbackConstants.GESTURE_THRESHOLD_ACTIVATE,
            HapticFeedbackConstants.GESTURE_THRESHOLD_DEACTIVATE,
            HapticFeedbackConstants.DRAG_START,
            HapticFeedbackConstants.SEGMENT_TICK,
            HapticFeedbackConstants.SEGMENT_FREQUENT_TICK
        ).associateBy { HapticFeedbackType(it) }
    }

}
