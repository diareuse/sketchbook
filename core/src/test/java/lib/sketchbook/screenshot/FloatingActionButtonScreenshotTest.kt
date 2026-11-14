package lib.sketchbook.screenshot

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import lib.sketchbook.ColorfulStripes
import lib.sketchbook.SketchbookExtendedFloatingActionButton
import lib.sketchbook.SketchbookFloatingActionButton
import lib.sketchbook.SketchbookPreviewLayout
import lib.sketchbook.StripedBackdrop
import org.junit.Test
import org.robolectric.annotation.Config

class FloatingActionButtonScreenshotTest : ScreenshotTest() {

    @Test
    @Config(sdk = [26], qualifiers = Light)
    fun `floating action buttons over stripes on android 8 in light theme`() = captureRoot("Fabs_sdk26_light") { Fabs() }

    @Test
    @Config(sdk = [26], qualifiers = Dark)
    fun `floating action buttons over stripes on android 8 in dark theme`() = captureRoot("Fabs_sdk26_dark") { Fabs() }

    @Test
    @Config(sdk = [33], qualifiers = Light)
    fun `floating action buttons over stripes on android 13 in light theme`() = captureRoot("Fabs_sdk33_light") { Fabs() }

    @Test
    @Config(sdk = [33], qualifiers = Dark)
    fun `floating action buttons over stripes on android 13 in dark theme`() = captureRoot("Fabs_sdk33_dark") { Fabs() }

    @Composable
    private fun Fabs() = SketchbookPreviewLayout {
        StripedBackdrop(Modifier.fillMaxSize(), colors = ColorfulStripes) {
            Column(Modifier.padding(24.dp)) {
                SketchbookFloatingActionButton({}, "Add") { Icon(Icons.Default.Add, null) }
                SketchbookExtendedFloatingActionButton(
                    onClick = {},
                    modifier = Modifier.padding(top = 16.dp),
                    icon = { Icon(Icons.Default.Add, null) }
                ) { Text("Create") }
            }
        }
    }
}
