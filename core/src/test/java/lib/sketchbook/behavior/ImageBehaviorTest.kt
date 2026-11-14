package lib.sketchbook.behavior

import android.graphics.Bitmap
import android.graphics.Color as AndroidColor
import android.view.View
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.test.core.app.ApplicationProvider
import lib.sketchbook.Image
import lib.sketchbook.PaletteImageState
import lib.sketchbook.rememberPaletteImageState
import lib.sketchbook.util.putLongArray
import lib.sketchbook.util.rememberSharedPrefs
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ImageBehaviorTest : BehaviorTest() {

    @Test
    fun `palette of a red image is opaque red`() {
        val bitmap = Bitmap.createBitmap(IntArray(64 * 64) { AndroidColor.RED }, 64, 64, Bitmap.Config.ARGB_8888)
        var state: PaletteImageState? = null
        composeRule.setContent {
            val palette = rememberPaletteImageState(bitmap, color = Color.Black, contentColor = Color.White)
            state = palette
            Image(state = palette)
        }

        composeRule.waitUntil(timeoutMillis = 10_000) { state!!.palette.color != Color.Black }

        assertEquals(1f, state!!.palette.color.alpha, 0f)
        assertTrue(state!!.palette.color.red > .9f)
        assertEquals(1f, state!!.palette.textColor.alpha, 0f)
        assertEquals(1f, state!!.palette.titleColor.alpha, 0f)
    }

    @Test
    fun `shared preferences in edit mode accept writes without crashing`() {
        val editMode = object : View(ApplicationProvider.getApplicationContext()) {
            override fun isInEditMode() = true
        }
        composeRule.setContent {
            CompositionLocalProvider(LocalView provides editMode) {
                val prefs = rememberSharedPrefs("edit-mode")
                val editor = prefs.edit()
                editor.putLongArray("key", longArrayOf(1L))
                editor.apply()
            }
        }
        composeRule.waitForIdle()
    }
}
