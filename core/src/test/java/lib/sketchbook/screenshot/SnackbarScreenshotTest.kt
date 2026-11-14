package lib.sketchbook.screenshot

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import lib.sketchbook.ColorfulStripes
import lib.sketchbook.SketchbookPreviewLayout
import lib.sketchbook.SketchbookScaffold
import lib.sketchbook.SketchbookSnackbarHost
import lib.sketchbook.StripedBackdrop
import org.junit.Test
import org.robolectric.annotation.Config

class SnackbarScreenshotTest : ScreenshotTest() {

    @Test
    @Config(sdk = [26], qualifiers = Light)
    fun `snackbars over stripes on android 8 in light theme`() =
        captureRoot("Snackbar_sdk26_light", animated = true) { Snackbars() }

    @Test
    @Config(sdk = [26], qualifiers = Dark)
    fun `snackbars over stripes on android 8 in dark theme`() =
        captureRoot("Snackbar_sdk26_dark", animated = true) { Snackbars() }

    @Test
    @Config(sdk = [33], qualifiers = Light)
    fun `snackbars over stripes on android 13 in light theme`() =
        captureRoot("Snackbar_sdk33_light", animated = true) { Snackbars() }

    @Test
    @Config(sdk = [33], qualifiers = Dark)
    fun `snackbars over stripes on android 13 in dark theme`() =
        captureRoot("Snackbar_sdk33_dark", animated = true) { Snackbars() }

    @Composable
    private fun Snackbars() = SketchbookPreviewLayout {
        val first = remember { SnackbarHostState() }
        val second = remember { SnackbarHostState() }
        LaunchedEffect(Unit) {
            first.showSnackbar("Message sent", duration = SnackbarDuration.Indefinite)
        }
        LaunchedEffect(Unit) {
            second.showSnackbar("Item deleted", "Undo", true, SnackbarDuration.Indefinite)
        }
        StripedBackdrop(Modifier.fillMaxSize(), colors = ColorfulStripes) {
            Column(Modifier.align(Alignment.Center).fillMaxWidth()) {
                SketchbookSnackbarHost(first)
                SketchbookSnackbarHost(second)
            }
        }
    }
}
