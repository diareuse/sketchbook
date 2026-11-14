package lib.sketchbook

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.Dp
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
            .glass(Theme.container.button, RockerGlassElevation)
            .background(Theme.color.overlay.background.copy(.05f), Theme.container.button)
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