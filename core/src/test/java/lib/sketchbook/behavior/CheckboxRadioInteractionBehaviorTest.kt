package lib.sketchbook.behavior

import androidx.compose.material3.Text
import lib.sketchbook.SketchbookCheckbox
import lib.sketchbook.SketchbookRadioButton
import org.robolectric.annotation.Config

private val ToggleCases = listOf(
    ControlCase("checkOff", true, width = true) { m, e, act ->
        SketchbookCheckbox(false, { act() }, m, enabled = e)
    },
    ControlCase("checkOn", false, width = true) { m, e, act ->
        SketchbookCheckbox(true, { act() }, m, enabled = e)
    },
    ControlCase("checkLabeled", true) { m, e, act ->
        SketchbookCheckbox(false, { act() }, m, label = { Text("Accept") }, enabled = e)
    },
    ControlCase("radio", true, width = true) { m, e, act ->
        SketchbookRadioButton(false, act, m, enabled = e)
    },
    ControlCase("radioLabeled", true) { m, e, act ->
        SketchbookRadioButton(false, act, m, label = { Text("Option") }, enabled = e)
    }
)

@Config(sdk = [34])
class CheckboxRadioInteractionAndroid14BehaviorTest : ControlInteractionBehavior() {
    override val cases = ToggleCases
}

@Config(sdk = [33])
class CheckboxRadioInteractionAndroid13BehaviorTest : ControlInteractionBehavior() {
    override val cases = ToggleCases
}
