package lib.sketchbook.screenshot

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import lib.sketchbook.SketchbookPreviewLayout
import lib.sketchbook.SketchbookTopAppBar
import org.junit.Test
import org.robolectric.annotation.Config

class TopAppBarScreenshotTest : ScreenshotTest() {

    @Test
    @Config(qualifiers = Light)
    fun `top app bar expanded in light theme`() = captureRoot("TopAppBarExpanded_light") { Expanded() }

    @Test
    @Config(qualifiers = Dark)
    fun `top app bar expanded in dark theme`() = captureRoot("TopAppBarExpanded_dark") { Expanded() }

    @Test
    @Config(qualifiers = Light)
    fun `top app bar collapsed in light theme`() = captureRoot("TopAppBarCollapsed_light") { Collapsed() }

    @Test
    @Config(qualifiers = Dark)
    fun `top app bar collapsed in dark theme`() = captureRoot("TopAppBarCollapsed_dark") { Collapsed() }

    @Composable
    private fun Expanded() = Bar(initialHeightOffset = 0f)

    @Composable
    private fun Collapsed() = Bar(initialHeightOffset = -60f)

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    private fun Bar(initialHeightOffset: Float) =
        SketchbookPreviewLayout(padding = PaddingValues()) {
            val behavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(
                state = rememberTopAppBarState(initialHeightOffset = initialHeightOffset)
            )
            Column {
                SketchbookTopAppBar(
                    scrollBehavior = behavior,
                    title = { Text("I'm title") },
                    navigationIcon = {
                        IconButton(onClick = {}) { Icon(Icons.AutoMirrored.Default.ArrowBack, null) }
                    },
                    actions = {
                        IconButton(onClick = {}) { Icon(Icons.Default.MoreVert, null) }
                    }
                )
            }
        }
}
