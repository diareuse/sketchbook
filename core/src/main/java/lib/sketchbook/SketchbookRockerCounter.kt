@file:OptIn(ExperimentalMaterial3Api::class)

package lib.sketchbook

import androidx.compose.foundation.background
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import lib.sketchbook.theme.Theme

@Composable
fun SketchbookRockerCounter(
    decrement: @Composable () -> Unit,
    increment: @Composable () -> Unit,
    label: @Composable () -> Unit,
    modifier: Modifier = Modifier
) {
    SingleChoiceSegmentedButtonRow(
        modifier = modifier
            .background(Theme.color.overlay.background.copy(.05f), Theme.container.button)
            .clip(Theme.container.button),
        space = 0.dp
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