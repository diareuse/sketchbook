@file:OptIn(ExperimentalMaterial3WindowSizeClassApi::class)

package lib.sketchbook.layout

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.Measurable
import androidx.compose.ui.layout.MeasureResult
import androidx.compose.ui.layout.MeasureScope
import androidx.compose.ui.layout.layout
import androidx.compose.ui.node.CompositionLocalConsumerModifierNode
import androidx.compose.ui.node.LayoutAwareModifierNode
import androidx.compose.ui.node.LayoutModifierNode
import androidx.compose.ui.node.ModifierNodeElement
import androidx.compose.ui.node.currentValueOf
import androidx.compose.ui.node.requireDensity
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.coerceAtMost
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.isSpecified
import lib.sketchbook.util.findActivityOrNull

@Composable
fun rememberWindowSizeClass(): WindowSizeClass {
    val context = LocalContext.current
    val activity = context.findActivityOrNull()
        ?: return WindowSizeClass.calculateFromSize(DpSize.Zero)
    return calculateWindowSizeClass(activity = activity)
}

fun Modifier.largeScreenPadding(
    widthAtMost: Dp = Dp.Unspecified,
    onChange: (PaddingValues) -> Unit
): Modifier = this then LargeScreenPaddingElement(widthAtMost, onChange)

private data class LargeScreenPaddingElement(
    val widthAtMost: Dp,
    val onChange: (PaddingValues) -> Unit
) : ModifierNodeElement<LargeScreenPaddingNode>() {

    override fun create() = LargeScreenPaddingNode(widthAtMost, onChange)

    override fun update(node: LargeScreenPaddingNode) {
        node.update(widthAtMost, onChange)
    }
}

private class LargeScreenPaddingNode(
    private var widthAtMost: Dp,
    private var onChange: (PaddingValues) -> Unit
) : Modifier.Node(), LayoutAwareModifierNode, CompositionLocalConsumerModifierNode {

    private var size: IntSize? = null

    fun update(widthAtMost: Dp, onChange: (PaddingValues) -> Unit) {
        this.widthAtMost = widthAtMost
        this.onChange = onChange
        report()
    }

    override fun onRemeasured(size: IntSize) {
        this.size = size
        report()
    }

    private fun report() {
        val size = size ?: return
        val maxWidth = windowWidthLimit(widthAtMost)
        val density = requireDensity()
        val width = with(density) { size.width.toDp() }
        val wantedWidth = width.coerceAtMost(maxWidth)
        if (wantedWidth >= width)
            onChange(PaddingValues(0.dp))
        else
            onChange(PaddingValues(horizontal = (width - wantedWidth) / 2))
    }
}

fun Modifier.alignForLargeScreen(
    alignment: Alignment.Horizontal = Alignment.CenterHorizontally,
    widthAtMost: Dp = Dp.Unspecified
): Modifier = this then AlignForLargeScreenElement(alignment, widthAtMost)

private data class AlignForLargeScreenElement(
    val alignment: Alignment.Horizontal,
    val widthAtMost: Dp
) : ModifierNodeElement<AlignForLargeScreenNode>() {

    override fun create() = AlignForLargeScreenNode(alignment, widthAtMost)

    override fun update(node: AlignForLargeScreenNode) {
        node.alignment = alignment
        node.widthAtMost = widthAtMost
    }
}

private class AlignForLargeScreenNode(
    var alignment: Alignment.Horizontal,
    var widthAtMost: Dp
) : Modifier.Node(), LayoutModifierNode, CompositionLocalConsumerModifierNode {

    override fun MeasureScope.measure(
        measurable: Measurable,
        constraints: Constraints
    ): MeasureResult = alignedLayout(
        measurable = measurable,
        constraints = constraints,
        maxWidth = windowWidthLimit(widthAtMost),
        alignment = alignment
    )
}

private fun CompositionLocalConsumerModifierNode.windowWidthLimit(widthAtMost: Dp): Dp {
    val containerSize = currentValueOf(LocalWindowInfo).containerSize
    val density = requireDensity()
    val windowSize = with(density) { DpSize(containerSize.width.toDp(), containerSize.height.toDp()) }
    return WindowSizeClass.calculateFromSize(windowSize).widthSizeClass.widthDp.run {
        if (isSpecified && widthAtMost.isSpecified) coerceAtMost(widthAtMost) else this
    }
}

fun Modifier.alignForLargeScreen(
    sizeClass: WindowSizeClass,
    alignment: Alignment.Horizontal = Alignment.CenterHorizontally,
    widthAtMost: Dp = Dp.Unspecified
): Modifier = alignForLargeScreen(
    maxWidth = sizeClass.widthSizeClass.widthDp.run {
        if (isSpecified && widthAtMost.isSpecified) coerceAtMost(widthAtMost) else this
    },
    alignment = alignment
)

fun Modifier.alignForLargeScreen(
    maxWidth: Dp,
    alignment: Alignment.Horizontal = Alignment.CenterHorizontally
): Modifier = if (maxWidth.isSpecified) layout { measurable, constraints ->
    alignedLayout(measurable, constraints, maxWidth, alignment)
} else this

private fun MeasureScope.alignedLayout(
    measurable: Measurable,
    constraints: Constraints,
    maxWidth: Dp,
    alignment: Alignment.Horizontal
): MeasureResult {
    val coercedConstraints = if (maxWidth.isSpecified) {
        constraints.widthCoerceAtMost(maxWidth.roundToPx())
    } else constraints
    val placeable = measurable.measure(coercedConstraints)
    val width = if (maxWidth.isSpecified && constraints.hasBoundedWidth) {
        constraints.maxWidth
    } else placeable.width
    return layout(width, placeable.height) {
        val x = alignment.align(placeable.width, width, layoutDirection)
        placeable.placeRelative(x, 0)
    }
}

fun Constraints.widthCoerceAtMost(width: Int): Constraints {
    if (width >= maxWidth) return this
    return copy(minWidth = minWidth.coerceAtMost(width), maxWidth = maxWidth.coerceAtMost(width))
}

private val WindowWidthSizeClass.widthDp: Dp
    get() = when (this) {
        WindowWidthSizeClass.Compact -> Dp.Unspecified
        WindowWidthSizeClass.Medium -> 600.dp
        WindowWidthSizeClass.Expanded -> 600.dp
        else -> Dp.Unspecified
    }