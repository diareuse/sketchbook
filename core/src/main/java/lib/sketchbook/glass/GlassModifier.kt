package lib.sketchbook.glass

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import dev.chrisbanes.haze.hazeSource
import lib.sketchbook.theme.Theme

fun Modifier.glassSource(): Modifier = composed {
    val host = LocalGlassHost.current
    if (host == null) Modifier else Modifier.hazeSource(host.state)
}

@Composable
internal fun Modifier.glass(
    shape: Shape,
    tonalElevation: Dp = 0.dp,
    useGlow: Boolean = false,
    blurRadius: Dp = 24.dp,
    tint: Color = Theme.color.emphasis.background
): Modifier {
    val spec = remember(shape, tint, tonalElevation, useGlow, blurRadius) {
        GlassSpec.of(shape, tint, tonalElevation, useGlow, blurRadius)
    }
    val glass = LocalGlass.current
    val source = if (LocalGlassDepth.current > 0) null else LocalGlassHost.current
    val glassModifier = remember(glass, spec, source) { with(glass) { Modifier.glass(spec, source) } }
    return this then glassModifier
}
