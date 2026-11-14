package lib.sketchbook.glass

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import dev.chrisbanes.haze.hazeSource
import lib.sketchbook.theme.Theme

fun Modifier.glassSource(): Modifier = composed { glassSourceOf(LocalGlassHost.current) }

internal fun Modifier.glassSourceOf(host: GlassSource?): Modifier = composed {
    if (host == null) {
        Modifier
    } else {
        DisposableEffect(host) {
            host.attach()
            onDispose { host.detach() }
        }
        Modifier.hazeSource(host.state)
    }
}

@Composable
internal fun Modifier.glass(
    shape: Shape,
    tonalElevation: Dp = 0.dp,
    useGlow: Boolean = false,
    blurRadius: Dp = 24.dp,
    tint: Color = Theme.color.emphasis.background,
    tintStrength: Float? = null
): Modifier {
    val spec = remember(shape, tint, tonalElevation, useGlow, blurRadius, tintStrength) {
        GlassSpec.of(shape, tint, tonalElevation, useGlow, blurRadius, tintStrength)
    }
    val glass = LocalGlass.current
    val source = if (LocalGlassDepth.current > 0) null else LocalGlassHost.current?.takeIf { it.active }
    val glassModifier = remember(glass, spec, source) { with(glass) { Modifier.glass(spec, source) } }
    return this then glassModifier
}
