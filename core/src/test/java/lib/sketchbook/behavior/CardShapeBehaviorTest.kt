package lib.sketchbook.behavior

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PixelMap
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.toPixelMap
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.center
import androidx.compose.ui.test.down
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.unit.dp
import lib.sketchbook.SketchbookCard
import lib.sketchbook.distance
import lib.sketchbook.opaque
import lib.sketchbook.theme.Theme
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.robolectric.annotation.Config

private val CardCases = listOf(
    ControlCase("card", null) { m, e, act -> SketchbookCard(onClick = act, modifier = m, enabled = e) { Text("Card") } }
)

@Config(sdk = [34])
class CardInteractionAndroid14BehaviorTest : ControlInteractionBehavior() {
    override val cases = CardCases
}

@Config(sdk = [26])
class CardInteractionAndroid8BehaviorTest : ControlInteractionBehavior() {
    override val cases = CardCases
}

abstract class CardPaneBehavior : GlassPaneBehavior() {

    override val paneShape: @Composable () -> Shape = { Theme.container.card }

    @Test
    fun `static card pane edge matches the theme card shape`() = assertPane {
        SketchbookCard(it.size(200.dp, 100.dp)) {}
    }

    @Test
    fun `clickable card pane edge matches the theme card shape`() = assertPane {
        SketchbookCard(onClick = {}, modifier = it.size(200.dp, 100.dp)) {}
    }

    @Test
    fun `card with a specular rim keeps the rim inside the card shape`() {
        composeRule.mainClock.autoAdvance = false
        assertPane { SketchbookCard(it.size(200.dp, 100.dp), specularRim = true) {} }
    }
}

@Config(sdk = [26])
class CardPaneAndroid8BehaviorTest : CardPaneBehavior()

@Config(sdk = [31])
class CardPaneAndroid12BehaviorTest : CardPaneBehavior()

@Config(sdk = [33])
class CardPaneAndroid13BehaviorTest : CardPaneBehavior()

abstract class CardPressBehavior : GlassPaneBehavior() {

    override val paneShape: @Composable () -> Shape = { Theme.container.card }

    @Test
    fun `clickable card press highlight stays inside the card shape`() = assertPressInside {
        SketchbookCard(onClick = {}, modifier = it.size(200.dp, 100.dp)) {}
    }
}

@Config(sdk = [26])
class CardPressAndroid8BehaviorTest : CardPressBehavior()

@Config(sdk = [33])
class CardPressAndroid13BehaviorTest : CardPressBehavior()

abstract class CardMatteBehavior : BehaviorTest() {

    private var clicks = 0

    private fun show(left: @Composable () -> Unit, right: @Composable () -> Unit) {
        composeRule.mainClock.autoAdvance = false
        composeRule.setContent {
            Theme(darkTheme = false, dynamicColor = false) {
                Row(Modifier.size(360.dp, 100.dp).background(Color.Black).padding(10.dp)) {
                    left()
                    Spacer(Modifier.width(30.dp))
                    right()
                }
            }
        }
        composeRule.mainClock.advanceTimeBy(100)
        composeRule.waitForIdle()
    }

    private fun pixels(): PixelMap = composeRule.onRoot().captureToImage().toPixelMap()

    @Test
    fun `static and clickable cards render the same outline and fill`() {
        show(
            { SketchbookCard(Modifier.size(150.dp, 80.dp)) {} },
            { SketchbookCard(onClick = {}, modifier = Modifier.size(150.dp, 80.dp)) {} }
        )
        val image = pixels()

        var worst = 0
        for (y in 20 until 180) for (x in 20 until 320) {
            worst = maxOf(worst, distance(image.opaque(x, y), image.opaque(x + 360, y)))
        }
        assertTrue("worst difference $worst", worst <= 2)
    }

    @Test
    fun `disabled card is a flat matte fill even when glow and rim are requested`() {
        show(
            { SketchbookCard(onClick = {}, modifier = Modifier.size(150.dp, 80.dp), enabled = false, useGlow = true, specularRim = true) {} },
            { SketchbookCard(onClick = {}, modifier = Modifier.size(150.dp, 80.dp), enabled = false) {} }
        )
        val image = pixels()
        val reference = image.opaque(160, 100)

        var worst = 0
        for (y in 22 until 178 step 2) for (x in 80 until 260 step 2) {
            worst = maxOf(worst, distance(image.opaque(x, y), reference))
            worst = maxOf(worst, distance(image.opaque(x + 360, y), reference))
        }
        assertTrue("matte spread $worst", worst <= 4)
    }

    @Test
    fun `enabled card with glow and rim is visibly less flat than a disabled one`() {
        show(
            { SketchbookCard(onClick = {}, modifier = Modifier.size(150.dp, 80.dp), useGlow = true, specularRim = true) {} },
            { SketchbookCard(onClick = {}, modifier = Modifier.size(150.dp, 80.dp), enabled = false, useGlow = true, specularRim = true) {} }
        )
        val image = pixels()

        fun spread(offset: Int): Int {
            val reference = image.opaque(offset + 160, 100)
            var worst = 0
            for (y in 22 until 178 step 2) for (x in 80 until 260 step 2) {
                worst = maxOf(worst, distance(image.opaque(offset + x, y), reference))
            }
            return worst
        }
        assertTrue("enabled ${spread(0)} disabled ${spread(360)}", spread(0) > spread(360) + 20)
    }

    @Test
    fun `pressing a disabled card shows no press highlight and does not click`() {
        show(
            { SketchbookCard(onClick = { clicks++ }, modifier = Modifier.testTag("card").size(150.dp, 80.dp), enabled = false) {} },
            {}
        )
        val idle = pixels()

        composeRule.onNodeWithTag("card").performTouchInput { down(center) }
        composeRule.mainClock.advanceTimeBy(150)

        assertEquals(0, clicks)
        var worst = 0
        val pressed = pixels()
        for (y in 20 until 180) for (x in 20 until 320) worst = maxOf(worst, distance(idle.opaque(x, y), pressed.opaque(x, y)))
        assertTrue("press changed $worst", worst <= 2)
    }
}

@Config(sdk = [26])
class CardMatteAndroid8BehaviorTest : CardMatteBehavior()

@Config(sdk = [33])
class CardMatteAndroid13BehaviorTest : CardMatteBehavior()
