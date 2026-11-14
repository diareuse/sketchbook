package lib.sketchbook.behavior

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PixelMap
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.toPixelMap
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.getBoundsInRoot
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onRoot
import lib.sketchbook.SketchbookButton
import lib.sketchbook.SketchbookExtendedFloatingActionButton
import lib.sketchbook.SketchbookFloatingActionButton
import lib.sketchbook.distance
import lib.sketchbook.opaque
import lib.sketchbook.theme.Theme
import org.junit.Assert.assertTrue
import org.junit.Test
import org.robolectric.annotation.Config

private val FabCases = listOf(
    ControlCase("fab", null, width = true, disableable = false) { m, _, act ->
        SketchbookFloatingActionButton(act, "add", m) { Icon(Icons.Default.Add, null) }
    },
    ControlCase("extended", null, disableable = false) { m, _, act ->
        SketchbookExtendedFloatingActionButton(act, m, icon = { Icon(Icons.Default.Add, null) }) { Text("Create") }
    }
)

@Config(sdk = [34])
class FabInteractionAndroid14BehaviorTest : ControlInteractionBehavior() {
    override val cases = FabCases
}

@Config(sdk = [33])
class FabInteractionAndroid13BehaviorTest : ControlInteractionBehavior() {
    override val cases = FabCases
}

abstract class FabPaneBehavior : GlassPaneBehavior() {

    override val paneShape: @Composable () -> Shape = { Theme.container.card }

    @Test
    fun `floating action button pane edge matches the theme card shape without a shadow`() = assertPane {
        SketchbookFloatingActionButton({}, "add", it) { Icon(Icons.Default.Add, null) }
    }

    @Test
    fun `extended floating action button pane edge matches the theme card shape without a shadow`() = assertPane {
        SketchbookExtendedFloatingActionButton({}, it, icon = { Icon(Icons.Default.Add, null) }) { Text("Create") }
    }
}

@Config(sdk = [26])
class FabPaneAndroid8BehaviorTest : FabPaneBehavior()

@Config(sdk = [31])
class FabPaneAndroid12BehaviorTest : FabPaneBehavior()

@Config(sdk = [33])
class FabPaneAndroid13BehaviorTest : FabPaneBehavior()

abstract class FabPressBehavior : GlassPaneBehavior() {

    override val paneShape: @Composable () -> Shape = { Theme.container.card }

    @Test
    fun `floating action button press highlight stays inside the pane shape`() = assertPressInside {
        SketchbookFloatingActionButton({}, "add", it) { Icon(Icons.Default.Add, null) }
    }

    @Test
    fun `extended floating action button press highlight stays inside the pane shape`() = assertPressInside {
        SketchbookExtendedFloatingActionButton({}, it, icon = { Icon(Icons.Default.Add, null) }) { Text("Create") }
    }
}

@Config(sdk = [26])
class FabPressAndroid8BehaviorTest : FabPressBehavior()

@Config(sdk = [33])
class FabPressAndroid13BehaviorTest : FabPressBehavior()

@Config(sdk = [26])
class FabOpacityBehaviorTest : BehaviorTest() {

    @Test
    fun `floating action button lets less backdrop through than a filled button`() {
        val image = showOverRedAndBlue()

        assertTrue(leak(image, "fab") < leak(image, "button") / 2)
    }

    @Test
    fun `extended floating action button lets less backdrop through than a filled button`() {
        val image = showOverRedAndBlue()

        assertTrue(leak(image, "extended") < leak(image, "button") / 2)
    }

    @Test
    fun `floating action button is almost opaque`() {
        val image = showOverRedAndBlue()

        assertTrue("leak ${leak(image, "fab")}", leak(image, "fab") < 12)
    }

    @Test
    fun `floating action button is still glass and lets some backdrop through`() {
        val image = showOverRedAndBlue()

        assertTrue(leak(image, "fab") > 0)
    }

    private fun leak(image: PixelMap, name: String): Int =
        distance(sample(image, "${name}Red"), sample(image, "${name}Blue"))

    private fun sample(image: PixelMap, tag: String): Color {
        val b = composeRule.onNodeWithTag(tag).getBoundsInRoot()
        return image.opaque((b.left.value * 2 + 8).toInt(), ((b.top + b.bottom).value).toInt())
    }

    private fun showOverRedAndBlue(): PixelMap {
        composeRule.setContent {
            Theme(darkTheme = false, dynamicColor = false) {
                Column {
                    listOf("Red" to Color.Red, "Blue" to Color.Blue).forEach { (name, color) ->
                        Row(Modifier.background(color)) {
                            SketchbookFloatingActionButton({}, "add", Modifier.testTag("fab$name")) {
                                Icon(Icons.Default.Add, null)
                            }
                            SketchbookExtendedFloatingActionButton(
                                {},
                                Modifier.testTag("extended$name"),
                                icon = { Icon(Icons.Default.Add, null) }
                            ) { Text("Create") }
                            Box {
                                SketchbookButton({}, Modifier.testTag("button$name")) { Text("Go") }
                            }
                        }
                    }
                }
            }
        }
        composeRule.waitForIdle()
        return composeRule.onRoot().captureToImage().toPixelMap()
    }
}
