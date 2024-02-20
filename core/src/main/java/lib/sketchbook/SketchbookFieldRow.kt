package lib.sketchbook

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun SketchbookFieldRow(
    icon: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable RowScope.() -> Unit,
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        icon()
        content()
    }
}

@SketchbookComponentPreview
@SketchbookValidation
@Composable
private fun SketchbookFieldPreview() = SketchbookPreviewLayout(modifier = Modifier.padding(16.dp)) {
    SketchbookFieldRow(
        icon = { Icon(Icons.Default.Add, null) }
    ) {
        SketchbookTextField("", {}, modifier = Modifier.weight(1f), label = { Text("First") })
        SketchbookTextField("", {}, modifier = Modifier.weight(1f), label = { Text("Second") })
    }
}