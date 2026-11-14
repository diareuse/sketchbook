package lib.sketchbook.screenshot

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import lib.sketchbook.ColorfulStripes
import lib.sketchbook.SketchbookMultiChoiceSegmentedButton
import lib.sketchbook.SketchbookPreviewLayout
import lib.sketchbook.SketchbookSegmentedButton
import lib.sketchbook.StripedBackdrop
import org.junit.Test
import org.robolectric.annotation.Config

class SegmentedButtonScreenshotTest : ScreenshotTest() {

    private val options = listOf("Day", "Week", "Month")

    @Test
    @Config(sdk = [26], qualifiers = Light)
    fun `segmented buttons over stripes on android 8 in light theme`() = captureRoot("Segmented_sdk26_light") { Segmented() }

    @Test
    @Config(sdk = [26], qualifiers = Dark)
    fun `segmented buttons over stripes on android 8 in dark theme`() = captureRoot("Segmented_sdk26_dark") { Segmented() }

    @Test
    @Config(sdk = [33], qualifiers = Light)
    fun `segmented buttons over stripes on android 13 in light theme`() = captureRoot("Segmented_sdk33_light") { Segmented() }

    @Test
    @Config(sdk = [33], qualifiers = Dark)
    fun `segmented buttons over stripes on android 13 in dark theme`() = captureRoot("Segmented_sdk33_dark") { Segmented() }

    @Composable
    private fun Segmented() = SketchbookPreviewLayout {
        StripedBackdrop(Modifier.fillMaxSize(), colors = ColorfulStripes) {
            Column(Modifier.padding(24.dp)) {
                SketchbookSegmentedButton(1, {}, options.size, label = { Text(options[it]) })
                SketchbookMultiChoiceSegmentedButton(
                    setOf(0, 2),
                    {},
                    options.size,
                    Modifier.padding(top = 16.dp),
                    label = { Text(options[it]) }
                )
                SketchbookSegmentedButton(
                    1,
                    {},
                    options.size,
                    Modifier.padding(top = 16.dp),
                    label = { Text(options[it]) },
                    enabled = false
                )
            }
        }
    }
}
