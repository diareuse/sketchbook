package lib.sketchbook.behavior

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.getBoundsInRoot
import androidx.compose.ui.test.hasScrollAction
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.height
import lib.sketchbook.SketchbookButton
import lib.sketchbook.SketchbookEmptyState
import lib.sketchbook.theme.Theme
import org.junit.Assert.assertEquals
import org.junit.Test

class EmptyStateBehaviorTest : BehaviorTest() {

    private var clicks = 0

    private fun show(
        height: Int = 600,
        fontScale: Float = 1f,
        message: String? = "Nothing here yet",
        withIcon: Boolean = true,
        withAction: Boolean = true
    ) {
        composeRule.setContent {
            val density = LocalDensity.current
            CompositionLocalProvider(LocalDensity provides Density(density.density, fontScale)) {
                Theme(darkTheme = false, dynamicColor = false) {
                    Box(Modifier.size(360.dp, height.dp).testTag("host")) {
                        SketchbookEmptyState(
                            title = "No results",
                            modifier = Modifier.testTag("empty"),
                            message = message,
                            icon = if (withIcon) {
                                { Box(Modifier.testTag("icon").size(48.dp)) }
                            } else null,
                            action = if (withAction) {
                                { SketchbookButton(onClick = { clicks++ }, Modifier.testTag("action")) { Text("Retry") } }
                            } else null
                        )
                    }
                }
            }
        }
        composeRule.waitForIdle()
    }

    @Test
    fun `empty state shows icon title message and action`() {
        show()

        composeRule.onNodeWithTag("icon").assertIsDisplayed()
        composeRule.onNodeWithText("No results").assertIsDisplayed()
        composeRule.onNodeWithText("Nothing here yet").assertIsDisplayed()
        composeRule.onNodeWithTag("action").assertIsDisplayed()
    }

    @Test
    fun `empty state action invokes its click`() {
        show()

        composeRule.onNodeWithTag("action").performClick()

        assertEquals(1, clicks)
    }

    @Test
    fun `empty state without message icon and action shows only the title`() {
        show(message = null, withIcon = false, withAction = false)

        composeRule.onNodeWithText("No results").assertIsDisplayed()
        composeRule.onNodeWithTag("icon").assertDoesNotExist()
        composeRule.onNodeWithTag("action").assertDoesNotExist()
    }

    @Test
    fun `empty state title is exposed as a heading`() {
        show()

        composeRule.onNodeWithText("No results")
            .assert(SemanticsMatcher.keyIsDefined(SemanticsProperties.Heading))
    }

    @Test
    fun `empty state centers its content in tall containers`() {
        show(message = null, withIcon = false, withAction = false)

        val host = composeRule.onNodeWithTag("host").getBoundsInRoot()
        val title = composeRule.onNodeWithText("No results").getBoundsInRoot()
        val hostCenter = host.top + host.height / 2
        val titleCenter = title.top + title.height / 2
        assertEquals(hostCenter.value, titleCenter.value, 1f)
    }

    @Test
    fun `empty state stays reachable by scrolling at a large font scale in a short container`() {
        show(height = 160, fontScale = 2f)

        composeRule.onNode(hasScrollAction()).assertExists()
        composeRule.onNodeWithTag("action").performScrollTo().assertIsDisplayed()
    }
}
