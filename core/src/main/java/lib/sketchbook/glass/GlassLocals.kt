package lib.sketchbook.glass

import android.os.Build
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import dev.chrisbanes.haze.rememberHazeState

internal val LocalGlass = staticCompositionLocalOf<GlassSurface> { TintGlass() }

internal val LocalGlassHost = staticCompositionLocalOf<GlassSource?> { null }

internal val LocalGlassDepth = compositionLocalOf { 0 }

internal fun glassSurfaceFor(sdkInt: Int): GlassSurface {
    val tint = TintGlass()
    return TieredGlass(
        sdkInt = sdkInt,
        refractive = RefractiveGlass(fallback = tint),
        blur = BlurGlass(fallback = tint),
        tint = tint
    )
}

@Composable
internal fun ProvideGlass(content: @Composable () -> Unit) {
    val state = rememberHazeState()
    val source = remember(state) { GlassSource(state) }
    val glass = remember { glassSurfaceFor(Build.VERSION.SDK_INT) }
    CompositionLocalProvider(
        LocalGlass provides glass,
        LocalGlassHost provides source,
        LocalGlassDepth provides 0,
        content = content
    )
}

@Composable
internal fun GlassDepthScope(content: @Composable () -> Unit) {
    CompositionLocalProvider(
        LocalGlassDepth provides LocalGlassDepth.current + 1,
        content = content
    )
}
