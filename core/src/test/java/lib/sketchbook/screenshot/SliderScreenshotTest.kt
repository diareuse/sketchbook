package lib.sketchbook.screenshot

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import lib.sketchbook.ColorfulStripes
import lib.sketchbook.SketchbookPreviewLayout
import lib.sketchbook.SketchbookSlider
import lib.sketchbook.StripedBackdrop
import org.junit.Test
import org.robolectric.annotation.Config

class SliderScreenshotTest : ScreenshotTest() {

    @Test
    @Config(sdk = [26], qualifiers = Light)
    fun `sliders over stripes on android 8 in light theme`() = captureRoot("Sliders_sdk26_light") { Sliders() }

    @Test
    @Config(sdk = [26], qualifiers = Dark)
    fun `sliders over stripes on android 8 in dark theme`() = captureRoot("Sliders_sdk26_dark") { Sliders() }

    @Test
    @Config(sdk = [33], qualifiers = Light)
    fun `sliders over stripes on android 13 in light theme`() = captureRoot("Sliders_sdk33_light") { Sliders() }

    @Test
    @Config(sdk = [33], qualifiers = Dark)
    fun `sliders over stripes on android 13 in dark theme`() = captureRoot("Sliders_sdk33_dark") { Sliders() }

    @Composable
    private fun Sliders() = SketchbookPreviewLayout {
        StripedBackdrop(Modifier.fillMaxSize(), colors = ColorfulStripes) {
            Column(Modifier.padding(24.dp)) {
                SketchbookSlider(.3f, {})
                SketchbookSlider(.6f, {}, steps = 4)
                SketchbookSlider(.8f, {}, enabled = false)
            }
        }
    }
}
