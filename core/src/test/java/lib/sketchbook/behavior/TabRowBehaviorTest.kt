package lib.sketchbook.behavior

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PixelMap
import androidx.compose.ui.graphics.toPixelMap
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotDisplayed
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.assertIsNotSelected
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.getBoundsInRoot
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.swipeLeft
import androidx.compose.ui.unit.dp
import lib.sketchbook.SketchbookScrollableTabRow
import lib.sketchbook.SketchbookTab
import lib.sketchbook.SketchbookTabRow
import lib.sketchbook.distance
import lib.sketchbook.opaque
import lib.sketchbook.theme.Theme
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.robolectric.annotation.Config

class TabRowBehaviorTest : BehaviorTest() {

    private var selectedIndex by mutableIntStateOf(0)
    private val clicks = mutableListOf<Int>()

    private fun show(
        count: Int = 3,
        scrollable: Boolean = false,
        secondEnabled: Boolean = true,
        manualClock: Boolean = false
    ) {
        composeRule.mainClock.autoAdvance = !manualClock
        composeRule.setContent {
            Theme(darkTheme = false, dynamicColor = false) {
                Box(Modifier.size(360.dp, 200.dp).background(Color.White)) {
                    val tabs: @Composable () -> Unit = {
                        repeat(count) { index ->
                            SketchbookTab(
                                selected = selectedIndex == index,
                                onClick = { clicks += index; selectedIndex = index },
                                modifier = Modifier.testTag("tab$index"),
                                enabled = index != 1 || secondEnabled,
                                text = { Text("Tab $index") }
                            )
                        }
                    }
                    if (scrollable) {
                        SketchbookScrollableTabRow(selectedIndex, Modifier.testTag("row"), tabs = tabs)
                    } else {
                        SketchbookTabRow(selectedIndex, Modifier.testTag("row"), tabs = tabs)
                    }
                }
            }
        }
        composeRule.mainClock.advanceTimeBy(3_000)
        composeRule.waitForIdle()
    }

    @Test
    fun `tabs expose the tab role and their selected state`() {
        show()

        composeRule.onNodeWithTag("tab0")
            .assertIsSelected()
            .assert(SemanticsMatcher.expectValue(SemanticsProperties.Role, Role.Tab))
        composeRule.onNodeWithTag("tab1").assertIsNotSelected()
    }

    @Test
    fun `clicking a tab reports it and moves the selection`() {
        show()

        composeRule.onNodeWithTag("tab2").performClick()

        assertEquals(listOf(2), clicks)
        composeRule.onNodeWithTag("tab2").assertIsSelected()
    }

    @Test
    fun `disabled tab ignores clicks`() {
        show(secondEnabled = false)

        composeRule.onNodeWithTag("tab1").performClick()

        assertEquals(emptyList<Int>(), clicks)
        composeRule.onNodeWithTag("tab1").assertIsNotEnabled()
    }

    @Test
    fun `tabs share the width of the row equally`() {
        show()

        val widths = (0..2).map {
            composeRule.onNodeWithTag("tab$it").getBoundsInRoot().let { b -> b.right - b.left }
        }
        assertTrue(widths.all { it == widths.first() })
        assertTrue(widths.first() > 100.dp)
    }

    @Test
    fun `tabs keep at least a 48dp touch target`() {
        show()

        val bounds = composeRule.onNodeWithTag("tab0").getBoundsInRoot()
        assertTrue(bounds.bottom - bounds.top >= 48.dp)
    }

    @Test
    fun `scrollable row keeps tabs beyond the viewport reachable by scrolling`() {
        show(count = 10, scrollable = true)

        composeRule.onNodeWithTag("tab9").assertIsNotDisplayed()
        repeat(3) {
            composeRule.onNodeWithTag("row").performTouchInput { swipeLeft() }
            composeRule.waitForIdle()
        }
        composeRule.onNodeWithTag("tab9").assertIsDisplayed()
    }

    @Test
    fun `scrollable row can be swiped to reveal further tabs`() {
        show(count = 10, scrollable = true)

        composeRule.onNodeWithTag("row").performTouchInput { swipeLeft() }
        composeRule.waitForIdle()

        composeRule.onNodeWithTag("tab0").assertIsNotDisplayed()
    }

    @Test
    fun `scrollable row starts its first tab after the edge padding`() {
        show(count = 10, scrollable = true)

        assertTrue(composeRule.onNodeWithTag("tab0").getBoundsInRoot().left >= 16.dp)
    }

    private fun bottomPixel(image: PixelMap, tag: String): Int {
        val bounds = composeRule.onNodeWithTag(tag).getBoundsInRoot()
        val x = ((bounds.left + bounds.right).value / 2f * 2f).toInt()
        val y = ((bounds.bottom.value - 2f) * 2f).toInt()
        return distance(Color.White, image.opaque(x, y))
    }

    private fun snapshot() = composeRule.onRoot().captureToImage().toPixelMap()

    @Test
    @Config(sdk = [26])
    fun `indicator sits under the selected tab only`() {
        show(manualClock = true)
        val image = snapshot()

        assertTrue(bottomPixel(image, "tab0") > bottomPixel(image, "tab1") + 40)
    }

    @Test
    @Config(sdk = [26])
    fun `indicator slides to a newly selected tab instead of jumping`() {
        show(manualClock = true)
        val before = bottomPixel(snapshot(), "tab2")

        composeRule.onNodeWithTag("tab2").performClick()
        repeat(8) {
            composeRule.mainClock.advanceTimeByFrame()
            composeRule.waitForIdle()
        }
        val during = bottomPixel(snapshot(), "tab2")
        composeRule.mainClock.advanceTimeBy(3_000)
        val after = bottomPixel(snapshot(), "tab2")

        assertTrue("before $before during $during after $after", before + 40 < after && during < after)
    }
}
