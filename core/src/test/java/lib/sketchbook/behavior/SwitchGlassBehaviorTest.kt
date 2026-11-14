package lib.sketchbook.behavior

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PixelMap
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.toPixelMap
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.getBoundsInRoot
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onRoot
import androidx.compose.foundation.shape.CircleShape
import lib.sketchbook.SketchbookSwitch
import lib.sketchbook.distance
import lib.sketchbook.opaque
import lib.sketchbook.theme.Theme
import org.junit.Assert.assertTrue
import org.junit.Test
import org.robolectric.annotation.Config

private val SwitchCases = listOf(
    ControlCase("switchOff", true, width = true) { m, e, act ->
        SketchbookSwitch(false, { act() }, m, enabled = e)
    },
    ControlCase("switchOn", false, width = true) { m, e, act ->
        SketchbookSwitch(true, { act() }, m, enabled = e)
    }
)

@Config(sdk = [34])
class SwitchInteractionAndroid14BehaviorTest : ControlInteractionBehavior() {
    override val cases = SwitchCases
}

@Config(sdk = [33])
class SwitchInteractionAndroid13BehaviorTest : ControlInteractionBehavior() {
    override val cases = SwitchCases
}

abstract class SwitchPaneBehavior : GlassPaneBehavior() {

    override val paneShape: @Composable () -> Shape = { CircleShape }

    @Test
    fun `checked switch track pane edge matches its stadium clip`() = assertPane {
        SketchbookSwitch(true, {}, it)
    }

    @Test
    fun `unchecked switch track pane edge matches its stadium clip`() = assertPane {
        SketchbookSwitch(false, {}, it)
    }
}

@Config(sdk = [26])
class SwitchPaneAndroid8BehaviorTest : SwitchPaneBehavior()

@Config(sdk = [31])
class SwitchPaneAndroid12BehaviorTest : SwitchPaneBehavior()

@Config(sdk = [33])
class SwitchPaneAndroid13BehaviorTest : SwitchPaneBehavior()

abstract class SwitchPressBehavior : GlassPaneBehavior() {

    override val paneShape: @Composable () -> Shape = { CircleShape }

    @Test
    fun `switch press highlight stays inside the track shape`() = assertPressInside {
        SketchbookSwitch(false, {}, it)
    }
}

@Config(sdk = [26])
class SwitchPressAndroid8BehaviorTest : SwitchPressBehavior()

@Config(sdk = [33])
class SwitchPressAndroid13BehaviorTest : SwitchPressBehavior()

@Config(sdk = [26])
class SwitchColourBehaviorTest : BehaviorTest() {

    @Test
    fun `checked switch has a green track over a gray backdrop`() = assertGreen(Color.Gray, "on")

    @Test
    fun `checked switch has a green track over a white backdrop`() = assertGreen(Color.White, "on")

    @Test
    fun `unchecked switch has a red track over a gray backdrop`() = assertRed(Color.Gray, "off")

    @Test
    fun `unchecked switch has a red track over a white backdrop`() = assertRed(Color.White, "off")

    @Test
    fun `disabled checked switch keeps the green track`() = assertGreen(Color.Gray, "onDisabled")

    @Test
    fun `disabled unchecked switch keeps the red track`() = assertRed(Color.Gray, "offDisabled")

    @Test
    fun `disabled switch tracks look like the enabled ones`() {
        val image = show(Color.Gray)

        assertTrue(distance(track(image, "on", true), track(image, "onDisabled", true)) <= 45)
        assertTrue(distance(track(image, "off", false), track(image, "offDisabled", false)) <= 45)
    }

    @Test
    fun `the thumb is white whether the switch is on or off apart from its rim glow`() {
        val image = show(Color.Gray)

        listOf("on" to true, "off" to false, "onDisabled" to true, "offDisabled" to false).forEach { (tag, on) ->
            val px = thumb(image, tag, on)
            assertTrue("$tag $px", minOf(px.red, px.green, px.blue) * 255 >= 190)
        }
    }

    @Test
    fun `switch exposes the switch role and toggle state`() {
        show(Color.Gray)

        composeRule.onNodeWithTag("on").assert(SemanticsMatcher.expectValue(SemanticsProperties.Role, Role.Switch))
    }

    private fun assertGreen(backdrop: Color, tag: String) {
        val px = track(show(backdrop), tag, true)

        assertTrue("$px", px.green * 255 > px.red * 255 + 40 && px.green * 255 > px.blue * 255 + 40)
    }

    private fun assertRed(backdrop: Color, tag: String) {
        val px = track(show(backdrop), tag, false)

        assertTrue("$px", px.red * 255 > px.green * 255 + 60 && px.red * 255 > px.blue * 255 + 60)
    }

    private fun show(backdrop: Color): PixelMap {
        composeRule.setContent {
            Theme(darkTheme = false, dynamicColor = false) {
                Row(Modifier.background(backdrop)) {
                    SketchbookSwitch(true, {}, Modifier.testTag("on"))
                    SketchbookSwitch(false, {}, Modifier.testTag("off"))
                    SketchbookSwitch(true, {}, Modifier.testTag("onDisabled"), enabled = false)
                    SketchbookSwitch(false, {}, Modifier.testTag("offDisabled"), enabled = false)
                }
            }
        }
        composeRule.waitForIdle()
        return composeRule.onRoot().captureToImage().toPixelMap()
    }

    private fun track(image: PixelMap, tag: String, on: Boolean): Color {
        val b = composeRule.onNodeWithTag(tag).getBoundsInRoot()
        val x = (b.left.value + if (on) 8f else 44f) * 2
        return image.opaque(x.toInt(), ((b.top + b.bottom).value).toInt())
    }

    private fun thumb(image: PixelMap, tag: String, on: Boolean): Color {
        val b = composeRule.onNodeWithTag(tag).getBoundsInRoot()
        val x = (b.left.value + if (on) 36f else 16f) * 2
        return image.opaque(x.toInt(), ((b.top + b.bottom).value).toInt())
    }
}
