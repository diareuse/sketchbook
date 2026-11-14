package lib.sketchbook.behavior

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
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
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.performClick
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import lib.sketchbook.SketchbookScaffold
import lib.sketchbook.SketchbookSnackbarHost
import lib.sketchbook.StripedBackdrop
import lib.sketchbook.distance
import lib.sketchbook.opaque
import lib.sketchbook.theme.Theme
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.robolectric.annotation.Config

class SnackbarBehaviorTest : BehaviorTest() {

    private val state = SnackbarHostState()
    private lateinit var scope: CoroutineScope
    private var result: SnackbarResult? = null

    private fun show(content: @Composable () -> Unit = { SketchbookSnackbarHost(state) }) {
        composeRule.mainClock.autoAdvance = false
        composeRule.setContent {
            scope = rememberCoroutineScope()
            Theme(darkTheme = false, dynamicColor = false) { content() }
        }
        composeRule.mainClock.advanceTimeBy(16)
    }

    private fun post(
        message: String,
        actionLabel: String? = null,
        withDismissAction: Boolean = false,
        duration: SnackbarDuration = if (actionLabel == null) SnackbarDuration.Short else SnackbarDuration.Indefinite
    ) {
        scope.launch { result = state.showSnackbar(message, actionLabel, withDismissAction, duration) }
        composeRule.mainClock.advanceTimeBy(400)
    }

    @Test
    fun `snackbar shows the posted message`() {
        show()

        post("Saved")

        composeRule.onNodeWithText("Saved").assertIsDisplayed()
    }

    @Test
    fun `snackbar announces itself as a live region`() {
        show()

        post("Saved")

        composeRule.onNode(SemanticsMatcher.keyIsDefined(SemanticsProperties.LiveRegion)).assertExists()
    }

    @Test
    fun `snackbar without actions shows no buttons`() {
        show()

        post("Saved")

        composeRule.onNodeWithText("Undo").assertDoesNotExist()
        composeRule.onNodeWithContentDescription("Dismiss").assertDoesNotExist()
    }

    @Test
    fun `snackbar action reports that it was performed`() {
        show()
        post("Deleted", actionLabel = "Undo")

        composeRule.onNodeWithText("Undo").assertIsDisplayed().performClick()
        composeRule.mainClock.advanceTimeBy(400)

        assertEquals(SnackbarResult.ActionPerformed, result)
        composeRule.onNodeWithText("Deleted").assertDoesNotExist()
    }

    @Test
    fun `snackbar dismiss action closes it and reports dismissal`() {
        show()
        post("Deleted", withDismissAction = true, duration = SnackbarDuration.Indefinite)

        composeRule.onNodeWithContentDescription("Dismiss").performClick()
        composeRule.mainClock.advanceTimeBy(400)

        assertEquals(SnackbarResult.Dismissed, result)
        composeRule.onNodeWithText("Deleted").assertDoesNotExist()
    }

    @Test
    fun `snackbar goes away by itself after its duration`() {
        show()
        post("Saved")

        assertNull(result)
        composeRule.mainClock.advanceTimeBy(5000)

        assertEquals(SnackbarResult.Dismissed, result)
        composeRule.onNodeWithText("Saved").assertDoesNotExist()
    }

    @Test
    fun `snackbar works in the scaffold snackbar slot`() {
        show {
            SketchbookScaffold(snackbarHost = { SketchbookSnackbarHost(state) }) {
                Box(Modifier.fillMaxSize())
            }
        }

        post("Saved", actionLabel = "Undo")

        composeRule.onNodeWithText("Saved").assertIsDisplayed()
        composeRule.onNodeWithText("Undo").assertIsDisplayed()
    }

    @Test
    @Config(sdk = [26])
    fun `snackbar tints the content behind it`() {
        show {
            StripedBackdrop(Modifier.size(360.dp, 200.dp)) {
                Box(Modifier.align(Alignment.BottomCenter)) { SketchbookSnackbarHost(state) }
            }
        }

        post("Saved", actionLabel = "Undo")

        val image = composeRule.onRoot().captureToImage().toPixelMap()
        assertTrue(distance(Color.Red, image.opaque(40, image.height - 80)) > 60)
        assertEquals(Color.Red, image.opaque(2, 2))
    }
}
