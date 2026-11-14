package lib.sketchbook.haptic

import android.os.Build
import android.view.HapticFeedbackConstants
import androidx.compose.ui.hapticfeedback.HapticFeedback

internal fun HapticFeedback.confirmChange(on: Boolean?) {
    val atLeastUpsideDownCake = Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE
    when {
        on == null -> click()
        on && atLeastUpsideDownCake -> performHapticFeedback(HapticFeedbackConstants.TOGGLE_ON)
        on -> click()
        atLeastUpsideDownCake -> performHapticFeedback(HapticFeedbackConstants.TOGGLE_OFF)
        else -> tick()
    }
}

internal fun HapticFeedback.denial() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        performHapticFeedback(HapticFeedbackConstants.REJECT)
    } else {
        performHapticFeedback(HapticFeedbackConstants.LONG_PRESS)
    }
}
