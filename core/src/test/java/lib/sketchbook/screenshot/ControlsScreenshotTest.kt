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
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ControlsScreenshotTest : ScreenshotTest() {

    @Test
    @Config(qualifiers = Light)
    fun ColorSliderLight() = captureRoot("ColorSlider_light") { ColorSliderContent() }

    @Test
    @Config(qualifiers = Dark)
    fun ColorSliderDark() = captureRoot("ColorSlider_dark") { ColorSliderContent() }

    @Test
    @Config(qualifiers = Light)
    fun FieldRowLight() = captureRoot("FieldRow_light") { FieldRowContent() }

    @Test
    @Config(qualifiers = Dark)
    fun FieldRowDark() = captureRoot("FieldRow_dark") { FieldRowContent() }

    @Test
    @Config(qualifiers = Light)
    fun RockerCounterLight() = captureRoot("RockerCounter_light") { RockerCounterContent() }

    @Test
    @Config(qualifiers = Dark)
    fun RockerCounterDark() = captureRoot("RockerCounter_dark") { RockerCounterContent() }

    @Test
    @Config(qualifiers = Light)
    fun SwitchesLight() = captureRoot("Switches_light") { SwitchesContent() }

    @Test
    @Config(qualifiers = Dark)
    fun SwitchesDark() = captureRoot("Switches_dark") { SwitchesContent() }

    @Composable
    private fun ColorSliderContent() = SketchbookPreviewLayout(modifier = Modifier.padding(16.dp)) {
        SketchbookColorSlider(
            hue = 120f,
            onHueChange = {},
            saturation = .8f,
            onSaturationChange = {},
            value = .9f,
            onValueChange = {}
        )
    }

    @Composable
    private fun FieldRowContent() = SketchbookPreviewLayout(modifier = Modifier.padding(16.dp)) {
        SketchbookFieldRow(icon = { Text("+") }) {
            SketchbookTextField("", {}, modifier = Modifier.weight(1f), label = { Text("First") })
            SketchbookTextField("", {}, modifier = Modifier.weight(1f), label = { Text("Second") })
        }
    }

    @Composable
    private fun RockerCounterContent() = SketchbookPreviewLayout(modifier = Modifier.padding(16.dp)) {
        SketchbookRockerCounter(
            decrement = { Text("-", modifier = Modifier.padding(16.dp)) },
            increment = { Text("+", modifier = Modifier.padding(16.dp)) },
            label = { Text("3", modifier = Modifier.padding(16.dp)) }
        )
    }

    @Composable
    private fun SwitchesContent() = SketchbookPreviewLayout(modifier = Modifier.padding(16.dp)) {
        Column {
            SketchbookSwitch(checked = true, onCheckedChange = {})
            SketchbookSwitch(checked = false, onCheckedChange = {})
            SketchbookSwitch(checked = true, onCheckedChange = {}, enabled = false)
            SketchbookSwitch(checked = false, onCheckedChange = {}, enabled = false)
        }
    }
}
