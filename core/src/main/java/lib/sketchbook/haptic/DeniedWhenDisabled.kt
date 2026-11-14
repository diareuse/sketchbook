package lib.sketchbook.haptic

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.SuspendingPointerInputModifierNode
import androidx.compose.ui.layout.Measurable
import androidx.compose.ui.layout.MeasureResult
import androidx.compose.ui.layout.MeasureScope
import androidx.compose.ui.node.CompositionLocalConsumerModifierNode
import androidx.compose.ui.node.DelegatingNode
import androidx.compose.ui.node.LayoutModifierNode
import androidx.compose.ui.node.ModifierNodeElement
import androidx.compose.ui.node.currentValueOf
import androidx.compose.ui.platform.InspectorInfo
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

internal fun Modifier.deniedWhenDisabled(enabled: Boolean): Modifier =
    if (enabled) this else this then DeniedPressElement

private data object DeniedPressElement : ModifierNodeElement<DeniedPressNode>() {
    override fun create() = DeniedPressNode()

    override fun update(node: DeniedPressNode) = Unit

    override fun InspectorInfo.inspectableProperties() {
        name = "deniedWhenDisabled"
    }
}

private val WiggleAmplitude = 4.dp
private const val WiggleStepMillis = 40
private val WiggleSteps = listOf(-1f, 1f, -0.6f, 0.6f, 0f)

private class DeniedPressNode : DelegatingNode(), CompositionLocalConsumerModifierNode,
    LayoutModifierNode {

    private val offset = Animatable(0f)

    init {
        delegate(SuspendingPointerInputModifierNode {
            awaitEachGesture {
                awaitFirstDown(requireUnconsumed = false)
                currentValueOf(LocalHapticFeedback).denial()
                coroutineScope.launch { wiggle() }
            }
        })
    }

    override fun MeasureScope.measure(
        measurable: Measurable,
        constraints: Constraints,
    ): MeasureResult {
        val placeable = measurable.measure(constraints)
        return layout(placeable.width, placeable.height) {
            placeable.placeWithLayer(0, 0) { translationX = offset.value }
        }
    }

    private suspend fun wiggle() {
        val amplitude = with(currentValueOf(LocalDensity)) { WiggleAmplitude.toPx() }
        offset.snapTo(0f)
        WiggleSteps.forEach { step ->
            offset.animateTo(step * amplitude, tween(WiggleStepMillis))
        }
    }
}
