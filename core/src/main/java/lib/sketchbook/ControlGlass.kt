package lib.sketchbook

import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.layout.*
import androidx.compose.ui.unit.*
import lib.sketchbook.glass.asGlassShape
import lib.sketchbook.glass.glass
import lib.sketchbook.haptic.deniedWhenDisabled
import lib.sketchbook.theme.Theme

private val ControlGlassElevation = 12.dp

@Composable
internal fun Modifier.pressIndicated(shape: Shape, interactionSource: InteractionSource): Modifier =
    clip(shape).indication(interactionSource, LocalIndication.current)

private fun Modifier.minimumTouchTarget(size: Dp): Modifier =
    if (size.isUnspecified || size <= 0.dp) this
    else layout { measurable, constraints ->
        val placeable = measurable.measure(constraints)
        val minimum = size.roundToPx()
        val width = maxOf(placeable.width, minimum)
        val height = maxOf(placeable.height, minimum)
        layout(width, height) { placeable.place((width - placeable.width) / 2, (height - placeable.height) / 2) }
    }

@Composable
internal fun Modifier.glassControl(
    shape: Shape,
    enabled: Boolean,
    interactionSource: InteractionSource,
    tint: Color = Theme.color.emphasis.background,
    tintStrength: Float? = null
): Modifier {
    val glassShape = shape.asGlassShape()
    return deniedWhenDisabled(enabled)
        .minimumTouchTarget(LocalMinimumInteractiveComponentSize.current)
        .glass(
            glassShape,
            ControlGlassElevation,
            useGlow = enabled,
            tint = tint,
            tintStrength = tintStrength
        )
        .pressIndicated(glassShape, interactionSource)
}

@Composable
internal fun ThemePressHost(content: @Composable () -> Unit) {
    CompositionLocalProvider(
        LocalRippleConfiguration provides null,
        LocalMinimumInteractiveComponentSize provides Dp.Unspecified,
        content = content
    )
}

internal const val FilledTintStrength = .7f
internal const val ProminentTintStrength = .8f
internal const val TonalTintStrength = .6f
