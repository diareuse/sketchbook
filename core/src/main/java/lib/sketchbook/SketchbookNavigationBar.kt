package lib.sketchbook

import androidx.compose.foundation.interaction.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.*
import androidx.compose.material.icons.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.unit.*
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
) = NavigationItemCore(
    selected = selected,
    onClick = onClick,
    icon = icon,
    label = label,
    badge = badge,
    enabled = enabled,
    alwaysShowLabel = alwaysShowLabel,
    compositeColor = compositeColor,
    interactionSource = interactionSource,
    modifier = modifier.weight(1f)
)

@SketchbookComponentPreview
@SketchbookValidation
@Composable
private fun SketchbookNavigationBarPreview() = SketchbookPreviewLayout {
    SketchbookNavigationBar {
        SketchbookNavigationBarItem(true, {}, { Icon(Icons.Default.Home, null) }, label = { Text("Home") })
        SketchbookNavigationBarItem(false, {}, { Icon(Icons.Default.Search, null) }, label = { Text("Search") })
        SketchbookNavigationBarItem(
            false,
            {},
            { Icon(Icons.Default.Settings, null) },
            label = { Text("Settings") },
            badge = { Text("3") }
        )
    }
}
