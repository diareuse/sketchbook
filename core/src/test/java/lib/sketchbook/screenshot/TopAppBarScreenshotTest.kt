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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.withFrameNanos
import lib.sketchbook.SketchbookPreviewLayout
import lib.sketchbook.SketchbookTopAppBar

import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class TopAppBarScreenshotTest : ScreenshotTest() {

    @Test
    @Config(qualifiers = Light)
    fun TopAppBarExpandedLight() = captureRoot("TopAppBarExpanded_light") { TopAppBarExpandedContent() }

    @Test
    @Config(qualifiers = Dark)
    fun TopAppBarExpandedDark() = captureRoot("TopAppBarExpanded_dark") { TopAppBarExpandedContent() }

    @Test
    @Config(qualifiers = Light)
    fun TopAppBarCollapsedLight() = captureRoot("TopAppBarCollapsed_light") { TopAppBarCollapsedContent() }

    @Test
    @Config(qualifiers = Dark)
    fun TopAppBarCollapsedDark() = captureRoot("TopAppBarCollapsed_dark") { TopAppBarCollapsedContent() }

    @Composable
    private fun TopAppBarExpandedContent() = TopAppBarContent(collapse = false)

    @Composable
    private fun TopAppBarCollapsedContent() = TopAppBarContent(collapse = true)

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    private fun TopAppBarContent(collapse: Boolean) =
        SketchbookPreviewLayout(padding = PaddingValues()) {
            val behavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
            if (collapse) LaunchedEffect(Unit) {
                withFrameNanos { }
                behavior.state.heightOffset = -60f
            }
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
