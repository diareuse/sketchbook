package lib.sketchbook.behavior

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.toPixelMap
import androidx.compose.foundation.layout.RowScope
import lib.sketchbook.glass.GlassDepthScope
import lib.sketchbook.glass.GlassTier
import lib.sketchbook.glass.glass
import lib.sketchbook.glass.glassSource
import lib.sketchbook.glass.selectGlass
import lib.sketchbook.theme.Theme
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.robolectric.annotation.Config
import kotlin.math.abs

class GlassBehaviorTest : BehaviorTest() {

    @Test
    fun `glass tier follows the platform capabilities`() {
        assertEquals(GlassTier.Tint, selectGlass(26, hasSource = true))
        assertEquals(GlassTier.Tint, selectGlass(30, hasSource = true))
        assertEquals(GlassTier.Blur, selectGlass(31, hasSource = true))
        assertEquals(GlassTier.Blur, selectGlass(32, hasSource = true))
        assertEquals(GlassTier.Refractive, selectGlass(33, hasSource = true))
        assertEquals(GlassTier.Refractive, selectGlass(36, hasSource = true))
    }

    @Test
    fun `glass tier is tint whenever there is no source`() {
        listOf(26, 31, 33, 36).forEach {
            assertEquals(GlassTier.Tint, selectGlass(it, hasSource = false))
        }
    }

    @Test
    @Config(sdk = [26])
    fun `glass keeps the backdrop sharp below android 12`() {
        assertTrue(seamContrast() > Sharp)
    }

    @Test
    @Config(sdk = [31])
    fun `glass blurs the backdrop on android 12`() {
        assertTrue(seamContrast() < Soft)
    }

    @Test
    @Config(sdk = [31])
    fun `glass without a glass source keeps the backdrop sharp`() {
        assertTrue(seamContrast(registerSource = false) > Sharp)
    }

    @Test
    @Config(sdk = [31])
    fun `glass nested inside another glass does not blur again`() {
        assertTrue(seamContrast(nested = true) > Sharp)
    }

    @Test
    @Config(sdk = [33])
    fun `glass tints the backdrop on android 13`() {
        render()
        val image = composeRule.onRoot().captureToImage().toPixelMap()
        val inside = image[100, 100]
        assertNotEquals(Color.Red, Color(inside.red, inside.green, inside.blue, 1f))
        assertEquals(Color.Red, image[10, 100])
    }

    @Test
    fun `glass source is harmless without a theme`() {
        composeRule.setContent { Box(Modifier.size(20.dp).glassSource()) }
        composeRule.waitForIdle()
    }

    @Test
    fun `theme with dynamic color off provides the sketchbook palette`() {
        var container = Color.Unspecified
        composeRule.setContent {
            Theme(darkTheme = false, dynamicColor = false) { container = Theme.color.container.primary }
        }
        composeRule.waitForIdle()
        assertEquals(Color(0xFFFFDBC9), container)
    }

    private fun seamContrast(registerSource: Boolean = true, nested: Boolean = false): Int {
        render(registerSource, nested)
        val image = composeRule.onRoot().captureToImage().toPixelMap()
        val left = image[SeamX - 8, SeamY]
        val right = image[SeamX + 8, SeamY]
        return abs(left.red * 255 - right.red * 255).toInt()
    }

    private fun render(registerSource: Boolean = true, nested: Boolean = false) {
        composeRule.setContent {
            Theme(darkTheme = false, dynamicColor = false) {
                Box(Modifier.size(200.dp, 100.dp)) {
                    Row(Modifier.fillMaxSize().let { if (registerSource) it.glassSource() else it }) {
                        Half(Color.Red)
                        Half(Color.Blue)
                    }
                    if (nested) {
                        GlassDepthScope { Pane() }
                    } else {
                        Pane()
                    }
                }
            }
        }
        composeRule.waitForIdle()
    }

    @Composable
    private fun Pane() = Box(
        Modifier.fillMaxSize().padding(horizontal = 20.dp).glass(Theme.container.card)
    )

    @Composable
    private fun RowScope.Half(color: Color) = Box(Modifier.weight(1f).fillMaxSize().background(color))

    private companion object {
        const val SeamX = 200
        const val SeamY = 100
        const val Sharp = 150
        const val Soft = 120
    }
}
