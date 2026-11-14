package lib.sketchbook.shader

import android.provider.Settings
import androidx.compose.ui.node.CompositionLocalConsumerModifierNode
import androidx.compose.ui.node.currentValueOf
import androidx.compose.ui.platform.LocalContext

internal fun animationsAllowed(durationScale: Float): Boolean = durationScale > 0f

internal fun CompositionLocalConsumerModifierNode.systemAnimationsAllowed(): Boolean {
    val resolver = currentValueOf(LocalContext).contentResolver
    val scale = Settings.Global.getFloat(resolver, Settings.Global.ANIMATOR_DURATION_SCALE, 1f)
    return animationsAllowed(scale)
}
