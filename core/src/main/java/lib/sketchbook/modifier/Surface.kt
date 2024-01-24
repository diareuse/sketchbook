package lib.sketchbook.modifier

import androidx.compose.foundation.background
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

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