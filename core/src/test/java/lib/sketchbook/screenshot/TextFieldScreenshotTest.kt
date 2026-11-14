package lib.sketchbook.screenshot

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.unit.dp
import lib.sketchbook.SketchbookPreviewLayout
import lib.sketchbook.SketchbookTextField

import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class TextFieldScreenshotTest : ScreenshotTest() {

    @Test
    @Config(qualifiers = Light)
    fun TextFieldEmptyLight() = captureRoot("TextFieldEmpty_light") { TextFieldEmptyContent() }

    @Test
    @Config(qualifiers = Dark)
    fun TextFieldEmptyDark() = captureRoot("TextFieldEmpty_dark") { TextFieldEmptyContent() }

    @Test
    @Config(qualifiers = Light)
    fun TextFieldFocusedLight() = captureRoot("TextFieldFocused_light") { TextFieldFocusedContent() }

    @Test
    @Config(qualifiers = Dark)
    fun TextFieldFocusedDark() = captureRoot("TextFieldFocused_dark") { TextFieldFocusedContent() }

    @Composable
    private fun TextFieldEmptyContent() = SketchbookPreviewLayout(modifier = Modifier.padding(16.dp)) {
        SketchbookTextField("", {}, label = { Text("Label") }, placeholder = { Text("Hint") })
    }

    @Composable
    private fun TextFieldFocusedContent() = SketchbookPreviewLayout(modifier = Modifier.padding(16.dp)) {
        val requester = FocusRequester()
        LaunchedEffect(Unit) { requester.requestFocus() }
        Column {
            SketchbookTextField(
                "Typed text",
                {},
                modifier = Modifier.focusRequester(requester),
                label = { Text("Label") }
            )
        }
    }
}
