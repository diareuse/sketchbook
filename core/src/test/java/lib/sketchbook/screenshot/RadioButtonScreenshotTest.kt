package lib.sketchbook.screenshot

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import lib.sketchbook.SketchbookPreviewLayout
import lib.sketchbook.SketchbookRadioButton
import org.junit.Test
import org.robolectric.annotation.Config

class RadioButtonScreenshotTest : ScreenshotTest() {

    @Test
    @Config(sdk = [26], qualifiers = Light)
    fun `radio buttons in all states on android 8 in light theme`() = captureRoot("RadioButtons_sdk26_light") { Content() }

    @Test
    @Config(sdk = [26], qualifiers = Dark)
    fun `radio buttons in all states on android 8 in dark theme`() = captureRoot("RadioButtons_sdk26_dark") { Content() }

    @Test
    @Config(sdk = [33], qualifiers = Light)
    fun `radio buttons in all states on android 13 in light theme`() = captureRoot("RadioButtons_sdk33_light") { Content() }

    @Test
    @Config(sdk = [33], qualifiers = Dark)
    fun `radio buttons in all states on android 13 in dark theme`() = captureRoot("RadioButtons_sdk33_dark") { Content() }

    @Composable
    private fun Content() = SketchbookPreviewLayout {
        Column(Modifier.fillMaxSize().padding(24.dp)) {
            SketchbookRadioButton(true, {}, label = { Text("On") })
            SketchbookRadioButton(false, {}, label = { Text("Off") })
            SketchbookRadioButton(true, {}, label = { Text("Disabled on") }, enabled = false)
            SketchbookRadioButton(false, {}, label = { Text("Disabled off") }, enabled = false)
        }
    }
}
