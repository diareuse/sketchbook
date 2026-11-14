package lib.sketchbook.screenshot

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import lib.sketchbook.ColorfulStripes
import lib.sketchbook.SketchbookBottomSheet
import lib.sketchbook.SketchbookPreviewLayout
import lib.sketchbook.StripedBackdrop
import org.junit.Test
import org.robolectric.annotation.Config

class BottomSheetScreenshotTest : ScreenshotTest() {

    @Test
    @Config(sdk = [26], qualifiers = Light)
    fun `bottom sheet over stripes on android 8 in light theme`() = captureScreen("BottomSheet_sdk26_light") { Sheet() }

    @Test
    @Config(sdk = [26], qualifiers = Dark)
    fun `bottom sheet over stripes on android 8 in dark theme`() = captureScreen("BottomSheet_sdk26_dark") { Sheet() }

    @Test
    @Config(sdk = [33], qualifiers = Light)
    fun `bottom sheet over stripes on android 13 in light theme`() = captureScreen("BottomSheet_sdk33_light") { Sheet() }

    @Test
    @Config(sdk = [33], qualifiers = Dark)
    fun `bottom sheet over stripes on android 13 in dark theme`() = captureScreen("BottomSheet_sdk33_dark") { Sheet() }

    @Composable
    private fun Sheet() = SketchbookPreviewLayout {
        StripedBackdrop(Modifier.fillMaxSize(), colors = ColorfulStripes) {}
        SketchbookBottomSheet(visible = true, onDismissRequest = {}) {
            Text("Sheet title", Modifier.padding(16.dp))
            Text("Sheet content that sits on a tinted glass container", Modifier.padding(16.dp))
            Text("Another line", Modifier.padding(16.dp))
        }
    }
}
