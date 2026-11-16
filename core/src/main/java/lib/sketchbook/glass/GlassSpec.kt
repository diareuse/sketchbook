package lib.sketchbook.glass

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp

@Immutable
internal data class GlassSpec(
    val shape: Shape,
    val tint: Color,
    val tintStrength: Float,
    val rimIntensity: Float,
    val blurRadius: Dp
) {
    companion object {
        private const val BaseTint = .09f
        private const val TintPerElevationDp = .02f
        private const val MaxTint = .6f
        private const val GlowRim = .9f
        private const val QuietRim = .35f

        fun of(
            shape: Shape,
            tint: Color,
            tonalElevation: Dp,
            useGlow: Boolean,
            blurRadius: Dp,
            tintStrength: Float? = null,
            rimIntensity: Float? = null
        ) = GlassSpec(
            shape = glassShape(shape),
            tint = tint,
            tintStrength = tintStrength
                ?: (BaseTint + tonalElevation.value * TintPerElevationDp).coerceIn(BaseTint, MaxTint),
            rimIntensity = rimIntensity ?: if (useGlow) GlowRim else QuietRim,
            blurRadius = blurRadius
        )
    }
}
