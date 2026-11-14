package lib.sketchbook.screenshot

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import lib.sketchbook.ColorfulStripes
import lib.sketchbook.SketchbookCard
import lib.sketchbook.SketchbookFloatingActionButton
import lib.sketchbook.SketchbookPreviewLayout
import lib.sketchbook.SketchbookScaffold
import lib.sketchbook.StripedBackdrop
import org.junit.Test
import org.robolectric.annotation.Config

class ScaffoldScreenshotTest : ScreenshotTest() {

    @Test
    @Config(sdk = [26], qualifiers = Light)
    fun `scaffold with glass bars on android 8 in light theme`() = captureRoot("Scaffold_sdk26_light") { Scaffold() }

    @Test
    @Config(sdk = [26], qualifiers = Dark)
    fun `scaffold with glass bars on android 8 in dark theme`() = captureRoot("Scaffold_sdk26_dark") { Scaffold() }

    @Test
    @Config(sdk = [33], qualifiers = Light)
    fun `scaffold with glass bars on android 13 in light theme`() = captureRoot("Scaffold_sdk33_light") { Scaffold() }

    @Test
    @Config(sdk = [33], qualifiers = Dark)
    fun `scaffold with glass bars on android 13 in dark theme`() = captureRoot("Scaffold_sdk33_dark") { Scaffold() }

    @Composable
    private fun Scaffold() = SketchbookPreviewLayout {
        SketchbookScaffold(
            modifier = Modifier.fillMaxSize(),
            topBar = {
                SketchbookCard(Modifier.padding(16.dp).fillMaxWidth()) {
                    Text("Top bar", Modifier.padding(16.dp))
                }
            },
            bottomBar = {
                SketchbookCard(Modifier.padding(16.dp).fillMaxWidth().height(72.dp)) {
                    Text("Bottom bar", Modifier.padding(16.dp))
                }
            },
            floatingActionButton = {
                SketchbookFloatingActionButton({}, "Add", Modifier.padding(bottom = 8.dp)) {
                    Icon(Icons.Default.Add, null)
                }
            }
        ) {
            StripedBackdrop(Modifier.fillMaxSize(), source = false, colors = ColorfulStripes) {}
        }
    }
}
