package lib.sketchbook.haptic

import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.SuspendingPointerInputModifierNode
import androidx.compose.ui.node.CompositionLocalConsumerModifierNode
import androidx.compose.ui.node.DelegatingNode
import androidx.compose.ui.node.ModifierNodeElement
import androidx.compose.ui.node.currentValueOf
import androidx.compose.ui.platform.InspectorInfo
import androidx.compose.ui.platform.LocalHapticFeedback

internal fun Modifier.deniedWhenDisabled(enabled: Boolean): Modifier =
    if (enabled) this else this then DeniedPressElement

private data object DeniedPressElement : ModifierNodeElement<DeniedPressNode>() {
    override fun create() = DeniedPressNode()

    override fun update(node: DeniedPressNode) = Unit

    override fun InspectorInfo.inspectableProperties() {
        name = "deniedWhenDisabled"
    }
}

private class DeniedPressNode : DelegatingNode(), CompositionLocalConsumerModifierNode {

    init {
        delegate(SuspendingPointerInputModifierNode {
            awaitEachGesture {
                awaitFirstDown(requireUnconsumed = false)
                currentValueOf(LocalHapticFeedback).denial()
            }
        })
    }
}
