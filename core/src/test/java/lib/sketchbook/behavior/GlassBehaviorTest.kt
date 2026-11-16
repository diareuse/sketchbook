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
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.toPixelMap
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.shape.RoundedCornerShape
import dev.chrisbanes.haze.glass.GlassOptics
import dev.chrisbanes.haze.glass.OpticalSizeValue
import lib.sketchbook.glass.GlassDepthScope
import lib.sketchbook.glass.GlassSpec
import lib.sketchbook.glass.refractiveOptics
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
    @Config(sdk = [26])
    fun `elevated glass lights its rim more than flat glass`() {
        val flat = rimContrast(0.dp)
        composeRule.activityRule.scenario.recreate()
        val elevated = rimContrast(8.dp)

        assertTrue("flat $flat elevated $elevated", elevated > flat + RimMargin)
    }

    @Test
    @Config(sdk = [31])
    fun `flat glass at zero elevation does not blur the backdrop`() {
        assertTrue(seamContrast(elevation = 0.dp) > Sharp)
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
    fun `glass renders a tinted pane over the backdrop on android 13`() {
        render()
        val image = composeRule.onRoot().captureToImage().toPixelMap()
        val inside = image[100, 100]
        assertNotEquals(Color.Red, Color(inside.red, inside.green, inside.blue, 1f))
        assertEquals(Color.Red, image[10, 100])
    }

    @Test
    fun `refractive optics blur fully with the spec radius at every size`() {
        val spec = GlassSpec.of(RoundedCornerShape(12.dp), Color.Black, 24.dp)
        val optics = refractiveOptics(spec, GlassOptics.regular)
        assertEquals(OpticalSizeValue.Fixed(24.dp), optics.blurRadius)
        assertEquals(OpticalSizeValue.Fixed(1f), optics.depth)
        assertEquals(GlassOptics.regular.refractionProfile, optics.refractionProfile)
    }

    @Test
    fun `glass source without a theme leaves the content visible`() {
        composeRule.setContent { Box(Modifier.size(20.dp).glassSource().background(Color.Red)) }
        composeRule.waitForIdle()
        assertEquals(Color.Red, composeRule.onRoot().captureToImage().toPixelMap()[5, 5])
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

    private fun seamContrast(registerSource: Boolean = true, nested: Boolean = false, elevation: Dp = PaneElevation): Int {
        render(registerSource, nested, elevation)
        val image = composeRule.onRoot().captureToImage().toPixelMap()
        val left = image[SeamX - 8, SeamY]
        val right = image[SeamX + 8, SeamY]
        return abs(left.red * 255 - right.red * 255).toInt()
    }

    private fun rimContrast(elevation: Dp): Int {
        composeRule.setContent {
            Theme(darkTheme = false, dynamicColor = false) {
                Box(Modifier.size(100.dp).background(Color.Black).padding(10.dp)) {
                    Box(Modifier.fillMaxSize().glass(Theme.container.card, tonalElevation = elevation))
                }
            }
        }
        composeRule.waitForIdle()
        val image = composeRule.onRoot().captureToImage().toPixelMap()
        val inner = image[100, 100]
        return (20..24).maxOf { y ->
            val p = image[100, y]
            ((abs(p.red - inner.red) + abs(p.green - inner.green) + abs(p.blue - inner.blue)) * 255).toInt()
        }
    }

    private fun render(registerSource: Boolean = true, nested: Boolean = false, elevation: Dp = PaneElevation) {
        composeRule.setContent {
            Theme(darkTheme = false, dynamicColor = false) {
                Box(Modifier.size(200.dp, 100.dp)) {
                    Row(Modifier.fillMaxSize().let { if (registerSource) it.glassSource() else it }) {
                        Half(Color.Red)
                        Half(Color.Blue)
                    }
                    if (nested) {
                        GlassDepthScope { Pane(elevation) }
                    } else {
                        Pane(elevation)
                    }
                }
            }
        }
        composeRule.waitForIdle()
    }

    @Composable
    private fun Pane(elevation: Dp) = Box(
        Modifier.fillMaxSize().padding(horizontal = 20.dp).glass(Theme.container.card, tonalElevation = elevation)
    )

    @Composable
    private fun RowScope.Half(color: Color) = Box(Modifier.weight(1f).fillMaxSize().background(color))

    private companion object {
        val PaneElevation = 12.dp
        const val SeamX = 200
        const val SeamY = 100
        const val RimMargin = 30
        const val Sharp = 150
        const val Soft = 120
    }
}
