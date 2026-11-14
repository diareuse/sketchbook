@file:OptIn(ExperimentalMaterial3Api::class)

package lib.sketchbook.behavior

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.test.*
import androidx.compose.ui.unit.*
import lib.sketchbook.SketchbookTopAppBar
import lib.sketchbook.StripedBackdrop
import lib.sketchbook.opaque
import lib.sketchbook.redSpread
import lib.sketchbook.theme.Theme
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.robolectric.annotation.Config

class TopAppBarGlassBehaviorTest : BehaviorTest() {

    private lateinit var barState: TopAppBarState

    private fun scrollTo(offset: Float) {
        composeRule.runOnIdle { barState.heightOffset = offset }
        composeRule.waitForIdle()
    }

    private fun render(offset: Float, source: Boolean = true) {
        composeRule.setContent {
            Theme(darkTheme = false, dynamicColor = false) {
                val state = rememberTopAppBarState(initialHeightOffset = offset)
                barState = state
                StripedBackdrop(Modifier.size(360.dp, 400.dp), source = source) {
                    SketchbookTopAppBar(
                        modifier = Modifier.fillMaxWidth(),
                        title = { Text("T") }
                    )
                }
            }
        }
        composeRule.waitForIdle()
    }

    private fun barPixels() = composeRule.onRoot().captureToImage().toPixelMap()

    private fun fieldPixel(x: Int) = barPixels().opaque(x, 20)

    @Test
    @Config(sdk = [26])
    fun `expanded bar leaves the content behind it untouched`() {
        render(Expanded)

        assertEquals(Color.Red, fieldPixel(10))
    }

    @Test
    @Config(sdk = [26])
    fun `collapsed bar over a glass source keeps the backdrop sharp below android 12`() {
        render(Collapsed)

        assertTrue(barPixels().redSpread(20, 100..620) > 100)
    }

    @Test
    @Config(sdk = [31])
    fun `collapsed bar without a glass source keeps the backdrop sharp`() {
        render(Collapsed, source = false)

        assertTrue(barPixels().redSpread(20, 100..620) > 100)
    }

    private companion object {
        const val Expanded = 0f
        const val Collapsed = -48f
    }
}
