package lib.sketchbook

import androidx.compose.material3.LocalMinimumInteractiveComponentSize
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import lib.sketchbook.glass.glass

private val ControlGlassElevation = 12.dp

@Composable
internal fun Modifier.glassControl(shape: Shape, enabled: Boolean): Modifier =
    minimumInteractiveComponentSize().glass(shape, ControlGlassElevation, useGlow = enabled)

@Composable
internal fun GlassTouchTarget(glass: Boolean, content: @Composable () -> Unit) {
    if (glass) {
        CompositionLocalProvider(
            LocalMinimumInteractiveComponentSize provides Dp.Unspecified,
            content = content
        )
    } else {
        content()
    }
}
