package lib.sketchbook.screenshot

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import lib.sketchbook.ColorfulStripes
import lib.sketchbook.SketchbookPreviewLayout
import lib.sketchbook.SketchbookToastHost
import lib.sketchbook.SketchbookToastState
import lib.sketchbook.StripedBackdrop
import lib.sketchbook.ToastDuration
import org.junit.Test
import org.robolectric.annotation.Config

class ToastScreenshotTest : ScreenshotTest() {

    @Test
    @Config(sdk = [26], qualifiers = Light)
    fun `toast over stripes on android 8 in light theme`() = captureRoot("Toast_sdk26_light", animated = true) { Toast() }

    @Test
    @Config(sdk = [26], qualifiers = Dark)
    fun `toast over stripes on android 8 in dark theme`() = captureRoot("Toast_sdk26_dark", animated = true) { Toast() }

    @Test
    @Config(sdk = [33], qualifiers = Light)
    fun `toast over stripes on android 13 in light theme`() = captureRoot("Toast_sdk33_light", animated = true) { Toast() }

    @Test
    @Config(sdk = [33], qualifiers = Dark)
    fun `toast over stripes on android 13 in dark theme`() = captureRoot("Toast_sdk33_dark", animated = true) { Toast() }

    @Composable
    private fun Toast() = SketchbookPreviewLayout {
        val state = remember { SketchbookToastState() }
        LaunchedEffect(Unit) { state.show("Copied to clipboard", ToastDuration(60_000)) }
        StripedBackdrop(Modifier.fillMaxSize(), colors = ColorfulStripes) {
            SketchbookToastHost(state, Modifier.align(Alignment.Center))
        }
    }
}
