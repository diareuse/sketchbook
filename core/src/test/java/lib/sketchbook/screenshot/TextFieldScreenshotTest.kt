package lib.sketchbook.screenshot

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.fillMaxSize
import lib.sketchbook.ColorfulStripes
import lib.sketchbook.SketchbookPreviewLayout
import lib.sketchbook.StripedBackdrop
import lib.sketchbook.SketchbookTextField
import org.junit.Test
import org.robolectric.annotation.Config

class TextFieldScreenshotTest : ScreenshotTest() {

    @Test
    @Config(qualifiers = Light)
    fun `empty text field with label and placeholder in light theme`() = captureRoot("TextFieldEmpty_light") { Empty() }

    @Test
    @Config(qualifiers = Dark)
    fun `empty text field with label and placeholder in dark theme`() = captureRoot("TextFieldEmpty_dark") { Empty() }

    @Test
    @Config(qualifiers = Light)
    fun `focused text field with typed text in light theme`() = captureRoot("TextFieldFocused_light") { Focused() }

    @Test
    @Config(qualifiers = Dark)
    fun `focused text field with typed text in dark theme`() = captureRoot("TextFieldFocused_dark") { Focused() }

    @Test
    @Config(qualifiers = Light)
    fun `text fields in error with supporting text and disabled in light theme`() = captureRoot("TextFieldVariants_light") { Variants() }

    @Test
    @Config(qualifiers = Dark)
    fun `text fields in error with supporting text and disabled in dark theme`() = captureRoot("TextFieldVariants_dark") { Variants() }

    @Test
    @Config(sdk = [26], qualifiers = Light)
    fun `text fields over stripes on android 8 in light theme`() = captureRoot("TextFieldGlass_sdk26_light") { OverStripes() }

    @Test
    @Config(sdk = [26], qualifiers = Dark)
    fun `text fields over stripes on android 8 in dark theme`() = captureRoot("TextFieldGlass_sdk26_dark") { OverStripes() }

    @Test
    @Config(sdk = [33], qualifiers = Light)
    fun `text fields over stripes on android 13 in light theme`() = captureRoot("TextFieldGlass_sdk33_light") { OverStripes() }

    @Test
    @Config(sdk = [33], qualifiers = Dark)
    fun `text fields over stripes on android 13 in dark theme`() = captureRoot("TextFieldGlass_sdk33_dark") { OverStripes() }

    @Composable
    private fun OverStripes() = SketchbookPreviewLayout {
        StripedBackdrop(Modifier.fillMaxSize(), colors = ColorfulStripes) {
            Column(Modifier.padding(16.dp)) {
                SketchbookTextField("Typed text", {}, label = { Text("Label") })
                SketchbookTextField(
                    "Invalid",
                    {},
                    modifier = Modifier.padding(top = 16.dp),
                    label = { Text("Error") },
                    isError = true,
                    supportingText = { Text("Something is wrong") }
                )
                SketchbookTextField(
                    "Disabled",
                    {},
                    modifier = Modifier.padding(top = 16.dp),
                    label = { Text("Disabled") },
                    enabled = false
                )
            }
        }
    }

    @Composable
    private fun Empty() = SketchbookPreviewLayout(modifier = Modifier.padding(16.dp)) {
        SketchbookTextField("", {}, label = { Text("Label") }, placeholder = { Text("Hint") })
    }

    @Composable
    private fun Focused() = SketchbookPreviewLayout(modifier = Modifier.padding(16.dp)) {
        val requester = remember { FocusRequester() }
        LaunchedEffect(Unit) { requester.requestFocus() }
        SketchbookTextField(
            "Typed text",
            {},
            modifier = Modifier.focusRequester(requester),
            label = { Text("Label") }
        )
    }

    @Composable
    private fun Variants() = SketchbookPreviewLayout(modifier = Modifier.padding(16.dp)) {
        Column {
            SketchbookTextField(
                "Invalid",
                {},
                label = { Text("Error") },
                isError = true,
                supportingText = { Text("Something is wrong") }
            )
            SketchbookTextField(
                "Disabled",
                {},
                label = { Text("Disabled") },
                enabled = false
            )
        }
    }
}
