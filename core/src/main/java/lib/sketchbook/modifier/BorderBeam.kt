package lib.sketchbook.modifier

import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import lib.sketchbook.shader.BeamLook
import lib.sketchbook.shader.BorderBeamElement

enum class BeamStyle { Large, Small, Line }

enum class BeamDirection { Clockwise, CounterClockwise }

object BeamPalette {

    val colorful: List<Color> = listOf(
        Color(0.25f, 0.85f, 0.45f),
        Color(0.20f, 0.60f, 0.95f),
        Color(0.70f, 0.35f, 0.95f),
        Color(1.00f, 0.35f, 0.65f),
        Color(1.00f, 0.65f, 0.25f)
    )

    fun glass(accent: Color): List<Color> {
        val alpha = accent.alpha
        return listOf(
            accent.copy(alpha = alpha * TailAlpha),
            accent.shiftedHue(-WideShift).copy(alpha = alpha * BodyAlpha),
            accent,
            accent.shiftedHue(WideShift).copy(alpha = alpha),
            lerp(accent.shiftedHue(2 * WideShift), Color.White, HeadWhite).copy(alpha = alpha)
        )
    }

    private fun Color.shiftedHue(degrees: Float): Color {
        val hsv = FloatArray(3)
        android.graphics.Color.colorToHSV(toArgb(), hsv)
        val hue = if (hsv[1] < NeutralSaturation) NeutralHue else hsv[0]
        return Color.hsv((hue + degrees + 360f) % 360f, maxOf(hsv[1], PastelSaturation), hsv[2], alpha)
    }

    private const val TailAlpha = .2f
    private const val BodyAlpha = .7f
    private const val WideShift = 35f
    private const val HeadWhite = .6f
    private const val NeutralSaturation = .15f
    private const val NeutralHue = 210f
    private const val PastelSaturation = .35f
}

fun Modifier.borderBeam(
    shape: Shape,
    colors: List<Color> = BeamPalette.colorful,
    style: BeamStyle = BeamStyle.Large,
    direction: BeamDirection = BeamDirection.Clockwise,
    active: Boolean = true,
    intensity: Float = 1f,
    strokeWidth: Dp = 2.dp,
    durationMillis: Int = 2400
): Modifier = this then BorderBeamElement(
    shape = shape,
    look = BeamLook(colors, style, direction, intensity, strokeWidth),
    active = active,
    animated = true,
    durationMillis = durationMillis
)
