package lib.sketchbook.screenshot

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Badge
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import lib.sketchbook.ColorfulStripes
import lib.sketchbook.SketchbookNavigationBar
import lib.sketchbook.SketchbookNavigationBarItem
import lib.sketchbook.SketchbookPreviewLayout
import lib.sketchbook.StripedBackdrop
import org.junit.Test
import org.robolectric.annotation.Config

class NavigationBarScreenshotTest : ScreenshotTest() {

    @Test
    @Config(sdk = [26], qualifiers = Light)
    fun `navigation bar over stripes on android 8 in light theme`() = captureRoot("NavigationBar_sdk26_light") { Bar() }

    @Test
    @Config(sdk = [26], qualifiers = Dark)
    fun `navigation bar over stripes on android 8 in dark theme`() = captureRoot("NavigationBar_sdk26_dark") { Bar() }

    @Test
    @Config(sdk = [33], qualifiers = Light)
    fun `navigation bar over stripes on android 13 in light theme`() = captureRoot("NavigationBar_sdk33_light") { Bar() }

    @Test
    @Config(sdk = [33], qualifiers = Dark)
    fun `navigation bar over stripes on android 13 in dark theme`() = captureRoot("NavigationBar_sdk33_dark") { Bar() }

    @Composable
    private fun Bar() = SketchbookPreviewLayout {
        StripedBackdrop(Modifier.fillMaxSize(), colors = ColorfulStripes) {
            Box(Modifier.align(Alignment.BottomCenter)) {
                SketchbookNavigationBar {
                    SketchbookNavigationBarItem(true, {}, { Icon(Icons.Default.Home, null) }, label = { Text("Home") })
                    SketchbookNavigationBarItem(false, {}, { Icon(Icons.Default.Search, null) }, label = { Text("Search") })
                    SketchbookNavigationBarItem(
                        selected = false,
                        onClick = {},
                        icon = { Icon(Icons.Default.Settings, null) },
                        label = { Text("Settings") },
                        badge = { Badge { Text("3") } }
                    )
                }
            }
        }
    }
}
