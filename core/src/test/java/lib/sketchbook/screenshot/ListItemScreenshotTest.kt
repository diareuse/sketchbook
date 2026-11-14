package lib.sketchbook.screenshot

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import lib.sketchbook.ColorfulStripes
import lib.sketchbook.SketchbookCard
import lib.sketchbook.SketchbookListItem
import lib.sketchbook.SketchbookPreviewLayout
import lib.sketchbook.StripedBackdrop
import lib.sketchbook.theme.Theme
import org.junit.Test
import org.robolectric.annotation.Config

class ListItemScreenshotTest : ScreenshotTest() {

    @Test
    @Config(sdk = [26], qualifiers = Light)
    fun `list items over stripes on android 8 in light theme`() = captureRoot("ListItems_sdk26_light") { ListItems() }

    @Test
    @Config(sdk = [26], qualifiers = Dark)
    fun `list items over stripes on android 8 in dark theme`() = captureRoot("ListItems_sdk26_dark") { ListItems() }

    @Test
    @Config(sdk = [33], qualifiers = Light)
    fun `list items over stripes on android 13 in light theme`() = captureRoot("ListItems_sdk33_light") { ListItems() }

    @Test
    @Config(sdk = [33], qualifiers = Dark)
    fun `list items over stripes on android 13 in dark theme`() = captureRoot("ListItems_sdk33_dark") { ListItems() }

    @Composable
    private fun ListItems() = SketchbookPreviewLayout {
        StripedBackdrop(Modifier.fillMaxSize(), colors = ColorfulStripes) {
            SketchbookCard(Modifier.padding(24.dp).fillMaxWidth()) {
                val color = Theme.color.emphasis.background
                SketchbookListItem(
                    headline = { Text("Headline") },
                    supporting = { Text("Supporting text") },
                    leading = { Icon(Icons.Default.Star, null) },
                    compositeColor = color
                )
                SketchbookListItem(
                    headline = { Text("Selected item") },
                    overline = { Text("Overline") },
                    supporting = { Text("Supporting text") },
                    trailing = { Text("Trailing") },
                    onClick = {},
                    selected = true,
                    compositeColor = color
                )
                SketchbookListItem(
                    headline = { Text("Disabled item") },
                    onClick = {},
                    enabled = false,
                    compositeColor = color
                )
            }
        }
    }
}
