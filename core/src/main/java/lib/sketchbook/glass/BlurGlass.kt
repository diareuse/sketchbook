package lib.sketchbook.glass

import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import dev.chrisbanes.haze.HazeInput
import dev.chrisbanes.haze.blur.HazeBlurStyle
import dev.chrisbanes.haze.blur.hazeBlur

internal class BlurGlass(
    private val fallback: GlassSurface
) : GlassSurface {
    override fun Modifier.glass(spec: GlassSpec, source: GlassSource?): Modifier {
        if (source == null) return with(fallback) { glass(spec, null) }
        val style = HazeBlurStyle { blurRadius(spec.blurRadius) }
        return clip(spec.shape)
            .hazeBlur(HazeInput.Sources(source.state), style)
            .glassTint(spec)
    }
}
