package lib.sketchbook

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp
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
                .glass(shape, BarGlassElevation, useGlow)
                .padding(BarPadding)
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
