package lib.sketchbook.behavior

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PixelMap
import androidx.compose.ui.graphics.toPixelMap
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.center
import androidx.compose.ui.test.down
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.unit.dp
import lib.sketchbook.SketchbookListItem
import lib.sketchbook.changedPixels
import lib.sketchbook.distance
import lib.sketchbook.opaque
import lib.sketchbook.theme.Theme
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.robolectric.annotation.Config

abstract class ListItemShapeBehavior : BehaviorTest() {

    private val itemWidth = 140
    private val itemHeight = 72
    private val gap = 20

    private fun show(selected: Boolean) {
        composeRule.mainClock.autoAdvance = false
        composeRule.setContent {
            Theme(darkTheme = false, dynamicColor = false) {
                Row(Modifier.background(Color.Black).padding(10.dp)) {
                    SketchbookListItem(
                        headline = { Text("") },
                        modifier = Modifier.testTag("item").size(itemWidth.dp, itemHeight.dp),
                        onClick = {},
                        selected = selected
                    )
                    Spacer(Modifier.width(gap.dp))
                    Box(Modifier.size(itemWidth.dp, itemHeight.dp).clip(Theme.container.buttonSmall).background(Color.White))
                }
            }
        }
        composeRule.mainClock.advanceTimeBy(500)
        composeRule.waitForIdle()
    }

    private fun image(): PixelMap = composeRule.onRoot().captureToImage().toPixelMap()

    private val offset get() = (itemWidth + gap) * 2

    private fun insideReference(image: PixelMap, x: Int, y: Int): Boolean {
        for (dy in -1..1) for (dx in -1..1) {
            val px = x + offset + dx
            if (px in 0 until image.width && y + dy in 0 until image.height &&
                distance(image.opaque(px, y + dy), Color.White) < 30
            ) return true
        }
        return false
    }

    @Test
    fun `selected list item tint follows the theme squircle`() {
        show(selected = true)
        val image = image()

        assertTrue(distance(image.opaque(20 + itemWidth, 20 + itemHeight), Color.Black) > 6)
        assertEquals(Color.Black, image.opaque(20, 20))
        val leaked = mutableListOf<Pair<Int, Int>>()
        for (y in 0 until image.height) for (x in 0 until offset) {
            if (distance(image.opaque(x, y), Color.Black) > 4 && !insideReference(image, x, y)) leaked += x to y
        }
        assertEquals(emptyList<Pair<Int, Int>>(), leaked.take(8))
    }

    @Test
    fun `list item press highlight stays inside the theme squircle`() {
        show(selected = false)
        val idle = image()

        composeRule.onNodeWithTag("item").performTouchInput { down(center) }
        composeRule.mainClock.advanceTimeBy(150)
        val changed = idle.changedPixels(image(), 1).filter { it.first < offset }

        assertTrue("press changed nothing", changed.isNotEmpty())
        assertEquals(emptyList<Pair<Int, Int>>(), changed.filterNot { insideReference(idle, it.first, it.second) }.take(8))
    }
}

@Config(sdk = [26])
class ListItemShapeAndroid8BehaviorTest : ListItemShapeBehavior()

@Config(sdk = [33])
class ListItemShapeAndroid13BehaviorTest : ListItemShapeBehavior()
