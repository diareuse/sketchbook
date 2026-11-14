package lib.sketchbook.catalog

import android.graphics.Bitmap
import android.graphics.Color as AndroidColor
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import lib.sketchbook.Image
import lib.sketchbook.SketchbookColorSlider
import lib.sketchbook.SketchbookFieldRow
import lib.sketchbook.SketchbookIconButton
import lib.sketchbook.SketchbookRockerCounter
import lib.sketchbook.SketchbookTextField

internal val ExistingDemos = listOf(
    Demo(Group.Existing, "Color slider") {
        var hue by remember { mutableFloatStateOf(120f) }
        var saturation by remember { mutableFloatStateOf(1f) }
        var value by remember { mutableFloatStateOf(1f) }
        Showcase("HSV") {
            SketchbookColorSlider(hue, { hue = it }, saturation, { saturation = it }, value, { value = it })
        }
    },
    Demo(Group.Existing, "Rocker counter") {
        var count by remember { mutableStateOf(3) }
        Showcase("Counter") {
            SketchbookRockerCounter(
                decrement = { SketchbookIconButton({ count-- }, "Decrement") { Text("-") } },
                increment = { SketchbookIconButton({ count++ }, "Increment") { Icon(Icons.Default.Add, null) } },
                label = { Text("$count") }
            )
        }
    },
    Demo(Group.Existing, "Field row") {
        var text by remember { mutableStateOf("") }
        Showcase("Icon and field") {
            SketchbookFieldRow(icon = { Icon(Icons.Default.Search, null) }) {
                SketchbookTextField(text, { text = it }, Modifier.fillMaxWidth(), label = { Text("Search") })
            }
        }
    },
    Demo(Group.Existing, "Image") {
        val bitmap = remember {
            Bitmap.createBitmap(IntArray(64 * 64) { AndroidColor.rgb(30, 136, 229) }, 64, 64, Bitmap.Config.ARGB_8888)
        }
        Showcase("Bitmap source") { Image(bitmap, Modifier.size(96.dp), contentDescription = "Blue square") }
    }
)
