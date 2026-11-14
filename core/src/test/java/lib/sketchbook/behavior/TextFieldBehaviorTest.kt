package lib.sketchbook.behavior

import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.getBoundsInRoot
import androidx.compose.ui.unit.Dp
import lib.sketchbook.SketchbookTextField
import lib.sketchbook.theme.Theme
import org.junit.Assert.assertTrue
import org.junit.Test

class TextFieldBehaviorTest : BehaviorTest() {

    private fun heightOf(value: String, singleLine: Boolean): Dp {
        composeRule.setContent {
            Theme(darkTheme = false) {
                SketchbookTextField(
                    value = value,
                    onValueChange = {},
                    modifier = Modifier.testTag("field"),
                    singleLine = singleLine
                )
            }
        }
        composeRule.waitForIdle()
        return composeRule.onNodeWithTag("field").getBoundsInRoot().let { it.bottom - it.top }
    }

    @Test
    fun `multi line text field grows with its content`() {
        val tall = heightOf("a\nb\nc\nd", singleLine = false)

        assertTrue("height $tall should exceed one line", tall.value > 80f)
    }

    @Test
    fun `single line text field keeps one line for multi line content`() {
        val single = heightOf("a\nb\nc\nd", singleLine = true)

        assertTrue("height $single should be one line", single.value < 80f)
    }
}
