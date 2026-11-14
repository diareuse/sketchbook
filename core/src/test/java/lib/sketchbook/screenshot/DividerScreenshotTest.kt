package lib.sketchbook.screenshot

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import lib.sketchbook.SketchbookHorizontalDivider
import lib.sketchbook.SketchbookPreviewLayout
import lib.sketchbook.SketchbookVerticalDivider
import org.junit.Test
import org.robolectric.annotation.Config

class DividerScreenshotTest : ScreenshotTest() {

    @Test
    @Config(sdk = [26], qualifiers = Light)
    fun `dividers on android 8 in light theme`() = captureRoot("Dividers_sdk26_light") { Dividers() }

    @Test
    @Config(sdk = [26], qualifiers = Dark)
    fun `dividers on android 8 in dark theme`() = captureRoot("Dividers_sdk26_dark") { Dividers() }

    @Test
    @Config(sdk = [33], qualifiers = Light)
    fun `dividers on android 13 in light theme`() = captureRoot("Dividers_sdk33_light") { Dividers() }

    @Test
    @Config(sdk = [33], qualifiers = Dark)
    fun `dividers on android 13 in dark theme`() = captureRoot("Dividers_sdk33_dark") { Dividers() }

    @Composable
    private fun Dividers() = SketchbookPreviewLayout {
        Column(Modifier.fillMaxSize().padding(24.dp)) {
            Text("Above")
            SketchbookHorizontalDivider(Modifier.padding(vertical = 12.dp))
            Text("Below")
            SketchbookHorizontalDivider(Modifier.padding(vertical = 12.dp), thickness = 3.dp)
            Row(Modifier.height(96.dp)) {
                Text("Left")
                SketchbookVerticalDivider(Modifier.padding(horizontal = 12.dp))
                Text("Middle")
                SketchbookVerticalDivider(Modifier.padding(horizontal = 12.dp), thickness = 3.dp)
                Text("Right")
            }
        }
    }
}
