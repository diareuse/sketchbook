package lib.sketchbook.behavior

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.getBoundsInRoot
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.height
import androidx.compose.ui.unit.dp
import lib.sketchbook.SketchbookButton
import lib.sketchbook.SketchbookGlassButton
import lib.sketchbook.SketchbookGlassIconButton
import lib.sketchbook.SketchbookIconButton
import lib.sketchbook.theme.Theme
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ControlSizeBehaviorTest : BehaviorTest() {

    @Test
    fun `glass and plain icon buttons side by side are at least 48dp tall and share a top`() {
        showRow()

        assertAlignedAtLeast48("glassIcon", "plainIcon")
    }

    @Test
    fun `glass and plain buttons side by side are at least 48dp tall and share a top`() {
        showRow()

        assertAlignedAtLeast48("glassButton", "plainButton")
    }

    @Test
    fun `all four controls in one row share a top`() {
        showRow()

        assertAlignedAtLeast48("glassIcon", "plainIcon", "glassButton", "plainButton")
    }

    private fun showRow() {
        composeRule.setContent {
            Theme(darkTheme = false, dynamicColor = false) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(Modifier.testTag("glassIcon")) {
                        SketchbookGlassIconButton({}, "glass") { Box(Modifier.size(24.dp)) }
                    }
                    Box(Modifier.testTag("plainIcon")) {
                        SketchbookIconButton({}, "plain") { Box(Modifier.size(24.dp)) }
                    }
                    Box(Modifier.testTag("glassButton")) { SketchbookGlassButton({}) { Text("Go") } }
                    Box(Modifier.testTag("plainButton")) { SketchbookButton({}) { Text("Go") } }
                }
            }
        }
        composeRule.waitForIdle()
    }

    private fun assertAlignedAtLeast48(vararg tags: String) {
        val bounds = tags.map { composeRule.onNodeWithTag(it).getBoundsInRoot() }
        bounds.forEachIndexed { i, b ->
            assertTrue("${tags[i]} height ${b.height}", b.height >= Minimum)
            assertEquals("${tags[i]} top", bounds[0].top, b.top)
        }
    }

    private companion object {
        val Minimum: Dp = 48.dp
    }
}
