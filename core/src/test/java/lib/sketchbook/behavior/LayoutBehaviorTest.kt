package lib.sketchbook.behavior

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.assertWidthIsEqualTo
import androidx.compose.ui.test.getBoundsInRoot
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import lib.sketchbook.layout.alignForLargeScreen
import lib.sketchbook.layout.fixedWidth
import lib.sketchbook.layout.largeScreenPadding
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test
import org.robolectric.annotation.Config

class LayoutBehaviorTest : BehaviorTest() {

    @Test
    fun `fixed width pins both bounds to the given width`() {
        val fixed = Constraints(minWidth = 0, maxWidth = 100, minHeight = 5, maxHeight = 50).fixedWidth(40)

        assertEquals(40, fixed.minWidth)
        assertEquals(40, fixed.maxWidth)
        assertEquals(5, fixed.minHeight)
        assertEquals(50, fixed.maxHeight)
    }

    @Test
    fun `fixed width defaults to the maximum width`() {
        val fixed = Constraints(minWidth = 0, maxWidth = 100).fixedWidth()

        assertEquals(100, fixed.minWidth)
        assertEquals(100, fixed.maxWidth)
    }

    @Test
    @Config(qualifiers = "w800dp-h1000dp-normal-notnight-xhdpi")
    fun `content is centered and capped at 600dp on a medium window`() {
        composeRule.setContent {
            Box(Modifier.fillMaxWidth().alignForLargeScreen()) {
                Box(Modifier.testTag("content").fillMaxWidth().height(10.dp))
            }
        }
        composeRule.waitForIdle()

        val bounds = composeRule.onNodeWithTag("content").getBoundsInRoot()
        assertEquals(100f, bounds.left.value, 0.5f)
        assertEquals(600f, (bounds.right - bounds.left).value, 0.5f)
    }

    @Test
    @Config(qualifiers = "w360dp-h640dp-normal-notnight-xhdpi")
    fun `content fills a compact window`() {
        composeRule.setContent {
            Box(Modifier.fillMaxWidth().alignForLargeScreen()) {
                Box(Modifier.testTag("content").fillMaxWidth().height(10.dp))
            }
        }
        composeRule.waitForIdle()

        val bounds = composeRule.onNodeWithTag("content").getBoundsInRoot()
        assertEquals(0f, bounds.left.value, 0.5f)
        assertEquals(360f, (bounds.right - bounds.left).value, 0.5f)
    }

    @Test
    fun `aligning for a large screen inside an unbounded width does not crash`() {
        composeRule.setContent {
            Box(Modifier.horizontalScroll(rememberScrollState())) {
                Box(Modifier.testTag("content").alignForLargeScreen(maxWidth = 600.dp).size(900.dp, 10.dp))
            }
        }
        composeRule.waitForIdle()

        composeRule.onNodeWithTag("content").assertWidthIsEqualTo(600.dp)
    }

    @Test
    @Config(qualifiers = "w800dp-h1000dp-normal-notnight-xhdpi")
    fun `large screen padding reports the horizontal gutter on a medium window`() {
        var padding: PaddingValues? = null
        composeRule.setContent {
            Box(Modifier.fillMaxWidth().largeScreenPadding { padding = it }.height(10.dp))
        }
        composeRule.waitForIdle()

        assertNotNull(padding)
        assertEquals(100.dp, (padding as PaddingValues).calculateLeftPadding(LayoutDirection.Ltr))
    }

    @Test
    @Config(qualifiers = "w360dp-h640dp-normal-notnight-xhdpi")
    fun `large screen padding reports no gutter on a compact window`() {
        var padding: PaddingValues? = null
        composeRule.setContent {
            Box(Modifier.fillMaxWidth().largeScreenPadding { padding = it }.height(10.dp))
        }
        composeRule.waitForIdle()

        assertEquals(0.dp, (padding as PaddingValues).calculateLeftPadding(LayoutDirection.Ltr))
    }
}
