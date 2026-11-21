package lib.sketchbook.behavior

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.assertContentDescriptionEquals
import androidx.compose.ui.test.assertHeightIsAtLeast
import androidx.compose.ui.test.assertWidthIsAtLeast
import androidx.compose.ui.test.assertWidthIsEqualTo
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.unit.dp
import lib.sketchbook.SketchbookExtendedFloatingActionButton
import lib.sketchbook.SketchbookFloatingActionButton
import lib.sketchbook.theme.Theme
import org.junit.Assert.assertEquals
import org.junit.Test

class FloatingActionButtonBehaviorTest : BehaviorTest() {

    private var fabClicks = 0
    private var extendedClicks = 0
    private var expanded by mutableStateOf(true)

    private fun show() {
        composeRule.setContent {
            Theme(darkTheme = false, dynamicColor = false) {
                Column {
                    SketchbookFloatingActionButton(
                        onClick = { fabClicks++ },
                        contentDescription = "Add item",
                        modifier = Modifier.testTag("fab")
                    ) { Box(Modifier.size(24.dp)) }
                    SketchbookExtendedFloatingActionButton(
                        onClick = { extendedClicks++ },
                        modifier = Modifier.testTag("extended"),
                        expanded = expanded,
                        icon = { Box(Modifier.size(24.dp)) }
                    ) { Text("Create") }
                }
            }
        }
        composeRule.waitForIdle()
    }

    @Test
    fun `floating action button invokes onClick and exposes role and description`() {
        show()

        composeRule.onNodeWithTag("fab").performClick()

        assertEquals(1, fabClicks)
        composeRule.onNodeWithTag("fab")
            .assertContentDescriptionEquals("Add item")
            .assert(SemanticsMatcher.expectValue(SemanticsProperties.Role, Role.Button))
            .assertHeightIsAtLeast(56.dp)
            .assertWidthIsAtLeast(56.dp)
    }

    @Test
    fun `extended floating action button invokes onClick and shows its text`() {
        show()

        composeRule.onNodeWithTag("extended").performClick()

        assertEquals(1, extendedClicks)
        composeRule.onNodeWithText("Create", useUnmergedTree = true).assertExists()
        composeRule.onNodeWithTag("extended").assertWidthIsAtLeast(96.dp)
        composeRule.onNodeWithTag("extended").assert(SemanticsMatcher.expectValue(SemanticsProperties.Role, Role.Button))
    }

    @Test
    fun `extended floating action button hides its text when collapsed`() {
        show()

        expanded = false
        composeRule.waitForIdle()

        composeRule.onNodeWithText("Create", useUnmergedTree = true).assertDoesNotExist()
        composeRule.onNodeWithTag("extended").assertWidthIsEqualTo(56.dp)
        composeRule.onNodeWithTag("extended").performClick()
        assertEquals(1, extendedClicks)
    }
}
