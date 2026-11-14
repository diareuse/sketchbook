package lib.sketchbook.shader

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.colorspace.ColorSpaces
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.unit.Dp
import lib.sketchbook.modifier.BeamDirection
import lib.sketchbook.modifier.BeamStyle
import lib.sketchbook.util.Api
import lib.sketchbook.util.sdkAtLeast
import kotlin.math.min

internal const val MaxBeamColors = 8

internal data class BeamLook(
    val colors: List<Color>,
    val style: BeamStyle,
    val direction: BeamDirection,
    val intensity: Float,
    val strokeWidth: Dp
) {
    val arc: Float get() = if (style == BeamStyle.Small) SmallArc else LargeArc

    val glowReach: Float
        get() = when (style) {
            BeamStyle.Large -> LargeGlow
            BeamStyle.Small -> SmallGlow
            BeamStyle.Line -> LineGlow
        }

    val directionSign: Float get() = if (direction == BeamDirection.Clockwise) 1f else -1f

    val palette: List<Color> get() = fitPalette(colors)

    private companion object {
        const val LargeArc = .36f
        const val SmallArc = .5f
        const val LargeGlow = 20f
        const val SmallGlow = 4f
        const val LineGlow = 10f
    }
}

internal class BeamGeometry(val path: Path, val samples: OutlineSamples) {
    val polyline: Path by lazy(LazyThreadSafetyMode.NONE) {
        Path().apply {
            val points = samples.points
            val count = points.size / 2
            moveTo(points[0], points[1])
            for (i in 1 until count) lineTo(points[2 * i], points[2 * i + 1])
            close()
        }
    }
}

internal fun interface BeamPainter {
    fun DrawScope.paint(geometry: BeamGeometry, look: BeamLook, phase: Float, fade: Float)
}

internal fun beamPainter(): BeamPainter =
    if (sdkAtLeast(Api.Agsl)) AgslBeamPainter() else FallbackBeamPainter()

internal fun fitPalette(colors: List<Color>, max: Int = MaxBeamColors): List<Color> {
    val source = colors.ifEmpty { listOf(Color.White) }
    if (source.size <= max) return source
    return List(max) { source[it * (source.size - 1) / (max - 1)] }
}

internal fun packPalette(palette: List<Color>): FloatArray {
    val out = FloatArray(MaxBeamColors * 4)
    palette.forEachIndexed { i, color ->
        val c = color.convert(ColorSpaces.Srgb)
        out[i * 4] = c.red
        out[i * 4 + 1] = c.green
        out[i * 4 + 2] = c.blue
        out[i * 4 + 3] = c.alpha
    }
    return out
}

internal fun samplePalette(palette: List<Color>, t: Float): Color {
    if (palette.size == 1) return palette[0]
    val x = t.coerceIn(0f, 1f) * (palette.size - 1)
    val i = min(x.toInt(), palette.size - 2)
    return lerp(palette[i], palette[i + 1], x - i)
}
