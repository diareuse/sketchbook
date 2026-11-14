package lib.sketchbook.behavior

import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.assertTextContains
import androidx.compose.ui.test.assertTouchHeightIsEqualTo
import androidx.compose.ui.test.hasSetTextAction
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performImeAction
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.unit.dp
import lib.sketchbook.SketchbookSearchBar
import lib.sketchbook.theme.Theme
import org.junit.Assert.assertEquals
import org.junit.Test

class SearchBarBehaviorTest : BehaviorTest() {

    private var query by mutableStateOf("")
    private val searches = mutableListOf<String>()

    private fun show(enabled: Boolean = true, withResults: Boolean = false, initial: String = "") {
        query = initial
        composeRule.setContent {
            Theme(darkTheme = false, dynamicColor = false) {
                SketchbookSearchBar(
                    query = query,
                    onQueryChange = { query = it },
                    onSearch = { searches += it },
                    clearContentDescription = "Clear search",
                    modifier = Modifier.testTag("bar"),
                    placeholder = { Text("Search here") },
                    results = if (withResults) {
                        { Text("Result one") }
                    } else null,
                    enabled = enabled
                )
            }
        }
        composeRule.waitForIdle()
    }

    @Test
    fun `typing updates the query`() {
        show()

        composeRule.onNode(hasSetTextAction()).performTextInput("kotlin")

        assertEquals("kotlin", query)
        composeRule.onNode(hasSetTextAction()).assertTextContains("kotlin")
    }

    @Test
    fun `search ime action reports the current query`() {
        show(initial = "compose")

        composeRule.onNode(hasSetTextAction()).performImeAction()

        assertEquals(listOf("compose"), searches)
    }

    @Test
    fun `search ime action reports text typed after the first composition`() {
        show()

        composeRule.onNode(hasSetTextAction()).performTextInput("typed")
        composeRule.onNode(hasSetTextAction()).performImeAction()

        assertEquals(listOf("typed"), searches)
    }

    @Test
    fun `clear button empties the query and then disappears`() {
        show(initial = "compose")

        composeRule.onNodeWithContentDescription("Clear search").performClick()

        assertEquals("", query)
        composeRule.onNodeWithContentDescription("Clear search").assertDoesNotExist()
    }

    @Test
    fun `clear button keeps a 48dp touch target`() {
        show(initial = "compose")

        composeRule.onNodeWithContentDescription("Clear search").assertTouchHeightIsEqualTo(48.dp)
    }

    @Test
    fun `clear button is absent while the query is empty`() {
        show()

        composeRule.onNodeWithContentDescription("Clear search").assertDoesNotExist()
        composeRule.onNodeWithText("Search here").assertIsDisplayed()
    }

    @Test
    fun `results slot is shown only when provided`() {
        show(withResults = true)

        composeRule.onNodeWithText("Result one").assertIsDisplayed()
    }

    @Test
    fun `no results are shown by default`() {
        show()

        composeRule.onNodeWithText("Result one").assertDoesNotExist()
    }

    @Test
    fun `disabled search bar is not editable and offers no clear button`() {
        show(enabled = false, initial = "compose")

        composeRule.onNode(hasSetTextAction()).assertDoesNotExist()
        composeRule.onNodeWithText("compose").assertIsNotEnabled()
        composeRule.onNodeWithContentDescription("Clear search").assertDoesNotExist()
    }
}
