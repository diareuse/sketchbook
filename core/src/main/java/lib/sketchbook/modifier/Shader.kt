package lib.sketchbook.modifier

import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import lib.sketchbook.shader.MeshGradientElement
import lib.sketchbook.shader.ShimmerElement
import lib.sketchbook.shader.SpecularBorderElement
import lib.sketchbook.shader.toMeshColors

fun Modifier.shimmer(
    color: Color = Color.White.copy(alpha = .5f),
    durationMillis: Int = 1600,
    enabled: Boolean = true
): Modifier = this then ShimmerElement(color, durationMillis, enabled)

fun Modifier.specularBorder(
    shape: Shape,
    width: Dp = 1.5.dp,
    color: Color = Color.White,
    durationMillis: Int = 4000,
    animated: Boolean = true
): Modifier = this then SpecularBorderElement(shape, width, color, durationMillis, animated)

fun Modifier.meshGradientBackground(
    colors: List<Color>,
    durationMillis: Int = 10_000,
    animated: Boolean = true
): Modifier = this then MeshGradientElement(colors.toMeshColors(), durationMillis, animated)
