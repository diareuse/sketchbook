package lib.sketchbook.layout

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection

fun Arrangement.Horizontal.arrange(
    density: Density,
    direction: LayoutDirection,
    fullSize: Int,
    vararg itemSizes: Int
): IntArray {
    return IntArray(itemSizes.size).apply {
        density.arrange(fullSize, itemSizes, direction, this)
    }
}