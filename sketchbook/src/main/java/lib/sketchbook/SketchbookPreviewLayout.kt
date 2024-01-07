package lib.sketchbook

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.unit.*
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