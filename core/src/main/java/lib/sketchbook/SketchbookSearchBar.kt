package lib.sketchbook

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import lib.sketchbook.glass.glass
import lib.sketchbook.theme.Theme

private val ResultsGlassElevation = 12.dp

@Composable
fun SketchbookSearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    onSearch: (String) -> Unit,
    clearContentDescription: String,
    modifier: Modifier = Modifier,
    placeholder: (@Composable () -> Unit)? = null,
    leadingIcon: (@Composable () -> Unit)? = { Icon(Icons.Default.Search, null) },
    results: (@Composable ColumnScope.() -> Unit)? = null,
    enabled: Boolean = true,
    compositeColor: Color = Theme.color.emphasis.background
) {
    val keyboardOptions = remember { KeyboardOptions(imeAction = ImeAction.Search) }
    val keyboardActions = remember(onSearch, query) { KeyboardActions(onSearch = { onSearch(query) }) }
    Column(modifier = modifier) {
        SketchbookTextField(
            value = query,
            onValueChange = onQueryChange,
            modifier = Modifier.fillMaxWidth(),
            placeholder = placeholder,
            leadingIcon = leadingIcon,
            trailingIcon = if (query.isEmpty() || !enabled) null else {
                {
                    SketchbookIconButton(
                        onClick = { onQueryChange("") },
                        contentDescription = clearContentDescription,
                        compositeColor = compositeColor
                    ) { Icon(Icons.Default.Close, null) }
                }
            },
            enabled = enabled,
            keyboardOptions = keyboardOptions,
            keyboardActions = keyboardActions,
            compositeColor = compositeColor
        )
        if (results != null) {
            Column(
                modifier = Modifier
                    .padding(top = 8.dp)
                    .fillMaxWidth()
                    .glass(Theme.container.card, ResultsGlassElevation),
                content = results
            )
        }
    }
}

@SketchbookComponentPreview
@SketchbookValidation
@Composable
private fun SketchbookSearchBarPreview() = SketchbookPreviewLayout {
    Column(modifier = Modifier.padding(16.dp)) {
        SketchbookSearchBar(
            query = "",
            onQueryChange = {},
            onSearch = {},
            clearContentDescription = "Clear",
            placeholder = { Text("Search") }
        )
        SketchbookSearchBar(
            query = "Sketch",
            onQueryChange = {},
            onSearch = {},
            clearContentDescription = "Clear",
            modifier = Modifier.padding(top = 16.dp),
            results = {
                Text("Sketchbook", modifier = Modifier.padding(16.dp))
                Text("Sketch pad", modifier = Modifier.padding(16.dp))
            }
        )
    }
}
