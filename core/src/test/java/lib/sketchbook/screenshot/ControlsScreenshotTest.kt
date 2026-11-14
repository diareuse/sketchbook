package lib.sketchbook.screenshot

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import lib.sketchbook.SketchbookColorSlider
import lib.sketchbook.SketchbookFieldRow
import lib.sketchbook.SketchbookPreviewLayout
import lib.sketchbook.SketchbookRockerCounter
import lib.sketchbook.SketchbookSwitch
import lib.sketchbook.SketchbookTextField
import org.junit.Test
import org.robolectric.annotation.Config

class ControlsScreenshotTest : ScreenshotTest() {

    @Test
    @Config(qualifiers = Light)
    fun `color slider with default values in light theme`() = captureRoot("ColorSlider_light") { ColorSliderDefault() }

    @Test
    @Config(qualifiers = Dark)
    fun `color slider with default values in dark theme`() = captureRoot("ColorSlider_dark") { ColorSliderDefault() }

    @Test
    @Config(qualifiers = Light)
    fun `color slider with custom hue saturation and value in light theme`() = captureRoot("ColorSliderCustom_light") { ColorSliderCustom() }

    @Test
    @Config(qualifiers = Dark)
    fun `color slider with custom hue saturation and value in dark theme`() = captureRoot("ColorSliderCustom_dark") { ColorSliderCustom() }

    @Test
    @Config(qualifiers = Light)
    fun `field row with icon and two text fields in light theme`() = captureRoot("FieldRow_light") { FieldRow() }

    @Test
    @Config(qualifiers = Dark)
    fun `field row with icon and two text fields in dark theme`() = captureRoot("FieldRow_dark") { FieldRow() }

    @Test
    @Config(qualifiers = Light)
    fun `rocker counter with label in light theme`() = captureRoot("RockerCounter_light") { RockerCounter() }

    @Test
    @Config(qualifiers = Dark)
    fun `rocker counter with label in dark theme`() = captureRoot("RockerCounter_dark") { RockerCounter() }

    @Test
    @Config(qualifiers = Light)
    fun `switch in checked unchecked and disabled states in light theme`() = captureRoot("Switches_light") { Switches() }

    @Test
    @Config(qualifiers = Dark)
    fun `switch in checked unchecked and disabled states in dark theme`() = captureRoot("Switches_dark") { Switches() }

    @Composable
    private fun ColorSliderDefault() = ColorSliderContent(120f, .8f, .9f)

    @Composable
    private fun ColorSliderCustom() = ColorSliderContent(300f, .4f, .6f)

    @Composable
    private fun ColorSliderContent(hue: Float, saturation: Float, value: Float) =
        SketchbookPreviewLayout(modifier = Modifier.padding(16.dp)) {
            SketchbookColorSlider(
                hue = hue,
                onHueChange = {},
                saturation = saturation,
                onSaturationChange = {},
                value = value,
                onValueChange = {}
            )
        }

    @Composable
    private fun FieldRow() = SketchbookPreviewLayout(modifier = Modifier.padding(16.dp)) {
        SketchbookFieldRow(icon = { Text("+") }) {
            SketchbookTextField("", {}, modifier = Modifier.weight(1f), label = { Text("First") })
            SketchbookTextField("", {}, modifier = Modifier.weight(1f), label = { Text("Second") })
        }
    }

    @Composable
    private fun RockerCounter() = SketchbookPreviewLayout(modifier = Modifier.padding(16.dp)) {
        SketchbookRockerCounter(
            decrement = { Text("-", modifier = Modifier.padding(16.dp)) },
            increment = { Text("+", modifier = Modifier.padding(16.dp)) },
            label = { Text("3", modifier = Modifier.padding(16.dp)) }
        )
    }

    @Composable
    private fun Switches() = SketchbookPreviewLayout(modifier = Modifier.padding(16.dp)) {
        Column {
            SketchbookSwitch(checked = true, onCheckedChange = {})
            SketchbookSwitch(checked = false, onCheckedChange = {})
            SketchbookSwitch(checked = true, onCheckedChange = {}, enabled = false)
            SketchbookSwitch(checked = false, onCheckedChange = {}, enabled = false)
        }
    }
}
