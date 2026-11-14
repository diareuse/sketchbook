package lib.sketchbook.screenshot

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import lib.sketchbook.ColorfulStripes
import lib.sketchbook.SketchbookPreviewLayout
import lib.sketchbook.SketchbookScrollableTabRow
import lib.sketchbook.SketchbookTab
import lib.sketchbook.SketchbookTabRow
import lib.sketchbook.StripedBackdrop
import org.junit.Test
import org.robolectric.annotation.Config

class TabRowScreenshotTest : ScreenshotTest() {

    @Test
    @Config(sdk = [26], qualifiers = Light)
    fun `tab rows over stripes on android 8 in light theme`() = captureRoot("TabRows_sdk26_light") { TabRows() }

    @Test
    @Config(sdk = [26], qualifiers = Dark)
    fun `tab rows over stripes on android 8 in dark theme`() = captureRoot("TabRows_sdk26_dark") { TabRows() }

    @Test
    @Config(sdk = [33], qualifiers = Light)
    fun `tab rows over stripes on android 13 in light theme`() = captureRoot("TabRows_sdk33_light") { TabRows() }

    @Test
    @Config(sdk = [33], qualifiers = Dark)
    fun `tab rows over stripes on android 13 in dark theme`() = captureRoot("TabRows_sdk33_dark") { TabRows() }

    @Composable
    private fun TabRows() = SketchbookPreviewLayout {
        StripedBackdrop(Modifier.fillMaxSize(), colors = ColorfulStripes) {
            Column(Modifier.padding(24.dp)) {
                SketchbookTabRow(selectedTabIndex = 1) {
                    SketchbookTab(false, {}, text = { Text("Photos") })
                    SketchbookTab(true, {}, text = { Text("Albums") })
                    SketchbookTab(false, {}, text = { Text("Places") })
                }
                SketchbookScrollableTabRow(selectedTabIndex = 0, modifier = Modifier.padding(top = 24.dp)) {
                    listOf("Recent", "Favourites", "Shared", "Archive", "Trash").forEachIndexed { index, title ->
                        SketchbookTab(index == 0, {}, text = { Text(title) })
                    }
                }
            }
        }
    }
}
