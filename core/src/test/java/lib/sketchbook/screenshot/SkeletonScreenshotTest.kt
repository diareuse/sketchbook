package lib.sketchbook.screenshot

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import lib.sketchbook.SketchbookPreviewLayout
import lib.sketchbook.SketchbookSkeleton
import lib.sketchbook.SketchbookSkeletonCircle
import lib.sketchbook.SketchbookSkeletonText
import org.junit.Test
import org.robolectric.annotation.Config

class SkeletonScreenshotTest : ScreenshotTest() {

    @Test
    @Config(sdk = [26], qualifiers = Light)
    fun `skeletons on android 8 in light theme`() = captureRoot("Skeleton_sdk26_light", animated = true) { Skeletons() }

    @Test
    @Config(sdk = [26], qualifiers = Dark)
    fun `skeletons on android 8 in dark theme`() = captureRoot("Skeleton_sdk26_dark", animated = true) { Skeletons() }

    @Test
    @Config(sdk = [33], qualifiers = Light)
    fun `skeletons on android 13 in light theme`() = captureRoot("Skeleton_sdk33_light", animated = true) { Skeletons() }

    @Test
    @Config(sdk = [33], qualifiers = Dark)
    fun `skeletons on android 13 in dark theme`() = captureRoot("Skeleton_sdk33_dark", animated = true) { Skeletons() }

    @Composable
    private fun Skeletons() = SketchbookPreviewLayout(Modifier.fillMaxSize()) {
        Column(Modifier.padding(24.dp), verticalArrangement = Arrangement.spacedBy(24.dp)) {
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                SketchbookSkeletonCircle(size = 56.dp)
                SketchbookSkeletonText(Modifier.fillMaxWidth(), lines = 2)
            }
            SketchbookSkeleton(Modifier.fillMaxWidth().height(140.dp))
            SketchbookSkeletonText(lines = 4)
        }
    }
}
