package lib.sketchbook.behavior

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toPixelMap
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.center
import androidx.compose.ui.test.down
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.unit.dp
import lib.sketchbook.SketchbookCard
import lib.sketchbook.theme.Theme
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Test
import org.robolectric.annotation.Config

abstract class CardPressIndicationBehavior : BehaviorTest() {

    private val haptics = RecordingHapticFeedback()

    private fun show() {
        composeRule.mainClock.autoAdvance = false
        composeRule.setContent {
            CompositionLocalProvider(LocalHapticFeedback provides haptics) {
                Theme(darkTheme = false, dynamicColor = false) {
                    SketchbookCard(onClick = {}, modifier = Modifier.testTag("card").size(120.dp).background(Color.Black)) {}
                }
            }
        }
        composeRule.mainClock.advanceTimeBy(16)
    }

    private fun frame(): FloatArray {
        val map = composeRule.onRoot().captureToImage().toPixelMap()
        return FloatArray(map.width * map.height) { map[it % map.width, it / map.width].let { c -> c.red + c.green + c.blue } }
    }

    @Test
    fun `a pressed clickable card shows the theme press indication and emits no haptic`() {
        show()
        val idle = frame()

        composeRule.onNodeWithTag("card").performTouchInput { down(center) }
        composeRule.mainClock.advanceTimeBy(150)

        assertFalse(idle.contentEquals(frame()))
        assertEquals(emptyList<HapticFeedbackType>(), haptics.performed)
    }
}

@Config(sdk = [26])
class CardPressIndicationAndroid8BehaviorTest : CardPressIndicationBehavior()

@Config(sdk = [33])
class CardPressIndicationAndroid13BehaviorTest : CardPressIndicationBehavior()
