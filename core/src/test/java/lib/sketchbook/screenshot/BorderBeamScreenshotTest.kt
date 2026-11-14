package lib.sketchbook.screenshot

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp
import lib.sketchbook.ColorfulStripes
import lib.sketchbook.SketchbookPreviewLayout
import lib.sketchbook.StripedBackdrop
import lib.sketchbook.modifier.borderBeam
import lib.sketchbook.shape.SquircleShape
import org.junit.Test
import org.robolectric.annotation.Config

class BorderBeamScreenshotTest : ScreenshotTest() {

    @Test
    @Config(sdk = [26], qualifiers = Light)
    fun `border beam over stripes on android 8 in light theme`() =
        captureRoot("BorderBeam_sdk26_light", animated = true) { Beams() }

    @Test
    @Config(sdk = [26], qualifiers = Dark)
    fun `border beam over stripes on android 8 in dark theme`() =
        captureRoot("BorderBeam_sdk26_dark", animated = true) { Beams() }

    @Test
    @Config(sdk = [33], qualifiers = Light)
    fun `border beam over stripes on android 13 in light theme`() =
        captureRoot("BorderBeam_sdk33_light", animated = true) { Beams() }

    @Test
    @Config(sdk = [33], qualifiers = Dark)
    fun `border beam over stripes on android 13 in dark theme`() =
        captureRoot("BorderBeam_sdk33_dark", animated = true) { Beams() }

    @Composable
    private fun Beams() = SketchbookPreviewLayout {
        StripedBackdrop(Modifier.fillMaxSize(), colors = ColorfulStripes) {
            Column(Modifier.padding(24.dp), verticalArrangement = Arrangement.spacedBy(24.dp)) {
                Beam(SquircleShape(32.dp), "Squircle", Modifier.fillMaxWidth().height(120.dp))
                Beam(CircleShape, "Circle", Modifier.size(120.dp))
                Beam(RoundedCornerShape(16.dp), "Rounded 16dp", Modifier.fillMaxWidth().height(120.dp))
            }
        }
    }

    @Composable
    private fun Beam(shape: Shape, label: String, modifier: Modifier) =
        Box(modifier.borderBeam(shape), contentAlignment = Alignment.Center) { Text(label) }
}
