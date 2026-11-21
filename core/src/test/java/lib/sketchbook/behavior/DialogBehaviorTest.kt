package lib.sketchbook.behavior

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.unit.dp
import lib.sketchbook.SketchbookDialog
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.robolectric.shadows.ShadowDialog

class DialogBehaviorTest : BehaviorTest() {

    private var visible by mutableStateOf(true)
    private var dismissals = 0

    private fun dialog() {
        composeRule.setContent {
            SketchbookDialog(
                visible = visible,
                onVisibleChange = {
                    if (!it) dismissals++
                    visible = it
                },
                dialog = { Text("Dialog body", Modifier.testTag("body")) }
            ) {
                Box(Modifier.size(10.dp))
            }
        }
        composeRule.waitForIdle()
    }

    private fun bodyPresent() = composeRule.onAllNodes(hasTestTag("body")).fetchSemanticsNodes().isNotEmpty()

    @Test
    fun `dialog content stays during the exit animation and disappears once it completes`() {
        dialog()
        assertTrue(bodyPresent())

        composeRule.mainClock.autoAdvance = false
        visible = false
        composeRule.mainClock.advanceTimeBy(150)
        assertTrue(bodyPresent())

        composeRule.mainClock.advanceTimeBy(3_000)
        composeRule.waitForIdle()
        assertTrue(!bodyPresent())
    }

    @Test
    fun `back press dismisses the dialog exactly once`() {
        dialog()

        composeRule.runOnUiThread { ShadowDialog.getLatestDialog().onBackPressed() }
        composeRule.waitForIdle()

        assertEquals(1, dismissals)
    }
}
