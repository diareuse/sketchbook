package lib.sketchbook.screenshot

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import lib.sketchbook.SketchbookAnimatedBackground
import lib.sketchbook.SketchbookCard
import lib.sketchbook.SketchbookPreviewLayout
import org.junit.Test
import org.robolectric.annotation.Config

class AnimatedBackgroundScreenshotTest : ScreenshotTest() {

    @Test
    @Config(sdk = [26], qualifiers = Light)
    fun `animated background on android 8 in light theme`() =
        captureRoot("AnimatedBackground_sdk26_light", animated = true) { Background() }

    @Test
    @Config(sdk = [26], qualifiers = Dark)
    fun `animated background on android 8 in dark theme`() =
        captureRoot("AnimatedBackground_sdk26_dark", animated = true) { Background() }

    @Test
    @Config(sdk = [33], qualifiers = Light)
    fun `animated background on android 13 in light theme`() =
        captureRoot("AnimatedBackground_sdk33_light", animated = true) { Background() }

    @Test
    @Config(sdk = [33], qualifiers = Dark)
    fun `animated background on android 13 in dark theme`() =
        captureRoot("AnimatedBackground_sdk33_dark", animated = true) { Background() }

    @Test
    @Config(sdk = [33], qualifiers = Light)
    fun `default animated background on android 13 in light theme`() =
        captureRoot("AnimatedBackgroundDefault_sdk33_light", animated = true) { DefaultBackground() }

    @Test
    @Config(sdk = [33], qualifiers = Dark)
    fun `default animated background on android 13 in dark theme`() =
        captureRoot("AnimatedBackgroundDefault_sdk33_dark", animated = true) { DefaultBackground() }

    @Test
    @Config(sdk = [26], qualifiers = Light)
    fun `default animated background on android 8 in light theme`() =
        captureRoot("AnimatedBackgroundDefault_sdk26_light", animated = true) { DefaultBackground() }

    @Test
    @Config(sdk = [26], qualifiers = Dark)
    fun `default animated background on android 8 in dark theme`() =
        captureRoot("AnimatedBackgroundDefault_sdk26_dark", animated = true) { DefaultBackground() }

    @Composable
    private fun DefaultBackground() = SketchbookPreviewLayout {
        SketchbookAnimatedBackground(modifier = Modifier.fillMaxSize()) {
            SketchbookCard(Modifier.align(Alignment.Center).padding(24.dp)) {
                Text("Glass over the default background", Modifier.padding(24.dp))
            }
        }
    }

    @Composable
    private fun Background() = SketchbookPreviewLayout {
        SketchbookAnimatedBackground(
            modifier = Modifier.fillMaxSize(),
            colors = listOf(Color(0xFFE53935), Color(0xFF1E88E5), Color(0xFF43A047), Color(0xFFFDD835))
        ) {
            SketchbookCard(Modifier.align(Alignment.Center).padding(24.dp)) {
                Text("Glass over an animated mesh", Modifier.padding(24.dp))
            }
        }
    }
}
