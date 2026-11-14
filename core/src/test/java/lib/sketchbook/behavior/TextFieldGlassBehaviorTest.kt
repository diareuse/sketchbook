package lib.sketchbook.behavior

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PixelMap
import androidx.compose.ui.graphics.toPixelMap
import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.unit.dp
import lib.sketchbook.SketchbookTextField
import lib.sketchbook.StripedBackdrop
import lib.sketchbook.contrastRatio
import lib.sketchbook.distance
import lib.sketchbook.opaque
import lib.sketchbook.theme.Theme
import lib.sketchbook.theme.contentColorFor
import org.junit.Assert.assertTrue
import org.junit.Test
import org.robolectric.annotation.Config

class TextFieldGlassBehaviorTest : BehaviorTest() {

    private var textColor = Color.Unspecified

    private fun render(
        dark: Boolean = false,
        enabled: Boolean = true,
        isError: Boolean = false
    ): PixelMap {
        composeRule.setContent {
            Theme(darkTheme = dark, dynamicColor = false) {
                textColor = Theme.color.contentColorFor(Theme.color.emphasis.background)
                Field(enabled, isError)
            }
        }
        composeRule.waitForIdle()
        return composeRule.onRoot().captureToImage().toPixelMap()
    }

    @Composable
    private fun Field(enabled: Boolean, isError: Boolean) = StripedBackdrop(Modifier.size(360.dp, 120.dp)) {
        SketchbookTextField(
            value = "",
            onValueChange = {},
            modifier = Modifier.fillMaxWidth(),
            enabled = enabled,
            isError = isError
        )
    }

    private fun container(image: PixelMap) = image.opaque(NarrowStripeX, 60)

    @Test
    @Config(sdk = [26])
    fun `container lets the backdrop show through`() {
        val image = render()

        assertTrue(distance(image.opaque(RedStripeX, 60), image.opaque(BlueStripeX, 60)) > 40)
    }

    @Test
    @Config(sdk = [26])
    fun `container tints different backdrop stripes towards one colour`() {
        val image = render()
        val red = image.opaque(RedStripeX, 60)
        val blue = image.opaque(BlueStripeX, 60)

        assertTrue(distance(red, blue) < distance(Color.Red, Color.Blue))
    }

    @Test
    @Config(sdk = [26])
    fun `text stays readable on the container over every backdrop stripe in light theme`() {
        val image = render()

        listOf(RedStripeX, BlueStripeX).forEach {
            val ratio = contrastRatio(textColor, image.opaque(it, 60))
            assertTrue("x=$it ratio $ratio", ratio >= 4.5f)
        }
    }

    @Test
    @Config(sdk = [26], qualifiers = "w360dp-h640dp-normal-night-xhdpi")
    fun `text stays readable on the container over every backdrop stripe in dark theme`() {
        val image = render(dark = true)

        listOf(RedStripeX, BlueStripeX).forEach {
            val ratio = contrastRatio(textColor, image.opaque(it, 60))
            assertTrue("x=$it ratio $ratio", ratio >= 4.5f)
        }
    }

    @Test
    @Config(sdk = [26])
    fun `error container is distinguishable from the regular container`() {
        val regular = container(render())
        val error = container(renderAgain(isError = true))

        assertTrue(distance(regular, error) > 40)
    }

    @Test
    @Config(sdk = [26])
    fun `disabled container is distinguishable from the regular container`() {
        val regular = container(render())
        val disabled = container(renderAgain(enabled = false))

        assertTrue(distance(regular, disabled) > 10)
    }

    private fun renderAgain(enabled: Boolean = true, isError: Boolean = false): PixelMap {
        composeRule.activityRule.scenario.recreate()
        return render(enabled = enabled, isError = isError)
    }

    private companion object {
        const val NarrowStripeX = 20
        const val RedStripeX = 20
        const val BlueStripeX = 60
    }
}
