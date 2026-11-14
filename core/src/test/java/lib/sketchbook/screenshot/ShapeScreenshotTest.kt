package lib.sketchbook.screenshot

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp
import lib.sketchbook.SketchbookPreviewLayout
import lib.sketchbook.shape.CompositeShape
import lib.sketchbook.shape.CutoutShape
import lib.sketchbook.shape.SquircleShape
import org.junit.Test
import org.robolectric.annotation.Config

class ShapeScreenshotTest : ScreenshotTest() {

    @Test
    @Config(qualifiers = Light)
    fun `squircle shapes with increasing smoothing in light theme`() = captureRoot("Squircle_light") { Squircle() }

    @Test
    @Config(qualifiers = Dark)
    fun `squircle shapes with increasing smoothing in dark theme`() = captureRoot("Squircle_dark") { Squircle() }

    @Test
    @Config(qualifiers = Light)
    fun `cutout shape in all four orientations in light theme`() = captureRoot("Cutout_light") { Cutout() }

    @Test
    @Config(qualifiers = Dark)
    fun `cutout shape in all four orientations in dark theme`() = captureRoot("Cutout_dark") { Cutout() }

    @Test
    @Config(qualifiers = Light)
    fun `composite shape of squircle intersected with circle in light theme`() = captureRoot("Composite_light") { Composite() }

    @Test
    @Config(qualifiers = Dark)
    fun `composite shape of squircle intersected with circle in dark theme`() = captureRoot("Composite_dark") { Composite() }

    @Composable
    private fun ShapeBox(shape: Shape) = Box(
        Modifier
            .padding(8.dp)
            .size(72.dp)
            .clip(shape)
            .background(Color(0xFF3F51B5))
    )

    @Composable
    private fun Squircle() = SketchbookPreviewLayout(modifier = Modifier.padding(16.dp)) {
        Row {
            ShapeBox(SquircleShape(percent = 50, cornerSmoothing = 0f))
            ShapeBox(SquircleShape(percent = 50, cornerSmoothing = .6f))
            ShapeBox(SquircleShape(percent = 50, cornerSmoothing = 1f))
        }
    }

    @Composable
    private fun Cutout() = SketchbookPreviewLayout(modifier = Modifier.padding(16.dp)) {
        Row {
            CutoutShape.Orientation.entries.forEach {
                ShapeBox(CutoutShape(CornerSize(16.dp), it))
            }
        }
    }

    @Composable
    private fun Composite() = SketchbookPreviewLayout(modifier = Modifier.padding(16.dp)) {
        ShapeBox(
            CompositeShape.build {
                setBaseline(SquircleShape(percent = 30))
                addShape(CircleShape, alignment = Alignment.Center)
            }
        )
    }
}
