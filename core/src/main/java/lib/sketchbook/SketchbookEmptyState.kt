package lib.sketchbook

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import lib.sketchbook.theme.Theme

private val EmptyStatePadding = 24.dp
private val EmptyStateSpacing = 12.dp

@Composable
fun SketchbookEmptyState(
    title: String,
    modifier: Modifier = Modifier,
    message: String? = null,
    icon: (@Composable () -> Unit)? = null,
    action: (@Composable () -> Unit)? = null
) = BoxWithConstraints(modifier) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = maxHeight)
            .verticalScroll(rememberScrollState())
            .padding(EmptyStatePadding),
        verticalArrangement = Arrangement.spacedBy(EmptyStateSpacing, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        icon?.invoke()
        Text(
            text = title,
            modifier = Modifier.semantics { heading() },
            style = Theme.textStyle.title,
            textAlign = TextAlign.Center
        )
        if (message != null) {
            Text(text = message, style = Theme.textStyle.body, textAlign = TextAlign.Center)
        }
        action?.invoke()
    }
}

@SketchbookComponentPreview
@SketchbookValidation
@Composable
private fun SketchbookEmptyStatePreview() = SketchbookPreviewLayout {
    SketchbookEmptyState(
        title = "No results found",
        message = "Try a different search term or clear the active filters.",
        icon = { Icon(Icons.Default.Search, contentDescription = null, modifier = Modifier.size(48.dp)) },
        action = { SketchbookButton(onClick = {}) { Text("Clear filters") } }
    )
}
