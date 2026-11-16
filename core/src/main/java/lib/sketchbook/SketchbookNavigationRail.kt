package lib.sketchbook

import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.*
import androidx.compose.foundation.shape.*
import androidx.compose.material.icons.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.unit.*
import androidx.compose.ui.util.*
import lib.sketchbook.glass.asGlassShape
import lib.sketchbook.glass.glass
import lib.sketchbook.theme.Theme

private val RailGlassElevation = 16.dp
private val RailPadding = 4.dp

@Composable
fun SketchbookNavigationRail(
    modifier: Modifier = Modifier,
    windowInsets: WindowInsets = WindowInsets.systemBars.only(WindowInsetsSides.Start + WindowInsetsSides.Vertical),
    shape: Shape = Theme.container.card,
    useGlow: Boolean = true,
    header: (@Composable ColumnScope.() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxHeight()
            .windowInsetsPadding(windowInsets)
            .padding(start = 12.dp, top = 12.dp, end = 8.dp, bottom = 12.dp)
            .width(IntrinsicSize.Min)
    ) {
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .glass(shape.asGlassShape(), RailGlassElevation, useGlow)
                .padding(start = RailPadding)
                .selectableGroup(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            header?.invoke(this)
            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(4.dp, Alignment.CenterVertically),
                content = content
            )
        }
    }
}

private const val IndicatorAlpha = 1f
private const val IndicatorMinScale = .0f

@Composable
fun ColumnScope.SketchbookNavigationRailItem(
    selected: Boolean,
    onClick: () -> Unit,
    icon: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    label: (@Composable () -> Unit)? = null,
    alwaysShowLabel: Boolean = true,
    badge: (@Composable () -> Unit)? = null,
    compositeColor: Color = Theme.color.emphasis.background,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() }
) = Row(
    modifier = modifier,
    verticalAlignment = Alignment.CenterVertically
) {
    val progress by animateFloatAsState(
        targetValue = if (selected) 1f else 0f,
        animationSpec = spring(),
        label = "indicator"
    )
    val indicatorColor = LocalContentColor.current.copy(IndicatorAlpha)
    NavigationItemCore(
        selected = selected,
        onClick = onClick,
        icon = icon,
        label = label,
        badge = badge,
        enabled = enabled,
        alwaysShowLabel = alwaysShowLabel,
        compositeColor = compositeColor,
        interactionSource = interactionSource,
    )
    Box(
        Modifier
            .size(4.dp, 24.dp)
            .graphicsLayer {
                alpha = progress
                scaleY = lerp(IndicatorMinScale, 1f, progress)
            }
            .background(
                color = indicatorColor,
                shape = RoundedCornerShape(topStart = 4.dp, bottomStart = 4.dp)
            )
    )
}

@SketchbookComponentPreview
@SketchbookValidation
@Composable
private fun SketchbookNavigationRailPreview() = SketchbookPreviewLayout {
    SketchbookNavigationRail(header = { Text("Menu") }) {
        SketchbookNavigationRailItem(
            true,
            {},
            { Icon(Icons.Default.Home, null) },
            label = { Text("Home") })
        SketchbookNavigationRailItem(
            false,
            {},
            { Icon(Icons.Default.Search, null) },
            label = { Text("Search") })
    }
}
