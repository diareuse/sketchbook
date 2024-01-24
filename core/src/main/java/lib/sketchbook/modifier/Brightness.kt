package lib.sketchbook.modifier

import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.platform.LocalContext
import lib.sketchbook.util.findActivity

fun Modifier.screenBrightness(full: Boolean) = screenBrightness(if (full) 1f else -1f)

fun Modifier.screenBrightness(brightness: Float) = composed {
    val context = LocalContext.current
    DisposableEffect(context, brightness) {
        if (brightness < 0) return@DisposableEffect onDispose {}
        val window = context.findActivity().window
        val initialBrightness = window.attributes.screenBrightness
        window.attributes = window.attributes.apply {
            screenBrightness = brightness
        }
        onDispose {
            window.attributes = window.attributes.apply {
                screenBrightness = initialBrightness
            }
        }
    }
    this
}