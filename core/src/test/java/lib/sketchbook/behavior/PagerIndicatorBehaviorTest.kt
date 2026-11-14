package lib.sketchbook.behavior

import androidx.compose.foundation.background
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toPixelMap
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.assertWidthIsEqualTo
import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import lib.sketchbook.PagerIndicatorStyle
import lib.sketchbook.SketchbookPagerIndicator
import lib.sketchbook.distance
import lib.sketchbook.opaque
import lib.sketchbook.theme.Theme
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class PagerIndicatorBehaviorTest : BehaviorTest() {

    private var offset by mutableFloatStateOf(0f)
    private var compositions = 0

    private val active = Color.Red
    private val inactive = Color.Blue

    private fun show(direction: LayoutDirection = LayoutDirection.Ltr, content: @Composable () -> Unit) {
        composeRule.mainClock.autoAdvance = false
        composeRule.setContent {
            SideEffect { compositions++ }
            CompositionLocalProvider(LocalLayoutDirection provides direction) {
                Theme(darkTheme = false, dynamicColor = false) {
                    Box(Modifier.background(Color.Black)) { content() }
                }
            }
        }
        composeRule.mainClock.advanceTimeBy(50)
    }

    private fun pixels() = composeRule.onRoot().captureToImage().toPixelMap()

    private fun dotCenter(index: Int) = 12 + 48 * index

    @Composable
    private fun Indicator(
        count: Int = 4,
        page: Int = 0,
        style: PagerIndicatorStyle = PagerIndicatorStyle.Dots
    ) = SketchbookPagerIndicator(
        pageCount = count,
        currentPage = page,
        currentPageOffsetFraction = { offset },
        modifier = Modifier.testTag("pager"),
        style = style,
        activeColor = active,
        inactiveColor = inactive,
        dotSize = 12.dp,
        spacing = 12.dp
    )

    @Test
    fun `indicator is as wide as its dots and gaps`() {
        show { Indicator(count = 4) }

        composeRule.onNodeWithTag("pager").assertWidthIsEqualTo(12.dp * 4 + 12.dp * 3)
    }

    @Test
    fun `indicator highlights the current page`() {
        show { Indicator(page = 2) }

        val image = pixels()
        assertTrue(distance(active, image.opaque(dotCenter(2), 12)) < 60)
        assertTrue(distance(inactive, image.opaque(dotCenter(0), 12)) < 60)
        assertTrue(distance(inactive, image.opaque(dotCenter(3), 12)) < 60)
    }

    @Test
    fun `indicator blends neighbouring dots while swiping`() {
        offset = .5f
        show { Indicator(page = 1) }

        val image = pixels()
        val a = distance(active, image.opaque(dotCenter(1), 12))
        val b = distance(active, image.opaque(dotCenter(2), 12))
        assertTrue(a in 60..400)
        assertTrue(b in 60..400)
        assertTrue(Math.abs(a - b) < 60)
    }

    @Test
    fun `worm indicator stretches over the gap while swiping`() {
        offset = .5f
        show { Indicator(page = 0, style = PagerIndicatorStyle.Worm) }

        val gap = pixels().opaque(dotCenter(0) + 24, 12)

        assertTrue(distance(active, gap) < 120)
    }

    @Test
    fun `dot indicator leaves the gap empty while swiping`() {
        offset = .5f
        show { Indicator(page = 0) }

        assertEquals(Color.Black, pixels().opaque(dotCenter(0) + 24, 12))
    }

    @Test
    fun `indicator mirrors in a right to left layout`() {
        show(LayoutDirection.Rtl) { Indicator(page = 0) }

        val image = pixels()
        assertTrue(distance(active, image.opaque(dotCenter(3), 12)) < 60)
        assertTrue(distance(inactive, image.opaque(dotCenter(0), 12)) < 60)
    }

    @Test
    fun `indicator follows the swipe offset without recomposing`() {
        show { Indicator(page = 0) }
        val before = pixels().opaque(dotCenter(1), 12)
        val count = compositions

        offset = .5f
        composeRule.mainClock.advanceTimeBy(32)

        assertFalse(before == pixels().opaque(dotCenter(1), 12))
        assertEquals(count, compositions)
    }

    @Test
    fun `indicator describes the current page to accessibility`() {
        show { Indicator(page = 1) }

        composeRule.onNodeWithTag("pager")
            .assert(SemanticsMatcher.expectValue(SemanticsProperties.StateDescription, "Page 2 of 4"))
    }

    @Test
    fun `indicator keeps the current page inside the page range`() {
        show { Indicator(count = 3, page = 9) }

        assertTrue(distance(active, pixels().opaque(dotCenter(2), 12)) < 60)
    }

    @Test
    fun `indicator with no pages draws nothing`() {
        show { Indicator(count = 0) }

        composeRule.onNodeWithTag("pager").assertWidthIsEqualTo(0.dp)
    }

    @OptIn(ExperimentalFoundationApi::class)
    @Composable
    private fun StateIndicator(state: PagerState) = SketchbookPagerIndicator(
        pagerState = state,
        modifier = Modifier.testTag("pager"),
        activeColor = active,
        inactiveColor = inactive,
        dotSize = 12.dp,
        spacing = 12.dp
    )

    @Test
    fun `pager state indicator follows the pager page`() {
        lateinit var state: PagerState
        lateinit var scope: CoroutineScope
        show {
            state = rememberPagerState { 4 }
            scope = rememberCoroutineScope()
            Column {
                HorizontalPager(state, Modifier.size(100.dp)) { Box(Modifier.size(100.dp)) }
                StateIndicator(state)
            }
        }

        scope.launch { state.scrollToPage(2) }
        composeRule.mainClock.advanceTimeBy(100)

        val image = pixels()
        assertTrue(distance(active, image.opaque(dotCenter(2), 212)) < 60)
        composeRule.onNodeWithTag("pager")
            .assert(SemanticsMatcher.expectValue(SemanticsProperties.StateDescription, "Page 3 of 4"))
    }
}
