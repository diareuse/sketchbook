package lib.sketchbook.behavior

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PixelMap
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.toPixelMap
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.unit.dp
import lib.sketchbook.SketchbookSearchBar
import lib.sketchbook.StripedBackdrop
import lib.sketchbook.contrastRatio
import lib.sketchbook.distance
import lib.sketchbook.opaque
import lib.sketchbook.redSpread
import lib.sketchbook.theme.Theme
import lib.sketchbook.theme.contentColorFor
import org.junit.Assert.assertTrue
import org.junit.Test
import org.robolectric.annotation.Config

abstract class SearchBarPaneBehavior : GlassPaneBehavior() {

    override val paneShape: @Composable () -> Shape = { Theme.container.button }

    @Test
    fun `search bar pane edge matches the theme button shape`() = assertPane {
        SketchbookSearchBar(
            query = "",
            onQueryChange = {},
            onSearch = {},
            clearContentDescription = "Clear",
            modifier = it.fillMaxWidth()
        )
    }

    @Test
    fun `search bar with a query keeps its pane edge on the theme button shape`() = assertPane {
        SketchbookSearchBar(
            query = "Sketch",
            onQueryChange = {},
            onSearch = {},
            clearContentDescription = "Clear",
            modifier = it.fillMaxWidth()
        )
    }
}

@Config(sdk = [26])
class SearchBarPaneAndroid8BehaviorTest : SearchBarPaneBehavior()

@Config(sdk = [31])
class SearchBarPaneAndroid12BehaviorTest : SearchBarPaneBehavior()

@Config(sdk = [33])
class SearchBarPaneAndroid13BehaviorTest : SearchBarPaneBehavior()

class SearchBarGlassBehaviorTest : BehaviorTest() {

    private var textColor = Color.Unspecified

    private fun render(dark: Boolean = false, results: (@Composable androidx.compose.foundation.layout.ColumnScope.() -> Unit)? = null): PixelMap {
        composeRule.setContent {
            Theme(darkTheme = dark, dynamicColor = false) {
                textColor = Theme.color.contentColorFor(Theme.color.emphasis.background)
                StripedBackdrop(Modifier.size(360.dp, 240.dp)) {
                    SketchbookSearchBar(
                        query = "",
                        onQueryChange = {},
                        onSearch = {},
                        clearContentDescription = "Clear",
                        modifier = Modifier.fillMaxWidth(),
                        leadingIcon = null,
                        results = results
                    )
                }
            }
        }
        composeRule.waitForIdle()
        return composeRule.onRoot().captureToImage().toPixelMap()
    }

    @Test
    @Config(sdk = [26])
    fun `search bar tints the backdrop and still lets it show through`() {
        val image = render()
        val red = image.opaque(RedStripeX, 60)
        val blue = image.opaque(BlueStripeX, 60)

        assertTrue(distance(red, blue) > 30)
        assertTrue(distance(red, blue) < distance(Color.Red, Color.Blue))
    }

    @Test
    @Config(sdk = [31])
    fun `search bar blurs the backdrop on android 12`() {
        val image = render()

        val spread = image.redSpread(100, 100..620)
        assertTrue("spread $spread", spread < 120)
    }

    @Test
    @Config(sdk = [26])
    fun `search bar text stays readable over every backdrop stripe in light theme`() {
        val image = render()

        listOf(RedStripeX, BlueStripeX).forEach {
            val ratio = contrastRatio(textColor, image.opaque(it, 60))
            assertTrue("x=$it ratio $ratio", ratio >= 4.5f)
        }
    }

    @Test
    @Config(sdk = [26], qualifiers = "w360dp-h640dp-normal-night-xhdpi")
    fun `search bar text stays readable over every backdrop stripe in dark theme`() {
        val image = render(dark = true)

        listOf(RedStripeX, BlueStripeX).forEach {
            val ratio = contrastRatio(textColor, image.opaque(it, 60))
            assertTrue("x=$it ratio $ratio", ratio >= 4.5f)
        }
    }

    @Test
    @Config(sdk = [33])
    fun `results panel clips its content to the rounded glass shape`() {
        val image = render {
            Box(Modifier.testTag("results").fillMaxWidth().height(80.dp).background(Color.Black))
        }
        val bounds = composeRule.onNodeWithTag("results").fetchSemanticsNode().boundsInRoot

        assertTrue(distance(image.opaque(bounds.left.toInt() + 1, bounds.bottom.toInt() - 2), Color.Black) > 100)
        assertTrue(distance(image.opaque(bounds.right.toInt() - 2, bounds.bottom.toInt() - 2), Color.Black) > 100)
    }

    private companion object {
        const val RedStripeX = 20
        const val BlueStripeX = 60
    }
}
