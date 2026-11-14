package lib.sketchbook

import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.indication
import androidx.compose.foundation.interaction.InteractionSource
import androidx.compose.material3.LocalMinimumInteractiveComponentSize
import androidx.compose.material3.LocalRippleConfiguration
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.layout
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.isUnspecified
import androidx.compose.ui.unit.dp
import lib.sketchbook.glass.glass

private val ControlGlassElevation = 12.dp

@Composable
internal fun Modifier.pressIndicated(shape: Shape, interactionSource: InteractionSource): Modifier =
    clip(shape).indication(interactionSource, LocalIndication.current)

@Composable
internal fun Modifier.pressControl(shape: Shape, interactionSource: InteractionSource): Modifier =
    minimumTouchTarget(LocalMinimumInteractiveComponentSize.current).pressIndicated(shape, interactionSource)

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
internal fun Modifier.glassControl(shape: Shape, enabled: Boolean, interactionSource: InteractionSource): Modifier =
    minimumInteractiveComponentSize()
        .glass(shape, ControlGlassElevation, useGlow = enabled)
        .pressIndicated(shape, interactionSource)

@Composable
internal fun ThemePressHost(content: @Composable () -> Unit) {
    CompositionLocalProvider(
        LocalRippleConfiguration provides null,
        LocalMinimumInteractiveComponentSize provides Dp.Unspecified,
        content = content
    )
}
