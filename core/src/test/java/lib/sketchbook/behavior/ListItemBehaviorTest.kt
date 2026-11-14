package lib.sketchbook.behavior

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toPixelMap
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertHasNoClickAction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.assertIsNotSelected
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.getBoundsInRoot
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.performClick
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.height
import lib.sketchbook.SketchbookListItem
import lib.sketchbook.distance
import lib.sketchbook.opaque
import lib.sketchbook.theme.Theme
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ListItemBehaviorTest : BehaviorTest() {

    private var clicks = 0

    private fun show(
        onClick: (() -> Unit)? = null,
        selected: Boolean = false,
        enabled: Boolean = true,
        direction: LayoutDirection = LayoutDirection.Ltr,
        fontScale: Float = 1f
    ) {
        composeRule.setContent {
            val density = LocalDensity.current
            CompositionLocalProvider(
                LocalLayoutDirection provides direction,
                LocalDensity provides Density(density.density, fontScale)
            ) {
                Theme(darkTheme = false, dynamicColor = false) {
                    SketchbookListItem(
                        headline = { Text("Headline") },
                        modifier = Modifier.testTag("item"),
                        overline = { Text("Overline") },
                        supporting = { Text("Supporting") },
                        leading = { Text("L", Modifier.testTag("leading")) },
                        trailing = { Text("T", Modifier.testTag("trailing")) },
                        onClick = onClick,
                        selected = selected,
                        enabled = enabled
                    )
                }
            }
        }
        composeRule.waitForIdle()
    }

    @Test
    fun `list item shows every slot`() {
        show()

        listOf("Headline", "Overline", "Supporting", "L", "T").forEach {
            composeRule.onNodeWithText(it).assertIsDisplayed()
        }
    }

    @Test
    fun `list item without onClick is not clickable`() {
        show()

        composeRule.onNodeWithTag("item").assertHasNoClickAction()
    }

    @Test
    fun `list item with onClick invokes it`() {
        show(onClick = { clicks++ })

        composeRule.onNodeWithTag("item").assertHasClickAction().performClick()

        assertEquals(1, clicks)
    }

    @Test
    fun `disabled list item ignores clicks`() {
        show(onClick = { clicks++ }, enabled = false)

        composeRule.onNodeWithTag("item").performClick()

        assertEquals(0, clicks)
        composeRule.onNodeWithTag("item").assertIsNotEnabled()
    }

    @Test
    fun `selected list item is exposed as selected`() {
        show(onClick = {}, selected = true)

        composeRule.onNodeWithTag("item").assertIsSelected()
    }

    @Test
    fun `unselected list item is exposed as not selected`() {
        show(onClick = {}, selected = false)

        composeRule.onNodeWithTag("item").assertIsNotSelected()
    }

    @Test
    fun `clickable list item keeps at least a 48dp touch target`() {
        show(onClick = {})

        val bounds = composeRule.onNodeWithTag("item").getBoundsInRoot()
        assertTrue(bounds.height >= 48.dp)
    }

    @Test
    fun `selected list item is highlighted compared with an unselected one`() {
        composeRule.setContent {
            Theme(darkTheme = false, dynamicColor = false) {
                Column(Modifier.size(200.dp, 300.dp).background(Color.White)) {
                    SketchbookListItem({ Text("A") }, selected = true, onClick = {})
                    SketchbookListItem({ Text("B") }, selected = false, onClick = {})
                }
            }
        }
        composeRule.waitForIdle()

        val image = composeRule.onRoot().captureToImage().toPixelMap()
        assertTrue(distance(image.opaque(300, 56), image.opaque(300, 56 + 168)) > 15)
    }

    @Test
    fun `leading content sits before the headline in left to right layouts`() {
        show(direction = LayoutDirection.Ltr)

        val leading = composeRule.onNodeWithTag("leading", useUnmergedTree = true).getBoundsInRoot()
        val headline = composeRule.onNodeWithText("Headline", useUnmergedTree = true).getBoundsInRoot()
        assertTrue(leading.right <= headline.left)
    }

    @Test
    fun `leading content sits after the headline in right to left layouts`() {
        show(direction = LayoutDirection.Rtl)

        val leading = composeRule.onNodeWithTag("leading", useUnmergedTree = true).getBoundsInRoot()
        val headline = composeRule.onNodeWithText("Headline", useUnmergedTree = true).getBoundsInRoot()
        assertTrue(leading.left >= headline.right)
    }

    @Test
    fun `list item keeps every line inside its bounds at double font scale`() {
        show(fontScale = 2f)

        val item = composeRule.onNodeWithTag("item").getBoundsInRoot()
        val supporting = composeRule.onNodeWithText("Supporting", useUnmergedTree = true).getBoundsInRoot()
        val overline = composeRule.onNodeWithText("Overline", useUnmergedTree = true).getBoundsInRoot()
        assertTrue(supporting.bottom <= item.bottom)
        assertTrue(overline.top >= item.top)
    }
}
