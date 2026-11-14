package lib.sketchbook.screenshot

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import lib.sketchbook.DialogSurface
import lib.sketchbook.OverlayState
import lib.sketchbook.SketchbookDialog
import lib.sketchbook.SketchbookMenu
import lib.sketchbook.SketchbookPreviewLayout
import lib.sketchbook.rememberMenuState
import org.junit.Test
import org.robolectric.annotation.Config

class WindowScreenshotTest : ScreenshotTest() {

    @Test
    @Config(qualifiers = Light)
    fun `dialog surface with text in light theme`() = captureRoot("DialogSurface_light") { DialogSurfaceContent() }

    @Test
    @Config(qualifiers = Dark)
    fun `dialog surface with text in dark theme`() = captureRoot("DialogSurface_dark") { DialogSurfaceContent() }

    @Test
    @Config(qualifiers = Light)
    fun `visible dialog over blurred content in light theme`() = captureScreen("Dialog_light") { DialogContent() }

    @Test
    @Config(qualifiers = Dark)
    fun `visible dialog over blurred content in dark theme`() = captureScreen("Dialog_dark") { DialogContent() }

    @Test
    @Config(qualifiers = Light)
    fun `closed menu anchor in light theme`() = captureRoot("MenuClosed_light") { MenuClosed() }

    @Test
    @Config(qualifiers = Dark)
    fun `closed menu anchor in dark theme`() = captureRoot("MenuClosed_dark") { MenuClosed() }

    @Test
    @Config(qualifiers = Light)
    fun `opened menu with expansion in light theme`() = captureScreen("MenuOpen_light") { MenuOpen() }

    @Test
    @Config(qualifiers = Dark)
    fun `opened menu with expansion in dark theme`() = captureScreen("MenuOpen_dark") { MenuOpen() }

    @Composable
    private fun DialogSurfaceContent() = SketchbookPreviewLayout {
        DialogSurface {
            Column(Modifier.padding(16.dp)) { Text("Dialog") }
        }
    }

    @Composable
    private fun DialogContent() = SketchbookPreviewLayout {
        SketchbookDialog(
            visible = true,
            onVisibleChange = {},
            dialog = {
                DialogSurface {
                    Column(Modifier.padding(16.dp)) { Text("Dialog") }
                }
            }
        ) {
            Box(Modifier.fillMaxSize().background(Color.Gray))
        }
    }

    @Composable
    private fun MenuClosed() = MenuContent(open = false)

    @Composable
    private fun MenuOpen() = MenuContent(open = true)

    @Composable
    private fun MenuContent(open: Boolean) = SketchbookPreviewLayout {
        val overlay = remember { OverlayState() }
        val state = overlay.rememberMenuState()
        if (open) LaunchedEffect(Unit) { state.open() }
        Box(Modifier.fillMaxSize().padding(32.dp), contentAlignment = Alignment.TopStart) {
            SketchbookMenu(
                state = state,
                expansion = {
                    DialogSurface { Text("Expansion", Modifier.padding(16.dp)) }
                }
            ) {
                Box(Modifier.size(64.dp).background(Color(0xFF3F51B5)))
            }
        }
    }
}
