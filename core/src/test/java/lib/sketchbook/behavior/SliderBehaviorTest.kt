package lib.sketchbook.behavior

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.ProgressBarRangeInfo
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.assertRangeInfoEquals
import androidx.compose.ui.test.assertTouchHeightIsEqualTo
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performSemanticsAction
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.swipeRight
import lib.sketchbook.SketchbookSlider
import lib.sketchbook.theme.Theme
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import androidx.compose.ui.unit.dp

class SliderBehaviorTest : BehaviorTest() {

    private var plain by mutableFloatStateOf(.2f)
    private var stepped by mutableFloatStateOf(0f)
    private var ranged by mutableFloatStateOf(10f)
    private var finished = 0

    private fun show(enabled: Boolean = true) {
        composeRule.setContent {
            Theme(darkTheme = false, dynamicColor = false) {
                Column {
                    SketchbookSlider(
                        value = plain,
                        onValueChange = { plain = it },
                        modifier = Modifier.fillMaxWidth().testTag("plain"),
                        onValueChangeFinished = { finished++ },
                        enabled = enabled
                    )
                    SketchbookSlider(
                        value = stepped,
                        onValueChange = { stepped = it },
                        modifier = Modifier.fillMaxWidth().testTag("stepped"),
                        steps = 3,
                        enabled = enabled
                    )
                    SketchbookSlider(
                        value = ranged,
                        onValueChange = { ranged = it },
                        modifier = Modifier.fillMaxWidth().testTag("ranged"),
                        valueRange = 10f..50f,
                        enabled = enabled
                    )
                }
            }
        }
        composeRule.waitForIdle()
    }

    @Test
    fun `slider reports its value range and step count to accessibility`() {
        show()

        composeRule.onNodeWithTag("plain").assertRangeInfoEquals(ProgressBarRangeInfo(.2f, 0f..1f, 0))
        composeRule.onNodeWithTag("stepped").assertRangeInfoEquals(ProgressBarRangeInfo(0f, 0f..1f, 3))
        composeRule.onNodeWithTag("ranged").assertRangeInfoEquals(ProgressBarRangeInfo(10f, 10f..50f, 0))
    }

    @Test
    fun `slider value follows an accessibility set progress action`() {
        show()

        composeRule.onNodeWithTag("plain").performSemanticsAction(SemanticsActions.SetProgress) { it(.7f) }

        assertEquals(.7f, plain, 0.001f)
        composeRule.onNodeWithTag("plain").assertRangeInfoEquals(ProgressBarRangeInfo(.7f, 0f..1f, 0))
    }

    @Test
    fun `stepped slider snaps to the nearest step`() {
        show()

        composeRule.onNodeWithTag("stepped").performSemanticsAction(SemanticsActions.SetProgress) { it(.6f) }

        assertEquals(.5f, stepped, 0.001f)
    }

    @Test
    fun `slider honors a custom value range`() {
        show()

        composeRule.onNodeWithTag("ranged").performSemanticsAction(SemanticsActions.SetProgress) { it(30f) }

        assertEquals(30f, ranged, 0.001f)
    }

    @Test
    fun `dragging the slider changes the value and reports completion`() {
        show()

        composeRule.onNodeWithTag("plain").performTouchInput { swipeRight() }

        assertTrue("value $plain", plain > .8f)
        assertEquals(1, finished)
    }

    @Test
    fun `disabled slider ignores drags and reports disabled`() {
        show(enabled = false)

        composeRule.onNodeWithTag("plain").performTouchInput { swipeRight() }

        assertEquals(.2f, plain, 0.001f)
        composeRule.onNodeWithTag("plain").assertIsNotEnabled()
    }

    @Test
    fun `enabled slider reports enabled and keeps a 48dp touch target`() {
        show()

        composeRule.onNodeWithTag("plain").assertIsEnabled().assertTouchHeightIsEqualTo(48.dp)
    }
}
