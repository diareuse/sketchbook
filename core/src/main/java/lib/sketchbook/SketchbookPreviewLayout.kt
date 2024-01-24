package lib.sketchbook

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import lib.sketchbook.theme.Theme

@Composable
fun SketchbookPreviewLayout(
    modifier: Modifier = Modifier,
    padding: PaddingValues = PaddingValues(0.dp),
    content: @Composable () -> Unit
) = Theme {
    Surface {
        Box(modifier = modifier.padding(padding)) {
            content()
        }
    }
}

@Composable
fun SketchbookPreviewWearLayout(
    modifier: Modifier = Modifier,
    padding: PaddingValues = PaddingValues(16.dp),
    content: @Composable () -> Unit
) = Theme(darkTheme = true) {
    Box(
        modifier = modifier.padding(padding),
        contentAlignment = Alignment.Center
    ) {
        content()
    }
}