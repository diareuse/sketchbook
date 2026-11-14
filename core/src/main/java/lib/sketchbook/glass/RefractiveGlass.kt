package lib.sketchbook.glass

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
            shape(spec.shape as? RoundedCornerShape ?: DefaultCorner)
            tint(spec.tint.copy(alpha = spec.tintStrength))
            specularIntensity(0f)
            if (spec.rimIntensity <= 0f) {
                edgeShadow(Color.Transparent)
                ambientResponse(0f)
            }
        }
        return hazeGlass(HazeInput.Sources(source.state), style)
    }
}

private val DefaultCorner = RoundedCornerShape(24.dp)
