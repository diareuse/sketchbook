package lib.sketchbook.behavior

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import lib.sketchbook.modifier.BeamPalette
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class BeamPaletteBehaviorTest {

    @Test
    fun `the colorful palette fits the shader`() {
        assertTrue(BeamPalette.colorful.size in 2..8)
    }

    @Test
    fun `a glass palette from an accent has several distinct colors that fit the shader`() {
        listOf(Color.White, Color.Red, Color(0xFF2196F3)).forEach { accent ->
            val palette = BeamPalette.glass(accent)

            assertTrue("$accent size ${palette.size}", palette.size in 3..8)
            assertTrue("$accent distinct ${palette.distinct().size}", palette.distinct().size >= 4)
        }
    }

    @Test
    fun `a glass palette fades in from a faint tail`() {
        val palette = BeamPalette.glass(Color.Red)

        assertTrue(palette.first().alpha < palette.last().alpha)
    }

    @Test
    fun `a glass palette of a translucent accent never exceeds its opacity`() {
        val palette = BeamPalette.glass(Color.White.copy(alpha = .4f))

        assertTrue(palette.all { it.alpha <= .4f + 1e-3f })
    }

    @Test
    fun `a glass palette keeps the accent in its body`() {
        val accent = Color(0xFFE53935)

        assertEquals(true, BeamPalette.glass(accent).any { it.toArgb() == accent.toArgb() })
    }

    @Test
    fun `a neutral accent still produces tinted stops`() {
        val tinted = BeamPalette.glass(Color.White).count { it.red != it.green || it.green != it.blue }

        assertTrue("tinted stops $tinted", tinted >= 2)
    }
}
