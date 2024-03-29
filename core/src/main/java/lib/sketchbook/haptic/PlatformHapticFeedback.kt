package lib.sketchbook.haptic

import android.view.View
import androidx.compose.runtime.Immutable
import androidx.compose.ui.hapticfeedback.HapticFeedback
import androidx.compose.ui.hapticfeedback.HapticFeedbackType

@Immutable
class PlatformHapticFeedback(
    private val view: View
) : HapticFeedback {

    override fun performHapticFeedback(hapticFeedbackType: HapticFeedbackType) {
        val field = hapticFeedbackType::class.java
            .declaredFields.firstOrNull { it.name == "value" }
            ?: return
        field.isAccessible = true
        view.performHapticFeedback(field.getInt(hapticFeedbackType))
    }

}