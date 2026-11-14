package lib.sketchbook.screenshot

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import lib.sketchbook.OverlayState
import lib.sketchbook.SketchbookPreviewLayout
import lib.sketchbook.SketchbookPreviewWearLayout
import lib.sketchbook.modifier.LightSource
import lib.sketchbook.modifier.VerticalGravity
import lib.sketchbook.modifier.glow
import lib.sketchbook.modifier.overlay
import lib.sketchbook.modifier.square
import lib.sketchbook.modifier.stroke
import lib.sketchbook.modifier.surface
import lib.sketchbook.modifier.vertical
import lib.sketchbook.modifier.verticalOverlay
import lib.sketchbook.popOutBackground
import lib.sketchbook.theme.Theme
import org.junit.Test
import org.robolectric.annotation.Config

class ModifierScreenshotTest : ScreenshotTest() {

    @Test
    @Config(qualifiers = Light)
    fun `stroke modifier on card shape in light theme`() = captureRoot("Stroke_light") { Stroke() }

    @Test
    @Config(qualifiers = Dark)
    fun `stroke modifier on card shape in dark theme`() = captureRoot("Stroke_dark") { Stroke() }

    @Test
    @Config(qualifiers = Light)
    fun `glow modifier from two light sources on saturated background in light theme`() = captureRoot("Glow_light") { Glow() }

    @Test
    @Config(qualifiers = Dark)
    fun `glow modifier from two light sources on saturated background in dark theme`() = captureRoot("Glow_dark") { Glow() }

    @Test
    @Config(qualifiers = Light)
    fun `overlay modifier with default and custom colors in light theme`() = captureRoot("Overlay_light") { Overlay() }

    @Test
    @Config(qualifiers = Dark)
    fun `overlay modifier with default and custom colors in dark theme`() = captureRoot("Overlay_dark") { Overlay() }

    @Test
    @Config(qualifiers = Light)
    fun `vertical overlay modifier by height and by percent in light theme`() = captureRoot("VerticalOverlay_light") { VerticalOverlay() }

    @Test
    @Config(qualifiers = Dark)
    fun `vertical overlay modifier by height and by percent in dark theme`() = captureRoot("VerticalOverlay_dark") { VerticalOverlay() }

    @Test
    @Config(qualifiers = Light)
    fun `surface modifier with color brush and tonal elevation in light theme`() = captureRoot("Surface_light") { Surface() }

    @Test
    @Config(qualifiers = Dark)
    fun `surface modifier with color brush and tonal elevation in dark theme`() = captureRoot("Surface_dark") { Surface() }

    @Test
    @Config(qualifiers = Light)
    fun `square and vertical layout modifiers in light theme`() = captureRoot("SquareVertical_light") { SquareVertical() }

    @Test
    @Config(qualifiers = Dark)
    fun `square and vertical layout modifiers in dark theme`() = captureRoot("SquareVertical_dark") { SquareVertical() }

    @Test
    @Config(qualifiers = Light)
    fun `pop out background modifier while overlay is expanded in light theme`() = captureRoot("PopOut_light") { PopOut() }

    @Test
    @Config(qualifiers = Dark)
    fun `pop out background modifier while overlay is expanded in dark theme`() = captureRoot("PopOut_dark") { PopOut() }

    @Test
    @Config(qualifiers = Light)
    fun `wear preview layout in light theme`() = captureRoot("Wear_light") { Wear() }

    @Test
    @Config(qualifiers = Dark)
    fun `wear preview layout in dark theme`() = captureRoot("Wear_dark") { Wear() }

    @Composable
    private fun Stroke() = SketchbookPreviewLayout(modifier = Modifier.padding(16.dp)) {
        Box(
            Modifier
                .size(128.dp)
                .padding(16.dp)
                .stroke(Color.Black, Theme.container.card)
        )
    }

    @Composable
    private fun Glow() = SketchbookPreviewLayout(modifier = Modifier.padding(16.dp)) {
        Row {
            Box(
                Modifier
                    .padding(8.dp)
                    .size(96.dp)
                    .background(Color(0xFF1A237E), Theme.container.card)
                    .glow(Theme.container.card, color = Color.White, alpha = .9f)
            )
            Box(
                Modifier
                    .padding(8.dp)
                    .size(96.dp)
                    .background(Color(0xFF1A237E), Theme.container.card)
                    .glow(
                        Theme.container.card,
                        color = Color.White,
                        lightSource = LightSource.BottomRight,
                        alpha = .9f
                    )
            )
        }
    }

    @Composable
    private fun Overlay() = SketchbookPreviewLayout(modifier = Modifier.padding(16.dp)) {
        Row {
            Box(
                Modifier
                    .size(96.dp)
                    .background(Color(0xFF3F51B5))
                    .overlay()
            )
            Box(
                Modifier
                    .size(96.dp)
                    .background(Color(0xFF3F51B5))
                    .overlay(colorBottom = Color.Red)
            )
        }
    }

    @Composable
    private fun VerticalOverlay() = SketchbookPreviewLayout(modifier = Modifier.padding(16.dp)) {
        Row {
            Box(
                Modifier
                    .size(96.dp)
                    .background(Color(0xFF3F51B5))
                    .verticalOverlay(48.dp)
            )
            Box(
                Modifier
                    .size(96.dp)
                    .background(Color(0xFF3F51B5))
                    .verticalOverlay(.5f, VerticalGravity.Bottom)
            )
        }
    }

    @Composable
    private fun Surface() = SketchbookPreviewLayout(modifier = Modifier.padding(16.dp)) {
        Column {
            Box(
                Modifier
                    .padding(8.dp)
                    .size(72.dp)
                    .surface(Color(0xFF3F51B5), Theme.container.card, elevation = 8.dp)
            )
            Box(
                Modifier
                    .padding(8.dp)
                    .size(72.dp)
                    .surface(
                        Brush.horizontalGradient(listOf(Color.Red, Color.Blue)),
                        Theme.container.card,
                        elevation = 8.dp
                    )
            )
            Box(
                Modifier
                    .padding(8.dp)
                    .size(72.dp)
                    .surface(4.dp, Theme.container.card, elevation = 8.dp)
            )
        }
    }

    @Composable
    private fun SquareVertical() = SketchbookPreviewLayout(modifier = Modifier.padding(16.dp)) {
        Row {
            Box(Modifier.background(Color(0xFF3F51B5)).square()) {
                Text("Square", modifier = Modifier.padding(8.dp))
            }
            Box(Modifier.padding(start = 16.dp).background(Color(0xFF3F51B5)).vertical()) {
                Text("Vertical", modifier = Modifier.padding(8.dp))
            }
        }
    }

    @Composable
    private fun PopOut() = SketchbookPreviewLayout {
        val state = remember { OverlayState().apply { expanded = true } }
        Box(
            Modifier
                .fillMaxWidth()
                .height(128.dp)
                .popOutBackground(state)
        ) {
            Text("Background", modifier = Modifier.padding(16.dp))
        }
    }

    @Composable
    private fun Wear() = SketchbookPreviewWearLayout {
        Box(Modifier.size(96.dp).background(Color(0xFF3F51B5)))
    }
}
