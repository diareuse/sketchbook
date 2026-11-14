package lib.sketchbook.behavior

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PixelMap
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.toPixelMap
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.getBoundsInRoot
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.performClick
import androidx.compose.ui.unit.dp
import lib.sketchbook.SketchbookCheckbox
import lib.sketchbook.SketchbookRadioButton
import lib.sketchbook.distance
import lib.sketchbook.opaque
import lib.sketchbook.theme.Theme
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.robolectric.annotation.Config

@Config(sdk = [33])
class CheckboxVisualBehaviorTest : BehaviorTest() {

    private var checked by mutableStateOf(false)
    private var primary = Color.Unspecified
    private var onPrimary = Color.Unspecified

    @Test
    fun `unchecked checkbox box is a squircle of the theme small button shape`() {
        show(false, enabled = true)

        assertMaskMatches(maskMismatches("subject", "reference"))
    }

    @Test
    fun `checked checkbox box is a squircle of the theme small button shape`() {
        show(true, enabled = true)

        assertMaskMatches(maskMismatches("subject", "reference"))
    }

    @Test
    fun `checked checkbox fills the box with the primary emphasis colour`() {
        show(true, enabled = true)
        val image = frame()
        val c = centre("subject")

        assertTrue(distance(image.opaque(c.first - 20, c.second), primary) <= 12)
    }

    @Test
    fun `checked checkbox draws a thick check mark in the on primary colour`() {
        show(true, enabled = true)

        val strokePixels = count(frame(), "subject", onPrimary)

        assertTrue("check pixels $strokePixels", strokePixels >= 150)
    }

    @Test
    fun `unchecked checkbox draws no check mark and no border`() {
        show(false, enabled = true)
        val image = frame()
        val c = centre("subject")

        assertEquals(0, count(image, "subject", onPrimary))
        assertTrue(distance(image.opaque(c.first - 23, c.second), image.opaque(c.first - 16, c.second)) <= 12)
        assertTrue(distance(image.opaque(c.first, c.second - 23), image.opaque(c.first, c.second - 16)) <= 12)
    }

    @Test
    fun `the check mark draws on progressively`() {
        composeRule.mainClock.autoAdvance = false
        show(false, enabled = true)
        composeRule.onNodeWithTag("subject").performClick()
        composeRule.mainClock.advanceTimeBy(70)
        val partial = count(frame(), "subject", onPrimary)
        composeRule.mainClock.advanceTimeBy(1_000)
        val full = count(frame(), "subject", onPrimary)

        assertTrue("partial $partial full $full", partial in 1 until full)
    }

    @Test
    fun `checkbox exposes the checkbox role`() {
        show(false, enabled = true)

        composeRule.onNodeWithTag("subject").assert(SemanticsMatcher.expectValue(SemanticsProperties.Role, Role.Checkbox))
    }

    private fun show(initial: Boolean, enabled: Boolean) {
        checked = initial
        composeRule.setContent {
            Theme(darkTheme = false, dynamicColor = false) {
                primary = Theme.color.emphasis.primary
                onPrimary = Theme.color.overlay.primary
                Row(Modifier.background(Color.Red)) {
                    SketchbookCheckbox(checked, { checked = it }, Modifier.testTag("subject"), enabled = enabled)
                    Box(Modifier.size(48.dp).testTag("reference")) {
                        Box(
                            Modifier
                                .align(androidx.compose.ui.Alignment.Center)
                                .size(24.dp)
                                .clip(Theme.container.buttonSmall)
                                .background(Color.Black)
                        )
                    }
                }
            }
        }
        composeRule.waitForIdle()
    }

    private fun frame(): PixelMap = composeRule.onRoot().captureToImage().toPixelMap()

    private fun centre(tag: String): Pair<Int, Int> {
        val b = composeRule.onNodeWithTag(tag).getBoundsInRoot()
        return ((b.left + b.right).value).toInt() to ((b.top + b.bottom).value).toInt()
    }

    private fun count(image: PixelMap, tag: String, color: Color): Int {
        val c = centre(tag)
        var n = 0
        for (y in c.second - 26..c.second + 26) for (x in c.first - 26..c.first + 26) {
            if (distance(image.opaque(x, y), color) <= StrokeTolerance) n++
        }
        return n
    }

    private fun maskMismatches(subject: String, reference: String): List<String> {
        val image = frame()
        val s = centre(subject)
        val r = centre(reference)
        val failures = mutableListOf<String>()
        for (dy in -26..26) for (dx in -26..26) {
            val subject = distance(image.opaque(s.first + dx, s.second + dy), Color.Red)
            val reference = distance(image.opaque(r.first + dx, r.second + dy), Color.Red)
            if (reference > SolidCoverage && subject <= 3) failures += "($dx,$dy) hole"
            if (subject > 40 && reference <= 3) failures += "($dx,$dy) spill"
        }
        return failures
    }
}

@Config(sdk = [33])
class RadioVisualBehaviorTest : BehaviorTest() {

    private var selected by mutableStateOf(false)
    private var primary = Color.Unspecified
    private var onPrimary = Color.Unspecified

    @Test
    fun `unselected radio is a plain disc`() {
        show(false)

        assertMaskMatches(maskMismatches())
    }

    @Test
    fun `selected radio is the same disc`() {
        show(true)

        assertMaskMatches(maskMismatches())
    }

    @Test
    fun `selected radio fills the disc with the primary emphasis colour and a contrasting dot`() {
        show(true)
        val image = frame()
        val c = centre("subject")

        assertTrue(distance(image.opaque(c.first, c.second), onPrimary) <= 12)
        assertTrue(distance(image.opaque(c.first + 18, c.second), primary) <= 12)
        assertTrue(distance(image.opaque(c.first, c.second - 18), primary) <= 12)
    }

    @Test
    fun `unselected radio has no dot and no border`() {
        show(false)
        val image = frame()
        val c = centre("subject")

        assertTrue(distance(image.opaque(c.first, c.second), image.opaque(c.first + 14, c.second)) <= 12)
        assertTrue(distance(image.opaque(c.first + 23, c.second), image.opaque(c.first + 16, c.second)) <= 12)
    }

    @Test
    fun `the dot scales in progressively`() {
        composeRule.mainClock.autoAdvance = false
        show(false)
        composeRule.onNodeWithTag("subject").performClick()
        composeRule.mainClock.advanceTimeBy(70)
        val partial = dotPixels()
        composeRule.mainClock.advanceTimeBy(1_000)
        val full = dotPixels()

        assertTrue("partial $partial full $full", partial in 1 until full)
    }

    @Test
    fun `radio exposes the radio button role`() {
        show(false)

        composeRule.onNodeWithTag("subject").assert(SemanticsMatcher.expectValue(SemanticsProperties.Role, Role.RadioButton))
    }

    private fun show(initial: Boolean) {
        selected = initial
        composeRule.setContent {
            Theme(darkTheme = false, dynamicColor = false) {
                primary = Theme.color.emphasis.primary
                onPrimary = Theme.color.overlay.primary
                Row(Modifier.background(Color.Red)) {
                    SketchbookRadioButton(selected, { selected = true }, Modifier.testTag("subject"))
                    Box(Modifier.size(48.dp).testTag("reference")) {
                        Box(
                            Modifier
                                .align(androidx.compose.ui.Alignment.Center)
                                .size(24.dp)
                                .clip(CircleShape)
                                .background(Color.Black)
                        )
                    }
                }
            }
        }
        composeRule.waitForIdle()
    }

    private fun frame(): PixelMap = composeRule.onRoot().captureToImage().toPixelMap()

    private fun centre(tag: String): Pair<Int, Int> {
        val b = composeRule.onNodeWithTag(tag).getBoundsInRoot()
        return ((b.left + b.right).value).toInt() to ((b.top + b.bottom).value).toInt()
    }

    private fun dotPixels(): Int {
        val image = frame()
        val c = centre("subject")
        var n = 0
        for (y in c.second - 12..c.second + 12) for (x in c.first - 12..c.first + 12) {
            if (distance(image.opaque(x, y), onPrimary) <= StrokeTolerance) n++
        }
        return n
    }

    private fun maskMismatches(): List<String> {
        val image = frame()
        val s = centre("subject")
        val r = centre("reference")
        val failures = mutableListOf<String>()
        for (dy in -26..26) for (dx in -26..26) {
            val subject = distance(image.opaque(s.first + dx, s.second + dy), Color.Red)
            val reference = distance(image.opaque(r.first + dx, r.second + dy), Color.Red)
            if (reference > SolidCoverage && subject <= 3) failures += "($dx,$dy) hole"
            if (subject > 40 && reference <= 3) failures += "($dx,$dy) spill"
        }
        return failures
    }
}

private const val StrokeTolerance = 140
private const val SolidCoverage = 250
private const val AntiAliasingTolerance = 4

private fun assertMaskMatches(mismatches: List<String>) =
    assertTrue("${mismatches.size} mismatches ${mismatches.take(6)}", mismatches.size <= AntiAliasingTolerance)
