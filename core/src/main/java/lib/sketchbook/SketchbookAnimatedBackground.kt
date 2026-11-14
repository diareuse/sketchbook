package lib.sketchbook

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.currentStateAsState
import lib.sketchbook.modifier.meshGradientBackground
import lib.sketchbook.theme.Theme

private const val BaseDurationMillis = 10_000f

internal fun driftDurationMillis(speed: Float): Int =
    (BaseDurationMillis / speed).toInt().coerceAtLeast(1)

internal fun drifts(speed: Float, animated: Boolean, started: Boolean): Boolean =
    animated && started && speed > 0f && speed.isFinite()

@Composable
fun SketchbookAnimatedBackground(
    modifier: Modifier = Modifier,
    colors: List<Color> = listOf(
        Theme.color.emphasis.primary,
        Theme.color.emphasis.tertiary,
        Theme.color.emphasis.secondary
    ),
    speed: Float = 1f,
    animated: Boolean = true,
    baseColor: Color = Theme.color.container.background,
    content: @Composable BoxScope.() -> Unit
) {
    val lifecycleState by LocalLifecycleOwner.current.lifecycle.currentStateAsState()
    val started = lifecycleState.isAtLeast(Lifecycle.State.STARTED)
    val running = drifts(speed, animated, started)
    Box(
        modifier = modifier.meshGradientBackground(
            colors = colors,
            durationMillis = if (running) driftDurationMillis(speed) else driftDurationMillis(1f),
            animated = running,
            baseColor = baseColor
        ),
        content = content
    )
}

@SketchbookComponentPreview
@Composable
private fun SketchbookAnimatedBackgroundPreview() = SketchbookPreviewLayout {
    SketchbookAnimatedBackground {
        Text("Animated background", Modifier.padding(32.dp))
    }
}
