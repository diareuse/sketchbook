package lib.sketchbook.catalog

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import lib.sketchbook.SketchbookAnimatedBackground
import lib.sketchbook.SketchbookGlassBorder
import lib.sketchbook.SketchbookHorizontalDivider
import lib.sketchbook.SketchbookOutlinedButton
import lib.sketchbook.SketchbookSwitch
import lib.sketchbook.SketchbookVerticalDivider
import androidx.compose.foundation.layout.Row

internal val EffectDemos = listOf(
    Demo(Group.Effects, "Animated background") {
        var animated by remember { mutableStateOf(true) }
        Showcase("Mesh background") {
            SketchbookAnimatedBackground(Modifier.fillMaxWidth().height(200.dp), animated = animated) {
                Text("Animated background", Modifier.padding(24.dp))
            }
        }
        Showcase("Animated") { SketchbookSwitch(animated, { animated = it }) }
    },
    Demo(Group.Effects, "Glass border") {
        Showcase("Static") {
            SketchbookGlassBorder(Modifier.fillMaxWidth()) { Text("Specular border", Modifier.padding(24.dp)) }
        }
        Showcase("Light sweep") {
            SketchbookGlassBorder(Modifier.fillMaxWidth(), animated = true) { Text("Animated border", Modifier.padding(24.dp)) }
        }
    },
    Demo(Group.Effects, "Divider") {
        Showcase("Horizontal") { SketchbookHorizontalDivider() }
        Showcase("Vertical") {
            Row(Modifier.height(48.dp), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                Text("Left")
                SketchbookVerticalDivider()
                Text("Right")
            }
        }
    }
)
