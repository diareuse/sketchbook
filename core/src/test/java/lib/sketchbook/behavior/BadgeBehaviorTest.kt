package lib.sketchbook.behavior

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertWidthIsEqualTo
import androidx.compose.ui.test.getBoundsInRoot
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.height
import androidx.compose.ui.unit.width
import lib.sketchbook.SketchbookBadge
import lib.sketchbook.SketchbookBadgedBox
import lib.sketchbook.theme.Theme
import org.junit.Assert.assertTrue
import org.junit.Test

class BadgeBehaviorTest : BehaviorTest() {

    private fun show(content: @Composable () -> Unit) {
        composeRule.setContent { Theme(darkTheme = false, dynamicColor = false) { content() } }
        composeRule.waitForIdle()
    }

    @Test
    fun `dot badge is a small marker without text`() {
        show { SketchbookBadge(Modifier.testTag("badge")) }

        composeRule.onNodeWithTag("badge").assertIsDisplayed().assertWidthIsEqualTo(8.dp)
    }

    @Test
    fun `count badge shows the count`() {
        show { SketchbookBadge(count = 7) }

        composeRule.onNodeWithText("7").assertIsDisplayed()
    }

    @Test
    fun `count badge shows the exact count at the cap`() {
        show { SketchbookBadge(count = 99) }

        composeRule.onNodeWithText("99").assertIsDisplayed()
    }

    @Test
    fun `count badge caps large counts with a plus`() {
        show { SketchbookBadge(count = 100) }

        composeRule.onNodeWithText("99+").assertIsDisplayed()
    }

    @Test
    fun `count badge honours a custom cap`() {
        show { SketchbookBadge(count = 12, max = 9) }

        composeRule.onNodeWithText("9+").assertIsDisplayed()
    }

    @Test
    fun `count badge with no unread items shows nothing`() {
        show { SketchbookBadge(count = 0, modifier = Modifier.testTag("badge")) }

        composeRule.onNodeWithTag("badge").assertDoesNotExist()
    }

    @Test
    fun `badge rejects a cap below one`() {
        val failure = runCatching { show { SketchbookBadge(count = 1, max = 0) } }

        assertTrue(failure.exceptionOrNull() is IllegalArgumentException)
    }

    private fun showBadged(direction: LayoutDirection) = show {
        CompositionLocalProvider(LocalLayoutDirection provides direction) {
            Box(Modifier.size(100.dp)) {
                SketchbookBadgedBox(badge = { SketchbookBadge(Modifier.testTag("badge")) }) {
                    Box(Modifier.testTag("anchor").size(24.dp).background(Color.Gray))
                }
            }
        }
    }

    private fun centers(): Pair<DpOffset, DpOffset> {
        val anchor = composeRule.onNodeWithTag("anchor").getBoundsInRoot()
        val badge = composeRule.onNodeWithTag("badge").getBoundsInRoot()
        return DpOffset(anchor.left + anchor.width / 2, anchor.top + anchor.height / 2) to
            DpOffset(badge.left + badge.width / 2, badge.top + badge.height / 2)
    }

    @Test
    fun `badge anchors to the top end corner of its content`() {
        showBadged(LayoutDirection.Ltr)

        val (anchor, badge) = centers()
        assertTrue(badge.x > anchor.x)
        assertTrue(badge.y < anchor.y)
    }

    @Test
    fun `badge anchors to the top start corner in a right to left layout`() {
        showBadged(LayoutDirection.Rtl)

        val (anchor, badge) = centers()
        assertTrue(badge.x < anchor.x)
        assertTrue(badge.y < anchor.y)
    }
}
