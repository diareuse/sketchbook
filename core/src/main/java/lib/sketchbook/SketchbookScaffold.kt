package lib.sketchbook

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScaffoldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import lib.sketchbook.glass.glassSource
import lib.sketchbook.theme.Theme
import lib.sketchbook.theme.contentColorFor

@Composable
fun SketchbookScaffold(
    modifier: Modifier = Modifier,
    topBar: @Composable () -> Unit = {},
    bottomBar: @Composable () -> Unit = {},
    snackbarHost: @Composable () -> Unit = {},
    floatingActionButton: @Composable () -> Unit = {},
    floatingActionButtonPosition: FabPosition = FabPosition.End,
    containerColor: Color = Theme.color.container.background,
    contentColor: Color = Theme.color.contentColorFor(containerColor),
    contentWindowInsets: WindowInsets = ScaffoldDefaults.contentWindowInsets,
    content: @Composable (PaddingValues) -> Unit
) = Scaffold(
    modifier = modifier,
    topBar = topBar,
    bottomBar = bottomBar,
    snackbarHost = snackbarHost,
    floatingActionButton = floatingActionButton,
    floatingActionButtonPosition = floatingActionButtonPosition,
    containerColor = containerColor,
    contentColor = contentColor,
    contentWindowInsets = contentWindowInsets
) { padding ->
    Box(Modifier.fillMaxSize().glassSource()) {
        content(padding)
    }
}

@SketchbookComponentPreview
@SketchbookValidation
@Composable
private fun SketchbookScaffoldPreview() = SketchbookPreviewLayout {
    SketchbookScaffold(
        topBar = { Text("Top bar", Modifier.padding(16.dp)) },
        bottomBar = { Text("Bottom bar", Modifier.padding(16.dp)) },
        floatingActionButton = { Text("Fab") }
    ) { padding ->
        Box(Modifier.padding(padding).background(Color.Gray).fillMaxSize()) {
            Text("Content", Modifier.padding(16.dp))
        }
    }
}
