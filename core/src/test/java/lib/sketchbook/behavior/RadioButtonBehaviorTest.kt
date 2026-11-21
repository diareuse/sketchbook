package lib.sketchbook.behavior

import androidx.compose.foundation.layout.Column
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
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.assertIsNotSelected
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.assertTouchHeightIsEqualTo
import androidx.compose.ui.test.assertTouchWidthIsEqualTo
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.unit.dp
import lib.sketchbook.SketchbookRadioButton
import lib.sketchbook.theme.Theme
import org.junit.Assert.assertEquals
import org.junit.Test

class RadioButtonBehaviorTest : BehaviorTest() {

    private var chosen by mutableIntStateOf(0)
    private var clicks = 0

    private fun show(enabled: Boolean = true) {
        composeRule.setContent {
            Theme(darkTheme = false, dynamicColor = false) {
                Column {
                    SketchbookRadioButton(
                        selected = chosen == 0,
                        onClick = { clicks++; chosen = 0 },
                        modifier = Modifier.testTag("bare"),
                        enabled = enabled
                    )
                    SketchbookRadioButton(
                        selected = chosen == 1,
                        onClick = { clicks++; chosen = 1 },
                        modifier = Modifier.testTag("labeled"),
                        label = { Text("Option") },
                        enabled = enabled
                    )
                }
            }
        }
        composeRule.waitForIdle()
    }

    @Test
    fun `radio buttons select exclusively and expose the radio button role`() {
        show()
        composeRule.onNodeWithTag("bare").assertIsSelected()
        composeRule.onNodeWithTag("labeled").assertIsNotSelected()

        composeRule.onNodeWithTag("labeled").performClick()

        composeRule.onNodeWithTag("labeled")
            .assertIsSelected()
            .assert(SemanticsMatcher.expectValue(SemanticsProperties.Role, Role.RadioButton))
        composeRule.onNodeWithTag("bare")
            .assertIsNotSelected()
            .assert(SemanticsMatcher.expectValue(SemanticsProperties.Role, Role.RadioButton))
    }

    @Test
    fun `labeled radio button selects when its label is clicked`() {
        show()

        composeRule.onNodeWithText("Option").performClick()

        composeRule.onNodeWithTag("labeled").assertIsSelected()
    }

    @Test
    fun `disabled radio buttons ignore clicks`() {
        show(enabled = false)

        composeRule.onNodeWithTag("labeled").performClick()
        composeRule.onNodeWithText("Option").performClick()

        assertEquals(0, clicks)
        composeRule.onNodeWithTag("labeled").assertIsNotEnabled()
        composeRule.onNodeWithTag("bare").assertIsNotEnabled()
    }

    @Test
    fun `radio buttons keep a 48dp touch target`() {
        show()

        composeRule.onNodeWithTag("bare").assertTouchHeightIsEqualTo(48.dp).assertTouchWidthIsEqualTo(48.dp)
        composeRule.onNodeWithTag("labeled").assertTouchHeightIsEqualTo(48.dp)
    }
}
