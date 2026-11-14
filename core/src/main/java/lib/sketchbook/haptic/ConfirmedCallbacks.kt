package lib.sketchbook.haptic

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.platform.LocalHapticFeedback

@Composable
internal fun rememberConfirmedClick(onClick: () -> Unit): () -> Unit {
    val haptics = LocalHapticFeedback.current
    val current = rememberUpdatedState(onClick)
    return remember(haptics) {
        {
            haptics.confirmChange(null)
            current.value()
        }
    }
}

@Composable
internal fun rememberConfirmedChange(onChange: (Boolean) -> Unit): (Boolean) -> Unit {
    val haptics = LocalHapticFeedback.current
    val current = rememberUpdatedState(onChange)
    return remember(haptics) {
        { on ->
            haptics.confirmChange(on)
            current.value(on)
        }
    }
}

@Composable
internal fun rememberConfirmation(): (Boolean?) -> Unit {
    val haptics = LocalHapticFeedback.current
    return remember(haptics) { { on -> haptics.confirmChange(on) } }
}
