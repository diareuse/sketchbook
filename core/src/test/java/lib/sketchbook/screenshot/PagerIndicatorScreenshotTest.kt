package lib.sketchbook.screenshot

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import lib.sketchbook.PagerIndicatorStyle
import lib.sketchbook.SketchbookPagerIndicator
import lib.sketchbook.SketchbookPreviewLayout
import org.junit.Test
import org.robolectric.annotation.Config

class PagerIndicatorScreenshotTest : ScreenshotTest() {

    @Test
    @Config(sdk = [26], qualifiers = Light)
    fun `pager indicators on android 8 in light theme`() = captureRoot("PagerIndicator_sdk26_light") { Indicators() }

    @Test
    @Config(sdk = [26], qualifiers = Dark)
    fun `pager indicators on android 8 in dark theme`() = captureRoot("PagerIndicator_sdk26_dark") { Indicators() }

    @Test
    @Config(sdk = [33], qualifiers = Light)
    fun `pager indicators on android 13 in light theme`() = captureRoot("PagerIndicator_sdk33_light") { Indicators() }

    @Test
    @Config(sdk = [33], qualifiers = Dark)
    fun `pager indicators on android 13 in dark theme`() = captureRoot("PagerIndicator_sdk33_dark") { Indicators() }

    @Composable
    private fun Indicators() = SketchbookPreviewLayout {
        Column(
            Modifier.fillMaxWidth().padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            SketchbookPagerIndicator(pageCount = 5, currentPage = 0)
            SketchbookPagerIndicator(pageCount = 5, currentPage = 2, currentPageOffsetFraction = { .4f })
            SketchbookPagerIndicator(pageCount = 5, currentPage = 2, style = PagerIndicatorStyle.Worm)
            SketchbookPagerIndicator(
                pageCount = 5,
                currentPage = 1,
                currentPageOffsetFraction = { .45f },
                style = PagerIndicatorStyle.Worm
            )
        }
    }
}
