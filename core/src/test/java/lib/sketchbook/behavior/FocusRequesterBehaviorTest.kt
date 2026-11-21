package lib.sketchbook.behavior

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.focusable
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusState
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.unit.dp
import lib.sketchbook.util.rememberFocusRequester
import org.junit.Assert.assertTrue
import org.junit.Test

class FocusRequesterBehaviorTest : BehaviorTest() {

    @Test
    fun `requesting focus on a requester that is never attached does not crash`() {
        composeRule.setContent {
            rememberFocusRequester(requestFocus = true)
            Box(Modifier.size(10.dp))
        }

        composeRule.waitForIdle()
    }

    @Test
    fun `an attached requester receives focus when requested`() {
        var state: FocusState? = null
        composeRule.setContent {
            val requester = rememberFocusRequester(requestFocus = true)
            Box(
                Modifier
                    .size(10.dp)
                    .focusRequester(requester)
                    .onFocusChanged { state = it }
                    .focusable()
            )
        }
        composeRule.waitForIdle()

        assertTrue(state?.isFocused == true)
    }
}
