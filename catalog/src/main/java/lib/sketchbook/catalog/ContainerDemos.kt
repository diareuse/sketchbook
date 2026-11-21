package lib.sketchbook.catalog

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.unit.*
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
import lib.sketchbook.TabStyle
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
        var selected by remember { mutableIntStateOf(0) }
        Showcase("Tabs") {
            SketchbookTabRow(selected) {
                listOf("Photos", "Albums", "Places").forEachIndexed { index, name ->
                    val style = when (index) {
                        0 -> TabStyle.Start
                        2 -> TabStyle.End
                        else -> TabStyle.Center
                    }
                    SketchbookTab(
                        selected == index,
                        { selected = index },
                        style,
                        text = { Text(name) })
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
            Box(Modifier.height(400.dp)) {
                SketchbookNavigationRail(windowInsets = WindowInsets(0), header = { Text("Menu") }) {
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
            Box(Modifier
                .popOutBackground(overlay)
                .fillMaxWidth()
                .height(160.dp)) {
                SketchbookMenu(
                    state = menu,
                    modifier = Modifier
                        .align(Alignment.Center)
                        .wrapContentSize(),
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
