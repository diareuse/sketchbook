package lib.sketchbook.catalog

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import lib.sketchbook.DialogSurface
import lib.sketchbook.OverlayState
import lib.sketchbook.SketchbookBottomSheet
import lib.sketchbook.SketchbookButton
import lib.sketchbook.SketchbookCard
import lib.sketchbook.SketchbookDialog
import lib.sketchbook.SketchbookListItem
import lib.sketchbook.SketchbookMenu
import lib.sketchbook.SketchbookNavigationBar
import lib.sketchbook.SketchbookNavigationBarItem
import lib.sketchbook.SketchbookNavigationRail
import lib.sketchbook.SketchbookNavigationRailItem
import lib.sketchbook.SketchbookOutlinedButton
import lib.sketchbook.SketchbookTab
import lib.sketchbook.SketchbookTabRow
import lib.sketchbook.popOutBackground
import lib.sketchbook.rememberMenuState

private val Items = listOf("Home" to Icons.Default.Home, "Search" to Icons.Default.Search, "Settings" to Icons.Default.Settings)

internal val ContainerDemos = listOf(
    Demo(Group.Containers, "Card") {
        Showcase("Static") { SketchbookCard(Modifier.fillMaxWidth()) { Text("Static card", Modifier.padding(16.dp)) } }
        Showcase("Clickable with glow") {
            SketchbookCard({}, Modifier.fillMaxWidth(), useGlow = true) { Text("Clickable card", Modifier.padding(16.dp)) }
        }
        Showcase("Specular rim") {
            SketchbookCard(Modifier.fillMaxWidth(), specularRim = true) { Text("Rim card", Modifier.padding(16.dp)) }
        }
        Showcase("Disabled") {
            SketchbookCard({}, Modifier.fillMaxWidth(), enabled = false) { Text("Disabled card", Modifier.padding(16.dp)) }
        }
    },
    Demo(Group.Containers, "List item") {
        var selected by remember { mutableStateOf(1) }
        Showcase("Selectable") {
            repeat(3) { index ->
                SketchbookListItem(
                    headline = { Text("Item $index") },
                    supporting = { Text("Supporting text") },
                    leading = { Icon(Icons.Default.Star, null) },
                    onClick = { selected = index },
                    selected = selected == index
                )
            }
        }
        Showcase("Static and disabled") {
            SketchbookListItem(headline = { Text("Static") }, overline = { Text("Overline") })
            SketchbookListItem(headline = { Text("Disabled") }, onClick = {}, enabled = false)
        }
    },
    Demo(Group.Containers, "Tab row") {
        var selected by remember { mutableStateOf(0) }
        Showcase("Tabs") {
            SketchbookTabRow(selected) {
                listOf("Photos", "Albums", "Places").forEachIndexed { index, name ->
                    SketchbookTab(selected == index, { selected = index }, text = { Text(name) })
                }
            }
        }
    },
    Demo(Group.Containers, "Navigation bar") {
        var selected by remember { mutableStateOf(0) }
        Showcase("Bar") {
            SketchbookNavigationBar {
                Items.forEachIndexed { index, (name, icon) ->
                    SketchbookNavigationBarItem(selected == index, { selected = index }, { Icon(icon, null) }, label = { Text(name) })
                }
            }
        }
    },
    Demo(Group.Containers, "Navigation rail") {
        var selected by remember { mutableStateOf(0) }
        Showcase("Rail") {
            Box(Modifier.height(280.dp)) {
                SketchbookNavigationRail(header = { Text("Menu") }) {
                    Items.forEachIndexed { index, (name, icon) ->
                        SketchbookNavigationRailItem(selected == index, { selected = index }, { Icon(icon, null) }, label = { Text(name) })
                    }
                }
            }
        }
    },
    Demo(Group.Containers, "Dialog") {
        var visible by remember { mutableStateOf(false) }
        Showcase("Glass dialog") {
            SketchbookDialog(
                visible = visible,
                onVisibleChange = { visible = it },
                dialog = {
                    DialogSurface {
                        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                            Text("Dialog content")
                            SketchbookButton({ visible = false }) { Text("Close") }
                        }
                    }
                }
            ) {
                SketchbookOutlinedButton({ visible = true }) { Text("Open dialog") }
            }
        }
    },
    Demo(Group.Containers, "Bottom sheet") {
        var visible by remember { mutableStateOf(false) }
        Showcase("Glass sheet") {
            SketchbookOutlinedButton({ visible = true }) { Text("Open sheet") }
            SketchbookBottomSheet(visible, { visible = false }) {
                Text("Sheet content", Modifier.padding(24.dp))
            }
        }
    },
    Demo(Group.Containers, "Menu") {
        val overlay = remember { OverlayState() }
        val menu = overlay.rememberMenuState()
        val scope = rememberCoroutineScope()
        Showcase("Pop-out menu") {
            Box(Modifier.popOutBackground(overlay).fillMaxWidth().height(160.dp)) {
                SketchbookMenu(
                    state = menu,
                    modifier = Modifier.align(Alignment.Center).wrapContentSize(),
                    expansion = {
                        Column {
                            SketchbookListItem(headline = { Text("Edit") }, onClick = { scope.launch { menu.close() } })
                            SketchbookListItem(headline = { Text("Share") }, onClick = { scope.launch { menu.close() } })
                        }
                    }
                ) {
                    SketchbookOutlinedButton({ scope.launch { menu.open() } }) { Text("Open menu") }
                }
            }
        }
    }
)
