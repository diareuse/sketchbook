package lib.sketchbook

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.translate
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.PixelMap
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.IntRect
import androidx.compose.ui.unit.LayoutDirection

private const val BackdropTolerance = 12
private const val EdgeMargin = 3f
private const val BandWidth = 10f

class PaneProbe(
    private val image: PixelMap,
    private val backdrop: List<Color>,
    private val region: IntRect
) {

    fun isBackdrop(x: Int, y: Int): Boolean =
        backdrop.any { distance(image.opaque(x, y), it) <= BackdropTolerance }

    val bounds: IntRect by lazy {
        var left = Int.MAX_VALUE
        var top = Int.MAX_VALUE
        var right = Int.MIN_VALUE
        var bottom = Int.MIN_VALUE
        for (y in region.top until region.bottom) for (x in region.left until region.right) {
            if (!isBackdrop(x, y)) {
                left = minOf(left, x)
                top = minOf(top, y)
                right = maxOf(right, x + 1)
                bottom = maxOf(bottom, y + 1)
            }
        }
        IntRect(left, top, right, bottom)
    }

    fun roundRect(shape: Shape, density: Density): RoundRect {
        val size = Size(bounds.width.toFloat(), bounds.height.toFloat())
        val outline = shape.createOutline(size, LayoutDirection.Ltr, density)
        val rounded = (outline as? Outline.Rounded)?.roundRect
            ?: RoundRect(0f, 0f, size.width, size.height, 0f, 0f)
        return rounded.translate(Offset(bounds.left.toFloat(), bounds.top.toFloat()))
    }

    fun outside(pixels: List<Pair<Int, Int>>, shape: Shape, density: Density, slack: Float): List<Pair<Int, Int>> {
        val limit = roundRect(shape, density).grow(slack)
        return pixels.filter { (x, y) -> !limit.contains(Offset(x + .5f, y + .5f)) }
    }

    fun mismatches(shape: Shape, density: Density): List<String> {
        val rr = roundRect(shape, density)
        val inner = rr.grow(-EdgeMargin)
        val core = rr.grow(-BandWidth)
        val outer = rr.grow(EdgeMargin)
        val halo = rr.grow(BandWidth)
        val failures = mutableListOf<String>()
        for (y in bounds.top - BandWidth.toInt() until bounds.bottom + BandWidth.toInt()) {
            for (x in bounds.left - BandWidth.toInt() until bounds.right + BandWidth.toInt()) {
                if (x !in region.left until region.right || y !in region.top until region.bottom) continue
                val p = Offset(x + .5f, y + .5f)
                val inBand = inner.contains(p) && !core.contains(p)
                val outBand = halo.contains(p) && !outer.contains(p)
                if (inBand && isBackdrop(x, y)) failures += "($x,$y) inside the shape shows the backdrop"
                if (outBand && !isBackdrop(x, y)) failures += "($x,$y) outside the shape is covered"
            }
        }
        return failures
    }
}

fun PixelMap.changedPixels(other: PixelMap, threshold: Int = 6): List<Pair<Int, Int>> {
    val changed = mutableListOf<Pair<Int, Int>>()
    for (y in 0 until height) for (x in 0 until width) {
        if (distance(opaque(x, y), other.opaque(x, y)) > threshold) changed += x to y
    }
    return changed
}

private fun RoundRect.grow(delta: Float): RoundRect {
    fun CornerRadius.grown() = CornerRadius(maxOf(0f, x + delta), maxOf(0f, y + delta))
    return RoundRect(
        left - delta, top - delta, right + delta, bottom + delta,
        topLeftCornerRadius.grown(), topRightCornerRadius.grown(),
        bottomRightCornerRadius.grown(), bottomLeftCornerRadius.grown()
    )
}
