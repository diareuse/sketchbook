package lib.sketchbook.behavior

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.assertIsNotSelected
import androidx.compose.ui.test.assertIsOff
import androidx.compose.ui.test.assertIsOn
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.assertTouchHeightIsEqualTo
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.unit.dp
import lib.sketchbook.SketchbookMultiChoiceSegmentedButton
import lib.sketchbook.SketchbookSegmentedButton
import lib.sketchbook.theme.Theme
import org.junit.Assert.assertEquals
import org.junit.Test

class SegmentedButtonBehaviorTest : BehaviorTest() {

    private var single by mutableIntStateOf(0)
    private var multi by mutableStateOf(setOf(0))
    private var changes = 0

    private fun show(enabled: Boolean = true) {
        composeRule.setContent {
            Theme(darkTheme = false, dynamicColor = false) {
                Column {
                    SketchbookSegmentedButton(
                        selectedIndex = single,
                        onSelectedIndexChange = { changes++; single = it },
                        count = 3,
                        label = { Text("S$it") },
                        enabled = enabled
                    )
                    SketchbookMultiChoiceSegmentedButton(
                        selectedIndices = multi,
                        onSelectedIndicesChange = { changes++; multi = it },
                        count = 3,
                        label = { Text("M$it") },
                        enabled = enabled
                    )
                }
            }
        }
        composeRule.waitForIdle()
    }

    @Test
    fun `single choice selects exactly one segment and exposes the radio button role`() {
        show()
        composeRule.onNodeWithText("S0").assertIsSelected()

        composeRule.onNodeWithText("S2").performClick()

        composeRule.onNodeWithText("S2")
            .assertIsSelected()
            .assert(SemanticsMatcher.expectValue(SemanticsProperties.Role, Role.RadioButton))
        composeRule.onNodeWithText("S0").assertIsNotSelected()
        composeRule.onNodeWithText("S1").assertIsNotSelected()
    }

    @Test
    fun `multi choice toggles segments independently and exposes their toggle state`() {
        show()
        composeRule.onNodeWithText("M0").assertIsOn()
        composeRule.onNodeWithText("M1").assertIsOff()

        composeRule.onNodeWithText("M1").performClick()
        composeRule.onNodeWithText("M0").performClick()

        composeRule.onNodeWithText("M1").assertIsOn()
        composeRule.onNodeWithText("M0").assertIsOff()
        assertEquals(setOf(1), multi)
    }

    @Test
    fun `disabled segmented buttons ignore clicks and report disabled`() {
        show(enabled = false)

        composeRule.onNodeWithText("S1").performClick()
        composeRule.onNodeWithText("M1").performClick()

        assertEquals(0, changes)
        composeRule.onNodeWithText("S1").assertIsNotEnabled()
        composeRule.onNodeWithText("M1").assertIsNotEnabled()
    }

    @Test
    fun `segments keep a 48dp touch target`() {
        show()

        composeRule.onNodeWithText("S1").assertTouchHeightIsEqualTo(48.dp)
        composeRule.onNodeWithText("M1").assertTouchHeightIsEqualTo(48.dp)
    }
}
