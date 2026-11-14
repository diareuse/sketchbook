package lib.sketchbook.behavior

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PixelMap
import androidx.compose.ui.graphics.toPixelMap
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.assertHeightIsEqualTo
import androidx.compose.ui.test.assertWidthIsEqualTo
import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import lib.sketchbook.SketchbookHorizontalDivider
import lib.sketchbook.SketchbookVerticalDivider
import lib.sketchbook.distance
import lib.sketchbook.opaque
import lib.sketchbook.theme.Theme
import org.junit.Assert.assertTrue
import org.junit.Test

class DividerBehaviorTest : BehaviorTest() {

    private fun show(content: @Composable () -> Unit) {
        composeRule.setContent {
            Theme(darkTheme = false, dynamicColor = false) {
                Box(
                    Modifier.size(200.dp, 100.dp).background(Color.White),
                    contentAlignment = Alignment.Center
                ) { content() }
            }
        }
        composeRule.waitForIdle()
    }

    private fun pixels(): PixelMap = composeRule.onRoot().captureToImage().toPixelMap()

    @Test
    fun `horizontal divider is one physical pixel thick and fills the width`() {
        show { SketchbookHorizontalDivider(Modifier.testTag("divider")) }

        composeRule.onNodeWithTag("divider").assertHeightIsEqualTo(.5.dp).assertWidthIsEqualTo(200.dp)
    }

    @Test
    fun `vertical divider is one physical pixel thick and fills the height`() {
        show { SketchbookVerticalDivider(Modifier.testTag("divider")) }

        composeRule.onNodeWithTag("divider").assertWidthIsEqualTo(.5.dp).assertHeightIsEqualTo(100.dp)
    }

    @Test
    fun `divider honours an explicit thickness`() {
        show { SketchbookHorizontalDivider(Modifier.testTag("divider"), thickness = 4.dp) }

        composeRule.onNodeWithTag("divider").assertHeightIsEqualTo(4.dp)
    }

    @Test
    fun `horizontal divider is solid in the middle and fades out towards both ends`() {
        show { SketchbookHorizontalDivider(thickness = 4.dp, color = Color.Black, fadeLength = 40.dp) }
        val image = pixels()

        val middle = distance(Color.White, image.opaque(200, 100))
        assertTrue("middle $middle", middle > 600)
        assertTrue("left end", distance(Color.White, image.opaque(1, 100)) < 40)
        assertTrue("right end", distance(Color.White, image.opaque(398, 100)) < 40)
        val halfway = distance(Color.White, image.opaque(40, 100))
        assertTrue("fading $halfway", halfway in 100 until middle)
    }

    @Test
    fun `vertical divider is solid in the middle and fades out towards both ends`() {
        show { SketchbookVerticalDivider(thickness = 4.dp, color = Color.Black, fadeLength = 40.dp) }
        val image = pixels()

        assertTrue(distance(Color.White, image.opaque(200, 100)) > 600)
        assertTrue("top end", distance(Color.White, image.opaque(200, 1)) < 40)
        assertTrue("bottom end", distance(Color.White, image.opaque(200, 198)) < 40)
    }

    @Test
    fun `divider shorter than twice the fade length still draws`() {
        show {
            Box(Modifier.size(30.dp, 10.dp), contentAlignment = Alignment.Center) {
                SketchbookHorizontalDivider(thickness = 4.dp, color = Color.Black, fadeLength = 40.dp)
            }
        }

        assertTrue(distance(Color.White, pixels().opaque(200, 100)) > 100)
    }
}
