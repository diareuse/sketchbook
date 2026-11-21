package lib.sketchbook.modifier

import androidx.compose.ui.Modifier
import androidx.compose.ui.node.CompositionLocalConsumerModifierNode
import androidx.compose.ui.node.ModifierNodeElement
import androidx.compose.ui.node.currentValueOf
import androidx.compose.ui.platform.LocalContext
import lib.sketchbook.util.findActivity

fun Modifier.screenBrightness(full: Boolean) = screenBrightness(if (full) 1f else -1f)

fun Modifier.screenBrightness(brightness: Float): Modifier =
    this then ScreenBrightnessElement(brightness)

private data class ScreenBrightnessElement(
    val brightness: Float
) : ModifierNodeElement<ScreenBrightnessNode>() {

    override fun create() = ScreenBrightnessNode(brightness)

    override fun update(node: ScreenBrightnessNode) {
        node.update(brightness)
    }
}

private class ScreenBrightnessNode(
    private var brightness: Float
) : Modifier.Node(), CompositionLocalConsumerModifierNode {

    private var restore: (() -> Unit)? = null

    override fun onAttach() {
        apply()
    }

    override fun onDetach() {
        release()
    }

    fun update(brightness: Float) {
        if (this.brightness == brightness) return
        release()
        this.brightness = brightness
        apply()
    }

    private fun apply() {
        if (brightness < 0) return
        val window = currentValueOf(LocalContext).findActivity().window
        val initialBrightness = window.attributes.screenBrightness
        window.attributes = window.attributes.apply {
            screenBrightness = brightness
        }
        restore = {
            window.attributes = window.attributes.apply {
                screenBrightness = initialBrightness
            }
        }
    }

    private fun release() {
        restore?.invoke()
        restore = null
    }
}
