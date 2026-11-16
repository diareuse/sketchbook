package lib.sketchbook.behavior

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toPixelMap
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.getBoundsInRoot
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.height
import lib.sketchbook.SketchbookCard
import lib.sketchbook.SketchbookScaffold
import lib.sketchbook.StripedBackdrop
import lib.sketchbook.redSpread
import lib.sketchbook.theme.Theme
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.robolectric.annotation.Config

class ScaffoldBehaviorTest : BehaviorTest() {

    private var padding = PaddingValues()

    private fun show(
        top: Boolean = false,
        bottom: Boolean = false,
        insets: WindowInsets = WindowInsets(0, 0, 0, 0),
        content: @Composable (PaddingValues) -> Unit = {
            padding = it
            Box(Modifier.fillMaxSize().testTag("content"))
        },
        bottomBar: @Composable () -> Unit = { Box(Modifier.fillMaxWidth().height(80.dp)) }
    ) {
        composeRule.setContent {
            Theme(darkTheme = false, dynamicColor = false) {
                SketchbookScaffold(
                    modifier = Modifier.fillMaxSize(),
                    topBar = if (top) {
                        { Box(Modifier.fillMaxWidth().height(56.dp).testTag("top")) }
                    } else {
                        {}
                    },
                    bottomBar = if (bottom) bottomBar else {
                        {}
                    },
                    floatingActionButton = { Text("Fab") },
                    snackbarHost = { Text("Snack") },
                    contentWindowInsets = insets,
                    content = content
                )
            }
        }
        composeRule.waitForIdle()
    }

    @Test
    fun `content padding includes the top bar height`() {
        show(top = true)

        assertEquals(56.dp, padding.calculateTopPadding())
    }

    @Test
    fun `content padding includes the bottom bar height`() {
        show(bottom = true)

        assertEquals(80.dp, padding.calculateBottomPadding())
    }

    @Test
    fun `content padding includes both bars together`() {
        show(top = true, bottom = true)

        assertEquals(56.dp, padding.calculateTopPadding())
        assertEquals(80.dp, padding.calculateBottomPadding())
    }

    @Test
    fun `content padding falls back to the window insets without bars`() {
        show(insets = WindowInsets(left = 4.dp, top = 10.dp, right = 6.dp, bottom = 20.dp))

        assertEquals(10.dp, padding.calculateTopPadding())
        assertEquals(20.dp, padding.calculateBottomPadding())
        assertEquals(4.dp, padding.calculateStartPadding(LayoutDirection.Ltr))
        assertEquals(6.dp, padding.calculateEndPadding(LayoutDirection.Ltr))
    }

    @Test
    fun `content extends behind the bars so floating bars can overlay it`() {
        show(top = true, bottom = true)

        val root = composeRule.onRoot().getBoundsInRoot()
        val content = composeRule.onNodeWithTag("content").getBoundsInRoot()
        assertEquals(root.height, content.height)
    }

    @Test
    fun `floating action button and snackbar host are shown`() {
        show()

        composeRule.onNodeWithText("Fab").assertIsDisplayed()
        composeRule.onNodeWithText("Snack").assertIsDisplayed()
    }

    @Test
    fun `floating action button sits above the bottom bar`() {
        show(bottom = true)

        val fab = composeRule.onNodeWithText("Fab").getBoundsInRoot()
        val root = composeRule.onRoot().getBoundsInRoot()
        assertTrue(fab.bottom <= root.bottom - 80.dp)
    }

    private fun showBlurCheck() {
        composeRule.setContent {
            Theme(darkTheme = false, dynamicColor = false) {
                SketchbookScaffold(
                    modifier = Modifier.size(360.dp, 400.dp),
                    bottomBar = { SketchbookCard(Modifier.fillMaxWidth().height(80.dp)) {} }
                ) {
                    StripedBackdrop(Modifier.fillMaxSize(), source = false) {}
                }
            }
        }
        composeRule.waitForIdle()
    }

    @Test
    @Config(sdk = [31])
    fun `glass bar over scaffold content blurs that content on android 12`() {
        showBlurCheck()

        val spread = composeRule.onRoot().captureToImage().toPixelMap().redSpread(760, 100..620)
        assertTrue("spread $spread", spread < 200)
    }

    @Test
    @Config(sdk = [31])
    fun `scaffold content above the glass bar stays sharp`() {
        showBlurCheck()

        val spread = composeRule.onRoot().captureToImage().toPixelMap().redSpread(300, 100..620)
        assertTrue("spread $spread", spread > 100)
    }
}
