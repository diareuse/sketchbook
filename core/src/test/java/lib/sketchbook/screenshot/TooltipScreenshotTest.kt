package lib.sketchbook.screenshot

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import lib.sketchbook.ColorfulStripes
import lib.sketchbook.SketchbookPreviewLayout
import lib.sketchbook.StripedBackdrop
import lib.sketchbook.TooltipSurface
import lib.sketchbook.theme.Theme
import org.junit.Test
import org.robolectric.annotation.Config

class TooltipScreenshotTest : ScreenshotTest() {

    @Test
    @Config(sdk = [26], qualifiers = Light)
    fun `tooltip surface over stripes on android 8 in light theme`() = captureRoot("Tooltip_sdk26_light") { Tooltip() }

    @Test
    @Config(sdk = [26], qualifiers = Dark)
    fun `tooltip surface over stripes on android 8 in dark theme`() = captureRoot("Tooltip_sdk26_dark") { Tooltip() }

    @Test
    @Config(sdk = [33], qualifiers = Light)
    fun `tooltip surface over stripes on android 13 in light theme`() = captureRoot("Tooltip_sdk33_light") { Tooltip() }

    @Test
    @Config(sdk = [33], qualifiers = Dark)
    fun `tooltip surface over stripes on android 13 in dark theme`() = captureRoot("Tooltip_sdk33_dark") { Tooltip() }

    @Composable
    private fun Tooltip() = SketchbookPreviewLayout {
        StripedBackdrop(Modifier.fillMaxSize(), colors = ColorfulStripes) {
            androidx.compose.foundation.layout.Box(Modifier.align(Alignment.Center)) {
                TooltipSurface(Theme.container.buttonSmall, Theme.color.emphasis.background) {
                    Text("Add to favorites", style = Theme.textStyle.caption)
                }
            }
        }
    }
}
