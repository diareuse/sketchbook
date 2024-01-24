@file:OptIn(ExperimentalMaterial3WindowSizeClassApi::class)

package lib.sketchbook.layout

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.layout.layout
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
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
) = composed {
    val sizeClass = rememberWindowSizeClass()
    val maxWidth = sizeClass.widthSizeClass.widthDp.coerceAtMost(widthAtMost)
    val density = LocalDensity.current
    remember(maxWidth, onChange) {
        onSizeChanged {
            val width = with(density) { it.width.toDp() }
            val wantedWidth = width.coerceAtMost(maxWidth)
            if (wantedWidth >= width)
                onChange(PaddingValues(0.dp))
            else
                onChange(PaddingValues(horizontal = (width - wantedWidth) / 2))
        }
    }
}

fun Modifier.alignForLargeScreen(
    alignment: Alignment.Horizontal = Alignment.CenterHorizontally,
    widthAtMost: Dp = Dp.Unspecified
) = composed {
    alignForLargeScreen(
        sizeClass = rememberWindowSizeClass(),
        alignment = alignment,
        widthAtMost = widthAtMost
    )
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
    val coercedConstraints = constraints.widthCoerceAtMost(maxWidth.roundToPx())
    val placeable = measurable.measure(coercedConstraints)
    layout(constraints.maxWidth, placeable.height) {
        val x = alignment.align(placeable.width, constraints.maxWidth, layoutDirection)
        placeable.placeRelative(x, 0)
    }
} else this

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