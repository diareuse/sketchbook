@file:OptIn(ExperimentalMaterial3Api::class)

package lib.sketchbook.behavior

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarState
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toPixelMap
import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.unit.dp
import lib.sketchbook.SketchbookTopAppBar
import lib.sketchbook.StripedBackdrop
import lib.sketchbook.contrastRatio
import lib.sketchbook.distance
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
                        title = { Text("T") },
                        scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(state),
                        contentPadding = PaddingValues()
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
    fun `collapsed bar tints the content behind it`() {
        render(Collapsed)

        assertTrue(distance(Color.Red, fieldPixel(10)) > 60)
    }

    @Test
    @Config(sdk = [34])
    fun `collapsed bar without a glass source is a tint on android 14`() {
        render(Collapsed, source = false)

        assertTrue(distance(Color.Red, fieldPixel(10)) > 60)
    }

    @Test
    @Config(sdk = [26])
    fun `bar backdrop fades in progressively while collapsing`() {
        render(Expanded)
        scrollTo(-15f)
        val early = distance(Color.Red, fieldPixel(10))
        scrollTo(Collapsed)
        val late = distance(Color.Red, fieldPixel(10))

        assertTrue("early $early late $late", early in 1 until late)
    }

    @Test
    @Config(sdk = [26])
    fun `collapsed bar keeps dark title text readable over any backdrop colour`() {
        render(Collapsed)
        val image = barPixels()
        val text = Color(0xFF1C1B1F)

        listOf(10, 50, 90).forEach {
            val ratio = contrastRatio(text, image.opaque(it, 20))
            assertTrue("x=$it ratio $ratio", ratio >= 4.5f)
        }
    }

    @Test
    @Config(sdk = [26])
    fun `collapsed bar over a glass source keeps the backdrop sharp below android 12`() {
        render(Collapsed)

        assertTrue(barPixels().redSpread(20, 100..620) > 100)
    }

    @Test
    @Config(sdk = [31])
    fun `collapsed bar over a glass source blurs the backdrop on android 12`() {
        render(Collapsed)

        val s = barPixels().redSpread(20, 100..620)
        assertTrue("spread $s", s < 120)
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
