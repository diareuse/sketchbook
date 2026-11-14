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
import lib.sketchbook.ColorfulStripes
import lib.sketchbook.StripedBackdrop
import lib.sketchbook.popOutBackground
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

    @Test
    @Config(sdk = [26], qualifiers = Light)
    fun `glass dialog surface over stripes on android 8 in light theme`() = captureRoot("DialogSurfaceGlass_sdk26_light") { GlassDialogSurface() }

    @Test
    @Config(sdk = [26], qualifiers = Dark)
    fun `glass dialog surface over stripes on android 8 in dark theme`() = captureRoot("DialogSurfaceGlass_sdk26_dark") { GlassDialogSurface() }

    @Test
    @Config(sdk = [33], qualifiers = Light)
    fun `glass dialog surface over stripes on android 13 in light theme`() = captureRoot("DialogSurfaceGlass_sdk33_light") { GlassDialogSurface() }

    @Test
    @Config(sdk = [33], qualifiers = Dark)
    fun `glass dialog surface over stripes on android 13 in dark theme`() = captureRoot("DialogSurfaceGlass_sdk33_dark") { GlassDialogSurface() }

    @Test
    @Config(sdk = [26], qualifiers = Light)
    fun `dialog over striped content on android 8 in light theme`() = captureScreen("DialogGlass_sdk26_light") { GlassDialog() }

    @Test
    @Config(sdk = [26], qualifiers = Dark)
    fun `dialog over striped content on android 8 in dark theme`() = captureScreen("DialogGlass_sdk26_dark") { GlassDialog() }

    @Test
    @Config(sdk = [33], qualifiers = Light)
    fun `dialog over striped content on android 13 in light theme`() = captureScreen("DialogGlass_sdk33_light") { GlassDialog() }

    @Test
    @Config(sdk = [33], qualifiers = Dark)
    fun `dialog over striped content on android 13 in dark theme`() = captureScreen("DialogGlass_sdk33_dark") { GlassDialog() }

    @Test
    @Config(sdk = [26], qualifiers = Light)
    fun `opened menu over striped content on android 8 in light theme`() = captureScreen("MenuGlass_sdk26_light") { GlassMenu() }

    @Test
    @Config(sdk = [26], qualifiers = Dark)
    fun `opened menu over striped content on android 8 in dark theme`() = captureScreen("MenuGlass_sdk26_dark") { GlassMenu() }

    @Test
    @Config(sdk = [33], qualifiers = Light)
    fun `opened menu over striped content on android 13 in light theme`() = captureScreen("MenuGlass_sdk33_light") { GlassMenu() }

    @Test
    @Config(sdk = [33], qualifiers = Dark)
    fun `opened menu over striped content on android 13 in dark theme`() = captureScreen("MenuGlass_sdk33_dark") { GlassMenu() }

    @Composable
    private fun GlassDialogSurface() = SketchbookPreviewLayout {
        StripedBackdrop(Modifier.fillMaxSize(), colors = ColorfulStripes) {
            DialogSurface(Modifier.align(Alignment.Center)) {
                Column(Modifier.padding(16.dp)) { Text("Dialog") }
            }
        }
    }

    @Composable
    private fun GlassDialog() = SketchbookPreviewLayout {
        SketchbookDialog(
            visible = true,
            onVisibleChange = {},
            dialog = {
                DialogSurface {
                    Column(Modifier.padding(16.dp)) { Text("Dialog") }
                }
            }
        ) {
            StripedBackdrop(Modifier.fillMaxSize(), colors = ColorfulStripes) {}
        }
    }

    @Composable
    private fun GlassMenu() = SketchbookPreviewLayout {
        val overlay = remember { OverlayState() }
        val state = overlay.rememberMenuState()
        LaunchedEffect(Unit) { state.open() }
        StripedBackdrop(
            Modifier.fillMaxSize().popOutBackground(overlay),
            colors = ColorfulStripes
        ) {
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
