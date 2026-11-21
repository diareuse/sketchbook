package lib.sketchbook.behavior

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import lib.sketchbook.SketchbookButton
import lib.sketchbook.SketchbookFilledIconButton
import lib.sketchbook.SketchbookGlassButton
import lib.sketchbook.SketchbookGlassIconButton
import lib.sketchbook.SketchbookIconButton
import lib.sketchbook.SketchbookOutlinedButton
import lib.sketchbook.SketchbookTonalButton
import lib.sketchbook.SketchbookTonalIconButton
import org.robolectric.annotation.Config

private val ButtonCases = listOf(
    ControlCase("filled", null) { m, e, act -> SketchbookButton(act, m, e) { Text("Go") } },
    ControlCase("tonal", null) { m, e, act -> SketchbookTonalButton(act, m, e) { Text("Go") } },
    ControlCase("outlined", null) { m, e, act -> SketchbookOutlinedButton(act, m, e) { Text("Go") } },
    ControlCase("glass", null) { m, e, act -> SketchbookGlassButton(act, m, e) { Text("Go") } },
    ControlCase("plainIcon", null, width = true) { m, e, act ->
        SketchbookIconButton(act, "plain", m, e) { Box(Modifier.size(24.dp)) }
    },
    ControlCase("filledIcon", null, width = true) { m, e, act ->
        SketchbookFilledIconButton(act, "filled", m, e) { Box(Modifier.size(24.dp)) }
    },
    ControlCase("tonalIcon", null, width = true) { m, e, act ->
        SketchbookTonalIconButton(act, "tonal", m, e) { Box(Modifier.size(24.dp)) }
    },
    ControlCase("glassIcon", null, width = true) { m, e, act ->
        SketchbookGlassIconButton(act, "glass", m, e) { Box(Modifier.size(24.dp)) }
    }
)

@Config(sdk = [34])
class ButtonInteractionAndroid14BehaviorTest : ControlInteractionBehavior() {
    override val cases = ButtonCases
}

@Config(sdk = [33])
class ButtonInteractionAndroid13BehaviorTest : ControlInteractionBehavior() {
    override val cases = ButtonCases
}
