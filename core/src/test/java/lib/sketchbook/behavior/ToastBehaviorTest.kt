package lib.sketchbook.behavior

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toPixelMap
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import lib.sketchbook.SketchbookToastHost
import lib.sketchbook.SketchbookToastState
import lib.sketchbook.StripedBackdrop
import lib.sketchbook.ToastDuration
import lib.sketchbook.distance
import lib.sketchbook.opaque
import lib.sketchbook.theme.Theme
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.robolectric.annotation.Config

class ToastBehaviorTest : BehaviorTest() {

    private val state = SketchbookToastState()
    private lateinit var scope: CoroutineScope
    private val finished = mutableListOf<String>()

    private fun show(content: @Composable () -> Unit = { SketchbookToastHost(state) }) {
        composeRule.mainClock.autoAdvance = false
        composeRule.setContent {
            scope = rememberCoroutineScope()
            Theme(darkTheme = false, dynamicColor = false) { content() }
        }
        composeRule.mainClock.advanceTimeBy(16)
    }

    private fun post(message: String, millis: Long = 1000): Job {
        val job = scope.launch {
            state.show(message, ToastDuration(millis))
            finished += message
        }
        composeRule.mainClock.advanceTimeBy(300)
        return job
    }

    private fun advance(millis: Long) = composeRule.mainClock.advanceTimeBy(millis)

    @Test
    fun `toast host shows nothing until a toast is requested`() {
        show()

        composeRule.onNodeWithText("Copied").assertDoesNotExist()
    }

    @Test
    fun `toast shows the requested message`() {
        show()

        post("Copied")

        composeRule.onNodeWithText("Copied").assertIsDisplayed()
    }

    @Test
    fun `toast announces itself as a live region`() {
        show()

        post("Copied")

        composeRule.onNode(SemanticsMatcher.keyIsDefined(SemanticsProperties.LiveRegion)).assertExists()
    }

    @Test
    fun `toast disappears and show returns after its duration`() {
        show()
        post("Copied", millis = 1000)

        assertTrue(finished.isEmpty())
        advance(1500)

        assertEquals(listOf("Copied"), finished)
        composeRule.onNodeWithText("Copied").assertDoesNotExist()
    }

    @Test
    fun `toast stays visible until its duration has elapsed`() {
        show()
        post("Copied", millis = 2000)

        advance(1000)

        composeRule.onNodeWithText("Copied").assertIsDisplayed()
        assertTrue(finished.isEmpty())
    }

    @Test
    fun `a second toast waits for the first to finish`() {
        show()
        post("First", millis = 1000)
        post("Second", millis = 1000)

        composeRule.onNodeWithText("First").assertIsDisplayed()
        composeRule.onNodeWithText("Second").assertDoesNotExist()

        advance(800)

        composeRule.onNodeWithText("Second").assertIsDisplayed()
        composeRule.onNodeWithText("First").assertDoesNotExist()
        assertEquals(listOf("First"), finished)

        advance(1500)

        assertEquals(listOf("First", "Second"), finished)
    }

    @Test
    fun `cancelling a toast request clears it from the screen`() {
        show()
        val job = post("Copied", millis = 5000)

        job.cancel()
        advance(600)

        composeRule.onNodeWithText("Copied").assertDoesNotExist()
        assertFalse(finished.contains("Copied"))
    }

    @Test
    fun `a toast requires a positive duration`() {
        val failure = runCatching { ToastDuration(0) }

        assertTrue(failure.exceptionOrNull() is IllegalArgumentException)
    }

    @Test
    fun `short toasts do not outlast long toasts`() {
        assertTrue(ToastDuration.Short.millis < ToastDuration.Long.millis)
    }

    @Test
    @Config(sdk = [26])
    fun `toast tints the content behind it`() {
        show {
            StripedBackdrop(Modifier.size(360.dp, 200.dp)) {
                Box(Modifier.align(Alignment.Center)) { SketchbookToastHost(state) }
            }
        }

        post("A rather long toast message", millis = 5000)

        val image = composeRule.onRoot().captureToImage().toPixelMap()
        assertTrue(distance(Color.Red, image.opaque(image.width / 2 - 140, image.height / 2 - 20)) > 60)
        assertEquals(Color.Red, image.opaque(2, 2))
    }
}
