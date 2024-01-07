package lib.sketchbook.modifier

import androidx.compose.foundation.*
import androidx.compose.material3.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.unit.*

fun Modifier.surface(
    color: Color,
    shape: Shape = RectangleShape,
    elevation: Dp = 0.dp,
    shadowColor: Color = color
) = shadow(elevation, shape, false, shadowColor, shadowColor)
    .clip(shape)
    .background(color, shape)

fun Modifier.surface(
    brush: Brush,
    shape: Shape = RectangleShape,
    elevation: Dp = 0.dp,
    shadowColor: Color = Color.Black
) = shadow(elevation, shape, false, shadowColor, shadowColor)
    .clip(shape)
    .background(brush, shape)

fun Modifier.surface(
    tonalElevation: Dp,
    shape: Shape = RectangleShape,
    elevation: Dp = 0.dp,
    shadowColor: Color = Color.Black
) = composed {
    val elevatedColor = MaterialTheme.colorScheme.surfaceColorAtElevation(tonalElevation)
    surface(elevatedColor, shape, elevation, shadowColor)
}