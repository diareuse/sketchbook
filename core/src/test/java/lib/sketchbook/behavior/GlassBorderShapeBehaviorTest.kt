package lib.sketchbook.behavior

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.toPixelMap
import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.unit.dp
import lib.sketchbook.SketchbookGlassBorder
import lib.sketchbook.theme.Theme
import org.junit.Assert.assertTrue
import org.junit.Test
import org.robolectric.annotation.Config

abstract class GlassBorderPaneBehavior : GlassPaneBehavior() {

    override val paneShape: @Composable () -> Shape = { Theme.container.card }

    @Test
    fun `glass border pane edge matches the theme card shape`() = assertPane {
        SketchbookGlassBorder(it.size(200.dp, 100.dp)) {}
    }

    @Test
    fun `glass border keeps its rim inside the glass shape`() {
        composeRule.mainClock.autoAdvance = false
        assertPane { SketchbookGlassBorder(it.size(200.dp, 100.dp), borderWidth = 3.dp, animated = false) {} }
    }

    @Test
    fun `animated glass border keeps its rim inside the glass shape`() {
        composeRule.mainClock.autoAdvance = false
        assertPane { SketchbookGlassBorder(it.size(200.dp, 100.dp), borderWidth = 3.dp, animated = true) {} }
    }
}

@Config(sdk = [26])
class GlassBorderPaneAndroid8BehaviorTest : GlassBorderPaneBehavior()

@Config(sdk = [31])
class GlassBorderPaneAndroid12BehaviorTest : GlassBorderPaneBehavior()

@Config(sdk = [33])
class GlassBorderPaneAndroid13BehaviorTest : GlassBorderPaneBehavior()

abstract class GlassBorderBeamBehavior : BehaviorTest() {

    @Test
    fun `a white glass border draws a multi colour gradient beam rather than a single glint`() {
        composeRule.mainClock.autoAdvance = false
        composeRule.setContent {
            Theme(darkTheme = false, dynamicColor = false) {
                Box(Modifier.size(240.dp, 140.dp).background(Color.Black)) {
                    Box(Modifier.size(240.dp, 140.dp)) {
                        SketchbookGlassBorder(Modifier.size(240.dp, 140.dp), borderWidth = 4.dp, animated = false) {}
                    }
                }
            }
        }
        composeRule.mainClock.advanceTimeBy(100)
        composeRule.waitForIdle()
        val image = composeRule.onRoot().captureToImage().toPixelMap()

        var saturated = 0
        for (y in 0 until image.height) for (x in 0 until image.width) {
            val c = image[x, y]
            if (maxOf(c.red, c.green, c.blue) - minOf(c.red, c.green, c.blue) > .12f) saturated++
        }
        assertTrue("saturated pixels $saturated", saturated > 40)
    }
}

@Config(sdk = [26])
class GlassBorderBeamAndroid8BehaviorTest : GlassBorderBeamBehavior()

@Config(sdk = [33])
class GlassBorderBeamAndroid13BehaviorTest : GlassBorderBeamBehavior()
