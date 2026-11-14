package lib.sketchbook.shader

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathMeasure
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.scale
import lib.sketchbook.modifier.BeamStyle
import kotlin.math.PI
import kotlin.math.max
import kotlin.math.pow
import kotlin.math.sin

internal class FallbackBeamPainter : BeamPainter {
    private val measure = PathMeasure()
    private val segment = Path()
    private var measured: BeamGeometry? = null

    override fun DrawScope.paint(geometry: BeamGeometry, look: BeamLook, phase: Float, fade: Float) {
        val strength = (look.intensity * fade).coerceIn(0f, 1f)
        if (strength <= 0f) return
        clipPath(geometry.path) {
            scale(density, density, Offset.Zero) {
                if (look.style == BeamStyle.Line) {
                    sweep(look, phase, strength)
                } else {
                    orbit(geometry, look, phase, strength)
                }
            }
        }
    }

    private fun DrawScope.orbit(geometry: BeamGeometry, look: BeamLook, phase: Float, strength: Float) {
        if (measured !== geometry) {
            measure.setPath(geometry.polyline, true)
            measured = geometry
        }
        val length = measure.length
        if (length <= 0f) return
        val palette = look.palette
        val arc = look.arc
        val glowWidth = max(look.glowReach * GlowScale, MinGlow) * Doubled
        for (k in 0 until Segments) {
            val t = (k + .5f) / Segments
            val color = samplePalette(palette, t)
            val weight = bell(t) * color.alpha * strength
            if (weight <= MinWeight) continue
            val position = (((1f - arc) + k * arc / Segments + look.directionSign * phase) % 1f + 1f) % 1f
            val from = position * length
            val to = from + arc * length / Segments * Overlap
            val pieces = if (to <= length) listOf(from to to) else listOf(from to length, 0f to (to - length))
            for ((a, b) in pieces) {
                segment.rewind()
                measure.getSegment(a, b, segment, true)
                val rgb = color.copy(alpha = 1f)
                drawPath(segment, rgb.copy(alpha = GlowAlpha * weight), style = Stroke(glowWidth, cap = StrokeCap.Butt))
                drawPath(segment, rgb.copy(alpha = MidAlpha * weight), style = Stroke(MidWidth * Doubled, cap = StrokeCap.Butt))
                drawPath(
                    segment,
                    rgb.copy(alpha = weight),
                    style = Stroke(look.strokeWidth.value * Doubled, cap = StrokeCap.Round)
                )
            }
        }
    }

    private fun DrawScope.sweep(look: BeamLook, phase: Float, strength: Float) {
        val width = size.width / density
        val y = size.height / density
        val length = width * SweepLength
        val triangle = if (phase < .5f) phase * 2f else (1f - phase) * 2f
        val eased = triangle * triangle * (3f - 2f * triangle)
        val start = (width - length) * eased
        val palette = look.palette
        val glowWidth = max(look.glowReach * GlowScale, MinGlow) * Doubled
        for (k in 0 until Segments) {
            val t = (k + .5f) / Segments
            val color = samplePalette(palette, t)
            val weight = bell(t) * color.alpha * strength
            if (weight <= MinWeight) continue
            val from = Offset(start + length * k / Segments, y)
            val to = Offset(start + length * (k + Overlap) / Segments, y)
            val rgb = color.copy(alpha = 1f)
            drawLine(rgb.copy(alpha = GlowAlpha * weight), from, to, glowWidth, StrokeCap.Round)
            drawLine(rgb.copy(alpha = MidAlpha * weight), from, to, MidWidth * Doubled, StrokeCap.Round)
            drawLine(rgb.copy(alpha = weight), from, to, look.strokeWidth.value * Doubled, StrokeCap.Round)
        }
    }

    private fun bell(t: Float): Float = max(sin(PI * t), 0.0).pow(1.2).toFloat()

    private companion object {
        const val Segments = 36
        const val Overlap = 1.05f
        const val GlowScale = .9f
        const val MinGlow = 6f
        const val Doubled = 2f
        const val MidWidth = 4f
        const val GlowAlpha = .22f
        const val MidAlpha = .45f
        const val MinWeight = .01f
        const val SweepLength = .28f
    }
}
