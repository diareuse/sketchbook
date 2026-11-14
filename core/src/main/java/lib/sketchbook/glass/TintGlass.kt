package lib.sketchbook.glass

import androidx.compose.foundation.background
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawOutline
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp

internal class TintGlass : GlassSurface {
    override fun Modifier.glass(spec: GlassSpec, source: GlassSource?): Modifier =
        clip(spec.shape).glassTint(spec)
}

internal fun Modifier.glassTint(spec: GlassSpec): Modifier =
    background(spec.tint.copy(alpha = spec.tintStrength), spec.shape).glassHighlights(spec)

private fun Modifier.glassHighlights(spec: GlassSpec): Modifier = drawWithCache {
    val outline = spec.shape.createOutline(size, layoutDirection, this)
    val end = Offset(size.width, size.height)
    val specular = Brush.linearGradient(
        0f to Color.White.copy(alpha = .22f * spec.rimIntensity),
        .45f to Color.Transparent,
        1f to Color.White.copy(alpha = .08f * spec.rimIntensity),
        start = Offset.Zero,
        end = end
    )
    val rim = Brush.linearGradient(
        0f to Color.White.copy(alpha = .6f * spec.rimIntensity),
        .5f to Color.White.copy(alpha = .08f * spec.rimIntensity),
        1f to Color.White.copy(alpha = .3f * spec.rimIntensity),
        start = Offset.Zero,
        end = end
    )
    val stroke = Stroke(width = 1.dp.toPx())
    onDrawBehind {
        drawOutline(outline, specular)
        drawOutline(outline, rim, style = stroke)
    }
}
