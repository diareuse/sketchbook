package lib.sketchbook.screenshot

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import lib.sketchbook.ColorfulStripes
import lib.sketchbook.SketchbookCard
import lib.sketchbook.SketchbookPreviewLayout
import lib.sketchbook.StripedBackdrop
import org.junit.Test
import org.robolectric.annotation.Config

class CardScreenshotTest : ScreenshotTest() {

    @Test
    @Config(sdk = [26], qualifiers = Light)
    fun `cards over stripes on android 8 in light theme`() = captureRoot("Cards_sdk26_light", animated = true) { Cards() }

    @Test
    @Config(sdk = [26], qualifiers = Dark)
    fun `cards over stripes on android 8 in dark theme`() = captureRoot("Cards_sdk26_dark", animated = true) { Cards() }

    @Test
    @Config(sdk = [33], qualifiers = Light)
    fun `cards over stripes on android 13 in light theme`() = captureRoot("Cards_sdk33_light", animated = true) { Cards() }

    @Test
    @Config(sdk = [33], qualifiers = Dark)
    fun `cards over stripes on android 13 in dark theme`() = captureRoot("Cards_sdk33_dark", animated = true) { Cards() }

    @Composable
    private fun Cards() = SketchbookPreviewLayout {
        StripedBackdrop(Modifier.fillMaxSize(), colors = ColorfulStripes) {
            Column(Modifier.padding(24.dp)) {
                SketchbookCard(Modifier.fillMaxWidth()) {
                    Text("Static card", Modifier.padding(16.dp))
                }
                SketchbookCard(onClick = {}, modifier = Modifier.padding(top = 16.dp).fillMaxWidth(), useGlow = true) {
                    Text("Clickable card with glow", Modifier.padding(16.dp))
                }
                SketchbookCard(onClick = {}, modifier = Modifier.padding(top = 16.dp).fillMaxWidth(), specularRim = true) {
                    Text("Specular rim", Modifier.padding(16.dp))
                }
                SketchbookCard(onClick = {}, modifier = Modifier.padding(top = 16.dp).fillMaxWidth(), enabled = false) {
                    Text("Disabled card", Modifier.padding(16.dp))
                }
            }
        }
    }
}
