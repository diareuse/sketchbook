package lib.sketchbook.behavior

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.toPixelMap
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.getBoundsInRoot
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onRoot
import lib.sketchbook.SketchbookChip
import lib.sketchbook.SketchbookFilterChip
import lib.sketchbook.distance
import lib.sketchbook.opaque
import lib.sketchbook.theme.Theme
import org.junit.Assert.assertTrue
import org.junit.Test
import org.robolectric.annotation.Config

private val ChipCases = listOf(
    ControlCase("chip", null) { m, e, act -> SketchbookChip(act, { Text("Chip") }, m, enabled = e) },
    ControlCase("filterOff", true) { m, e, act ->
        SketchbookFilterChip(false, act, { Text("Filter") }, m, enabled = e)
    },
    ControlCase("filterOn", false) { m, e, act ->
        SketchbookFilterChip(true, act, { Text("Filter") }, m, enabled = e)
    }
)

@Config(sdk = [34])
class ChipInteractionAndroid14BehaviorTest : ControlInteractionBehavior() {
    override val cases = ChipCases
}

@Config(sdk = [33])
class ChipInteractionAndroid13BehaviorTest : ControlInteractionBehavior() {
    override val cases = ChipCases
}

abstract class ChipPaneBehavior : GlassPaneBehavior() {

    override val paneShape: @Composable () -> Shape = { Theme.container.buttonSmall }

    @Test
    fun `chip pane edge matches the theme small button shape`() = assertPane {
        SketchbookChip({}, { Text("Chip") }, it)
    }

    @Test
    fun `filter chip pane edge matches the theme small button shape`() = assertPane {
        SketchbookFilterChip(false, {}, { Text("Filter") }, it)
    }

    @Test
    fun `selected filter chip pane edge matches the theme small button shape`() = assertPane {
        SketchbookFilterChip(true, {}, { Text("Filter") }, it)
    }
}

@Config(sdk = [26])
class ChipPaneAndroid8BehaviorTest : ChipPaneBehavior()

@Config(sdk = [31])
class ChipPaneAndroid12BehaviorTest : ChipPaneBehavior()

@Config(sdk = [33])
class ChipPaneAndroid13BehaviorTest : ChipPaneBehavior()

abstract class ChipPressBehavior : GlassPaneBehavior() {

    override val paneShape: @Composable () -> Shape = { Theme.container.buttonSmall }

    @Test
    fun `chip press highlight stays inside the pane shape`() = assertPressInside {
        SketchbookChip({}, { Text("Chip") }, it)
    }

    @Test
    fun `filter chip press highlight stays inside the pane shape`() = assertPressInside {
        SketchbookFilterChip(false, {}, { Text("Filter") }, it)
    }
}

@Config(sdk = [26])
class ChipPressAndroid8BehaviorTest : ChipPressBehavior()

@Config(sdk = [33])
class ChipPressAndroid13BehaviorTest : ChipPressBehavior()

@Config(sdk = [26])
class ChipBorderBehaviorTest : BehaviorTest() {

    @Test
    fun `selected filter chip draws no primary outline`() {
        var primary = Color.Unspecified
        composeRule.setContent {
            Theme(darkTheme = false, dynamicColor = false) {
                primary = Theme.color.emphasis.primary
                Box(Modifier.background(Color.White)) {
                    SketchbookFilterChip(true, {}, { Text("Filter") }, Modifier.testTag("chip"))
                }
            }
        }
        composeRule.waitForIdle()
        val image = composeRule.onRoot().captureToImage().toPixelMap()
        val b = composeRule.onNodeWithTag("chip").getBoundsInRoot()
        val x = (b.left.value * 2 + 1).toInt()
        val y = ((b.top + b.bottom).value).toInt()

        assertTrue(distance(image.opaque(x, y), primary) > 40)
    }
}
