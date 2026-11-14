package lib.sketchbook.behavior

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.getBoundsInRoot
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import lib.sketchbook.modifier.LightSource
import lib.sketchbook.modifier.glow
import lib.sketchbook.modifier.overlay
import lib.sketchbook.modifier.screenBrightness
import lib.sketchbook.modifier.stroke
import lib.sketchbook.modifier.vertical
import org.junit.Assert.assertEquals
import org.junit.Test

class ModifierBehaviorTest : BehaviorTest() {

    @Test
    fun `vertical measures its content with swapped constraints`() {
        composeRule.setContent {
            Box(Modifier.sizeIn(maxWidth = 40.dp, maxHeight = 300.dp)) {
                Box(Modifier.testTag("v").vertical().size(200.dp, 20.dp))
            }
        }
        composeRule.waitForIdle()

        val bounds = composeRule.onNodeWithTag("v").getBoundsInRoot()
        assertEquals(20f, (bounds.right - bounds.left).value, 0.01f)
        assertEquals(200f, (bounds.bottom - bounds.top).value, 0.01f)
    }

    private class CountingShape : Shape {
        var outlines = 0

        override fun createOutline(size: Size, layoutDirection: LayoutDirection, density: Density): Outline {
            outlines++
            return RectangleShape.createOutline(size, layoutDirection, density)
        }
    }

    @Test
    fun `stroke reuses its outline across redraws`() {
        val shape = CountingShape()
        var tick by mutableIntStateOf(0)
        composeRule.setContent {
            Box(
                Modifier
                    .testTag("s")
                    .size(64.dp)
                    .drawBehind { tick.hashCode() }
                    .stroke(Color.Red, shape)
            )
        }
        composeRule.waitForIdle()
        composeRule.onNodeWithTag("s").captureToImage()
        val first = shape.outlines

        repeat(3) {
            tick++
            composeRule.waitForIdle()
            composeRule.onNodeWithTag("s").captureToImage()
        }

        assertEquals(first, shape.outlines)
    }

    @Test
    fun `equal modifier parameters produce equal modifiers`() {
        val shape = RectangleShape
        assertEquals(Modifier.stroke(Color.Red, shape), Modifier.stroke(Color.Red, shape))
        assertEquals(Modifier.glow(shape), Modifier.glow(shape))
        assertEquals(
            Modifier.glow(shape, Color.Red, LightSource.Top, .3f, .1f, 3.dp),
            Modifier.glow(shape, Color.Red, LightSource.Top, .3f, .1f, 3.dp)
        )
        assertEquals(Modifier.overlay(), Modifier.overlay())
        assertEquals(Modifier.screenBrightness(1f), Modifier.screenBrightness(1f))
    }

    @Test
    fun `screen brightness applies while composed and restores afterwards`() {
        var applied by mutableStateOf(true)
        composeRule.setContent {
            Box(if (applied) Modifier.screenBrightness(true) else Modifier)
        }
        composeRule.waitForIdle()
        val window = composeRule.activity.window
        assertEquals(1f, window.attributes.screenBrightness, 0f)

        applied = false
        composeRule.waitForIdle()

        assertEquals(-1f, window.attributes.screenBrightness, 0f)
    }
}
