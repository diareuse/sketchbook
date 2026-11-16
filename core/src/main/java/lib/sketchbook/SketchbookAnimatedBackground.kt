package lib.sketchbook

import android.provider.Settings
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.*
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.currentStateAsState
import lib.sketchbook.glass.glassSource
import lib.sketchbook.mesh.DriftingNodeField
import lib.sketchbook.mesh.Touch
import lib.sketchbook.mesh.nodeCount
import lib.sketchbook.shader.animationsAllowed
import kotlin.math.hypot
import kotlin.math.sqrt

private const val BaseSpeedDp = 120f
private const val CellDp = 90f
private const val MaxFrameMillis = 50f
private const val FieldSeed = 7

internal fun drifts(speed: Float, animated: Boolean, started: Boolean): Boolean =
    animated && started && speed > 0f && speed.isFinite()

@Composable
private fun isDark(): Boolean = MaterialTheme.colorScheme.background.luminance() < .5f

private fun defaultColors(dark: Boolean): List<Color> =
    if (dark) listOf(Color(0xFF4338CA), Color(0xFF7B2394), Color(0xFF0722C9), Color(0xFF9D2A8C))
    else listOf(Color(0xFF81D4FA), Color(0xFFF48FB1), Color(0xFF80DEEA), Color(0xFFB39DDB))

private class TouchPoint {
    var x = 0f
    var y = 0f
    var active = false
}

@Composable
fun SketchbookAnimatedBackground(
    modifier: Modifier = Modifier,
    colors: List<Color> = defaultColors(isDark()),
    speed: Float = 0.25f,
    animated: Boolean = true,
    nodeShape: Shape = CircleShape,
    nodeSize: Dp = 6.dp,
    density: Float = 1f,
    linkDistance: Dp = 120.dp,
    touchRadius: Dp = 120.dp,
    vignette: Float = 0.65f,
    content: @Composable BoxScope.() -> Unit
) {
    val lifecycleState by LocalLifecycleOwner.current.lifecycle.currentStateAsState()
    val started = lifecycleState.isAtLeast(Lifecycle.State.STARTED)
    val resolver = LocalContext.current.contentResolver
    val systemAllows = remember(resolver) {
        animationsAllowed(Settings.Global.getFloat(resolver, Settings.Global.ANIMATOR_DURATION_SCALE, 1f))
    }
    val running = drifts(speed, animated && systemAllows, started)
    val touch = remember { TouchPoint() }
    Box(modifier = modifier.trackTouch(touch)) {
        NodeMeshLayer(
            colors = colors,
            speed = if (speed.isFinite()) speed.coerceAtLeast(0f) else 0f,
            running = running,
            nodeShape = nodeShape,
            nodeSize = nodeSize,
            density = density,
            linkDistance = linkDistance,
            touchRadius = touchRadius,
            vignette = vignette,
            touch = touch,
            modifier = Modifier.matchParentSize()
        )
        content()
    }
}

private fun Modifier.trackTouch(touch: TouchPoint): Modifier = pointerInput(touch) {
    awaitPointerEventScope {
        while (true) {
            val event = awaitPointerEvent(PointerEventPass.Initial)
            val pressed = event.changes.firstOrNull { it.pressed }
            touch.active = pressed != null
            if (pressed != null) {
                touch.x = pressed.position.x
                touch.y = pressed.position.y
            }
        }
    }
}

@Composable
private fun NodeMeshLayer(
    colors: List<Color>,
    speed: Float,
    running: Boolean,
    nodeShape: Shape,
    nodeSize: Dp,
    density: Float,
    linkDistance: Dp,
    touchRadius: Dp,
    vignette: Float,
    touch: TouchPoint,
    modifier: Modifier
) {
    val metrics = LocalDensity.current
    var size by remember { mutableStateOf(IntSize.Zero) }
    val count = nodeCount(size.width.toFloat(), size.height.toFloat(), with(metrics) { CellDp.dp.toPx() }, density)
    val field = remember(count) { DriftingNodeField(FieldSeed, count) }
    val tick = remember { mutableLongStateOf(0L) }
    val radius = with(metrics) { touchRadius.toPx() }.takeIf { it.isFinite() && it > 0f }
    val driftPx = speed * with(metrics) { BaseSpeedDp.dp.toPx() }
    val currentSize by rememberUpdatedState(size)
    val currentRadius by rememberUpdatedState(radius)
    val currentDrift by rememberUpdatedState(driftPx)
    LaunchedEffect(running, field) {
        if (!running) return@LaunchedEffect
        var last = 0L
        while (true) {
            withFrameNanos { now ->
                val dt = if (last == 0L) 0f else ((now - last) / 1_000_000f).coerceAtMost(MaxFrameMillis)
                last = now
                val point = currentRadius?.takeIf { touch.active }?.let { Touch(touch.x, touch.y, it) }
                field.step(dt, currentSize.width.toFloat(), currentSize.height.toFloat(), point, currentDrift)
                tick.longValue++
            }
        }
    }
    LaunchedEffect(field, size) {
        field.step(0f, size.width.toFloat(), size.height.toFloat(), null, 0f)
        tick.longValue++
    }
    Canvas(
        modifier
            .glassSource()
            .onSizeChanged { size = it }
            .graphicsLayer { compositingStrategy = CompositingStrategy.Offscreen }
            .drawWithCache {
                val nodePx = nodeSize.toPx()
                val outline = nodeShape.createOutline(Size(nodePx, nodePx), layoutDirection, this)
                val linkPx = linkDistance.toPx()
                val strokePx = 1.dp.toPx()
                val mask = vignette.takeIf { it > 0f }?.let {
                    Brush.radialGradient(
                        colorStops = arrayOf(
                            0f to Color.Black,
                            (1f - it).coerceIn(0f, .99f) to Color.Black,
                            1f to Color.Transparent
                        ),
                        center = Offset(this.size.width / 2f, this.size.height / 2f),
                        radius = hypot(this.size.width, this.size.height) / 2f
                    )
                }
                onDrawBehind {
                    tick.longValue
                    if (colors.isEmpty()) return@onDrawBehind
                    val nodes = field.nodes
                    if (linkPx > 0f) for (i in nodes.indices) for (j in i + 1 until nodes.size) {
                        val dx = nodes[i].x - nodes[j].x
                        val dy = nodes[i].y - nodes[j].y
                        val squared = dx * dx + dy * dy
                        if (squared < linkPx * linkPx) {
                            val d = sqrt(squared)
                            drawLine(
                                color = colors[i % colors.size].copy(alpha = (1f - d / linkPx) * .6f),
                                start = Offset(nodes[i].x, nodes[i].y),
                                end = Offset(nodes[j].x, nodes[j].y),
                                strokeWidth = strokePx
                            )
                        }
                    }
                    nodes.forEachIndexed { i, node ->
                        translate(node.x - nodePx / 2f, node.y - nodePx / 2f) {
                            drawOutline(outline, colors[i % colors.size])
                        }
                    }
                    if (mask != null) drawRect(mask, blendMode = BlendMode.DstIn)
                }
            }
    ) {}
}

@SketchbookComponentPreview
@Composable
private fun SketchbookAnimatedBackgroundPreview() = SketchbookPreviewLayout {
    SketchbookAnimatedBackground {
        Text("Animated background", Modifier.padding(32.dp))
    }
}
