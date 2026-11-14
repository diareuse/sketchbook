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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import lib.sketchbook.glass.glass
import lib.sketchbook.glass.glassSource
import lib.sketchbook.modifier.meshGradientBackground
import lib.sketchbook.modifier.shimmer
import lib.sketchbook.modifier.specularBorder
import lib.sketchbook.theme.Theme
import org.junit.Test
import org.robolectric.annotation.Config

class ShaderScreenshotTest : ScreenshotTest() {

    @Test
    @Config(sdk = [26], qualifiers = Light)
    fun `shader demo on android 8 uses gradient fallbacks`() =
        captureRoot("ShaderDemo_sdk26", animated = true) { Demo() }

    @Test
    @Config(sdk = [31], qualifiers = Light)
    fun `shader demo on android 12 uses gradient fallbacks with blur`() =
        captureRoot("ShaderDemo_sdk31", animated = true) { Demo() }

    @Test
    @Config(sdk = [33], qualifiers = Light)
    fun `shader demo on android 13 uses agsl shaders`() =
        captureRoot("ShaderDemo_sdk33", animated = true) { Demo() }

    @Composable
    private fun Demo() = Theme(darkTheme = false, dynamicColor = false) {
        val shape = Theme.container.card
        Box(
            Modifier
                .fillMaxSize()
                .glassSource()
                .meshGradientBackground(
                    listOf(Color(0xFFE53935), Color(0xFF1E88E5), Color(0xFF43A047), Color(0xFFFDD835))
                )
        )
        Column(Modifier.fillMaxSize().padding(24.dp)) {
            Box(
                Modifier
                    .fillMaxWidth()
                    .height(120.dp)
                    .glass(shape)
                    .specularBorder(shape)
            )
            Box(
                Modifier
                    .padding(top = 24.dp)
                    .fillMaxWidth()
                    .height(120.dp)
                    .clip(shape)
                    .background(Color(0xFF263238))
                    .shimmer()
            )
        }
    }
}
