package lib.sketchbook.behavior

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
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
import lib.sketchbook.SketchbookNavigationBar
import lib.sketchbook.SketchbookNavigationBarItem
import lib.sketchbook.SketchbookNavigationRail
import lib.sketchbook.SketchbookNavigationRailItem
import lib.sketchbook.StripedBackdrop
import lib.sketchbook.changedPixels
import lib.sketchbook.glass.asGlassShape
import lib.sketchbook.theme.Theme
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.robolectric.annotation.Config

interface NavigationFlavour {
    @Composable
    fun Container(selected: Int)
}

internal object BarFlavour : NavigationFlavour {
    @Composable
    override fun Container(selected: Int) {
        SketchbookNavigationBar {
            repeat(3) { index ->
                BarItem(index, index == selected)
            }
        }
    }

    @Composable
    private fun RowScope.BarItem(index: Int, selected: Boolean) {
        SketchbookNavigationBarItem(
            selected = selected,
            onClick = {},
            icon = { Box(Modifier.size(24.dp)) },
            modifier = Modifier.testTag("item$index")
        )
    }
}

internal object RailFlavour : NavigationFlavour {
    @Composable
    override fun Container(selected: Int) {
        SketchbookNavigationRail {
            repeat(3) { index ->
                SketchbookNavigationRailItem(
                    selected = index == selected,
                    onClick = {},
                    icon = { Box(Modifier.size(24.dp)) },
                    modifier = Modifier.testTag("item$index")
                )
            }
        }
    }
}

abstract class NavigationShapeBehavior : GlassPaneBehavior() {

    protected abstract val flavour: NavigationFlavour

    override val paneShape: @Composable () -> Shape = { Theme.container.card }

    private var selected by mutableStateOf(1)
    private var density = Density(1f)
    private var itemShape: Shape = RectangleShape

    @Test
    fun `navigation container pane edge matches the theme card shape`() = assertPane {
        flavour.Container(1)
    }

    private fun showStriped() {
        composeRule.setContent {
            Theme(darkTheme = false, dynamicColor = false) {
                itemShape = Theme.container.buttonSmall.asGlassShape()
                density = LocalDensity.current
                StripedBackdrop(Modifier.size(260.dp, 200.dp)) {
                    Box(Modifier.align(Alignment.Center)) { flavour.Container(selected) }
                }
            }
        }
        composeRule.waitForIdle()
    }

    private fun frame(): PixelMap = composeRule.onRoot().captureToImage().toPixelMap()

    private fun limitOf(bounds: Rect, slack: Float): RoundRect {
        val outline = itemShape.createOutline(Size(bounds.width, bounds.height), LayoutDirection.Ltr, density)
        val radius = (outline as Outline.Rounded).roundRect.topLeftCornerRadius
        return RoundRect(
            bounds.left - slack, bounds.top - slack, bounds.right + slack, bounds.bottom + slack,
            CornerRadius(radius.x + slack, radius.y + slack)
        )
    }

    @Test
    fun `item press highlight stays inside the item glass shape`() {
        showStriped()
        composeRule.mainClock.autoAdvance = false
        composeRule.mainClock.advanceTimeBy(16)
        val idle = frame()
        val bounds = composeRule.onNodeWithTag("item0").fetchSemanticsNode().boundsInRoot

        composeRule.onNodeWithTag("item0").performTouchInput { down(center) }
        composeRule.mainClock.advanceTimeBy(150)
        val changed = idle.changedPixels(frame(), 1).filter { bounds.inflate(40f).contains(Offset(it.first + .5f, it.second + .5f)) }

        val limit = limitOf(bounds, 1f)
        assertTrue("press changed nothing", changed.isNotEmpty())
        assertEquals(emptyList<Pair<Int, Int>>(), changed.filter { !limit.contains(Offset(it.first + .5f, it.second + .5f)) }.take(8))
    }

    @Test
    fun `selection indicator follows the item glass shape inside its item`() {
        selected = 2
        showStriped()
        val before = frame()
        selected = 0
        composeRule.waitForIdle()
        val after = frame()
        val bounds = composeRule.onNodeWithTag("item0").fetchSemanticsNode().boundsInRoot
        val indicatorWidth = 56f * composeRule.density.density
        val indicatorHeight = 32f * composeRule.density.density
        val indicator = Rect(
            bounds.center.x - indicatorWidth / 2,
            bounds.center.y - indicatorHeight / 2,
            bounds.center.x + indicatorWidth / 2,
            bounds.center.y + indicatorHeight / 2
        )

        val changed = before.changedPixels(after, 1).filter { bounds.inflate(40f).contains(Offset(it.first + .5f, it.second + .5f)) }
        val limit = limitOf(indicator, 1f)

        assertTrue("indicator not drawn", changed.isNotEmpty())
        assertEquals(emptyList<Pair<Int, Int>>(), changed.filter { !limit.contains(Offset(it.first + .5f, it.second + .5f)) }.take(8))
    }
}

abstract class NavigationBarShapeBehavior : NavigationShapeBehavior() {
    override val flavour: NavigationFlavour = BarFlavour
}

abstract class NavigationRailShapeBehavior : NavigationShapeBehavior() {
    override val flavour: NavigationFlavour = RailFlavour
}

@Config(sdk = [26])
class NavigationBarShapeAndroid8BehaviorTest : NavigationBarShapeBehavior()

@Config(sdk = [33])
class NavigationBarShapeAndroid13BehaviorTest : NavigationBarShapeBehavior()

@Config(sdk = [26])
class NavigationRailShapeAndroid8BehaviorTest : NavigationRailShapeBehavior()

@Config(sdk = [33])
class NavigationRailShapeAndroid13BehaviorTest : NavigationRailShapeBehavior()
