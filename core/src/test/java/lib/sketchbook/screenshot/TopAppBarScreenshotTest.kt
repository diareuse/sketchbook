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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import lib.sketchbook.ColorfulStripes
import lib.sketchbook.SketchbookPreviewLayout
import lib.sketchbook.StripedBackdrop
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

    @Test
    @Config(sdk = [26], qualifiers = Light)
    fun `collapsed top app bar over stripes on android 8 in light theme`() = captureRoot("TopAppBarGlass_sdk26_light") { GlassCollapsed() }

    @Test
    @Config(sdk = [26], qualifiers = Dark)
    fun `collapsed top app bar over stripes on android 8 in dark theme`() = captureRoot("TopAppBarGlass_sdk26_dark") { GlassCollapsed() }

    @Test
    @Config(sdk = [33], qualifiers = Light)
    fun `collapsed top app bar over stripes on android 13 in light theme`() = captureRoot("TopAppBarGlass_sdk33_light") { GlassCollapsed() }

    @Test
    @Config(sdk = [33], qualifiers = Dark)
    fun `collapsed top app bar over stripes on android 13 in dark theme`() = captureRoot("TopAppBarGlass_sdk33_dark") { GlassCollapsed() }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    private fun GlassCollapsed() = SketchbookPreviewLayout(padding = PaddingValues()) {
        val behavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(
            state = rememberTopAppBarState(initialHeightOffset = -48f)
        )
        StripedBackdrop(Modifier.fillMaxSize(), colors = ColorfulStripes) {
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
