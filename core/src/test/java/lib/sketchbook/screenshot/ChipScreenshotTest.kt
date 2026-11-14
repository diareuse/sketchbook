package lib.sketchbook.screenshot

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import lib.sketchbook.ColorfulStripes
import lib.sketchbook.SketchbookChip
import lib.sketchbook.SketchbookFilterChip
import lib.sketchbook.SketchbookPreviewLayout
import lib.sketchbook.StripedBackdrop
import org.junit.Test
import org.robolectric.annotation.Config

class ChipScreenshotTest : ScreenshotTest() {

    @Test
    @Config(sdk = [26], qualifiers = Light)
    fun `chips over stripes on android 8 in light theme`() = captureRoot("Chips_sdk26_light") { Chips() }

    @Test
    @Config(sdk = [26], qualifiers = Dark)
    fun `chips over stripes on android 8 in dark theme`() = captureRoot("Chips_sdk26_dark") { Chips() }

    @Test
    @Config(sdk = [33], qualifiers = Light)
    fun `chips over stripes on android 13 in light theme`() = captureRoot("Chips_sdk33_light") { Chips() }

    @Test
    @Config(sdk = [33], qualifiers = Dark)
    fun `chips over stripes on android 13 in dark theme`() = captureRoot("Chips_sdk33_dark") { Chips() }

    @Composable
    private fun Chips() = SketchbookPreviewLayout {
        StripedBackdrop(Modifier.fillMaxSize(), colors = ColorfulStripes) {
            Column(Modifier.padding(24.dp)) {
                Row {
                    SketchbookChip({}, { Text("Assist") })
                    SketchbookChip({}, { Text("Off") }, Modifier.padding(start = 8.dp), enabled = false)
                }
                Row(Modifier.padding(top = 12.dp)) {
                    SketchbookFilterChip(
                        selected = true,
                        onClick = {},
                        label = { Text("Selected") },
                        leadingIcon = { Icon(Icons.Default.Check, null) }
                    )
                    SketchbookFilterChip(false, {}, { Text("Idle") }, Modifier.padding(start = 8.dp))
                }
            }
        }
    }
}
