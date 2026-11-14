package lib.sketchbook.indication

import androidx.compose.foundation.IndicationNodeFactory
import androidx.compose.foundation.interaction.InteractionSource
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedback
import androidx.compose.ui.node.DelegatableNode
import androidx.compose.ui.node.DelegatingNode
import lib.sketchbook.haptic.VibrateNode
import lib.sketchbook.util.Api
import lib.sketchbook.util.sdkAtLeast

internal class GlassPressIndicationFactory(
    private val feedback: HapticFeedback,
    private val color: Color = Color.Unspecified
) : IndicationNodeFactory {

    override fun create(interactionSource: InteractionSource): DelegatableNode =
        GlassPressNode(interactionSource, feedback, color)

    override fun equals(other: Any?): Boolean =
        other is GlassPressIndicationFactory && other.feedback == feedback && other.color == color

    override fun hashCode(): Int = 31 * feedback.hashCode() + color.hashCode()
}

internal class GlassPressNode(
    interactionSource: InteractionSource,
    feedback: HapticFeedback,
    color: Color
) : DelegatingNode() {

    init {
        delegate(VibrateNode(interactionSource, feedback))
        delegate(pressVisual(interactionSource, color))
    }
}

private fun pressVisual(interactionSource: InteractionSource, color: Color): DelegatableNode =
    if (sdkAtLeast(Api.Agsl)) PressShaderNode(interactionSource, color)
    else ripple(color = color).create(interactionSource)

@Composable
internal fun rememberGlassPressIndication(feedback: HapticFeedback): IndicationNodeFactory =
    remember(feedback) { GlassPressIndicationFactory(feedback) }
