@file:OptIn(ExperimentalLayoutApi::class)

package lib.sketchbook.catalog

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import lib.sketchbook.SketchbookButton
import lib.sketchbook.SketchbookCheckbox
import lib.sketchbook.SketchbookChip
import lib.sketchbook.SketchbookExtendedFloatingActionButton
import lib.sketchbook.SketchbookFilledIconButton
import lib.sketchbook.SketchbookFilterChip
import lib.sketchbook.SketchbookFloatingActionButton
import lib.sketchbook.SketchbookGlassButton
import lib.sketchbook.SketchbookGlassIconButton
import lib.sketchbook.SketchbookIconButton
import lib.sketchbook.SketchbookMultiChoiceSegmentedButton
import lib.sketchbook.SketchbookOutlinedButton
import lib.sketchbook.SketchbookRadioButton
import lib.sketchbook.SketchbookSearchBar
import lib.sketchbook.SketchbookSegmentedButton
import lib.sketchbook.SketchbookSlider
import lib.sketchbook.SketchbookSwitch
import lib.sketchbook.SketchbookTextField
import lib.sketchbook.SketchbookTonalButton
import lib.sketchbook.SketchbookTonalIconButton

private val Spacing = Arrangement.spacedBy(8.dp)

internal val ActionDemos = listOf(
    Demo(Group.Actions, "Buttons") {
        Showcase("Enabled") {
            FlowRow(horizontalArrangement = Spacing, verticalArrangement = Spacing) {
                SketchbookButton({}) { Text("Filled") }
                SketchbookTonalButton({}) { Text("Tonal") }
                SketchbookOutlinedButton({}) { Text("Outlined") }
                SketchbookGlassButton({}) { Text("Glass") }
            }
        }
        Showcase("Disabled") {
            FlowRow(horizontalArrangement = Spacing, verticalArrangement = Spacing) {
                SketchbookButton({}, enabled = false) { Text("Filled") }
                SketchbookTonalButton({}, enabled = false) { Text("Tonal") }
                SketchbookOutlinedButton({}, enabled = false) { Text("Outlined") }
                SketchbookGlassButton({}, enabled = false) { Text("Glass") }
            }
        }
    },
    Demo(Group.Actions, "Icon buttons") {
        Showcase("Enabled") {
            Row(horizontalArrangement = Spacing) {
                SketchbookIconButton({}, "Plain") { Icon(Icons.Default.Favorite, null) }
                SketchbookFilledIconButton({}, "Filled") { Icon(Icons.Default.Favorite, null) }
                SketchbookTonalIconButton({}, "Tonal") { Icon(Icons.Default.Favorite, null) }
                SketchbookGlassIconButton({}, "Glass") { Icon(Icons.Default.Favorite, null) }
            }
        }
        Showcase("Disabled") {
            Row(horizontalArrangement = Spacing) {
                SketchbookIconButton({}, "Plain", enabled = false) { Icon(Icons.Default.Favorite, null) }
                SketchbookFilledIconButton({}, "Filled", enabled = false) { Icon(Icons.Default.Favorite, null) }
                SketchbookTonalIconButton({}, "Tonal", enabled = false) { Icon(Icons.Default.Favorite, null) }
                SketchbookGlassIconButton({}, "Glass", enabled = false) { Icon(Icons.Default.Favorite, null) }
            }
        }
    },
    Demo(Group.Actions, "Floating action buttons") {
        Showcase("Regular") { SketchbookFloatingActionButton({}, "Add") { Icon(Icons.Default.Add, null) } }
        Showcase("Extended") {
            SketchbookExtendedFloatingActionButton({}, icon = { Icon(Icons.Default.Add, null) }) { Text("Create") }
        }
    },
    Demo(Group.Actions, "Chips") {
        var selected by remember { mutableStateOf(setOf("Photos")) }
        Showcase("Assist") {
            Row(horizontalArrangement = Spacing) {
                SketchbookChip({}, { Text("Chip") })
                SketchbookChip({}, { Text("Icon") }, leadingIcon = { Icon(Icons.Default.Check, null) })
                SketchbookChip({}, { Text("Disabled") }, enabled = false)
            }
        }
        Showcase("Filter") {
            FlowRow(horizontalArrangement = Spacing, verticalArrangement = Spacing) {
                listOf("Photos", "Videos", "Music").forEach { name ->
                    SketchbookFilterChip(
                        selected = name in selected,
                        onClick = { selected = if (name in selected) selected - name else selected + name },
                        label = { Text(name) }
                    )
                }
                SketchbookFilterChip(true, {}, { Text("Disabled") }, enabled = false)
            }
        }
    },
    Demo(Group.Actions, "Checkbox") {
        var checked by remember { mutableStateOf(true) }
        Showcase("Interactive") { SketchbookCheckbox(checked, { checked = it }, label = { Text("Checked: $checked") }) }
        Showcase("Bare") { SketchbookCheckbox(checked, { checked = it }) }
        Showcase("Disabled") {
            SketchbookCheckbox(true, {}, label = { Text("Checked") }, enabled = false)
            SketchbookCheckbox(false, {}, label = { Text("Unchecked") }, enabled = false)
        }
    },
    Demo(Group.Actions, "Radio button") {
        var selected by remember { mutableStateOf(0) }
        Showcase("Interactive") {
            listOf("Small", "Medium", "Large").forEachIndexed { index, name ->
                SketchbookRadioButton(selected == index, { selected = index }, label = { Text(name) })
            }
        }
        Showcase("Disabled") { SketchbookRadioButton(true, {}, label = { Text("Selected") }, enabled = false) }
    },
    Demo(Group.Actions, "Segmented buttons") {
        var single by remember { mutableStateOf(1) }
        var multi by remember { mutableStateOf(setOf(0)) }
        val labels = listOf("Day", "Week", "Month")
        Showcase("Single choice") {
            SketchbookSegmentedButton(single, { single = it }, labels.size, Modifier.fillMaxWidth(), label = { Text(labels[it]) })
        }
        Showcase("Multi choice") {
            SketchbookMultiChoiceSegmentedButton(multi, { multi = it }, labels.size, Modifier.fillMaxWidth(), label = { Text(labels[it]) })
        }
        Showcase("Disabled") {
            SketchbookSegmentedButton(0, {}, labels.size, Modifier.fillMaxWidth(), label = { Text(labels[it]) }, enabled = false)
        }
    },
    Demo(Group.Actions, "Switch") {
        var checked by remember { mutableStateOf(true) }
        Showcase("Interactive") { SketchbookSwitch(checked, { checked = it }) }
        Showcase("Disabled") {
            Row(horizontalArrangement = Spacing) {
                SketchbookSwitch(true, {}, enabled = false)
                SketchbookSwitch(false, {}, enabled = false)
            }
        }
    },
    Demo(Group.Actions, "Slider") {
        var value by remember { mutableStateOf(.4f) }
        var stepped by remember { mutableStateOf(.5f) }
        Showcase("Continuous") { SketchbookSlider(value, { value = it }) }
        Showcase("Stepped") { SketchbookSlider(stepped, { stepped = it }, steps = 4) }
        Showcase("Disabled") { SketchbookSlider(.6f, {}, enabled = false) }
    },
    Demo(Group.Actions, "Text field") {
        var text by remember { mutableStateOf("") }
        Showcase("Empty") { SketchbookTextField(text, { text = it }, Modifier.fillMaxWidth(), label = { Text("Label") }) }
        Showcase("Error") {
            SketchbookTextField("Invalid", {}, Modifier.fillMaxWidth(), label = { Text("Label") }, isError = true, supportingText = { Text("Something is wrong") })
        }
        Showcase("Disabled") { SketchbookTextField("Locked", {}, Modifier.fillMaxWidth(), enabled = false) }
    },
    Demo(Group.Actions, "Search bar") {
        var query by remember { mutableStateOf("Sketch") }
        val all = listOf("Sketchbook", "Sketch pad", "Sketch tool")
        Showcase("With results") {
            SketchbookSearchBar(
                query = query,
                onQueryChange = { query = it },
                onSearch = {},
                clearContentDescription = "Clear",
                placeholder = { Text("Search") },
                results = query.takeIf { it.isNotEmpty() }?.let { q ->
                    { all.filter { it.contains(q, ignoreCase = true) }.forEach { Text(it, Modifier.padding(16.dp)) } }
                }
            )
        }
    }
)
