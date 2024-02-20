package lib.sketchbook.util

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.focus.FocusRequester

@Composable
fun rememberFocusRequester(requestFocus: Boolean = true): FocusRequester {
    return remember { FocusRequester() }.also {
        if (requestFocus) DisposableEffect(it) {
            it.requestFocus()
            onDispose {}
        }
    }
}