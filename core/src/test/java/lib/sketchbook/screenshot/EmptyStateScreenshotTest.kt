package lib.sketchbook.screenshot

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import lib.sketchbook.SketchbookButton
import lib.sketchbook.SketchbookEmptyState
import lib.sketchbook.SketchbookPreviewLayout
import org.junit.Test
import org.robolectric.annotation.Config

class EmptyStateScreenshotTest : ScreenshotTest() {

    @Test
    @Config(sdk = [26], qualifiers = Light)
    fun `empty state on android 8 in light theme`() = captureRoot("EmptyState_sdk26_light") { EmptyState() }

    @Test
    @Config(sdk = [26], qualifiers = Dark)
    fun `empty state on android 8 in dark theme`() = captureRoot("EmptyState_sdk26_dark") { EmptyState() }

    @Test
    @Config(sdk = [33], qualifiers = Light)
    fun `empty state on android 13 in light theme`() = captureRoot("EmptyState_sdk33_light") { EmptyState() }

    @Test
    @Config(sdk = [33], qualifiers = Dark)
    fun `empty state on android 13 in dark theme`() = captureRoot("EmptyState_sdk33_dark") { EmptyState() }

    @Composable
    private fun EmptyState() = SketchbookPreviewLayout(Modifier.fillMaxSize()) {
        SketchbookEmptyState(
            title = "No results found",
            message = "Try a different search term or clear the active filters.",
            icon = { Icon(Icons.Default.Search, contentDescription = null, modifier = Modifier.size(48.dp)) },
            action = { SketchbookButton(onClick = {}) { Text("Clear filters") } }
        )
    }
}
