package lib.sketchbook.behavior

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.longClick
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.unit.dp
import lib.sketchbook.SketchbookTooltip
import lib.sketchbook.theme.Theme
import org.junit.Test

class TooltipBehaviorTest : BehaviorTest() {

    private fun show(content: @Composable () -> Unit) {
        composeRule.setContent {
            Theme(darkTheme = false, dynamicColor = false) {
                Box(Modifier.size(200.dp).padding(60.dp)) { content() }
            }
        }
        composeRule.waitForIdle()
    }

    private fun anchor() = Modifier.testTag("content").size(48.dp).background(Color.Gray)

    @Test
    fun `tooltip stays hidden until the anchor is interacted with`() {
        show { SketchbookTooltip("Add to favorites", Modifier.testTag("anchor")) { Box(anchor()) } }

        composeRule.onNodeWithText("Add to favorites").assertDoesNotExist()
    }

    @Test
    fun `tooltip appears when the anchor is long pressed`() {
        show { SketchbookTooltip("Add to favorites", Modifier.testTag("anchor")) { Box(anchor()) } }

        composeRule.onNodeWithTag("anchor").performTouchInput { longClick() }
        composeRule.waitForIdle()

        composeRule.onNodeWithText("Add to favorites").assertIsDisplayed()
    }

    @Test
    fun `tooltip renders custom slot content`() {
        show {
            SketchbookTooltip(tooltip = { Text("Custom hint") }, modifier = Modifier.testTag("anchor")) { Box(anchor()) }
        }

        composeRule.onNodeWithTag("anchor").performTouchInput { longClick() }
        composeRule.waitForIdle()

        composeRule.onNodeWithText("Custom hint").assertIsDisplayed()
    }

    @Test
    fun `tooltip keeps the anchor content visible`() {
        show { SketchbookTooltip("Add to favorites", Modifier.testTag("anchor")) { Box(anchor()) } }

        composeRule.onNodeWithTag("content", useUnmergedTree = true).assertIsDisplayed()
    }
}
