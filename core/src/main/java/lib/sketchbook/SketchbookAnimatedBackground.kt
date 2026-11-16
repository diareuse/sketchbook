package lib.sketchbook

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.unit.*
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.currentStateAsState
import lib.sketchbook.glass.glassSource
import lib.sketchbook.modifier.meshGradientBackground

private const val BaseDurationMillis = 10_000f

internal fun driftDurationMillis(speed: Float): Int =
    (BaseDurationMillis / speed).toInt().coerceAtLeast(1)

internal fun drifts(speed: Float, animated: Boolean, started: Boolean): Boolean =
    animated && started && speed > 0f && speed.isFinite()

@Composable
private fun isDark(): Boolean = MaterialTheme.colorScheme.background.luminance() < .5f

private fun defaultBase(dark: Boolean): Color = if (dark) Color(0xFF0A0A12) else Color(0xFFF7F6FC)

private fun defaultColors(dark: Boolean): List<Color> =
    if (dark) listOf(Color(0xFF4338CA), Color(0xFF7B2394), Color(0xFF0722C9), Color(0xFF9D2A8C))
    else listOf(Color(0xFF81D4FA), Color(0xFFF48FB1), Color(0xFF80DEEA), Color(0xFFB39DDB))

@Composable
fun SketchbookAnimatedBackground(
    modifier: Modifier = Modifier,
    colors: List<Color> = defaultColors(isDark()),
    speed: Float = 0.25f,
    animated: Boolean = true,
    baseColor: Color = defaultBase(isDark()),
    content: @Composable BoxScope.() -> Unit
) {
    val lifecycleState by LocalLifecycleOwner.current.lifecycle.currentStateAsState()
    val started = lifecycleState.isAtLeast(Lifecycle.State.STARTED)
    val running = drifts(speed, animated, started)
    Box(modifier = modifier) {
        Box(
            modifier = Modifier
                .matchParentSize()
                .glassSource()
                .meshGradientBackground(
                    colors = colors,
                    durationMillis = if (running) driftDurationMillis(speed) else driftDurationMillis(
                        1f
                    ),
                    animated = running,
                    baseColor = baseColor
                )
        )
        content()
    }
}

@SketchbookComponentPreview
@Composable
private fun SketchbookAnimatedBackgroundPreview() = SketchbookPreviewLayout {
    SketchbookAnimatedBackground {
        Text("Animated background", Modifier.padding(32.dp))
    }
}
