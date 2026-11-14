package lib.sketchbook.screenshot

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Badge
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import lib.sketchbook.ColorfulStripes
import lib.sketchbook.SketchbookNavigationRail
import lib.sketchbook.SketchbookNavigationRailItem
import lib.sketchbook.SketchbookPreviewLayout
import lib.sketchbook.StripedBackdrop
import org.junit.Test
import org.robolectric.annotation.Config

class NavigationRailScreenshotTest : ScreenshotTest() {

    @Test
    @Config(sdk = [26], qualifiers = Light)
    fun `navigation rail over stripes on android 8 in light theme`() = captureRoot("NavigationRail_sdk26_light") { Rail() }

    @Test
    @Config(sdk = [26], qualifiers = Dark)
    fun `navigation rail over stripes on android 8 in dark theme`() = captureRoot("NavigationRail_sdk26_dark") { Rail() }

    @Test
    @Config(sdk = [33], qualifiers = Light)
    fun `navigation rail over stripes on android 13 in light theme`() = captureRoot("NavigationRail_sdk33_light") { Rail() }

    @Test
    @Config(sdk = [33], qualifiers = Dark)
    fun `navigation rail over stripes on android 13 in dark theme`() = captureRoot("NavigationRail_sdk33_dark") { Rail() }

    @Composable
    private fun Rail() = SketchbookPreviewLayout {
        StripedBackdrop(Modifier.fillMaxSize(), colors = ColorfulStripes) {
            SketchbookNavigationRail(header = { Text("Menu") }) {
                SketchbookNavigationRailItem(true, {}, { Icon(Icons.Default.Home, null) }, label = { Text("Home") })
                SketchbookNavigationRailItem(false, {}, { Icon(Icons.Default.Search, null) }, label = { Text("Search") })
                SketchbookNavigationRailItem(
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
