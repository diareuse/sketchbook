package lib.sketchbook.util

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.IntSize
import kotlin.math.roundToInt

fun IntSize.toDpSize(density: Density): DpSize {
    return with(density) { DpSize(width.toDp(), height.toDp()) }
}

fun DpSize.toSize(density: Density) = with(density) {
    Size(width = width.toPx(), height = height.toPx())
}

fun Size.toIntSize() =
    IntSize(width = width.roundToInt(), height = height.roundToInt())

fun Offset.toDpOffset(density: Density) = with(density) { DpOffset(x.toDp(), y.toDp()) }