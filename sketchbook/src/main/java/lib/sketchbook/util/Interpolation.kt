package lib.sketchbook.util

import androidx.compose.ui.text.*
import androidx.compose.ui.text.font.*
import androidx.compose.ui.unit.*
import androidx.compose.ui.util.*

fun lerp(start: Float, stop: Float, fraction: Float) =
    (1 - fraction) * start + fraction * stop

fun fastLerp(start: TextStyle, end: TextStyle, fraction: Float) = start.copy(
    fontSize = lerp(start.fontSize, end.fontSize, fraction),
    fontWeight = when {
        start.fontWeight == null -> end.fontWeight
        end.fontWeight == null -> start.fontWeight
        else -> FontWeight(lerp(start.fontWeight!!.weight, end.fontWeight!!.weight, fraction))
    }
)

fun lerp(start: Constraints, end: Constraints, fraction: Float) = start.copy(
    minWidth = lerp(start.minWidth, end.minWidth, fraction),
    minHeight = lerp(start.minHeight, end.minHeight, fraction),
    maxWidth = lerp(start.maxWidth, end.maxWidth, fraction),
    maxHeight = lerp(start.maxHeight, end.maxHeight, fraction)
)