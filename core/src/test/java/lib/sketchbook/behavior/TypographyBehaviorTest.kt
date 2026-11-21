package lib.sketchbook.behavior

import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.font.ResourceFont
import lib.sketchbook.R
import lib.sketchbook.theme.Theme
import org.junit.Assert.assertEquals
import org.junit.Test

class TypographyBehaviorTest : BehaviorTest() {

    @Test
    fun `theme font family maps each sora file to its true weight`() {
        var family: FontFamily? = null
        composeRule.setContent { Theme(darkTheme = false) { family = Theme.textStyle.title.fontFamily } }
        composeRule.waitForIdle()

        val fonts = (family as androidx.compose.ui.text.font.FontListFontFamily).fonts
            .map { it as ResourceFont }
            .associate { it.weight to it.resId }
        assertEquals(R.font.sora_medium, fonts[FontWeight.Medium])
        assertEquals(R.font.sora_semi_bold, fonts[FontWeight.SemiBold])
        assertEquals(R.font.sora_bold, fonts[FontWeight.Bold])
        assertEquals(R.font.sora_extra_bold, fonts[FontWeight.ExtraBold])
        assertEquals(4, fonts.size)
    }
}
