package lib.sketchbook.behavior

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toPixelMap
import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.unit.dp
import lib.sketchbook.DialogSurface
import lib.sketchbook.OverlayState
import lib.sketchbook.SketchbookDialog
import lib.sketchbook.StripedBackdrop
import lib.sketchbook.popOutBackground
import lib.sketchbook.glass.glassSource
import lib.sketchbook.redSpread
import lib.sketchbook.contrastRatio
import lib.sketchbook.opaque
import lib.sketchbook.theme.Theme
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.robolectric.annotation.Config

class WindowGlassBehaviorTest : BehaviorTest() {

    private fun surfaceSpread(
        source: Boolean = true,
        useGlow: Boolean = false,
        backdrop: @androidx.compose.runtime.Composable (@androidx.compose.runtime.Composable () -> Unit) -> Unit = { it() }
    ): Int {
        composeRule.setContent {
            Theme(darkTheme = false, dynamicColor = false) {
                StripedBackdrop(Modifier.size(360.dp, 200.dp), source = source) {
                    backdrop {
                        DialogSurface(
                            modifier = Modifier.align(Alignment.Center),
                            padding = PaddingValues(0.dp),
                            useGlow = useGlow
                        ) { Box(Modifier.size(280.dp, 100.dp)) }
                    }
                }
            }
        }
        composeRule.waitForIdle()
        return composeRule.onRoot().captureToImage().toPixelMap().redSpread(200, 140..580)
    }

    @Test
    @Config(sdk = [26])
    fun `dialog surface keeps the backdrop sharp below android 12`() {
        assertTrue(surfaceSpread() > Sharp)
    }

    @Test
    @Config(sdk = [31])
    fun `dialog surface over a glass source blurs the backdrop on android 12`() {
        assertTrue(surfaceSpread() < Soft)
    }

    @Test
    @Config(sdk = [31])
    fun `dialog surface without a glass source keeps the backdrop sharp`() {
        assertTrue(surfaceSpread(source = false) > Sharp)
    }

    @Test
    @Config(sdk = [34])
    fun `dialog surface without a glass source is a sharp tinted pane on android 14`() {
        val spread = surfaceSpread(source = false)
        val image = composeRule.onRoot().captureToImage().toPixelMap()

        assertTrue("spread $spread", spread > Sharp)
        assertNotEquals(Color.Red, image.run { this[320, 200].let { Color(it.red, it.green, it.blue, 1f) } })
    }

    @Test
    @Config(sdk = [31])
    fun `dialog is a glass source for a dialog surface placed over its content`() {
        composeRule.setContent {
            Theme(darkTheme = false, dynamicColor = false) {
                Box(Modifier.size(360.dp, 200.dp)) {
                    SketchbookDialog(
                        visible = false,
                        onVisibleChange = {},
                        dialog = {}
                    ) {
                        StripedBackdrop(Modifier.size(360.dp, 200.dp), source = false) {}
                    }
                    DialogSurface(
                        modifier = Modifier.align(Alignment.Center),
                        padding = PaddingValues(0.dp)
                    ) { Box(Modifier.size(280.dp, 100.dp)) }
                }
            }
        }
        composeRule.waitForIdle()
        val spread = composeRule.onRoot().captureToImage().toPixelMap().redSpread(200, 140..580)
        assertTrue("spread $spread", spread < Soft)
    }

    @Test
    @Config(sdk = [31])
    fun `pop out background is a glass source for a dialog surface placed over it`() {
        composeRule.setContent {
            Theme(darkTheme = false, dynamicColor = false) {
                val overlay = remember { OverlayState() }
                Box(Modifier.size(360.dp, 200.dp)) {
                    StripedBackdrop(
                        Modifier.size(360.dp, 200.dp).popOutBackground(overlay),
                        source = false
                    ) {}
                    DialogSurface(
                        modifier = Modifier.align(Alignment.Center),
                        padding = PaddingValues(0.dp)
                    ) { Box(Modifier.size(280.dp, 100.dp)) }
                }
            }
        }
        composeRule.waitForIdle()
        val spread = composeRule.onRoot().captureToImage().toPixelMap().redSpread(200, 140..580)
        assertTrue("spread $spread", spread < Soft)
    }

    @Test
    @Config(sdk = [33])
    fun `dialog surface renders a tinted pane on android 13 and leaves the backdrop outside intact`() {
        surfaceSpread()
        val image = composeRule.onRoot().captureToImage().toPixelMap()
        assertEquals(Color.Red, image.run { this[10, 100].let { Color(it.red, it.green, it.blue, 1f) } })
        assertNotEquals(Color.Red, image.run { this[320, 200].let { Color(it.red, it.green, it.blue, 1f) } })
    }

    private fun paneContrast(dark: Boolean): List<Float> {
        var text = Color.Unspecified
        composeRule.setContent {
            Theme(darkTheme = dark, dynamicColor = false) {
                text = Theme.color.content.background
                StripedBackdrop(Modifier.size(360.dp, 200.dp)) {
                    DialogSurface(
                        modifier = Modifier.align(Alignment.Center),
                        padding = PaddingValues(0.dp)
                    ) { Box(Modifier.size(280.dp, 100.dp)) }
                }
            }
        }
        composeRule.waitForIdle()
        val image = composeRule.onRoot().captureToImage().toPixelMap()
        return listOf(300, 340).map { contrastRatio(text, image.opaque(it, 200)) }
    }

    @Test
    @Config(sdk = [26])
    fun `dialog surface text stays readable over saturated backdrop in light theme`() {
        paneContrast(dark = false).forEach { assertTrue("ratio $it", it >= 4.5f) }
    }

    @Test
    @Config(sdk = [26], qualifiers = "w360dp-h640dp-normal-night-xhdpi")
    fun `dialog surface text stays readable over saturated backdrop in dark theme`() {
        paneContrast(dark = true).forEach { assertTrue("ratio $it", it >= 4.5f) }
    }

    @Test
    @Config(sdk = [31])
    fun `dialog surface nested in another glass stays a sharp tint`() {
        composeRule.setContent {
            Theme(darkTheme = false, dynamicColor = false) {
                StripedBackdrop(Modifier.size(360.dp, 200.dp)) {
                    SketchbookDialog(visible = false, onVisibleChange = {}, dialog = {}) {
                        Box(Modifier.size(360.dp, 200.dp)) {
                            DialogSurface(
                                modifier = Modifier.align(Alignment.Center),
                                padding = PaddingValues(0.dp)
                            ) { Box(Modifier.size(280.dp, 100.dp)) }
                        }
                    }
                }
            }
        }
        composeRule.waitForIdle()
        val image = composeRule.onRoot().captureToImage().toPixelMap()

        assertTrue(image.redSpread(200, 140..580) > Sharp)
    }

    private companion object {
        const val Sharp = 100
        const val Soft = 70
    }
}
