package lib.sketchbook.behavior

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Text
import androidx.compose.foundation.layout.Column
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
import androidx.compose.ui.test.assertHeightIsEqualTo
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.assertTouchHeightIsEqualTo
import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.getBoundsInRoot
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.performClick
import androidx.compose.ui.unit.dp
import lib.sketchbook.SketchbookButton
import lib.sketchbook.SketchbookGlassButton
import lib.sketchbook.SketchbookOutlinedButton
import lib.sketchbook.SketchbookTonalButton
import lib.sketchbook.distance
import lib.sketchbook.opaque
import lib.sketchbook.theme.Theme
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.robolectric.annotation.Config

class ButtonBehaviorTest : BehaviorTest() {

    private val variants: List<Pair<String, @Composable (() -> Unit, Boolean, Modifier) -> Unit>> = listOf(
        "filled" to { onClick, enabled, modifier ->
            SketchbookButton(onClick, modifier, enabled) { Text("Go") }
        },
        "tonal" to { onClick, enabled, modifier ->
            SketchbookTonalButton(onClick, modifier, enabled) { Text("Go") }
        },
        "outlined" to { onClick, enabled, modifier ->
            SketchbookOutlinedButton(onClick, modifier, enabled) { Text("Go") }
        },
        "glass" to { onClick, enabled, modifier ->
            SketchbookGlassButton(onClick, modifier, enabled) { Text("Go") }
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
    fun `every button variant invokes onClick when enabled`() {
        showAll()
        variants.forEach { (name, _) ->
            composeRule.onNodeWithTag(name).performClick()
            assertEquals(name, 1, clicks[name])
        }
    }

    @Test
    fun `every disabled button variant ignores clicks and reports disabled`() {
        showAll(enabled = false)
        variants.forEach { (name, _) ->
            composeRule.onNodeWithTag(name).performClick()
            composeRule.onNodeWithTag(name).assertIsNotEnabled()
            assertEquals(name, null, clicks[name])
        }
    }

    @Test
    fun `every button variant exposes the button role and enabled state`() {
        showAll()
        variants.forEach { (name, _) ->
            composeRule.onNodeWithTag(name)
                .assertIsEnabled()
                .assert(SemanticsMatcher.expectValue(SemanticsProperties.Role, Role.Button))
        }
    }

    @Test
    fun `every button variant keeps a 48dp touch target`() {
        showAll()
        variants.forEach { (name, _) ->
            composeRule.onNodeWithTag(name).assertTouchHeightIsEqualTo(48.dp)
        }
    }

    @Test
    fun `glass button is as tall as the other buttons`() {
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
    fun `every button variant is a translucent glass over the backdrop`() {
        composeRule.setContent {
            Theme(darkTheme = false, dynamicColor = false) {
                Column {
                    variants.indices.forEach { index ->
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
        variants.indices.forEach { index ->
            val a = composeRule.onNodeWithTag("a$index").getBoundsInRoot()
            val b = composeRule.onNodeWithTag("b$index").getBoundsInRoot()
            val y = ((a.top + a.bottom).value).toInt()
            val ax = (a.left.value * 2 + 14).toInt()
            val bx = (b.left.value * 2 + 14).toInt()
            assertTrue("variant $index", distance(image.opaque(ax, y), image.opaque(bx, y)) > 30)
        }
    }
}
