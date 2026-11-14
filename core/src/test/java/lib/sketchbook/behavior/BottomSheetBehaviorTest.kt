package lib.sketchbook.behavior

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.getBoundsInRoot
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.unit.dp
import lib.sketchbook.SketchbookBottomSheet
import lib.sketchbook.theme.Theme
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.robolectric.shadows.ShadowDialog

class BottomSheetBehaviorTest : BehaviorTest() {

    private var visible by mutableStateOf(true)
    private var dismissals = 0

    private fun show(handle: Boolean = true) {
        composeRule.setContent {
            Theme(darkTheme = false, dynamicColor = false) {
                Box(Modifier.size(10.dp))
                SketchbookBottomSheet(
                    visible = visible,
                    onDismissRequest = {
                        dismissals++
                        visible = false
                    },
                    dragHandle = if (handle) {
                        { Text("Handle") }
                    } else null
                ) {
                    Text("First")
                    Text("Second")
                }
            }
        }
        composeRule.waitForIdle()
    }

    private fun waitUntilGone(text: String) = composeRule.waitUntil(5_000) {
        composeRule.onAllNodes(androidx.compose.ui.test.hasText(text)).fetchSemanticsNodes().isEmpty()
    }

    @Test
    fun `sheet shows its content while visible`() {
        show()

        composeRule.onNodeWithText("First").assertIsDisplayed()
        composeRule.onNodeWithText("Second").assertIsDisplayed()
    }

    @Test
    fun `sheet shows the drag handle slot above the content`() {
        show()

        composeRule.onNodeWithText("Handle").assertIsDisplayed()
    }

    @Test
    fun `drag handle is centred horizontally`() {
        show()

        val handle = composeRule.onNodeWithText("Handle").getBoundsInRoot()
        val first = composeRule.onNodeWithText("First").getBoundsInRoot()
        assertTrue(handle.left > first.left + 50.dp)
    }

    @Test
    fun `sheet without a drag handle omits it`() {
        show(handle = false)

        composeRule.onNodeWithText("Handle").assertDoesNotExist()
        composeRule.onNodeWithText("First").assertIsDisplayed()
    }

    @Test
    fun `sheet is absent while not visible`() {
        visible = false
        show()

        composeRule.onNodeWithText("First").assertDoesNotExist()
    }

    @Test
    fun `sheet appears when visibility turns on`() {
        visible = false
        show()

        visible = true
        composeRule.waitForIdle()

        composeRule.onNodeWithText("First").assertIsDisplayed()
    }

    @Test
    fun `sheet disappears after visibility turns off`() {
        show()

        visible = false

        waitUntilGone("First")
    }

    @Test
    fun `back press requests dismissal exactly once`() {
        show()

        composeRule.runOnUiThread { ShadowDialog.getLatestDialog().onBackPressed() }
        composeRule.waitForIdle()

        assertEquals(1, dismissals)
    }
}
