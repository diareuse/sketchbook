package lib.sketchbook

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.text.font.*
import androidx.compose.ui.text.style.*
import androidx.compose.ui.unit.*
import lib.sketchbook.glass.asGlassShape
import lib.sketchbook.glass.glass
import lib.sketchbook.theme.Theme

private val RockerGlassElevation: Dp = 17.dp

@Composable
fun SketchbookRockerCounter(
    decrement: @Composable () -> Unit,
    increment: @Composable () -> Unit,
    label: @Composable () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .glass(Theme.container.button.asGlassShape(), RockerGlassElevation)
            .background(
                Theme.color.overlay.background.copy(.05f),
                Theme.container.button.asGlassShape()
            )
            .defaultMinSize(minHeight = 40.dp)
            .width(IntrinsicSize.Min),
        verticalAlignment = Alignment.CenterVertically
    ) {
        decrement()
        ProvideTextStyle(
            Theme.textStyle.body.copy(
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
        ) {
            label()
        }
        increment()
    }
}

@SketchbookComponentPreview
@SketchbookValidation
@Composable
private fun SketchbookRockerCounterPreview(
) = SketchbookPreviewLayout {
    SketchbookRockerCounter(
        decrement = { IconButton({}) { Icon(Icons.Default.FavoriteBorder, null) } },
        increment = { IconButton({}) { Icon(Icons.Default.Favorite, null) } },
        label = { Text("3") }
    )
}