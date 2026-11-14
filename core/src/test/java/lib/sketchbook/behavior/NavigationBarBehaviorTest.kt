package lib.sketchbook.behavior

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
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
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.assertIsNotSelected
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.getBoundsInRoot
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.performClick
import androidx.compose.ui.unit.dp
import lib.sketchbook.SketchbookNavigationBar
import lib.sketchbook.SketchbookNavigationBarItem
import lib.sketchbook.distance
import lib.sketchbook.opaque
import lib.sketchbook.theme.Theme
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.robolectric.annotation.Config

class NavigationBarBehaviorTest : BehaviorTest() {

    private var selectedIndex by mutableIntStateOf(0)
    private val clicks = mutableListOf<Int>()

    private fun show(
        insets: WindowInsets = WindowInsets(0, 0, 0, 0),
        alwaysShowLabel: Boolean = true,
        secondEnabled: Boolean = true,
        badge: Boolean = false
    ) {
        composeRule.mainClock.autoAdvance = false
        composeRule.setContent {
            Theme(darkTheme = false, dynamicColor = false) {
                Box(
                    Modifier.size(360.dp, 400.dp).background(Color.White),
                    contentAlignment = Alignment.BottomCenter
                ) {
                    SketchbookNavigationBar(windowInsets = insets) {
                        SketchbookNavigationBarItem(
                            selected = selectedIndex == 0,
                            onClick = { clicks += 0; selectedIndex = 0 },
                            icon = { Icon(Icons.Default.Home, null) },
                            modifier = Modifier.testTag("home"),
                            label = { Text("Home") },
                            alwaysShowLabel = alwaysShowLabel,
                            badge = if (badge) {
                                { Text("3") }
                            } else null
                        )
                        SketchbookNavigationBarItem(
                            selected = selectedIndex == 1,
                            onClick = { clicks += 1; selectedIndex = 1 },
                            icon = { Icon(Icons.Default.Search, null) },
                            modifier = Modifier.testTag("search"),
                            enabled = secondEnabled,
                            label = { Text("Search") },
                            alwaysShowLabel = alwaysShowLabel
                        )
                    }
                }
            }
        }
        composeRule.mainClock.advanceTimeBy(3_000)
        composeRule.waitForIdle()
    }

    @Test
    fun `items expose the tab role and their selected state`() {
        show()

        composeRule.onNodeWithTag("home")
            .assertIsSelected()
            .assert(SemanticsMatcher.expectValue(SemanticsProperties.Role, Role.Tab))
        composeRule.onNodeWithTag("search").assertIsNotSelected()
    }

    @Test
    fun `clicking an item reports it and moves the selection`() {
        show()

        composeRule.onNodeWithTag("search").performClick()
        composeRule.mainClock.advanceTimeBy(100)

        assertEquals(listOf(1), clicks)
        composeRule.onNodeWithTag("search").assertIsSelected()
        composeRule.onNodeWithTag("home").assertIsNotSelected()
    }

    @Test
    fun `disabled item ignores clicks`() {
        show(secondEnabled = false)

        composeRule.onNodeWithTag("search").performClick()

        assertEquals(emptyList<Int>(), clicks)
        composeRule.onNodeWithTag("search").assertIsNotEnabled()
    }

    @Test
    fun `labels of unselected items are hidden when not always shown`() {
        show(alwaysShowLabel = false)

        composeRule.onNodeWithText("Home").assertIsDisplayed()
        composeRule.onNodeWithText("Search").assertDoesNotExist()
    }

    @Test
    fun `labels of every item are shown by default`() {
        show()

        composeRule.onNodeWithText("Home").assertIsDisplayed()
        composeRule.onNodeWithText("Search").assertIsDisplayed()
    }

    @Test
    fun `badge slot is rendered on its item`() {
        show(badge = true)

        composeRule.onNodeWithText("3", useUnmergedTree = true).assertIsDisplayed()
    }

    @Test
    fun `items keep at least a 48dp touch target`() {
        show()

        val bounds = composeRule.onNodeWithTag("home").getBoundsInRoot()
        assertTrue(bounds.bottom - bounds.top >= 48.dp)
        assertTrue(bounds.right - bounds.left >= 48.dp)
    }

    @Test
    fun `bar floats with a margin from the screen edges`() {
        show()

        val home = composeRule.onNodeWithTag("home").getBoundsInRoot()
        val search = composeRule.onNodeWithTag("search").getBoundsInRoot()
        val root = composeRule.onRoot().getBoundsInRoot()
        assertTrue(home.left >= 16.dp)
        assertTrue(search.right <= root.right - 16.dp)
        assertTrue(home.bottom <= root.bottom - 8.dp)
    }

    @Test
    fun `bar stays above the bottom window inset`() {
        show(insets = WindowInsets(bottom = 24.dp))

        val home = composeRule.onNodeWithTag("home").getBoundsInRoot()
        val root = composeRule.onRoot().getBoundsInRoot()
        assertTrue("bottom ${home.bottom} root ${root.bottom}", home.bottom <= root.bottom - 24.dp)
    }

    @Test
    fun `bar content does not grow past its floating pill when the inset is zero`() {
        show()

        val home = composeRule.onNodeWithTag("home").getBoundsInRoot()
        val root = composeRule.onRoot().getBoundsInRoot()
        assertTrue(root.bottom - home.bottom < 24.dp)
    }

    private fun indicatorDistance(image: PixelMap): Int {
        val bounds = composeRule.onNodeWithTag("search").getBoundsInRoot()
        val x = ((bounds.left + bounds.right).value / 2f * 2f - 40f).toInt()
        val y = ((bounds.top.value + 18f) * 2f).toInt()
        return distance(Color.White, image.opaque(x, y))
    }

    private fun snapshot() = composeRule.onRoot().captureToImage().toPixelMap()

    @Test
    @Config(sdk = [26])
    fun `selected indicator fades in progressively when the selection moves`() {
        show()
        val before = indicatorDistance(snapshot())

        composeRule.onNodeWithTag("search").performClick()
        repeat(7) {
            composeRule.mainClock.advanceTimeByFrame()
            composeRule.waitForIdle()
        }
        val during = indicatorDistance(snapshot())
        composeRule.mainClock.advanceTimeBy(3_000)
        val after = indicatorDistance(snapshot())

        assertTrue("before $before during $during after $after", before < during && during < after)
    }
}
