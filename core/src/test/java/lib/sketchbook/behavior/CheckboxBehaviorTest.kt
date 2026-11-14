package lib.sketchbook.behavior

import androidx.compose.foundation.layout.Column
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
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.assertIsOff
import androidx.compose.ui.test.assertIsOn
import androidx.compose.ui.test.assertTouchHeightIsEqualTo
import androidx.compose.ui.test.assertTouchWidthIsEqualTo
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.unit.dp
import lib.sketchbook.SketchbookCheckbox
import lib.sketchbook.theme.Theme
import org.junit.Assert.assertEquals
import org.junit.Test

class CheckboxBehaviorTest : BehaviorTest() {

    private var bare by mutableStateOf(false)
    private var labeled by mutableStateOf(false)
    private var changes = 0

    private fun show(enabled: Boolean = true) {
        composeRule.setContent {
            Theme(darkTheme = false, dynamicColor = false) {
                Column {
                    SketchbookCheckbox(
                        checked = bare,
                        onCheckedChange = { changes++; bare = it },
                        modifier = Modifier.testTag("bare"),
                        enabled = enabled
                    )
                    SketchbookCheckbox(
                        checked = labeled,
                        onCheckedChange = { changes++; labeled = it },
                        modifier = Modifier.testTag("labeled"),
                        label = { Text("Accept") },
                        enabled = enabled
                    )
                }
            }
        }
        composeRule.waitForIdle()
    }

    @Test
    fun `checkbox toggles on click and exposes the checkbox role`() {
        show()
        composeRule.onNodeWithTag("bare").assertIsOff()

        composeRule.onNodeWithTag("bare").performClick()

        composeRule.onNodeWithTag("bare")
            .assertIsOn()
            .assert(SemanticsMatcher.expectValue(SemanticsProperties.Role, Role.Checkbox))
        composeRule.onNodeWithTag("bare").performClick()
        composeRule.onNodeWithTag("bare").assertIsOff()
    }

    @Test
    fun `labeled checkbox toggles when its label is clicked`() {
        show()

        composeRule.onNodeWithText("Accept").performClick()

        composeRule.onNodeWithTag("labeled")
            .assertIsOn()
            .assert(SemanticsMatcher.expectValue(SemanticsProperties.Role, Role.Checkbox))
    }

    @Test
    fun `disabled checkboxes ignore clicks`() {
        show(enabled = false)

        composeRule.onNodeWithTag("bare").performClick()
        composeRule.onNodeWithText("Accept").performClick()

        assertEquals(0, changes)
        composeRule.onNodeWithTag("bare").assertIsNotEnabled()
        composeRule.onNodeWithTag("labeled").assertIsNotEnabled()
    }

    @Test
    fun `checkboxes keep a 48dp touch target`() {
        show()

        composeRule.onNodeWithTag("bare").assertTouchHeightIsEqualTo(48.dp).assertTouchWidthIsEqualTo(48.dp)
        composeRule.onNodeWithTag("labeled").assertTouchHeightIsEqualTo(48.dp)
    }
}
