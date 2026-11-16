@file:OptIn(ExperimentalMaterial3Api::class)

package lib.sketchbook.catalog

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.material.icons.*
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.input.nestedscroll.*
import androidx.compose.ui.platform.*
import androidx.compose.ui.unit.*
import lib.sketchbook.SketchbookAnimatedBackground
import lib.sketchbook.SketchbookIconButton
import lib.sketchbook.SketchbookListItem
import lib.sketchbook.SketchbookNavigationBar
import lib.sketchbook.SketchbookNavigationBarItem
import lib.sketchbook.SketchbookScaffold
import lib.sketchbook.SketchbookTopAppBar
import lib.sketchbook.glass.glassSource
import lib.sketchbook.theme.Theme

@Composable
internal fun CatalogApp() {
    var group by rememberSaveable { mutableStateOf(Group.Actions) }
    var open by rememberSaveable { mutableStateOf<String?>(null) }
    val demo = Catalog.firstOrNull { it.title == open }
    BackHandler(enabled = demo != null) { open = null }
    Box(Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        SketchbookAnimatedBackground(Modifier
            .fillMaxSize()
            .glassSource()) {}
        if (demo == null) {
            ListScreen(group, onGroupChange = { group = it }, onOpen = { open = it.title })
        } else {
            DetailScreen(demo, onBack = { open = null })
        }
    }
}

@Composable
private fun ListScreen(group: Group, onGroupChange: (Group) -> Unit, onOpen: (Demo) -> Unit) {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    SketchbookScaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        containerColor = Color.Transparent,
        contentColor = Theme.color.content.background,
        topBar = { SketchbookTopAppBar(title = { Text(group.title) }) },
        bottomBar = {
            SketchbookNavigationBar {
                Group.entries.forEach {
                    SketchbookNavigationBarItem(
                        selected = it == group,
                        onClick = { onGroupChange(it) },
                        icon = { Icon(it.icon, null) },
                        label = { Text(it.short) }
                    )
                }
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .testTag("catalog-list"),
            contentPadding = padding,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            items(Catalog.filter { it.group == group }, key = { it.title }) { demo ->
                SketchbookListItem(
                    headline = { Text(demo.title) },
                    modifier = Modifier.padding(horizontal = 16.dp),
                    onClick = { onOpen(demo) }
                )
            }
        }
    }
}

@Composable
private fun DetailScreen(demo: Demo, onBack: () -> Unit) {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        containerColor = Color.Transparent,
        contentColor = Theme.color.content.background,
        topBar = {
            SketchbookTopAppBar(
                title = { Text(demo.title) },
                navigationIcon = {
                    SketchbookIconButton(onClick = onBack, contentDescription = "Back") {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, null)
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp),
            content = demo.content
        )
    }
}
