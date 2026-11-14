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
import lib.sketchbook.SketchbookSearchBar
import lib.sketchbook.StripedBackdrop
import org.junit.Test
import org.robolectric.annotation.Config

class SearchBarScreenshotTest : ScreenshotTest() {

    @Test
    @Config(sdk = [26], qualifiers = Light)
    fun `search bars over stripes on android 8 in light theme`() = captureRoot("SearchBars_sdk26_light") { SearchBars() }

    @Test
    @Config(sdk = [26], qualifiers = Dark)
    fun `search bars over stripes on android 8 in dark theme`() = captureRoot("SearchBars_sdk26_dark") { SearchBars() }

    @Test
    @Config(sdk = [33], qualifiers = Light)
    fun `search bars over stripes on android 13 in light theme`() = captureRoot("SearchBars_sdk33_light") { SearchBars() }

    @Test
    @Config(sdk = [33], qualifiers = Dark)
    fun `search bars over stripes on android 13 in dark theme`() = captureRoot("SearchBars_sdk33_dark") { SearchBars() }

    @Composable
    private fun SearchBars() = SketchbookPreviewLayout {
        StripedBackdrop(Modifier.fillMaxSize(), colors = ColorfulStripes) {
            Column(Modifier.padding(24.dp)) {
                SketchbookSearchBar(
                    query = "",
                    onQueryChange = {},
                    onSearch = {},
                    clearContentDescription = "Clear",
                    placeholder = { Text("Search") }
                )
                SketchbookSearchBar(
                    query = "Sketch",
                    onQueryChange = {},
                    onSearch = {},
                    clearContentDescription = "Clear",
                    modifier = Modifier.padding(top = 16.dp),
                    results = {
                        Text("Sketchbook", modifier = Modifier.padding(16.dp))
                        Text("Sketch pad", modifier = Modifier.padding(16.dp))
                    }
                )
            }
        }
    }
}
