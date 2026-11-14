package lib.sketchbook

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.PrimaryScrollableTabRow
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.TabIndicatorScope
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import lib.sketchbook.glass.glass
import lib.sketchbook.theme.Theme
import lib.sketchbook.theme.contentColorFor

private val RowGlassElevation = 12.dp
private val IndicatorGlassElevation = 24.dp
private val IndicatorHeight = 4.dp
private val IndicatorInset = 12.dp
private val RowPadding = 4.dp
private const val UnselectedAlpha = .8f

@Composable
private fun TabIndicatorScope.GlassIndicator(selectedTabIndex: Int) {
    Box(
        Modifier
            .tabIndicatorOffset(selectedTabIndex)
            .padding(horizontal = IndicatorInset)
            .height(IndicatorHeight)
            .glass(
                shape = CircleShape,
                tonalElevation = IndicatorGlassElevation,
                useGlow = true,
                tint = Theme.color.emphasis.primary
            )
    )
}

@Composable
private fun GlassTabContainer(
    modifier: Modifier,
    shape: Shape,
    useGlow: Boolean,
    content: @Composable () -> Unit
) {
    Box(
        modifier
            .glass(shape, RowGlassElevation, useGlow)
            .padding(RowPadding)
    ) { content() }
}

@Composable
fun SketchbookTabRow(
    selectedTabIndex: Int,
    modifier: Modifier = Modifier,
    shape: Shape = Theme.container.card,
    compositeColor: Color = Theme.color.emphasis.background,
    useGlow: Boolean = true,
    tabs: @Composable () -> Unit
) {
    GlassTabContainer(modifier.fillMaxWidth(), shape, useGlow) {
        PrimaryTabRow(
            selectedTabIndex = selectedTabIndex,
            containerColor = Color.Transparent,
            contentColor = Theme.color.contentColorFor(compositeColor),
            indicator = { GlassIndicator(selectedTabIndex) },
            divider = {},
            tabs = tabs
        )
    }
}

@Composable
fun SketchbookScrollableTabRow(
    selectedTabIndex: Int,
    modifier: Modifier = Modifier,
    shape: Shape = Theme.container.card,
    compositeColor: Color = Theme.color.emphasis.background,
    useGlow: Boolean = true,
    edgePadding: Dp = 16.dp,
    tabs: @Composable () -> Unit
) {
    GlassTabContainer(modifier.fillMaxWidth(), shape, useGlow) {
        PrimaryScrollableTabRow(
            selectedTabIndex = selectedTabIndex,
            containerColor = Color.Transparent,
            contentColor = Theme.color.contentColorFor(compositeColor),
            edgePadding = edgePadding,
            indicator = { GlassIndicator(selectedTabIndex) },
            divider = {},
            tabs = tabs
        )
    }
}

@Composable
fun SketchbookTab(
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    text: (@Composable () -> Unit)? = null,
    icon: (@Composable () -> Unit)? = null,
    compositeColor: Color = Theme.color.emphasis.background,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() }
) {
    val content = Theme.color.contentColorFor(compositeColor)
    ThemePressHost {
        Tab(
            selected = selected,
            onClick = onClick,
            modifier = modifier.pressIndicated(RectangleShape, interactionSource),
            enabled = enabled,
            text = text,
            icon = icon,
            selectedContentColor = content,
            unselectedContentColor = content.copy(UnselectedAlpha),
            interactionSource = interactionSource
        )
    }
}

@SketchbookComponentPreview
@SketchbookValidation
@Composable
private fun SketchbookTabRowPreview() = SketchbookPreviewLayout {
    SketchbookTabRow(selectedTabIndex = 1, modifier = Modifier.padding(16.dp)) {
        SketchbookTab(false, {}, text = { Text("Photos") })
        SketchbookTab(true, {}, text = { Text("Albums") })
        SketchbookTab(false, {}, text = { Text("Places") })
    }
}
