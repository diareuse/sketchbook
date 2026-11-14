package lib.sketchbook.behavior

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.PixelMap
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.toPixelMap
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.center
import androidx.compose.ui.test.down
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import lib.sketchbook.SketchbookScrollableTabRow
import lib.sketchbook.SketchbookTab
import lib.sketchbook.SketchbookTabRow
import lib.sketchbook.StripedBackdrop
import lib.sketchbook.changedPixels
import lib.sketchbook.glass.asGlassShape
import lib.sketchbook.theme.Theme
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.robolectric.annotation.Config

private typealias TabRowContent = @Composable (Modifier, Int, @Composable () -> Unit) -> Unit

private val StandardRow: TabRowContent = { modifier, selected, tabs ->
    SketchbookTabRow(selected, modifier, tabs = tabs)
}

private val ScrollableRow: TabRowContent = { modifier, selected, tabs ->
    SketchbookScrollableTabRow(selected, modifier, edgePadding = 4.dp, tabs = tabs)
}

abstract class TabRowShapeBehavior : GlassPaneBehavior() {

    protected abstract val row: TabRowContent

    override val paneShape: @Composable () -> Shape = { Theme.container.card }

    @Composable
    private fun Tabs(selected: Int) {
        repeat(3) {
            SketchbookTab(
                selected = it == selected,
                onClick = {},
                modifier = Modifier.testTag("tab$it"),
                icon = { Icon(Icons.Default.Home, null) },
                text = { Text("Tab$it") }
            )
        }
    }

    @Test
    fun `tab row pane edge matches the theme card shape`() = assertPane {
        row(it, 1) { Tabs(1) }
    }

    protected fun assertPressInsideTabShape() {
        var density = Density(1f)
        var shape: Shape = RectangleShape
        composeRule.setContent {
            Theme(darkTheme = false, dynamicColor = false) {
                shape = Theme.container.buttonSmall.asGlassShape()
                density = LocalDensity.current
                StripedBackdrop(Modifier.size(260.dp, 160.dp)) {
                    Box(Modifier.align(Alignment.Center)) { row(Modifier, 1) { Tabs(1) } }
                }
            }
        }
        composeRule.mainClock.autoAdvance = false
        composeRule.mainClock.advanceTimeBy(16)
        val idle = frame()
        val bounds = composeRule.onNodeWithTag("tab1").fetchSemanticsNode().boundsInRoot

        composeRule.onNodeWithTag("tab1").performTouchInput { down(center) }
        composeRule.mainClock.advanceTimeBy(150)
        val changed = idle.changedPixels(frame(), 1).filter { (x, y) -> bounds.inflate(40f).contains(Offset(x + .5f, y + .5f)) }

        val outline = shape.createOutline(Size(bounds.width, bounds.height), LayoutDirection.Ltr, density)
        val rounded = (outline as Outline.Rounded).roundRect
        val limit = RoundRect(
            bounds.left - 2f, bounds.top - 2f, bounds.right + 2f, bounds.bottom + 2f,
            CornerRadius(rounded.topLeftCornerRadius.x + 2f, rounded.topLeftCornerRadius.y + 2f)
        )
        assertTrue("press changed nothing", changed.isNotEmpty())
        assertEquals(emptyList<Pair<Int, Int>>(), changed.filter { !limit.contains(Offset(it.first + .5f, it.second + .5f)) }.take(8))
    }

    @Test
    fun `tab row pane edge is stable when a tab is selected`() = assertPane {
        row(it, 2) { Tabs(2) }
    }

    private fun frame(): PixelMap = composeRule.onRoot().captureToImage().toPixelMap()
}

abstract class TabRowPressBehavior : TabRowShapeBehavior() {

    @Test
    fun `tab press highlight stays inside the tab glass shape`() = assertPressInsideTabShape()
}

@Config(sdk = [26])
class TabRowShapeAndroid8BehaviorTest : TabRowPressBehavior() {
    override val row = StandardRow
}

@Config(sdk = [33])
class TabRowShapeAndroid13BehaviorTest : TabRowPressBehavior() {
    override val row = StandardRow
}

@Config(sdk = [26])
class ScrollableTabRowShapeAndroid8BehaviorTest : TabRowPressBehavior() {
    override val row = ScrollableRow
}

@Config(sdk = [33])
class ScrollableTabRowShapeAndroid13BehaviorTest : TabRowPressBehavior() {
    override val row = ScrollableRow
}

@Config(sdk = [31])
class TabRowShapeAndroid12BehaviorTest : TabRowShapeBehavior() {
    override val row = StandardRow
}

abstract class TabRowIndicatorBehavior : BehaviorTest() {

    protected abstract val row: TabRowContent

    private var selected by mutableIntStateOf(0)

    @Composable
    private fun Tabs() {
        repeat(3) {
            SketchbookTab(
                selected = it == selected,
                onClick = {},
                modifier = Modifier.testTag("tab$it"),
                icon = { Box(Modifier.size(24.dp)) }
            )
        }
    }

    @Test
    fun `selection indicator stays inside the row container and under its tab`() {
        composeRule.setContent {
            Theme(darkTheme = false, dynamicColor = false) {
                StripedBackdrop(Modifier.size(260.dp, 160.dp)) {
                    Box(Modifier.align(Alignment.Center).testTag("row")) { row(Modifier, selected) { Tabs() } }
                }
            }
        }
        composeRule.waitForIdle()
        val first = composeRule.onRoot().captureToImage().toPixelMap()
        selected = 1
        composeRule.waitForIdle()
        val second = composeRule.onRoot().captureToImage().toPixelMap()

        val container = composeRule.onNodeWithTag("row").fetchSemanticsNode().boundsInRoot
        val tabs = listOf(0, 1).map { composeRule.onNodeWithTag("tab$it").fetchSemanticsNode().boundsInRoot }
        val changed = first.changedPixels(second)
        val stray = changed.filter { (x, y) ->
            val p = Offset(x + .5f, y + .5f)
            !container.contains(p) || tabs.none { it.inflate(1f).contains(p) }
        }

        assertTrue("indicator not drawn", changed.isNotEmpty())
        assertEquals(emptyList<Pair<Int, Int>>(), stray.take(8))
    }
}

@Config(sdk = [26])
class TabRowIndicatorAndroid8BehaviorTest : TabRowIndicatorBehavior() {
    override val row = StandardRow
}

@Config(sdk = [33])
class TabRowIndicatorAndroid13BehaviorTest : TabRowIndicatorBehavior() {
    override val row = StandardRow
}

@Config(sdk = [33])
class ScrollableTabRowIndicatorAndroid13BehaviorTest : TabRowIndicatorBehavior() {
    override val row = ScrollableRow
}
