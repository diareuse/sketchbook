package lib.sketchbook.behavior

import android.os.Build
import android.view.HapticFeedbackConstants
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.getBoundsInRoot
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.height
import androidx.compose.ui.unit.width
import lib.sketchbook.theme.Theme
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ControlCase(
    val name: String,
    val on: Boolean?,
    val width: Boolean = false,
    val content: @Composable (modifier: Modifier, enabled: Boolean, act: () -> Unit) -> Unit
)

abstract class ControlInteractionBehavior : BehaviorTest() {

    protected abstract val cases: List<ControlCase>

    private val haptics = RecordingHapticFeedback()
    private val acted = mutableMapOf<String, Int>()

    private fun show(enabled: Boolean) {
        composeRule.setContent {
            Theme(darkTheme = false, dynamicColor = false) {
                CompositionLocalProvider(LocalHapticFeedback provides haptics) {
                    Column {
                        cases.forEach { case ->
                            Box(Modifier.testTag(case.name)) {
                                case.content(Modifier, enabled) {
                                    acted[case.name] = (acted[case.name] ?: 0) + 1
                                }
                            }
                        }
                    }
                }
            }
        }
        composeRule.waitForIdle()
    }

    @Test
    fun `an enabled control reports one change and performs exactly one confirmation haptic`() {
        show(enabled = true)
        cases.forEach { case ->
            haptics.performed.clear()

            composeRule.onNodeWithTag(case.name).performClick()
            composeRule.waitForIdle()

            assertEquals(case.name, 1, acted[case.name])
            assertEquals(case.name, listOf(confirmation(case.on)), haptics.performed)
        }
    }

    @Test
    fun `a disabled control performs exactly one denial haptic and reports no change`() {
        show(enabled = false)
        cases.forEach { case ->
            haptics.performed.clear()

            composeRule.onNodeWithTag(case.name).performClick()
            composeRule.waitForIdle()

            assertEquals(case.name, null, acted[case.name])
            assertEquals(case.name, listOf(denial()), haptics.performed)
        }
    }

    @Test
    fun `every control is at least 48dp tall`() {
        show(enabled = true)
        cases.forEach { case ->
            val bounds = composeRule.onNodeWithTag(case.name).getBoundsInRoot()
            assertTrue("${case.name} height ${bounds.height}", bounds.height >= 48.dp)
            if (case.width) assertTrue("${case.name} width ${bounds.width}", bounds.width >= 48.dp)
        }
    }

    private fun confirmation(on: Boolean?): HapticFeedbackType {
        val modern = Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE
        return HapticFeedbackType(
            when {
                on == null -> HapticFeedbackConstants.CONTEXT_CLICK
                on && modern -> HapticFeedbackConstants.TOGGLE_ON
                on -> HapticFeedbackConstants.CONTEXT_CLICK
                modern -> HapticFeedbackConstants.TOGGLE_OFF
                else -> HapticFeedbackConstants.CLOCK_TICK
            }
        )
    }

    private fun denial(): HapticFeedbackType = HapticFeedbackType(
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) HapticFeedbackConstants.REJECT
        else HapticFeedbackConstants.LONG_PRESS
    )
}
