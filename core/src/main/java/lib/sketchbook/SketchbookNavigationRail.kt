package lib.sketchbook

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
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

private val RailGlassElevation = 16.dp
private val RailPadding = 8.dp

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
    ) {
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .glass(shape, RailGlassElevation, useGlow)
                .padding(RailPadding)
                .selectableGroup(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp, Alignment.CenterVertically)
        ) {
            header?.invoke(this)
            content()
        }
    }
}

@Composable
fun SketchbookNavigationRailItem(
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
    modifier = modifier
)

@SketchbookComponentPreview
@SketchbookValidation
@Composable
private fun SketchbookNavigationRailPreview() = SketchbookPreviewLayout {
    SketchbookNavigationRail(header = { Text("Menu") }) {
        SketchbookNavigationRailItem(true, {}, { Icon(Icons.Default.Home, null) }, label = { Text("Home") })
        SketchbookNavigationRailItem(false, {}, { Icon(Icons.Default.Search, null) }, label = { Text("Search") })
    }
}
