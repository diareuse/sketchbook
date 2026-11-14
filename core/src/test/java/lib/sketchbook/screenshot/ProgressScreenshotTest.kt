package lib.sketchbook.screenshot

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import lib.sketchbook.ColorfulStripes
import lib.sketchbook.SketchbookCircularProgress
import lib.sketchbook.SketchbookLinearProgress
import lib.sketchbook.SketchbookPreviewLayout
import lib.sketchbook.StripedBackdrop
import org.junit.Test
import org.robolectric.annotation.Config

class ProgressScreenshotTest : ScreenshotTest() {

    @Test
    @Config(sdk = [26], qualifiers = Light)
    fun `progress over stripes on android 8 in light theme`() =
        captureRoot("Progress_sdk26_light", animated = true) { Progress() }

    @Test
    @Config(sdk = [26], qualifiers = Dark)
    fun `progress over stripes on android 8 in dark theme`() =
        captureRoot("Progress_sdk26_dark", animated = true) { Progress() }

    @Test
    @Config(sdk = [33], qualifiers = Light)
    fun `progress over stripes on android 13 in light theme`() =
        captureRoot("Progress_sdk33_light", animated = true) { Progress() }

    @Test
    @Config(sdk = [33], qualifiers = Dark)
    fun `progress over stripes on android 13 in dark theme`() =
        captureRoot("Progress_sdk33_dark", animated = true) { Progress() }

    @Composable
    private fun Progress() = SketchbookPreviewLayout {
        StripedBackdrop(Modifier.fillMaxSize(), colors = ColorfulStripes) {
            Column(Modifier.padding(24.dp), verticalArrangement = Arrangement.spacedBy(24.dp)) {
                SketchbookLinearProgress(progress = { 0f })
                SketchbookLinearProgress(progress = { .4f })
                SketchbookLinearProgress(progress = { 1f })
                SketchbookLinearProgress()
                Row(horizontalArrangement = Arrangement.spacedBy(24.dp)) {
                    SketchbookCircularProgress(progress = { .25f })
                    SketchbookCircularProgress(progress = { .75f })
                    SketchbookCircularProgress()
                }
            }
        }
    }
}
