@file:OptIn(ExperimentalMaterial3Api::class)

package lib.sketchbook.catalog

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import lib.sketchbook.SketchbookAnimatedBackground
import lib.sketchbook.SketchbookIconButton
import lib.sketchbook.SketchbookListItem
import lib.sketchbook.SketchbookNavigationBar
import lib.sketchbook.SketchbookNavigationBarItem
import lib.sketchbook.SketchbookScaffold
import lib.sketchbook.SketchbookTopAppBar
import lib.sketchbook.glass.glassSource

@Composable
internal fun CatalogApp() {
    var group by rememberSaveable { mutableStateOf(Group.Actions) }
    var open by rememberSaveable { mutableStateOf<String?>(null) }
    val demo = Catalog.firstOrNull { it.title == open }
    BackHandler(enabled = demo != null) { open = null }
    Box(Modifier.fillMaxSize()) {
        SketchbookAnimatedBackground(Modifier.fillMaxSize().glassSource()) {}
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
        topBar = { SketchbookTopAppBar(title = { Text(group.title) }, scrollBehavior = scrollBehavior) },
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
            modifier = Modifier.fillMaxSize().testTag("catalog-list"),
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
    SketchbookScaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        containerColor = Color.Transparent,
        topBar = {
            SketchbookTopAppBar(
                title = { Text(demo.title) },
                scrollBehavior = scrollBehavior,
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
