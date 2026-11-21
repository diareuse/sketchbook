package lib.sketchbook.behavior

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PixelMap
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.toPixelMap
import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.unit.dp
import lib.sketchbook.StripedBackdrop
import lib.sketchbook.glass.glass
import lib.sketchbook.shape.SquircleShape
import lib.sketchbook.theme.Theme
import org.junit.Assert.assertTrue
import org.junit.Test
import org.robolectric.annotation.Config

abstract class GlassShapeBehavior : BehaviorTest() {

    @Test
    fun `glass over a squircle shape leaves the outside of the rounded corner untouched`() {
        val image = render { SquircleShape(Radius.dp) }

        assertBackdrop(image, PaneLeft + 1, PaneTop + 1)
        assertBackdrop(image, PaneLeft + 6, PaneTop + 6)
        assertBackdrop(image, PaneLeft + InsideSquircleOutsideRounded, PaneTop + InsideSquircleOutsideRounded)
    }

    @Test
    fun `glass over a squircle shape covers the straight edges and the inside of the corner arc`() {
        val image = render { SquircleShape(Radius.dp) }

        assertGlass(image, PaneLeft + PaneWidth / 2, PaneTop + 3)
        assertGlass(image, PaneLeft + PaneWidth / 2, PaneTop + PaneHeight - 4)
        assertGlass(image, PaneLeft + 3, PaneTop + PaneHeight / 2)
        assertGlass(image, PaneLeft + PaneWidth - 4, PaneTop + PaneHeight / 2)
        assertGlass(image, PaneLeft + InsideArc, PaneTop + InsideArc)
    }

    @Test
    fun `glass over the theme button shape follows rounded corners of the same size`() {
        val image = render { Theme.container.button }

        assertBackdrop(image, PaneLeft + 1, PaneTop + 1)
        assertGlass(image, PaneLeft + PaneWidth / 2, PaneTop + 3)
        assertGlass(image, PaneLeft + 3, PaneTop + PaneHeight / 2)
    }

    private fun render(shape: @Composable () -> Shape): PixelMap {
        composeRule.setContent {
            Theme(darkTheme = false, dynamicColor = false) {
                StripedBackdrop(Modifier.size(BackdropWidth.dp, BackdropHeight.dp)) {
                    Box(Modifier.align(Alignment.Center).size((PaneWidth / 2).dp, (PaneHeight / 2).dp).glass(shape()))
                }
            }
        }
        composeRule.waitForIdle()
        return composeRule.onRoot().captureToImage().toPixelMap()
    }

    private fun assertBackdrop(image: PixelMap, x: Int, y: Int) {
        val c = image[x, y]
        val opaque = Color(c.red, c.green, c.blue, 1f)
        assertTrue("pixel ($x,$y) is $opaque", opaque == Color.Red || opaque == Color.Blue)
    }

    private fun assertGlass(image: PixelMap, x: Int, y: Int) {
        val c = image[x, y]
        val opaque = Color(c.red, c.green, c.blue, 1f)
        assertTrue("pixel ($x,$y) is $opaque", opaque != Color.Red && opaque != Color.Blue)
    }

    private companion object {
        const val Radius = 40
        const val BackdropWidth = 240
        const val BackdropHeight = 140
        const val PaneWidth = 400
        const val PaneHeight = 200
        const val PaneLeft = 40
        const val PaneTop = 40
        const val InsideSquircleOutsideRounded = 21
        const val InsideArc = 30
    }
}

@Config(sdk = [26])
class GlassShapeAndroid8BehaviorTest : GlassShapeBehavior()

@Config(sdk = [31])
class GlassShapeAndroid12BehaviorTest : GlassShapeBehavior()

@Config(sdk = [33])
class GlassShapeAndroid13BehaviorTest : GlassShapeBehavior()
