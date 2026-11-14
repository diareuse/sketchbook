@file:OptIn(ExperimentalMaterial3Api::class)

package lib.sketchbook.behavior

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.getBoundsInRoot
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import lib.sketchbook.SketchbookTopAppBar
import lib.sketchbook.theme.Theme
import lib.sketchbook.util.fastLerp
import org.junit.Assert.assertEquals
import org.junit.Test

class TopAppBarBehaviorTest : BehaviorTest() {

    private var contentPadding by mutableStateOf(PaddingValues())

    private fun bar() {
        composeRule.setContent {
            Theme(darkTheme = false) {
                SketchbookTopAppBar(
                    modifier = Modifier.testTag("bar"),
                    title = { Text("I'm title") },
                    scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(
                        state = rememberTopAppBarState()
                    ),
                    contentPadding = contentPadding
                )
            }
        }
        composeRule.waitForIdle()
    }

    private fun barHeight(): Float {
        composeRule.waitForIdle()
        return composeRule.onNodeWithTag("bar").getBoundsInRoot().let { it.bottom.value - it.top.value }
    }

    @Test
    fun `top content padding is counted once in the bar height`() {
        contentPadding = PaddingValues(top = 0.dp)
        bar()
        val flat = barHeight()

        contentPadding = PaddingValues(top = 12.dp)

        assertEquals(12f, barHeight() - flat, 0.01f)
    }

    @Test
    fun `bottom content padding is counted once in the bar height`() {
        contentPadding = PaddingValues(bottom = 0.dp)
        bar()
        val flat = barHeight()

        contentPadding = PaddingValues(bottom = 12.dp)

        assertEquals(12f, barHeight() - flat, 0.01f)
    }

    @Test
    fun `title is exposed to accessibility once`() {
        bar()

        composeRule.onAllNodesWithText("I'm title").assertCountEquals(1)
    }

    @Test
    fun `interpolating text styles also interpolates line height and letter spacing`() {
        val start = TextStyle(fontSize = 36.sp, lineHeight = 36.sp, letterSpacing = 1.sp, fontWeight = FontWeight.Normal)
        val end = TextStyle(fontSize = 20.sp, lineHeight = 20.sp, letterSpacing = 0.sp, fontWeight = FontWeight.Medium)

        val mid = fastLerp(start, end, .5f)

        assertEquals(28f, mid.lineHeight.value, 0.001f)
        assertEquals(.5f, mid.letterSpacing.value, 0.001f)
    }

    @Test
    fun `interpolating text styles with unspecified or mismatched units does not crash`() {
        val start = TextStyle(fontSize = 36.sp, lineHeight = 1.2.em, letterSpacing = 1.sp)
        val end = TextStyle(fontSize = 20.sp, lineHeight = 20.sp)

        fastLerp(start, end, .5f)
    }
}
