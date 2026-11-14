package lib.sketchbook.screenshot

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import lib.sketchbook.ColorfulStripes
import lib.sketchbook.SketchbookFilledIconButton
import lib.sketchbook.SketchbookGlassIconButton
import lib.sketchbook.SketchbookIconButton
import lib.sketchbook.SketchbookPreviewLayout
import lib.sketchbook.SketchbookTonalIconButton
import lib.sketchbook.StripedBackdrop
import org.junit.Test
import org.robolectric.annotation.Config

class IconButtonScreenshotTest : ScreenshotTest() {

    @Test
    @Config(sdk = [26], qualifiers = Light)
    fun `icon buttons over stripes on android 8 in light theme`() = captureRoot("IconButtons_sdk26_light") { IconButtons() }

    @Test
    @Config(sdk = [26], qualifiers = Dark)
    fun `icon buttons over stripes on android 8 in dark theme`() = captureRoot("IconButtons_sdk26_dark") { IconButtons() }

    @Test
    @Config(sdk = [33], qualifiers = Light)
    fun `icon buttons over stripes on android 13 in light theme`() = captureRoot("IconButtons_sdk33_light") { IconButtons() }

    @Test
    @Config(sdk = [33], qualifiers = Dark)
    fun `icon buttons over stripes on android 13 in dark theme`() = captureRoot("IconButtons_sdk33_dark") { IconButtons() }

    @Composable
    private fun IconButtons() = SketchbookPreviewLayout {
        StripedBackdrop(Modifier.fillMaxSize(), colors = ColorfulStripes) {
            Row(Modifier.padding(24.dp)) {
                SketchbookIconButton({}, "Plain") { Icon(Icons.Default.Favorite, null) }
                SketchbookFilledIconButton({}, "Filled") { Icon(Icons.Default.Favorite, null) }
                SketchbookTonalIconButton({}, "Tonal") { Icon(Icons.Default.Favorite, null) }
                SketchbookGlassIconButton({}, "Glass") { Icon(Icons.Default.Favorite, null) }
                SketchbookGlassIconButton({}, "Disabled", enabled = false) { Icon(Icons.Default.Favorite, null) }
            }
        }
    }
}
