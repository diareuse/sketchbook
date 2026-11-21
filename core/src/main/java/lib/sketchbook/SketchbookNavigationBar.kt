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

private val BarGlassElevation = 16.dp
private val BarPadding = 4.dp

@Composable
fun SketchbookNavigationBar(
    modifier: Modifier = Modifier,
    windowInsets: WindowInsets = WindowInsets.systemBars.only(WindowInsetsSides.Horizontal + WindowInsetsSides.Bottom),
    shape: Shape = Theme.container.card,
    useGlow: Boolean = true,
    content: @Composable RowScope.() -> Unit
) {
    Box(
        modifier = modifier
            .windowInsetsPadding(windowInsets)
            .padding(start = 16.dp, top = 8.dp, end = 16.dp, bottom = 12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .glass(shape.asGlassShape(), BarGlassElevation, useGlow)
                .padding(horizontal = BarPadding)
                .selectableGroup(),
            verticalAlignment = Alignment.CenterVertically,
            content = content
        )
    }
}

private const val IndicatorAlpha = 1f
private const val IndicatorMinScale = .0f

@Composable
fun RowScope.SketchbookNavigationBarItem(
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
) = Column(
    modifier = modifier.weight(1f),
    horizontalAlignment = Alignment.CenterHorizontally
) {
    val progress by animateFloatAsState(
        targetValue = if (selected) 1f else 0f,
        animationSpec = spring(),
        label = "indicator"
    )
    val indicatorColor = LocalContentColor.current.copy(IndicatorAlpha)
    Box(
        Modifier
            .size(24.dp, 4.dp)
            .graphicsLayer {
                alpha = progress
                scaleX = lerp(IndicatorMinScale, 1f, progress)
            }
            .background(
                color = indicatorColor,
                shape = RoundedCornerShape(bottomStart = 4.dp, bottomEnd = 4.dp)
            )
    )
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
        modifier = modifier
    )
}

@SketchbookComponentPreview
@SketchbookValidation
@Composable
private fun SketchbookNavigationBarPreview() = SketchbookPreviewLayout {
    SketchbookNavigationBar {
        SketchbookNavigationBarItem(
            true,
            {},
            { Icon(Icons.Default.Home, null) },
            label = { Text("Home") })
        SketchbookNavigationBarItem(
            false,
            {},
            { Icon(Icons.Default.Search, null) },
            label = { Text("Search") })
        SketchbookNavigationBarItem(
            false,
            {},
            { Icon(Icons.Default.Settings, null) },
            label = { Text("Settings") },
            badge = { Text("3") }
        )
    }
}
