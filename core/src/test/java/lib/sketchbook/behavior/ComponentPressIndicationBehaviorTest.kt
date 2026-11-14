package lib.sketchbook.behavior

import android.os.Build
import android.view.HapticFeedbackConstants
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toPixelMap
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.SemanticsNodeInteraction
import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.junit4.ComposeContentTestRule
import androidx.compose.ui.test.center
import androidx.compose.ui.test.down
import androidx.compose.ui.test.up
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.unit.dp
import lib.sketchbook.SketchbookButton
import lib.sketchbook.SketchbookCheckbox
import lib.sketchbook.SketchbookChip
import lib.sketchbook.SketchbookExtendedFloatingActionButton
import lib.sketchbook.SketchbookFilledIconButton
import lib.sketchbook.SketchbookFilterChip
import lib.sketchbook.SketchbookFloatingActionButton
import lib.sketchbook.SketchbookGlassButton
import lib.sketchbook.SketchbookGlassIconButton
import lib.sketchbook.SketchbookIconButton
import lib.sketchbook.SketchbookListItem
import lib.sketchbook.SketchbookMultiChoiceSegmentedButton
import lib.sketchbook.SketchbookNavigationBar
import lib.sketchbook.SketchbookNavigationBarItem
import lib.sketchbook.SketchbookNavigationRail
import lib.sketchbook.SketchbookNavigationRailItem
import lib.sketchbook.SketchbookOutlinedButton
import lib.sketchbook.SketchbookRadioButton
import lib.sketchbook.SketchbookSearchBar
import lib.sketchbook.SketchbookSegmentedButton
import lib.sketchbook.SketchbookSwitch
import lib.sketchbook.SketchbookTab
import lib.sketchbook.SketchbookTabRow
import lib.sketchbook.SketchbookTonalButton
import lib.sketchbook.SketchbookTonalIconButton
import lib.sketchbook.theme.Theme
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Test
import org.robolectric.annotation.Config

abstract class ComponentPressIndicationBehavior : BehaviorTest() {

    private val haptics = RecordingHapticFeedback()

    @Test
    fun `a pressed filled button shows the theme press indication`() = assertIndicated {
        SketchbookButton({}, it) { Text("Go") }
    }

    @Test
    fun `a pressed tonal button shows the theme press indication`() = assertIndicated {
        SketchbookTonalButton({}, it) { Text("Go") }
    }

    @Test
    fun `a pressed outlined button shows the theme press indication`() = assertIndicated {
        SketchbookOutlinedButton({}, it) { Text("Go") }
    }

    @Test
    fun `a pressed glass button shows the theme press indication`() = assertIndicated {
        SketchbookGlassButton({}, it) { Text("Go") }
    }

    @Test
    fun `a pressed icon button shows the theme press indication`() = assertIndicated {
        SketchbookIconButton({}, "go", it) { Icon(Icons.Default.Add, null) }
    }

    @Test
    fun `a pressed filled icon button shows the theme press indication`() = assertIndicated {
        SketchbookFilledIconButton({}, "go", it) { Icon(Icons.Default.Add, null) }
    }

    @Test
    fun `a pressed tonal icon button shows the theme press indication`() = assertIndicated {
        SketchbookTonalIconButton({}, "go", it) { Icon(Icons.Default.Add, null) }
    }

    @Test
    fun `a pressed glass icon button shows the theme press indication`() = assertIndicated {
        SketchbookGlassIconButton({}, "go", it) { Icon(Icons.Default.Add, null) }
    }

    @Test
    fun `a pressed floating action button shows the theme press indication`() = assertIndicated {
        SketchbookFloatingActionButton({}, "go", it) { Icon(Icons.Default.Add, null) }
    }

    @Test
    fun `a pressed extended floating action button shows the theme press indication`() = assertIndicated {
        SketchbookExtendedFloatingActionButton({}, it, icon = { Icon(Icons.Default.Add, null) }) { Text("Go") }
    }

    @Test
    fun `a pressed chip shows the theme press indication`() = assertIndicated {
        SketchbookChip({}, { Text("Go") }, it)
    }

    @Test
    fun `a pressed filter chip shows the theme press indication`() = assertIndicated {
        SketchbookFilterChip(false, {}, { Text("Go") }, it)
    }

    @Test
    fun `a pressed checkbox shows the theme press indication`() = assertIndicated {
        SketchbookCheckbox(false, {}, it)
    }

    @Test
    fun `a pressed labeled checkbox shows the theme press indication`() = assertIndicated {
        SketchbookCheckbox(false, {}, it, label = { Text("Go") })
    }

    @Test
    fun `a pressed radio button shows the theme press indication`() = assertIndicated {
        SketchbookRadioButton(false, {}, it)
    }

    @Test
    fun `a pressed labeled radio button shows the theme press indication`() = assertIndicated {
        SketchbookRadioButton(false, {}, it, label = { Text("Go") })
    }

    @Test
    fun `a pressed single choice segmented button shows the theme press indication`() = assertIndicated {
        SketchbookSegmentedButton(0, {}, 3, it, label = { Text("S$it") })
    }

    @Test
    fun `a pressed multi choice segmented button shows the theme press indication`() = assertIndicated {
        SketchbookMultiChoiceSegmentedButton(emptySet(), {}, 3, it, label = { Text("S$it") })
    }

    @Test
    fun `a pressed tab shows the theme press indication`() = assertIndicated {
        SketchbookTabRow(0, Modifier.size(200.dp, 56.dp)) {
            SketchbookTab(true, {}, Modifier.testTag(Target), text = { Text("One") })
            SketchbookTab(false, {}, text = { Text("Two") })
        }
    }

    @Test
    fun `a pressed navigation bar item shows the theme press indication`() = assertIndicated {
        SketchbookNavigationBar(Modifier.testTag(Target)) {
            SketchbookNavigationBarItem(false, {}, { Icon(Icons.Default.Home, null) }, label = { Text("Home") })
        }
    }

    @Test
    fun `a pressed navigation rail item shows the theme press indication`() = assertIndicated {
        SketchbookNavigationRail(Modifier.testTag(Target)) {
            SketchbookNavigationRailItem(false, {}, { Icon(Icons.Default.Home, null) }, label = { Text("Home") })
        }
    }

    @Test
    fun `a pressed list item shows the theme press indication`() = assertIndicated {
        SketchbookListItem(headline = { Text("Go") }, modifier = it, onClick = {})
    }

    @Test
    fun `a pressed search clear button shows the theme press indication`() = assertIndicated(target = { onNodeWithContentDescription("Clear") }) {
        SketchbookSearchBar("query", {}, {}, "Clear", Modifier.size(260.dp, 80.dp))
    }

    @Test
    fun `a pressed switch shows the theme press indication`() = assertIndicated {
        SketchbookSwitch(false, {}, it)
    }

    @Test
    fun `pressing and releasing a button emits exactly one confirmation and no press tick`() {
        show { SketchbookButton({}, it) { Text("Go") } }
        composeRule.onNodeWithTag(Target).performTouchInput { down(center) }
        composeRule.mainClock.advanceTimeBy(150)
        composeRule.onNodeWithTag(Target).performTouchInput { up() }
        composeRule.mainClock.advanceTimeBy(600)
        assertEquals(listOf(HapticFeedbackType(HapticFeedbackConstants.CONTEXT_CLICK)), haptics.performed)
    }

    @Test
    fun `pressing a disabled button performs exactly one denial haptic`() {
        show { SketchbookButton({}, it, enabled = false) { Text("Go") } }
        composeRule.onNodeWithTag(Target).performTouchInput { down(center) }
        composeRule.mainClock.advanceTimeBy(150)
        val denial = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) HapticFeedbackConstants.REJECT
        else HapticFeedbackConstants.LONG_PRESS
        assertEquals(listOf(HapticFeedbackType(denial)), haptics.performed)
    }

    private fun assertIndicated(
        target: ComposeContentTestRule.() -> SemanticsNodeInteraction = { onNodeWithTag(Target) },
        content: @Composable (Modifier) -> Unit
    ) {
        show(content)
        val idle = frame()

        composeRule.target().performTouchInput { down(center) }
        composeRule.mainClock.advanceTimeBy(150)

        assertEquals(emptyList<HapticFeedbackType>(), haptics.performed)
        assertFalse(idle.contentEquals(frame()))
    }

    private fun show(content: @Composable (Modifier) -> Unit) {
        composeRule.mainClock.autoAdvance = false
        composeRule.setContent {
            Theme(darkTheme = false, dynamicColor = false) {
                CompositionLocalProvider(LocalHapticFeedback provides haptics) {
                    Box(Modifier.size(280.dp).background(Color.Black), contentAlignment = Alignment.Center) {
                        content(Modifier.testTag(Target))
                    }
                }
            }
        }
        composeRule.mainClock.advanceTimeBy(16)
    }

    private fun frame(): FloatArray {
        val map = composeRule.onRoot().captureToImage().toPixelMap()
        return FloatArray(map.width * map.height) { map[it % map.width, it / map.width].let { c -> c.red + c.green + c.blue } }
    }

    private companion object {
        const val Target = "target"
    }
}

@Config(sdk = [26])
class ComponentPressIndicationAndroid8BehaviorTest : ComponentPressIndicationBehavior()

@Config(sdk = [33])
class ComponentPressIndicationAndroid13BehaviorTest : ComponentPressIndicationBehavior()
