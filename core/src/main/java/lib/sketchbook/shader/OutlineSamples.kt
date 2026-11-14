package lib.sketchbook.shader

import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.PathMeasure
import lib.sketchbook.util.toPath

internal const val BeamSampleCount = 64

internal class OutlineSamples(val points: FloatArray, val perimeter: Float)

internal fun sampleOutline(outline: Outline, density: Float, count: Int = BeamSampleCount): OutlineSamples {
    val measure = PathMeasure()
    measure.setPath(outline.toPath(), true)
    val length = measure.length
    val points = FloatArray(count * 2)
    if (length <= 0f) return OutlineSamples(points, 0f)
    for (i in 0 until count) {
        val p = measure.getPosition(length * i / count)
        points[2 * i] = p.x / density
        points[2 * i + 1] = p.y / density
    }
    if (signedArea(points) < 0f) reverseAfterFirst(points)
    return OutlineSamples(points, length / density)
}

private fun signedArea(points: FloatArray): Float {
    val count = points.size / 2
    var area = 0f
    for (i in 0 until count) {
        val j = (i + 1) % count
        area += points[2 * i] * points[2 * j + 1] - points[2 * j] * points[2 * i + 1]
    }
    return area
}

private fun reverseAfterFirst(points: FloatArray) {
    val count = points.size / 2
    for (i in 1..(count - 1) / 2) {
        val j = count - i
        val x = points[2 * i]
        val y = points[2 * i + 1]
        points[2 * i] = points[2 * j]
        points[2 * i + 1] = points[2 * j + 1]
        points[2 * j] = x
        points[2 * j + 1] = y
    }
}
