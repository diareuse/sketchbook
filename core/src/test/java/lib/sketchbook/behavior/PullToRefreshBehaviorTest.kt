package lib.sketchbook.behavior

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.ui.test.assertIsNotDisplayed
import androidx.compose.ui.test.swipeUp
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.ProgressBarRangeInfo
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.swipeDown
import androidx.compose.ui.unit.dp
import lib.sketchbook.SketchbookPullToRefresh
import lib.sketchbook.SketchbookScaffold
import lib.sketchbook.theme.Theme
import org.junit.Assert.assertEquals
import org.junit.Test
import org.robolectric.annotation.Config

class PullToRefreshBehaviorTest : BehaviorTest() {

    private var refreshes = 0
    private var refreshing by mutableStateOf(false)

    private val indeterminate = SemanticsMatcher.expectValue(
        SemanticsProperties.ProgressBarRangeInfo,
        ProgressBarRangeInfo.Indeterminate
    )

    @Composable
    private fun Refreshable() {
        SketchbookPullToRefresh(
            isRefreshing = refreshing,
            onRefresh = { refreshes++ },
            modifier = Modifier.size(300.dp, 500.dp).testTag("box")
        ) {
            Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).testTag("list")) {
                repeat(5) { Text("Row $it", Modifier.height(80.dp)) }
            }
        }
    }

    @Composable
    private fun StaticRefreshable() {
        SketchbookPullToRefresh(
            isRefreshing = refreshing,
            onRefresh = { refreshes++ },
            modifier = Modifier.size(300.dp, 500.dp).testTag("box")
        ) {
            Text("Static content", Modifier.testTag("static"))
        }
    }

    @Composable
    private fun LazyRefreshable() {
        SketchbookPullToRefresh(
            isRefreshing = refreshing,
            onRefresh = { refreshes++ },
            modifier = Modifier.size(300.dp, 500.dp)
        ) {
            LazyColumn(Modifier.fillMaxSize().testTag("lazy")) {
                items(20) { Text("Item $it", Modifier.height(80.dp)) }
            }
        }
    }

    private fun show(content: @Composable () -> Unit = { Refreshable() }) {
        composeRule.mainClock.autoAdvance = false
        composeRule.setContent { Theme(darkTheme = false, dynamicColor = false) { content() } }
        composeRule.mainClock.advanceTimeBy(100)
    }

    private fun pull(distance: Float) {
        composeRule.onNodeWithTag("list").performTouchInput {
            swipeDown(startY = 40f, endY = 40f + distance, durationMillis = 400)
        }
        composeRule.mainClock.advanceTimeBy(1000)
    }

    @Test
    fun `refresh container shows its content`() {
        show()

        composeRule.onNodeWithText("Row 0").assertIsDisplayed()
    }

    @Test
    fun `pulling past the threshold requests a refresh`() {
        show()

        pull(900f)

        assertEquals(1, refreshes)
    }

    @Test
    fun `pulling non scrollable content requests a refresh`() {
        show { StaticRefreshable() }

        composeRule.onNodeWithTag("box").performTouchInput {
            swipeDown(startY = 40f, endY = 940f, durationMillis = 400)
        }
        composeRule.mainClock.advanceTimeBy(1000)

        assertEquals(1, refreshes)
    }

    @Test
    fun `pulling a lazy list at its top requests a refresh and scrolling it still works`() {
        show { LazyRefreshable() }

        composeRule.onNodeWithTag("lazy").performTouchInput {
            swipeUp(startY = 400f, endY = 100f, durationMillis = 300)
        }
        composeRule.mainClock.advanceTimeBy(1000)
        composeRule.onNodeWithText("Item 0").assertIsNotDisplayed()
        assertEquals(0, refreshes)

        composeRule.onNodeWithTag("lazy").performTouchInput {
            swipeDown(startY = 40f, endY = 940f, durationMillis = 400)
        }
        composeRule.mainClock.advanceTimeBy(1000)
        composeRule.onNodeWithText("Item 0").assertIsDisplayed()
        assertEquals(1, refreshes)
    }

    @Test
    fun `a short pull does not request a refresh`() {
        show()

        pull(60f)

        assertEquals(0, refreshes)
    }

    @Test
    fun `no spinner is active while idle`() {
        show()

        composeRule.onNode(indeterminate).assertDoesNotExist()
    }

    @Test
    fun `an active refresh shows an indeterminate spinner`() {
        refreshing = true
        show()

        composeRule.onNode(indeterminate).assertExists()
    }

    @Test
    fun `the spinner goes away when the refresh completes`() {
        refreshing = true
        show()

        refreshing = false
        composeRule.mainClock.advanceTimeBy(1000)

        composeRule.onNode(indeterminate).assertDoesNotExist()
    }

    @Test
    @Config(sdk = [33])
    fun `refresh container works inside the scaffold content`() {
        refreshing = true
        show {
            SketchbookScaffold { padding ->
                Box(Modifier.fillMaxSize()) { Refreshable() }
            }
        }

        composeRule.onNodeWithText("Row 0").assertIsDisplayed()
        composeRule.onNode(indeterminate).assertExists()
    }
}
