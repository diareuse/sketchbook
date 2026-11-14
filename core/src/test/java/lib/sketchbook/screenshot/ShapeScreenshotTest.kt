package lib.sketchbook.screenshot

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.foundation.shape.CircleShape

import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ShapeScreenshotTest : ScreenshotTest() {

    @Test
    @Config(qualifiers = Light)
    fun SquircleLight() = captureRoot("Squircle_light") { SquircleContent() }

    @Test
    @Config(qualifiers = Dark)
    fun SquircleDark() = captureRoot("Squircle_dark") { SquircleContent() }

    @Test
    @Config(qualifiers = Light)
    fun CutoutLight() = captureRoot("Cutout_light") { CutoutContent() }

    @Test
    @Config(qualifiers = Dark)
    fun CutoutDark() = captureRoot("Cutout_dark") { CutoutContent() }

    @Test
    @Config(qualifiers = Light)
    fun CompositeLight() = captureRoot("Composite_light") { CompositeContent() }

    @Test
    @Config(qualifiers = Dark)
    fun CompositeDark() = captureRoot("Composite_dark") { CompositeContent() }

    @Composable
    private fun ShapeBox(shape: Shape) = Box(
        Modifier
            .padding(8.dp)
            .size(72.dp)
            .clip(shape)
            .background(Color(0xFF3F51B5))
    )

    @Composable
    private fun SquircleContent() = SketchbookPreviewLayout(modifier = Modifier.padding(16.dp)) {
        Row {
            ShapeBox(SquircleShape(percent = 50, cornerSmoothing = 0f))
            ShapeBox(SquircleShape(percent = 50, cornerSmoothing = .6f))
            ShapeBox(SquircleShape(percent = 50, cornerSmoothing = 1f))
        }
    }

    @Composable
    private fun CutoutContent() = SketchbookPreviewLayout(modifier = Modifier.padding(16.dp)) {
        Row {
            CutoutShape.Orientation.entries.forEach {
                ShapeBox(CutoutShape(CornerSize(16.dp), it))
            }
        }
    }

    @Composable
    private fun CompositeContent() = SketchbookPreviewLayout(modifier = Modifier.padding(16.dp)) {
        Column {
            ShapeBox(
                CompositeShape.build {
                    setBaseline(SquircleShape(percent = 30))
                    addShape(CircleShape, alignment = Alignment.Center)
                }
            )
        }
    }
}
