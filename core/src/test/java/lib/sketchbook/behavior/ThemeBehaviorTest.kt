package lib.sketchbook.behavior

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalHapticFeedback
import lib.sketchbook.theme.Theme
import org.junit.Assert.assertEquals
import org.junit.Assert.assertSame
import org.junit.Test

class ThemeBehaviorTest : BehaviorTest() {

    @Test
    fun `theme keeps its text container and haptic instances when the theme recomposes`() {
        var dark by mutableStateOf(false)
        val texts = mutableListOf<Any>()
        val containers = mutableListOf<Any>()
        val haptics = mutableListOf<Any>()

        composeRule.setContent {
            Theme(darkTheme = dark) {
                texts += Theme.textStyle
                containers += Theme.container
                haptics += LocalHapticFeedback.current
            }
        }
        composeRule.waitForIdle()
        dark = true
        composeRule.waitForIdle()

        assertEquals(2, texts.size)
        assertSame(texts[0], texts[1])
        assertSame(containers[0], containers[1])
        assertSame(haptics[0], haptics[1])
    }
}
