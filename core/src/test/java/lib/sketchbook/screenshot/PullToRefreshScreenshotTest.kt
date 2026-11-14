package lib.sketchbook.screenshot

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import lib.sketchbook.ColorfulStripes
import lib.sketchbook.SketchbookPreviewLayout
import lib.sketchbook.SketchbookPullToRefresh
import lib.sketchbook.StripedBackdrop
import org.junit.Test
import org.robolectric.annotation.Config

class PullToRefreshScreenshotTest : ScreenshotTest() {

    @Test
    @Config(sdk = [26], qualifiers = Light)
    fun `refreshing indicator over stripes on android 8 in light theme`() =
        captureRoot("PullToRefresh_sdk26_light", animated = true) { Refreshing() }

    @Test
    @Config(sdk = [26], qualifiers = Dark)
    fun `refreshing indicator over stripes on android 8 in dark theme`() =
        captureRoot("PullToRefresh_sdk26_dark", animated = true) { Refreshing() }

    @Test
    @Config(sdk = [33], qualifiers = Light)
    fun `refreshing indicator over stripes on android 13 in light theme`() =
        captureRoot("PullToRefresh_sdk33_light", animated = true) { Refreshing() }

    @Test
    @Config(sdk = [33], qualifiers = Dark)
    fun `refreshing indicator over stripes on android 13 in dark theme`() =
        captureRoot("PullToRefresh_sdk33_dark", animated = true) { Refreshing() }

    @Composable
    private fun Refreshing() = SketchbookPreviewLayout {
        StripedBackdrop(Modifier.fillMaxSize(), colors = ColorfulStripes) {
            SketchbookPullToRefresh(isRefreshing = true, onRefresh = {}, modifier = Modifier.fillMaxSize()) {}
        }
    }
}
