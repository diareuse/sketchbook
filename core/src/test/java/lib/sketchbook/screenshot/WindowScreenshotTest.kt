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
import lib.sketchbook.SketchbookDialog
import lib.sketchbook.SketchbookMenu
import lib.sketchbook.SketchbookPreviewLayout
import lib.sketchbook.OverlayState
import lib.sketchbook.rememberMenuState

import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class WindowScreenshotTest : ScreenshotTest() {

    @Test
    @Config(qualifiers = Light)
    fun DialogSurfaceLight() = captureRoot("DialogSurface_light") { DialogSurfaceContent() }

    @Test
    @Config(qualifiers = Dark)
    fun DialogSurfaceDark() = captureRoot("DialogSurface_dark") { DialogSurfaceContent() }

    @Test
    @Config(qualifiers = Light)
    fun DialogLight() = captureScreen("Dialog_light") { DialogContent() }

    @Test
    @Config(qualifiers = Dark)
    fun DialogDark() = captureScreen("Dialog_dark") { DialogContent() }

    @Test
    @Config(qualifiers = Light)
    fun MenuClosedLight() = captureRoot("MenuClosed_light") { MenuClosedContent() }

    @Test
    @Config(qualifiers = Dark)
    fun MenuClosedDark() = captureRoot("MenuClosed_dark") { MenuClosedContent() }

    @Test
    @Config(qualifiers = Light)
    fun MenuOpenLight() = captureScreen("MenuOpen_light") { MenuOpenContent() }

    @Test
    @Config(qualifiers = Dark)
    fun MenuOpenDark() = captureScreen("MenuOpen_dark") { MenuOpenContent() }

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
    private fun MenuClosedContent() = MenuContent(open = false)

    @Composable
    private fun MenuOpenContent() = MenuContent(open = true)

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
