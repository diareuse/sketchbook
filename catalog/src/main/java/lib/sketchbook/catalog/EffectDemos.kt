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
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import lib.sketchbook.SketchbookButton
import lib.sketchbook.SketchbookCard
import lib.sketchbook.modifier.BeamDirection
import lib.sketchbook.modifier.BeamPalette
import lib.sketchbook.modifier.BeamStyle
import lib.sketchbook.modifier.borderBeam
import lib.sketchbook.theme.Theme

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
    Demo(Group.Effects, "Border beam") {
        val shape = RoundedCornerShape(24.dp)
        Showcase("Box") {
            Box(Modifier.fillMaxWidth().height(96.dp).borderBeam(shape), contentAlignment = Alignment.Center) { Text("Box") }
        }
        Showcase("Column") {
            Column(Modifier.fillMaxWidth().borderBeam(shape, BeamPalette.glass(Color.White)).padding(24.dp)) {
                Text("Column")
                Text("Glass palette")
            }
        }
        Showcase("Card") {
            SketchbookCard(Modifier.fillMaxWidth().borderBeam(Theme.container.card, listOf(Color.Cyan, Color.Magenta, Color.Yellow))) {
                Text("Card with custom palette", Modifier.padding(24.dp))
            }
        }
        Showcase("Button") {
            SketchbookButton({}, Modifier.borderBeam(Theme.container.button, style = BeamStyle.Small)) { Text("Button, small beam") }
        }
        Showcase("Counter clockwise") {
            Box(Modifier.fillMaxWidth().height(72.dp).borderBeam(shape, direction = BeamDirection.CounterClockwise), contentAlignment = Alignment.Center) {
                Text("Counter clockwise")
            }
        }
        Showcase("Line") {
            Box(Modifier.fillMaxWidth().height(72.dp).borderBeam(shape, style = BeamStyle.Line), contentAlignment = Alignment.Center) {
                Text("Line style")
            }
        }
        Showcase("Inactive") {
            Box(Modifier.fillMaxWidth().height(72.dp).borderBeam(shape, active = false), contentAlignment = Alignment.Center) {
                Text("Inactive")
            }
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
