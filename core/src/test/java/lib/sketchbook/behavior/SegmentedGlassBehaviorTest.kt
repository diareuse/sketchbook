package lib.sketchbook.behavior

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PixelMap
import androidx.compose.ui.graphics.toPixelMap
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.getBoundsInRoot
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.height
import androidx.compose.ui.unit.width
import lib.sketchbook.SketchbookMultiChoiceSegmentedButton
import lib.sketchbook.SketchbookSegmentedButton
import lib.sketchbook.distance
import lib.sketchbook.opaque
import lib.sketchbook.theme.Theme
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.robolectric.annotation.Config

private val SegmentedCases = listOf(
    ControlCase("single", true, disableable = true) { m, e, act ->
        SketchbookSegmentedButton(0, { act() }, 3, m, label = { Text("S$it") }, enabled = e)
    },
    ControlCase("multiOff", true) { m, e, act ->
        SketchbookMultiChoiceSegmentedButton(emptySet(), { act() }, 3, m, label = { Text("M$it") }, enabled = e)
    },
    ControlCase("multiOn", false) { m, e, act ->
        SketchbookMultiChoiceSegmentedButton(setOf(1), { act() }, 3, m, label = { Text("N$it") }, enabled = e)
    }
)

@Config(sdk = [34])
class SegmentedInteractionAndroid14BehaviorTest : ControlInteractionBehavior() {
    override val cases = SegmentedCases
}

@Config(sdk = [33])
class SegmentedInteractionAndroid13BehaviorTest : ControlInteractionBehavior() {
    override val cases = SegmentedCases
}

abstract class SegmentedPaneBehavior : GlassPaneBehavior() {

    @Test
    fun `single choice row pane edge matches the theme button shape`() = assertPane {
        SketchbookSegmentedButton(1, {}, 3, it, label = { Text("Seg$it") })
    }

    @Test
    fun `multi choice row pane edge matches the theme button shape`() = assertPane {
        SketchbookMultiChoiceSegmentedButton(setOf(0, 2), {}, 3, it, label = { Text("Seg$it") })
    }
}

@Config(sdk = [26])
class SegmentedPaneAndroid8BehaviorTest : SegmentedPaneBehavior()

@Config(sdk = [31])
class SegmentedPaneAndroid12BehaviorTest : SegmentedPaneBehavior()

@Config(sdk = [33])
class SegmentedPaneAndroid13BehaviorTest : SegmentedPaneBehavior()

abstract class SegmentedPressBehavior : GlassPaneBehavior() {

    @Test
    fun `single choice press highlight stays inside the row shape`() = assertPressInside {
        SketchbookSegmentedButton(0, {}, 3, it, label = { Text("Seg$it") })
    }

    @Test
    fun `multi choice press highlight stays inside the row shape`() = assertPressInside {
        SketchbookMultiChoiceSegmentedButton(emptySet(), {}, 3, it, label = { Text("Seg$it") })
    }
}

@Config(sdk = [26])
class SegmentedPressAndroid8BehaviorTest : SegmentedPressBehavior()

@Config(sdk = [33])
class SegmentedPressAndroid13BehaviorTest : SegmentedPressBehavior()

@Config(sdk = [26])
class SegmentedLayoutBehaviorTest : BehaviorTest() {

    @Test
    fun `the row is exactly 48dp tall and every segment fills it`() {
        show()

        assertEquals(48.dp, composeRule.onNodeWithTag("row").getBoundsInRoot().height)
        segments().forEach { assertEquals(48.dp, it.height) }
    }

    @Test
    fun `selected and unselected segments are equally wide because there is no check icon or padding`() {
        show()

        val widths = segments().map { it.width }
        assertEquals(widths.first(), widths.last())
        assertEquals(widths.first(), widths[1])
    }

    @Test
    fun `the selected label stays centred in its segment because there is no check icon`() {
        show()
        val segment = segments()[0]
        val label = composeRule.onNodeWithText("Seg0", useUnmergedTree = true).getBoundsInRoot()

        assertEquals(((segment.left + segment.right) / 2).value, ((label.left + label.right) / 2).value, 1f)
    }

    @Test
    fun `adjacent segments are not separated by a border`() {
        val image = show(Color.White)
        val boundary = segments()[1].right.value * 2 - 1

        val on = image.opaque(boundary.toInt(), 52)
        val before = image.opaque(boundary.toInt() - 8, 52)
        val after = image.opaque(boundary.toInt() + 8, 52)

        assertTrue(distance(on, before) <= 12)
        assertTrue(distance(on, after) <= 12)
    }

    @Test
    fun `the selected segment is tinted inside the row`() {
        val image = show(Color.Gray)
        val selected = segments()[0]
        val other = segments()[2]
        val y = 52

        assertTrue(
            distance(
                image.opaque((selected.left.value * 2 + 6).toInt(), y),
                image.opaque((other.right.value * 2 - 6).toInt(), y)
            ) > 20
        )
    }

    @Test
    fun `single choice segments expose the radio button role`() {
        show()

        composeRule.onNodeWithText("Seg1")
            .assert(SemanticsMatcher.expectValue(SemanticsProperties.Role, Role.RadioButton))
    }

    private fun segments() = composeRule
        .onAllNodes(SemanticsMatcher.expectValue(SemanticsProperties.Role, Role.RadioButton))
        .fetchSemanticsNodes()
        .sortedBy { it.boundsInRoot.left }
        .map { with(composeRule.density) { it.boundsInRoot.let { r -> androidx.compose.ui.unit.DpRect(r.left.toDp(), r.top.toDp(), r.right.toDp(), r.bottom.toDp()) } } }

    private fun show(backdrop: Color = Color.Gray): PixelMap {
        composeRule.setContent {
            Theme(darkTheme = false, dynamicColor = false) {
                Column(Modifier.background(backdrop).fillMaxWidth()) {
                    Box(Modifier.testTag("row")) {
                        SketchbookSegmentedButton(0, {}, 3, label = { Text("Seg$it") })
                    }
                }
            }
        }
        composeRule.waitForIdle()
        return composeRule.onRoot().captureToImage().toPixelMap()
    }
}
