package lib.sketchbook.screenshot

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import lib.sketchbook.ColorfulStripes
import lib.sketchbook.SketchbookButton
import lib.sketchbook.SketchbookGlassButton
import lib.sketchbook.SketchbookOutlinedButton
import lib.sketchbook.SketchbookPreviewLayout
import lib.sketchbook.SketchbookTonalButton
import lib.sketchbook.StripedBackdrop
import org.junit.Test
import org.robolectric.annotation.Config

class ButtonScreenshotTest : ScreenshotTest() {

    @Test
    @Config(sdk = [26], qualifiers = Light)
    fun `buttons over stripes on android 8 in light theme`() = captureRoot("Buttons_sdk26_light") { Buttons() }

    @Test
    @Config(sdk = [26], qualifiers = Dark)
    fun `buttons over stripes on android 8 in dark theme`() = captureRoot("Buttons_sdk26_dark") { Buttons() }

    @Test
    @Config(sdk = [33], qualifiers = Light)
    fun `buttons over stripes on android 13 in light theme`() = captureRoot("Buttons_sdk33_light") { Buttons() }

    @Test
    @Config(sdk = [33], qualifiers = Dark)
    fun `buttons over stripes on android 13 in dark theme`() = captureRoot("Buttons_sdk33_dark") { Buttons() }

    @Composable
    private fun Buttons() = SketchbookPreviewLayout {
        StripedBackdrop(Modifier.fillMaxSize(), colors = ColorfulStripes) {
            Column(Modifier.padding(24.dp)) {
                SketchbookButton({}) { Text("Filled") }
                SketchbookTonalButton({}, Modifier.padding(top = 12.dp)) { Text("Tonal") }
                SketchbookOutlinedButton({}, Modifier.padding(top = 12.dp)) { Text("Outlined") }
                SketchbookGlassButton({}, Modifier.padding(top = 12.dp)) { Text("Glass") }
                SketchbookGlassButton({}, Modifier.padding(top = 12.dp), enabled = false) { Text("Disabled") }
            }
        }
    }
}
