package lib.sketchbook.theme

import androidx.compose.foundation.shape.CornerBasedShape
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material3.Shapes
import androidx.compose.runtime.Composable
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import lib.sketchbook.shape.SquircleShape

internal val ThemeShapes = Shapes(
    extraSmall = SquircleShape(6.dp),
    small = SquircleShape(12.dp),
    medium = SquircleShape(24.dp),
    large = SquircleShape(48.dp),
    extraLarge = SquircleShape(0.dp),
)

@Composable
fun CornerBasedShape.extendBy(padding: Dp): CornerBasedShape {
    return extendBy(padding, LocalDensity.current)
}

fun CornerBasedShape.extendBy(padding: Dp, density: Density) = copy(
    topStart = CornerSize(topStart.toDp(density) + padding),
    topEnd = CornerSize(topEnd.toDp(density) + padding),
    bottomEnd = CornerSize(bottomEnd.toDp(density) + padding),
    bottomStart = CornerSize(bottomStart.toDp(density) + padding)
)

private fun CornerSize.toDp(density: Density): Dp {
    return with(density) { toPx(Size.Unspecified, this).toDp() }
}