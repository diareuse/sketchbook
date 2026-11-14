package lib.sketchbook.behavior

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PixelMap
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.toPixelMap
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.center
import androidx.compose.ui.test.down
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.IntRect
import androidx.compose.ui.unit.dp
import lib.sketchbook.PaneProbe
import lib.sketchbook.SketchbookButton
import lib.sketchbook.SketchbookFilledIconButton
import lib.sketchbook.SketchbookGlassButton
import lib.sketchbook.SketchbookGlassIconButton
import lib.sketchbook.SketchbookIconButton
import lib.sketchbook.SketchbookOutlinedButton
import lib.sketchbook.SketchbookTonalButton
import lib.sketchbook.SketchbookTonalIconButton
import lib.sketchbook.StripeColors
import lib.sketchbook.StripedBackdrop
import lib.sketchbook.changedPixels
import lib.sketchbook.glass.asGlassShape
import lib.sketchbook.theme.Theme
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.robolectric.annotation.Config

abstract class GlassPaneBehavior : BehaviorTest() {

    protected open val paneShape: @Composable () -> Shape = { Theme.container.button }

    private var shape: Shape = RectangleShape
    private var density = Density(1f)

    protected fun assertPane(content: @Composable (Modifier) -> Unit) {
        show(content)
        val probe = probe(frame())

        assertTrue("pane ${probe.bounds}", probe.bounds.height >= 80)
        assertEquals(emptyList<String>(), probe.mismatches(shape, density).take(8))
    }

    protected fun assertPressInside(content: @Composable (Modifier) -> Unit) {
        composeRule.mainClock.autoAdvance = false
        show(content)
        composeRule.mainClock.advanceTimeBy(16)
        val idle = frame()
        composeRule.onNodeWithTag(Target).performTouchInput { down(center) }
        composeRule.mainClock.advanceTimeBy(150)
        val changed = idle.changedPixels(frame())
        val outside = probe(idle).outside(changed, shape, density, 2f)

        assertTrue("press changed nothing", changed.isNotEmpty())
        assertEquals(emptyList<Pair<Int, Int>>(), outside.take(8))
    }

    private fun show(content: @Composable (Modifier) -> Unit) {
        composeRule.setContent {
            Theme(darkTheme = false, dynamicColor = false) {
                shape = paneShape().asGlassShape()
                density = LocalDensity.current
                StripedBackdrop(Modifier.size(260.dp, 160.dp)) {
                    Box(Modifier.align(Alignment.Center)) { content(Modifier.testTag(Target)) }
                }
            }
        }
        composeRule.waitForIdle()
    }

    private fun frame(): PixelMap = composeRule.onRoot().captureToImage().toPixelMap()

    private fun probe(image: PixelMap) =
        PaneProbe(image, StripeColors, IntRect(0, 0, image.width, image.height))

    protected companion object {
        const val Target = "target"
    }
}

