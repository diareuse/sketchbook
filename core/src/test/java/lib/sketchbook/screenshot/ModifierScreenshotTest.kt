package lib.sketchbook.screenshot

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import lib.sketchbook.SketchbookPreviewLayout
import lib.sketchbook.modifier.glow
import lib.sketchbook.modifier.overlay
import lib.sketchbook.modifier.stroke
import lib.sketchbook.theme.Theme

import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ModifierScreenshotTest : ScreenshotTest() {

    @Test
    @Config(qualifiers = Light)
    fun StrokeLight() = captureRoot("Stroke_light") { StrokeContent() }

    @Test
    @Config(qualifiers = Dark)
    fun StrokeDark() = captureRoot("Stroke_dark") { StrokeContent() }

    @Test
    @Config(qualifiers = Light)
    fun GlowLight() = captureRoot("Glow_light") { GlowContent() }

    @Test
    @Config(qualifiers = Dark)
    fun GlowDark() = captureRoot("Glow_dark") { GlowContent() }

    @Test
    @Config(qualifiers = Light)
    fun OverlayLight() = captureRoot("Overlay_light") { OverlayContent() }

    @Test
    @Config(qualifiers = Dark)
    fun OverlayDark() = captureRoot("Overlay_dark") { OverlayContent() }

    @Composable
    private fun StrokeContent() = SketchbookPreviewLayout(modifier = Modifier.padding(16.dp)) {
        Box(
            Modifier
                .size(128.dp)
                .padding(16.dp)
                .stroke(Color.Black, Theme.container.card)
        )
    }

    @Composable
    private fun GlowContent() = SketchbookPreviewLayout(modifier = Modifier.padding(16.dp)) {
        Box(
            Modifier
                .size(128.dp)
                .background(Color.DarkGray, Theme.container.card)
                .glow(Theme.container.card)
        )
    }

    @Composable
    private fun OverlayContent() = SketchbookPreviewLayout(modifier = Modifier.padding(16.dp)) {
        Row {
            Box(
                Modifier
                    .size(128.dp)
                    .background(Color(0xFF3F51B5))
                    .overlay()
            )
            Box(
                Modifier
                    .size(128.dp)
                    .background(Color(0xFF3F51B5))
                    .overlay(colorBottom = Color.Red)
            )
        }
    }
}
