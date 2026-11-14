package lib.sketchbook.screenshot

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import lib.sketchbook.SketchbookBadge
import lib.sketchbook.SketchbookBadgedBox
import lib.sketchbook.SketchbookPreviewLayout
import org.junit.Test
import org.robolectric.annotation.Config

class BadgeScreenshotTest : ScreenshotTest() {

    @Test
    @Config(sdk = [26], qualifiers = Light)
    fun `badges on android 8 in light theme`() = captureRoot("Badge_sdk26_light") { Badges() }

    @Test
    @Config(sdk = [26], qualifiers = Dark)
    fun `badges on android 8 in dark theme`() = captureRoot("Badge_sdk26_dark") { Badges() }

    @Test
    @Config(sdk = [33], qualifiers = Light)
    fun `badges on android 13 in light theme`() = captureRoot("Badge_sdk33_light") { Badges() }

    @Test
    @Config(sdk = [33], qualifiers = Dark)
    fun `badges on android 13 in dark theme`() = captureRoot("Badge_sdk33_dark") { Badges() }

    @Composable
    private fun Badges() = SketchbookPreviewLayout {
        Row(Modifier.padding(24.dp), horizontalArrangement = Arrangement.spacedBy(24.dp)) {
            SketchbookBadge()
            SketchbookBadge(count = 7)
            SketchbookBadge(count = 250)
            SketchbookBadgedBox(badge = { SketchbookBadge(count = 3) }) {
                Icon(Icons.Default.Notifications, contentDescription = null)
            }
            SketchbookBadgedBox(badge = { SketchbookBadge() }) {
                Icon(Icons.Default.Notifications, contentDescription = null)
            }
        }
    }
}
