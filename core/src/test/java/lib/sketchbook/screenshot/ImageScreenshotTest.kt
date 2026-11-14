package lib.sketchbook.screenshot

import android.graphics.Bitmap
import android.graphics.Color
import androidx.compose.foundation.Image as FoundationImage
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.unit.dp
import coil3.toBitmap
import lib.sketchbook.Image
import lib.sketchbook.SketchbookPreviewLayout
import org.junit.Test
import org.robolectric.annotation.Config

class ImageScreenshotTest : ScreenshotTest() {

    @Test
    @Config(qualifiers = Light)
    fun `image loaded from in memory bitmap in light theme`() = captureRoot("Image_light", ready = { composeRule.onAllNodes(hasTestTag("loaded")).fetchSemanticsNodes().isNotEmpty() }) { Loaded() }

    @Test
    @Config(qualifiers = Dark)
    fun `image loaded from in memory bitmap in dark theme`() = captureRoot("Image_dark", ready = { composeRule.onAllNodes(hasTestTag("loaded")).fetchSemanticsNodes().isNotEmpty() }) { Loaded() }

    @Composable
    private fun Loaded() = SketchbookPreviewLayout(modifier = Modifier.padding(16.dp)) {
        val bitmap = remember { gradientBitmap() }
        Image(
            data = bitmap,
            modifier = Modifier.size(128.dp),
            success = {
                FoundationImage(
                    bitmap = it.result.image.toBitmap().asImageBitmap(),
                    contentDescription = null,
                    modifier = Modifier.testTag("loaded")
                )
            }
        )
    }

    private fun gradientBitmap(): Bitmap {
        val size = 64
        val pixels = IntArray(size * size) { Color.rgb((it % size) * 4, (it / size) * 4, 128) }
        return Bitmap.createBitmap(pixels, size, size, Bitmap.Config.ARGB_8888)
    }
}
