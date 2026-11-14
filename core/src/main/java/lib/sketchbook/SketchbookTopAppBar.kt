@file:OptIn(ExperimentalMaterial3Api::class)
@file:Suppress("NAME_SHADOWING")

package lib.sketchbook

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.material.icons.*
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.input.nestedscroll.*
import androidx.compose.ui.text.*
import androidx.compose.ui.tooling.preview.*
import androidx.compose.ui.unit.*
import lib.sketchbook.glass.glass
import lib.sketchbook.layout.plus
import lib.sketchbook.theme.Theme

@Composable
fun SketchbookTopAppBar(
    title: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    navigationIcon: @Composable () -> Unit = {},
    actions: @Composable () -> Unit = {},
    minHeight: Dp = 48.dp,
    arrangement: Arrangement.Horizontal = Arrangement.spacedBy(8.dp),
    titleStyle: TextStyle = Theme.textStyle.title
) = Row(
    modifier = modifier.padding(horizontal = 16.dp, vertical = 16.dp),
    verticalAlignment = Alignment.Top,
    horizontalArrangement = arrangement
) {
    navigationIcon()
    Box(
        modifier = Modifier
            .weight(1f)
            .glass(MaterialTheme.shapes.medium)
            .heightIn(min = minHeight)
            .padding(horizontal = 16.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        ProvideTextStyle(titleStyle) {
            title()
        }
    }
    actions()
}

@Preview(showBackground = true)
@Composable
private fun SketchbookTopAppBarPreview() = SketchbookPreviewLayout(padding = PaddingValues()) {
    val behavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    SideEffect {
        behavior.state.heightOffset = -280f
    }
    Scaffold(
        topBar = {
            SketchbookTopAppBar(
                title = { Text("I'm title title title title title title title title title") },
                navigationIcon = {
                    IconButton(onClick = {}) {
                        Icon(Icons.AutoMirrored.Default.ArrowBack, null)
                    }
                },
                actions = {
                    IconButton(onClick = {}) {
                        Icon(Icons.Default.MoreVert, null)
                    }
                }
            )
        }
    ) { padding ->
        SampleContent(padding = padding, behavior = behavior)
    }
}

@SketchbookComponentPreview
@SketchbookValidation
@Composable
private fun SketchbookTopAppBarTitleOnlyPreview() =
    SketchbookPreviewLayout(padding = PaddingValues()) {
        val behavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
        SideEffect {
            behavior.state.heightOffset = -170f
        }
        Scaffold(
            topBar = {
                SketchbookTopAppBar(
                    title = { Text("I'm title") }
                )
            }
        ) { padding ->
            SampleContent(padding = padding, behavior = behavior)
        }
    }

@SketchbookComponentPreview
@SketchbookValidation
@Composable
private fun SketchbookTopAppBarExpandedPreview() =
    SketchbookPreviewLayout(padding = PaddingValues()) {
        val behavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
        Scaffold(
            topBar = {
                SketchbookTopAppBar(
                    title = { Text("I'm title") },
                    navigationIcon = {
                        SketchbookIconButton(onClick = {}, contentDescription = null) {
                            Icon(Icons.AutoMirrored.Default.ArrowBack, null)
                        }
                    },
                    actions = {
                        SketchbookIconButton(onClick = {}, contentDescription = null) {
                            Icon(Icons.Default.MoreVert, null)
                        }
                    }
                )
            }
        ) { padding ->
            SampleContent(padding = padding, behavior = behavior)
        }
    }

@Composable
private fun SampleContent(padding: PaddingValues, behavior: TopAppBarScrollBehavior) {
    LazyColumn(
        modifier = Modifier.nestedScroll(behavior.nestedScrollConnection),
        contentPadding = padding.plus(24.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(100) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp)
                    .background(Color.Blue)
            )
        }
    }
}