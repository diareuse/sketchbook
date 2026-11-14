package lib.sketchbook.screenshot

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import lib.sketchbook.glass.glass
import lib.sketchbook.glass.glassSource
import lib.sketchbook.theme.Theme
import org.junit.Test
import org.robolectric.annotation.Config

class GlassScreenshotTest : ScreenshotTest() {

    @Test
    @Config(sdk = [26], qualifiers = Light)
    fun `glass demo on android 8 renders tint`() = captureRoot("Glass_sdk26") { Demo() }

    @Test
    @Config(sdk = [31], qualifiers = Light)
    fun `glass demo on android 12 renders blur`() = captureRoot("Glass_sdk31") { Demo() }

    @Test
    @Config(sdk = [33], qualifiers = Light)
    fun `glass demo on android 13 renders glass`() = captureRoot("Glass_sdk33") { Demo() }

    @Composable
    private fun Demo() = Theme(darkTheme = false, dynamicColor = false) {
        Box(Modifier.fillMaxSize()) {
            Box(
                Modifier
                    .fillMaxSize()
                    .glassSource()
                    .background(
                        Brush.linearGradient(listOf(Color.Red, Color.Green, Color.Blue, Color.Yellow))
                    )
            )
            Column(Modifier.fillMaxSize().padding(24.dp)) {
                Box(Modifier.fillMaxWidth().height(160.dp).glass(Theme.container.card))
                Box(
                    Modifier
                        .padding(top = 24.dp)
                        .fillMaxWidth()
                        .height(160.dp)
                        .glass(Theme.container.card, tonalElevation = 8.dp, useGlow = true)
                )
            }
        }
    }
}
