package lib.sketchbook.indication

import androidx.compose.foundation.IndicationNodeFactory
import androidx.compose.foundation.interaction.InteractionSource
import androidx.compose.material.ripple.RippleAlpha
import androidx.compose.material.ripple.createRippleModifierNode
import androidx.compose.material3.LocalContentColor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedback
import androidx.compose.ui.graphics.ColorProducer
import androidx.compose.ui.graphics.isSpecified
import androidx.compose.ui.node.CompositionLocalConsumerModifierNode
import androidx.compose.ui.node.DelegatableNode
import androidx.compose.ui.node.DelegatingNode
import androidx.compose.ui.node.currentValueOf
import androidx.compose.ui.unit.Dp
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
) : DelegatingNode(), CompositionLocalConsumerModifierNode {

    init {
        delegate(VibrateNode(interactionSource, feedback))
        delegate(pressVisual(interactionSource, color))
    }

    private fun pressVisual(interactionSource: InteractionSource, color: Color): DelegatableNode =
        if (sdkAtLeast(Api.Agsl)) PressShaderNode(interactionSource, color)
        else createRippleModifierNode(
            interactionSource = interactionSource,
            bounded = true,
            radius = Dp.Unspecified,
            color = ColorProducer { if (color.isSpecified) color else currentValueOf(LocalContentColor) },
            rippleAlpha = { RippleAlpha(PressedAlpha, FocusedAlpha, DraggedAlpha, HoveredAlpha) }
        )
}

private const val PressedAlpha = .1f
private const val FocusedAlpha = .1f
private const val DraggedAlpha = .16f
private const val HoveredAlpha = .08f

@Composable
internal fun rememberGlassPressIndication(feedback: HapticFeedback): IndicationNodeFactory =
    remember(feedback) { GlassPressIndicationFactory(feedback) }
