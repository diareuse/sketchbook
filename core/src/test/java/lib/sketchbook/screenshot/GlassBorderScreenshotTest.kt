package lib.sketchbook.screenshot

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import lib.sketchbook.ColorfulStripes
import lib.sketchbook.SketchbookGlassBorder
import lib.sketchbook.SketchbookPreviewLayout
import lib.sketchbook.StripedBackdrop
import org.junit.Test
import org.robolectric.annotation.Config

class GlassBorderScreenshotTest : ScreenshotTest() {

    @Test
    @Config(sdk = [26], qualifiers = Light)
    fun `glass border over stripes on android 8 in light theme`() =
        captureRoot("GlassBorder_sdk26_light", animated = true) { Borders() }

    @Test
    @Config(sdk = [26], qualifiers = Dark)
    fun `glass border over stripes on android 8 in dark theme`() =
        captureRoot("GlassBorder_sdk26_dark", animated = true) { Borders() }

    @Test
    @Config(sdk = [33], qualifiers = Light)
    fun `glass border over stripes on android 13 in light theme`() =
        captureRoot("GlassBorder_sdk33_light", animated = true) { Borders() }

    @Test
    @Config(sdk = [33], qualifiers = Dark)
    fun `glass border over stripes on android 13 in dark theme`() =
        captureRoot("GlassBorder_sdk33_dark", animated = true) { Borders() }

    @Composable
    private fun Borders() = SketchbookPreviewLayout {
        StripedBackdrop(Modifier.fillMaxSize(), colors = ColorfulStripes) {
            Column(Modifier.padding(24.dp), verticalArrangement = Arrangement.spacedBy(24.dp)) {
                SketchbookGlassBorder(Modifier.fillMaxWidth().height(120.dp), animated = true) {
                    Text("Animated sweep", Modifier.align(Alignment.Center))
                }
                SketchbookGlassBorder(Modifier.fillMaxWidth().height(120.dp), borderWidth = 3.dp) {
                    Text("Static rim", Modifier.align(Alignment.Center))
                }
                SketchbookGlassBorder(Modifier.height(120.dp).fillMaxWidth(), shape = CircleShape, animated = true) {
                    Text("Pill", Modifier.align(Alignment.Center))
                }
            }
        }
    }
}
