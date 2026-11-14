package lib.sketchbook

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PixelMap
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.unit.dp
import lib.sketchbook.glass.glassSource
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

val StripeColors = listOf(Color.Red, Color.Blue)

val ColorfulStripes = listOf(
    Color(0xFFE53935),
    Color(0xFFFDD835),
    Color(0xFF43A047),
    Color(0xFF1E88E5),
    Color(0xFF8E24AA)
)

@Composable
fun StripedBackdrop(
    modifier: Modifier = Modifier,
    source: Boolean = true,
    colors: List<Color> = StripeColors,
    content: @Composable BoxScope.() -> Unit
) = Box(modifier) {
    Row(Modifier.matchParentSize().let { if (source) it.glassSource() else it }) {
        repeat(StripeCount) {
            Box(
                Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .background(colors[it % colors.size])
            )
        }
    }
    content()
}

@Composable
fun StripedColumn(
    modifier: Modifier = Modifier,
    source: Boolean = true,
    colors: List<Color> = ColorfulStripes,
    content: @Composable BoxScope.() -> Unit
) = Box(modifier) {
    androidx.compose.foundation.layout.Column(
        Modifier.matchParentSize().let { if (source) it.glassSource() else it }
    ) {
        repeat(StripeCount) {
            Box(
                Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .background(colors[it % colors.size])
            )
        }
    }
    content()
}

fun PixelMap.redSpread(y: Int, xs: IntProgression): Int {
    var lo = 255
    var hi = 0
    for (x in xs) {
        val r = (this[x, y].red * 255).toInt()
        lo = min(lo, r)
        hi = max(hi, r)
    }
    return hi - lo
}

fun PixelMap.verticalRedSpread(x: Int, ys: IntProgression): Int {
    var lo = 255
    var hi = 0
    for (y in ys) {
        val r = (this[x, y].red * 255).toInt()
        lo = min(lo, r)
        hi = max(hi, r)
    }
    return hi - lo
}

fun contrastRatio(a: Color, b: Color): Float {
    val l1 = a.luminance() + .05f
    val l2 = b.luminance() + .05f
    return max(l1, l2) / min(l1, l2)
}

fun PixelMap.opaque(x: Int, y: Int): Color = this[x, y].let { Color(it.red, it.green, it.blue, 1f) }

fun distance(a: Color, b: Color): Int =
    (abs(a.red - b.red) * 255 + abs(a.green - b.green) * 255 + abs(a.blue - b.blue) * 255).toInt()

private const val StripeCount = 18
