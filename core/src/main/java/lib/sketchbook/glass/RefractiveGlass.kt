package lib.sketchbook.glass

import androidx.compose.foundation.shape.CornerBasedShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.chrisbanes.haze.HazeInput
import dev.chrisbanes.haze.glass.GlassStyle
import dev.chrisbanes.haze.glass.hazeGlass

internal class RefractiveGlass(
    private val fallback: GlassSurface
) : GlassSurface {
    override fun Modifier.glass(spec: GlassSpec, source: GlassSource?): Modifier {
        if (source == null) return with(fallback) { glass(spec, null) }
        val style = GlassStyle.regular.then {
            shape(spec.shape.rounded())
            tint(spec.tint.copy(alpha = spec.tintStrength))
            specularIntensity(spec.rimIntensity)
        }
        return hazeGlass(HazeInput.Sources(source.state), style)
    }
}

private val DefaultCorner = RoundedCornerShape(24.dp)

private fun androidx.compose.ui.graphics.Shape.rounded(): RoundedCornerShape = when (this) {
    is RoundedCornerShape -> this
    is CornerBasedShape -> RoundedCornerShape(topStart, topEnd, bottomEnd, bottomStart)
    else -> DefaultCorner
}
