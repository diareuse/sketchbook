package lib.sketchbook.behavior

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
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.assertIsNotSelected
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.getBoundsInRoot
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.performClick
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.height
import lib.sketchbook.SketchbookNavigationRail
import lib.sketchbook.navItem
import lib.sketchbook.SketchbookNavigationRailItem
import lib.sketchbook.theme.Theme
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class NavigationRailBehaviorTest : BehaviorTest() {

    private var selectedIndex by mutableIntStateOf(0)
    private val clicks = mutableListOf<Int>()

    private fun show(
        insets: WindowInsets = WindowInsets(0, 0, 0, 0),
        alwaysShowLabel: Boolean = true,
        secondEnabled: Boolean = true,
        header: Boolean = false
    ) {
        composeRule.setContent {
            Theme(darkTheme = false, dynamicColor = false) {
                Box(Modifier.size(360.dp, 400.dp)) {
                    SketchbookNavigationRail(
                        windowInsets = insets,
                        header = if (header) {
                            { Text("Header") }
                        } else null
                    ) {
                        SketchbookNavigationRailItem(
                            selected = selectedIndex == 0,
                            onClick = { clicks += 0; selectedIndex = 0 },
                            icon = { Icon(Icons.Default.Home, null) },
                            modifier = Modifier.testTag("home"),
                            label = { Text("Home") },
                            alwaysShowLabel = alwaysShowLabel,
                            badge = { Text("3") }
                        )
                        SketchbookNavigationRailItem(
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
        composeRule.waitForIdle()
    }

    @Test
    fun `items expose the tab role and their selected state`() {
        show()

        composeRule.navItem("home")
            .assertIsSelected()
            .assert(SemanticsMatcher.expectValue(SemanticsProperties.Role, Role.Tab))
        composeRule.navItem("search").assertIsNotSelected()
    }

    @Test
    fun `clicking an item reports it and moves the selection`() {
        show()

        composeRule.navItem("search").performClick()

        assertEquals(listOf(1), clicks)
        composeRule.navItem("search").assertIsSelected()
    }

    @Test
    fun `disabled item ignores clicks`() {
        show(secondEnabled = false)

        composeRule.navItem("search").performClick()

        assertEquals(emptyList<Int>(), clicks)
        composeRule.navItem("search").assertIsNotEnabled()
    }

    @Test
    fun `labels of unselected items are hidden when not always shown`() {
        show(alwaysShowLabel = false)

        composeRule.onNodeWithText("Home").assertIsDisplayed()
        composeRule.onNodeWithText("Search").assertDoesNotExist()
    }

    @Test
    fun `items are stacked vertically`() {
        show()

        val home = composeRule.onNodeWithTag("home").getBoundsInRoot()
        val search = composeRule.onNodeWithTag("search").getBoundsInRoot()
        assertTrue(search.top >= home.bottom)
    }

    @Test
    fun `header slot is shown above the items`() {
        show(header = true)

        composeRule.onNodeWithText("Header").assertIsDisplayed()
        val header = composeRule.onNodeWithText("Header").getBoundsInRoot()
        val home = composeRule.onNodeWithTag("home").getBoundsInRoot()
        val root = composeRule.onRoot().getBoundsInRoot()
        assertTrue(header.bottom <= home.top)
        assertTrue("header ${header.top}", header.top < root.height / 4)
    }

    @Test
    fun `badge slot is rendered on its item`() {
        show()

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
    fun `rail floats with a margin from the screen edge`() {
        show()

        assertTrue(composeRule.onNodeWithTag("home").getBoundsInRoot().left >= 8.dp)
    }

    @Test
    fun `rail stays clear of the start window inset`() {
        show(insets = WindowInsets(left = 24.dp))

        assertTrue(composeRule.onNodeWithTag("home").getBoundsInRoot().left >= 24.dp)
    }
}
