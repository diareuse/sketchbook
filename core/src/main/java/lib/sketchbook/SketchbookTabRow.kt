package lib.sketchbook

import androidx.compose.foundation.interaction.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.unit.*
import lib.sketchbook.glass.asGlassShape
import lib.sketchbook.glass.glass
import lib.sketchbook.theme.Theme
import lib.sketchbook.theme.contentColorFor

private val RowGlassElevation = 12.dp
private val IndicatorGlassElevation = 4.dp
private val IndicatorHeight = 4.dp
private val RowPadding = 4.dp
private const val UnselectedAlpha = .8f

@Composable
private fun TabIndicatorScope.GlassIndicator(selectedTabIndex: Int) {
    val shape = Theme.container.buttonSmall.asGlassShape()
    Box(
        Modifier
            .tabIndicatorOffset(selectedTabIndex, matchContentSize = true)
            .height(IndicatorHeight)
            .glass(
                shape = shape,
                tonalElevation = IndicatorGlassElevation,
                tint = LocalContentColor.current
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
            .glass(shape.asGlassShape(), RowGlassElevation, useGlow)
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
            modifier = modifier.pressIndicated(Theme.container.buttonSmall.asGlassShape(), interactionSource),
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
