@file:OptIn(ExperimentalMaterial3Api::class)

package lib.sketchbook.behavior

import androidx.compose.material3.*
import androidx.compose.ui.*
import androidx.compose.ui.platform.*
import androidx.compose.ui.test.*
import androidx.compose.ui.text.*
import androidx.compose.ui.text.font.*
import androidx.compose.ui.unit.*
import lib.sketchbook.SketchbookTopAppBar
import lib.sketchbook.theme.Theme
import lib.sketchbook.util.fastLerp
import org.junit.Assert.assertEquals
import org.junit.Test

class TopAppBarBehaviorTest : BehaviorTest() {

    private fun bar() {
        composeRule.setContent {
            Theme(darkTheme = false) {
                SketchbookTopAppBar(
                    modifier = Modifier.testTag("bar"),
                    title = { Text("I'm title") }
                )
            }
        }
        composeRule.waitForIdle()
    }

    @Test
    fun `title is exposed to accessibility once`() {
        bar()

        composeRule.onAllNodesWithText("I'm title").assertCountEquals(1)
    }

    @Test
    fun `interpolating text styles also interpolates line height and letter spacing`() {
        val start = TextStyle(
            fontSize = 36.sp,
            lineHeight = 36.sp,
            letterSpacing = 1.sp,
            fontWeight = FontWeight.Normal
        )
        val end = TextStyle(
            fontSize = 20.sp,
            lineHeight = 20.sp,
            letterSpacing = 0.sp,
            fontWeight = FontWeight.Medium
        )

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
