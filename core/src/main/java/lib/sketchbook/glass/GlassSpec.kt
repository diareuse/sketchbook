package lib.sketchbook.glass

import androidx.compose.runtime.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.unit.*

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
            tonalElevation: Dp
        ) = GlassSpec(
            shape = glassShape(shape),
            tint = tint,
            tintStrength = (BaseTint + tonalElevation.value * TintPerElevationDp)
                .coerceIn(BaseTint, MaxTint),
            rimIntensity = if (tonalElevation > 0.dp) GlowRim else QuietRim,
            blurRadius = tonalElevation
        )
    }
}
