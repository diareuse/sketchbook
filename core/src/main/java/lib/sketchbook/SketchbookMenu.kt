package lib.sketchbook

import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.animate
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedback
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.findRootCoordinates
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInWindow
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.window.DialogWindowProvider
import androidx.core.view.WindowCompat
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import lib.sketchbook.haptic.tick
import lib.sketchbook.util.toDpOffset
import lib.sketchbook.util.toDpSize

fun Modifier.popOutBackground(state: OverlayState) = composed {
    val blur by animateDpAsState(targetValue = if (state.expanded) 32.dp else 0.dp)
    val alpha by animateFloatAsState(targetValue = if (state.expanded) .5f else 1f)
    alpha(alpha).blur(blur)
}

@Stable
class OverlayState {
    var expanded: Boolean by mutableStateOf(false)
}

@Composable
fun OverlayState.rememberMenuState(): MenuState {
    val haptics = LocalHapticFeedback.current
    return remember(this, haptics) {
        MenuState(this, haptics)
    }
}

@Stable
class MenuState(
    private val scope: OverlayState,
    private val haptics: HapticFeedback
) {
    var expanded by mutableStateOf(false)
        private set
    var expansionAlpha by mutableFloatStateOf(0f)
        private set
    var scale by mutableFloatStateOf(0f)
        private set
    var shadowAlpha by mutableFloatStateOf(1f)
        private set

    var offset by mutableStateOf(DpOffset.Zero)
        private set
    var size by mutableStateOf(DpSize.Zero)
        private set
    var horizontalAlignment by mutableStateOf(Alignment.CenterHorizontally)
        private set
    var verticalAlignment by mutableStateOf(Alignment.Top)
        private set

    internal fun updatePosition(
        it: LayoutCoordinates,
        direction: LayoutDirection,
        density: Density
    ) {
        val rootSize = it.findRootCoordinates().size.toDpSize(density)
        offset = it.positionInWindow().toDpOffset(density)
        size = it.size.toDpSize(density)
        verticalAlignment = when (offset.y) {
            in 0.dp..(rootSize.height / 2) -> Alignment.Top
            else -> Alignment.Bottom
        }
        horizontalAlignment = when (offset.x + 2.dp) {
            in 0.dp..((rootSize.width / 2) - (size.width / 2)) -> when (direction) {
                LayoutDirection.Ltr -> Alignment.Start
                LayoutDirection.Rtl -> Alignment.End
            }

            in ((rootSize.width / 2) + (size.width / 2))..rootSize.width -> when (direction) {
                LayoutDirection.Ltr -> Alignment.End
                LayoutDirection.Rtl -> Alignment.Start
            }

            else -> Alignment.CenterHorizontally
        }
    }

    suspend fun open() {
        expanded = true
        shadowAlpha = 0f
        scope.expanded = true
        animate(scale, 1.1f) { it, _ -> scale = it }
        haptics.tick()
        animate(1.1f, 1f) { it, _ -> scale = it }
        haptics.tick()
        animate(expansionAlpha, 1f) { it, _ -> expansionAlpha = it }
    }

    suspend fun close() {
        animate(expansionAlpha, 0f) { it, _ -> expansionAlpha = it }
        scope.expanded = false
        animate(scale, 1.1f) { it, _ -> scale = it }
        haptics.tick()
        animate(1.1f, 1f) { it, _ -> scale = it }
        haptics.tick()
        shadowAlpha = 1f
        delay(100)
        expanded = false
    }

}

@Composable
fun SketchbookMenu(
    state: MenuState,
    modifier: Modifier = Modifier,
    expansion: @Composable () -> Unit,
    content: @Composable () -> Unit,
) {
    Box(
        modifier = modifier,
        propagateMinConstraints = true
    ) {
        val direction = LocalLayoutDirection.current
        val density = LocalDensity.current
        Box(
            modifier = Modifier
                .onGloballyPositioned { state.updatePosition(it, direction, density) }
                .alpha(state.shadowAlpha),
            propagateMinConstraints = true
        ) {
            content()
        }
    }
    val statusBars = WindowInsets.statusBars.asPaddingValues()
    val scope = rememberCoroutineScope()
    if (state.expanded) Dialog(
        onDismissRequest = {
            scope.launch { state.close() }
        },
        properties = DialogProperties(
            dismissOnBackPress = false,
            decorFitsSystemWindows = false,
            usePlatformDefaultWidth = false
        )
    ) {
        val view = LocalView.current
        SideEffect {
            (view.parent as? DialogWindowProvider)?.window?.let { window ->
                WindowCompat.setDecorFitsSystemWindows(window, false)
                window.setWindowAnimations(-1)
                window.setDimAmount(0f)
            }
        }
        Box(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(Unit) {
                    detectTapGestures { scope.launch { state.close() } }
                }
        ) {
            BackHandler {
                scope.launch {
                    state.close()
                }
            }
            val expansion = @Composable {
                Box(
                    modifier = Modifier
                        .alpha(state.expansionAlpha)
                        .scale(state.scale)
                ) {
                    expansion()
                }
            }
            val top = if (state.verticalAlignment === Alignment.Bottom) expansion else null
            val bottom = if (state.verticalAlignment === Alignment.Top) expansion else null
            AnchoredLayout(
                modifier = Modifier
                    .offset(state.offset.x, state.offset.y - statusBars.calculateTopPadding()),
                horizontalAlignment = state.horizontalAlignment,
                top = top,
                bottom = bottom
            ) {
                Box(
                    modifier = Modifier
                        .size(state.size)
                        .scale(state.scale),
                    propagateMinConstraints = true
                ) {
                    content()
                }
            }
        }
    }
}

@Composable
private fun AnchoredLayout(
    modifier: Modifier = Modifier,
    horizontalAlignment: Alignment.Horizontal = Alignment.CenterHorizontally,
    top: (@Composable () -> Unit)? = null,
    bottom: (@Composable () -> Unit)? = null,
    anchor: @Composable () -> Unit,
) = Layout(
    modifier = modifier,
    content = {
        Box(modifier = Modifier.layoutId("anchor")) { anchor() }
        if (top != null) Box(modifier = Modifier.layoutId("top")) { top() }
        if (bottom != null) Box(modifier = Modifier.layoutId("bottom")) { bottom() }
    }
) { measurables, constraints ->
    val anchor = measurables.first { it.layoutId == "anchor" }.measure(constraints)
    val top = measurables.find { it.layoutId == "top" }?.measure(constraints)
    val bottom = measurables.find { it.layoutId == "bottom" }?.measure(constraints)
    val width = maxOf(anchor.width, top?.width ?: 0, bottom?.width ?: 0)
    val height = anchor.height + (top?.height ?: 0) + (bottom?.height ?: 0)
    layout(width, height) {
        anchor.placeRelative(IntOffset.Zero)
        top?.placeRelative(
            x = horizontalAlignment.align(top.width, anchor.width, layoutDirection),
            y = -top.height
        )
        bottom?.placeRelative(
            x = horizontalAlignment.align(bottom.width, anchor.width, layoutDirection),
            y = anchor.height
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun DialogClonePreview() {
    val overlay = remember { OverlayState() }
    SketchbookPreviewLayout(modifier = Modifier.popOutBackground(overlay)) {
        val state = overlay.rememberMenuState()
        SketchbookMenu(
            state = state,
            modifier = Modifier
                .padding(100.dp, 200.dp)
                .wrapContentSize(),
            expansion = {
                Button(onClick = { /*TODO*/ }) {
                    Text("Foooo")
                }
            }
        ) {
            val scope = rememberCoroutineScope()
            Box(
                modifier = Modifier
                    .clickable { scope.launch { state.open() } }
                    .size(100.dp, 150.dp)
                    .background(Color.Green)
            )
        }
    }
}