package lib.sketchbook.behavior

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toPixelMap
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.assertIsNotSelected
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.assertTouchHeightIsEqualTo
import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.getBoundsInRoot
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.performClick
import androidx.compose.ui.unit.dp
import lib.sketchbook.SketchbookChip
import lib.sketchbook.SketchbookFilterChip
import lib.sketchbook.distance
import lib.sketchbook.opaque
import lib.sketchbook.theme.Theme
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.robolectric.annotation.Config

class ChipBehaviorTest : BehaviorTest() {

    private var chipClicks = 0
    private var selected by mutableStateOf(false)

    private fun show(enabled: Boolean = true) {
        composeRule.setContent {
            Theme(darkTheme = false, dynamicColor = false) {
                Column {
                    SketchbookChip(
                        onClick = { chipClicks++ },
                        label = { Text("Chip") },
                        modifier = Modifier.testTag("chip"),
                        enabled = enabled
                    )
                    SketchbookFilterChip(
                        selected = selected,
                        onClick = { selected = !selected },
                        label = { Text("Filter") },
                        modifier = Modifier.testTag("filter"),
                        enabled = enabled
                    )
                }
            }
        }
        composeRule.waitForIdle()
    }

    @Test
    fun `chip invokes onClick and exposes the button role`() {
        show()

        composeRule.onNodeWithTag("chip").performClick()

        assertEquals(1, chipClicks)
        composeRule.onNodeWithTag("chip")
            .assertIsEnabled()
            .assert(SemanticsMatcher.expectValue(SemanticsProperties.Role, Role.Button))
    }

    @Test
    fun `filter chip toggles its selected state and exposes the checkbox role`() {
        show()
        composeRule.onNodeWithTag("filter").assertIsNotSelected()

        composeRule.onNodeWithTag("filter").performClick()

        composeRule.onNodeWithTag("filter")
            .assertIsSelected()
            .assert(SemanticsMatcher.expectValue(SemanticsProperties.Role, Role.Checkbox))
        composeRule.onNodeWithTag("filter").performClick()
        composeRule.onNodeWithTag("filter").assertIsNotSelected()
    }

    @Test
    fun `disabled chips ignore clicks and report disabled`() {
        show(enabled = false)

        composeRule.onNodeWithTag("chip").performClick()
        composeRule.onNodeWithTag("filter").performClick()

        assertEquals(0, chipClicks)
        composeRule.onNodeWithTag("chip").assertIsNotEnabled()
        composeRule.onNodeWithTag("filter").assertIsNotEnabled().assertIsNotSelected()
    }

    @Test
    fun `chips keep a 48dp touch target`() {
        show()

        composeRule.onNodeWithTag("chip").assertTouchHeightIsEqualTo(48.dp)
        composeRule.onNodeWithTag("filter").assertTouchHeightIsEqualTo(48.dp)
    }

    @Test
    @Config(sdk = [26])
    fun `chip container is a translucent tint over the backdrop`() {
        composeRule.setContent {
            Theme(darkTheme = false, dynamicColor = false) {
                Row {
                    Box(Modifier.background(Color.Red)) {
                        SketchbookChip({}, { Text("Chip") }, Modifier.testTag("a"))
                    }
                    Box(Modifier.background(Color.Blue)) {
                        SketchbookChip({}, { Text("Chip") }, Modifier.testTag("b"))
                    }
                }
            }
        }
        composeRule.waitForIdle()
        val image = composeRule.onRoot().captureToImage().toPixelMap()
        val a = composeRule.onNodeWithTag("a").getBoundsInRoot()
        val b = composeRule.onNodeWithTag("b").getBoundsInRoot()
        val y = ((a.top + a.bottom).value).toInt()

        assertTrue(distance(image.opaque((a.left.value * 2 + 10).toInt(), y), image.opaque((b.left.value * 2 + 10).toInt(), y)) > 30)
    }

    @Test
    @Config(sdk = [26])
    fun `selected filter chip is visually distinct from unselected`() {
        composeRule.setContent {
            Theme(darkTheme = false, dynamicColor = false) {
                Row {
                    Box(Modifier.background(Color.White)) {
                        SketchbookFilterChip(true, {}, { Text("Filter") }, Modifier.testTag("on"))
                    }
                    Box(Modifier.background(Color.White)) {
                        SketchbookFilterChip(false, {}, { Text("Filter") }, Modifier.testTag("off"))
                    }
                }
            }
        }
        composeRule.waitForIdle()
        val image = composeRule.onRoot().captureToImage().toPixelMap()
        val on = composeRule.onNodeWithTag("on").getBoundsInRoot()
        val off = composeRule.onNodeWithTag("off").getBoundsInRoot()
        val y = ((on.top + on.bottom).value).toInt()

        assertTrue(distance(image.opaque((on.left.value * 2 + 10).toInt(), y), image.opaque((off.left.value * 2 + 10).toInt(), y)) > 20)
    }
}
