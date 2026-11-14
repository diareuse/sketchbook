package lib.sketchbook.behavior

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toPixelMap
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertHasNoClickAction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.assertTouchHeightIsEqualTo
import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.performClick
import androidx.compose.ui.unit.dp
import lib.sketchbook.SketchbookCard
import lib.sketchbook.StripedBackdrop
import lib.sketchbook.distance
import lib.sketchbook.opaque
import lib.sketchbook.redSpread
import lib.sketchbook.theme.Theme
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.robolectric.annotation.Config

class CardBehaviorTest : BehaviorTest() {

    private var clicks = 0

    private fun showStatic() {
        composeRule.setContent {
            Theme(darkTheme = false, dynamicColor = false) {
                SketchbookCard(Modifier.testTag("card")) { Text("Body") }
            }
        }
        composeRule.waitForIdle()
    }

    private fun showClickable(enabled: Boolean = true, content: String = "Body") {
        composeRule.setContent {
            Theme(darkTheme = false, dynamicColor = false) {
                SketchbookCard(
                    onClick = { clicks++ },
                    modifier = Modifier.testTag("card"),
                    enabled = enabled
                ) { Text(content) }
            }
        }
        composeRule.waitForIdle()
    }

    private fun showGlass(glow: Boolean = false, rim: Boolean = false, source: Boolean = true) {
        composeRule.mainClock.autoAdvance = false
        composeRule.setContent {
            Theme(darkTheme = false, dynamicColor = false) {
                StripedBackdrop(Modifier.size(360.dp, 200.dp), source = source) {
                    SketchbookCard(
                        modifier = Modifier.padding(20.dp).fillMaxWidth().height(120.dp),
                        useGlow = glow,
                        specularRim = rim
                    ) { Text("Body") }
                }
            }
        }
        composeRule.mainClock.advanceTimeBy(100)
        composeRule.waitForIdle()
    }

    private fun pixels() = composeRule.onRoot().captureToImage().toPixelMap()

    @Test
    fun `static card shows its content and is not clickable`() {
        showStatic()

        composeRule.onNodeWithText("Body").assertIsDisplayed()
        composeRule.onNodeWithTag("card").assertHasNoClickAction()
    }

    @Test
    fun `clickable card invokes onClick and exposes the button role`() {
        showClickable()

        composeRule.onNodeWithTag("card").performClick()

        assertEquals(1, clicks)
        composeRule.onNodeWithTag("card")
            .assertHasClickAction()
            .assertIsEnabled()
            .assert(SemanticsMatcher.expectValue(SemanticsProperties.Role, Role.Button))
    }

    @Test
    fun `disabled clickable card ignores clicks`() {
        showClickable(enabled = false)

        composeRule.onNodeWithTag("card").performClick()

        assertEquals(0, clicks)
        composeRule.onNodeWithTag("card").assertIsNotEnabled()
    }

    @Test
    fun `clickable card keeps a 48dp touch target around small content`() {
        showClickable(content = "x")

        composeRule.onNodeWithTag("card").assertTouchHeightIsEqualTo(48.dp)
    }

    @Test
    @Config(sdk = [26])
    fun `card tints the content behind it on android 8`() {
        showGlass()

        assertTrue(distance(Color.Red, pixels().opaque(80, 100)) > 60)
    }

    @Test
    @Config(sdk = [26])
    fun `card leaves the content outside its bounds untouched`() {
        showGlass()

        assertEquals(Color.Red, pixels().opaque(10, 100))
    }

    @Test
    @Config(sdk = [31])
    fun `card blurs the backdrop on android 12`() {
        showGlass()

        val spread = pixels().redSpread(100, 100..620)
        assertTrue("spread $spread", spread < 120)
    }

    @Test
    @Config(sdk = [31])
    fun `card without a glass source keeps the backdrop sharp`() {
        showGlass(source = false)

        assertTrue(pixels().redSpread(100, 100..620) > 100)
    }

    private fun showPair(left: Pair<Boolean, Boolean>, right: Pair<Boolean, Boolean>) {
        composeRule.mainClock.autoAdvance = false
        composeRule.setContent {
            Theme(darkTheme = false, dynamicColor = false) {
                Row(Modifier.size(360.dp, 100.dp).background(Color.Black).padding(10.dp)) {
                    SketchbookCard(Modifier.size(150.dp, 80.dp), useGlow = left.first, specularRim = left.second) {}
                    Spacer(Modifier.width(30.dp))
                    SketchbookCard(Modifier.size(150.dp, 80.dp), useGlow = right.first, specularRim = right.second) {}
                }
            }
        }
        composeRule.mainClock.advanceTimeBy(100)
        composeRule.waitForIdle()
    }

    private fun rimDifference(): Int {
        val image = pixels()
        return distance(image.opaque(180, 20), image.opaque(180 + 360, 20))
    }

    private fun strongestRimDifference(): Int {
        val image = pixels()
        var strongest = 0
        for (y in 20..40 step 2) for (x in 30..310 step 2) {
            strongest = maxOf(strongest, distance(image.opaque(x, y), image.opaque(x + 360, y)))
        }
        return strongest
    }

    @Test
    @Config(sdk = [26])
    fun `glow brightens the card rim`() {
        showPair(false to false, true to false)

        assertTrue("rim difference ${rimDifference()}", rimDifference() > 6)
    }

    @Test
    @Config(sdk = [26])
    fun `specular rim draws a highlight along the card border`() {
        showPair(false to false, false to true)

        assertTrue("rim difference ${strongestRimDifference()}", strongestRimDifference() > 20)
    }

    @Test
    @Config(sdk = [26])
    fun `cards with identical settings render identically`() {
        showPair(false to false, false to false)

        assertEquals(0, rimDifference())
    }
}
