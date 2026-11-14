package lib.sketchbook.behavior

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toPixelMap
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.assertContentDescriptionEquals
import androidx.compose.ui.test.assertHeightIsEqualTo
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.assertTouchHeightIsEqualTo
import androidx.compose.ui.test.assertTouchWidthIsEqualTo
import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.getBoundsInRoot
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.performClick
import androidx.compose.ui.unit.dp
import lib.sketchbook.SketchbookFilledIconButton
import lib.sketchbook.SketchbookGlassIconButton
import lib.sketchbook.SketchbookIconButton
import lib.sketchbook.SketchbookTonalIconButton
import lib.sketchbook.distance
import lib.sketchbook.opaque
import lib.sketchbook.theme.Theme
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.robolectric.annotation.Config

class IconButtonBehaviorTest : BehaviorTest() {

    private val variants: List<Pair<String, @Composable (() -> Unit, Boolean, Modifier) -> Unit>> = listOf(
        "plain" to { onClick, enabled, modifier ->
            SketchbookIconButton(onClick, "plain label", modifier, enabled) { Box(Modifier.size(24.dp)) }
        },
        "filled" to { onClick, enabled, modifier ->
            SketchbookFilledIconButton(onClick, "filled label", modifier, enabled) { Box(Modifier.size(24.dp)) }
        },
        "tonal" to { onClick, enabled, modifier ->
            SketchbookTonalIconButton(onClick, "tonal label", modifier, enabled) { Box(Modifier.size(24.dp)) }
        },
        "glass" to { onClick, enabled, modifier ->
            SketchbookGlassIconButton(onClick, "glass label", modifier, enabled) { Box(Modifier.size(24.dp)) }
        }
    )

    private val clicks = mutableStateMapOf<String, Int>()

    private fun showAll(enabled: Boolean = true) {
        composeRule.setContent {
            Theme(darkTheme = false, dynamicColor = false) {
                Column {
                    variants.forEach { (name, button) ->
                        button({ clicks[name] = (clicks[name] ?: 0) + 1 }, enabled, Modifier.testTag(name))
                    }
                }
            }
        }
        composeRule.waitForIdle()
    }

    @Test
    fun `every icon button variant invokes onClick when enabled`() {
        showAll()
        variants.forEach { (name, _) ->
            composeRule.onNodeWithTag(name).performClick()
            assertEquals(name, 1, clicks[name])
        }
    }

    @Test
    fun `every disabled icon button variant ignores clicks and reports disabled`() {
        showAll(enabled = false)
        variants.forEach { (name, _) ->
            composeRule.onNodeWithTag(name).performClick()
            composeRule.onNodeWithTag(name).assertIsNotEnabled()
            assertEquals(name, null, clicks[name])
        }
    }

    @Test
    fun `every icon button variant exposes role button and its content description`() {
        showAll()
        variants.forEach { (name, _) ->
            composeRule.onNodeWithTag(name)
                .assertIsEnabled()
                .assertContentDescriptionEquals("$name label")
                .assert(SemanticsMatcher.expectValue(SemanticsProperties.Role, Role.Button))
        }
    }

    @Test
    fun `every icon button variant keeps a 48dp touch target`() {
        showAll()
        variants.forEach { (name, _) ->
            composeRule.onNodeWithTag(name).assertTouchHeightIsEqualTo(48.dp).assertTouchWidthIsEqualTo(48.dp)
        }
    }

    @Test
    fun `glass icon button is as tall as the other icon buttons`() {
        composeRule.setContent {
            Theme(darkTheme = false, dynamicColor = false) {
                Box(Modifier.testTag("host")) { variants[3].second({}, true, Modifier) }
            }
        }
        composeRule.waitForIdle()
        composeRule.onNodeWithTag("host").assertHeightIsEqualTo(48.dp)
    }

    @Test
    @Config(sdk = [26])
    fun `glass and tonal icon buttons are translucent over the backdrop`() {
        composeRule.setContent {
            Theme(darkTheme = false, dynamicColor = false) {
                Column {
                    listOf(2, 3).forEach { index ->
                        Row {
                            Box(Modifier.background(Color.Red)) {
                                variants[index].second({}, true, Modifier.testTag("a$index"))
                            }
                            Box(Modifier.background(Color.Blue)) {
                                variants[index].second({}, true, Modifier.testTag("b$index"))
                            }
                        }
                    }
                }
            }
        }
        composeRule.waitForIdle()
        val image = composeRule.onRoot().captureToImage().toPixelMap()
        listOf(2, 3).forEach { index ->
            val a = composeRule.onNodeWithTag("a$index").getBoundsInRoot()
            val b = composeRule.onNodeWithTag("b$index").getBoundsInRoot()
            val y = ((a.top + a.bottom).value).toInt()
            val ax = ((a.left + a.right).value + 24).toInt()
            val bx = ((b.left + b.right).value + 24).toInt()
            assertTrue("variant $index", distance(image.opaque(ax, y), image.opaque(bx, y)) > 30)
        }
    }
}
