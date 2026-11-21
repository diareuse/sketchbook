package lib.sketchbook

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.SnackbarData
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp
import lib.sketchbook.glass.glass
import lib.sketchbook.theme.Theme
import lib.sketchbook.theme.contentColorFor

private val SnackbarElevation = 20.dp
private val SnackbarMargin = 12.dp
private val SnackbarPadding = 16.dp

@Composable
fun SketchbookSnackbar(
    snackbarData: SnackbarData,
    modifier: Modifier = Modifier,
    shape: Shape = Theme.container.card,
    compositeColor: Color = Theme.color.emphasis.background,
    actionColor: Color = Theme.color.emphasis.primary,
    dismissContentDescription: String = "Dismiss"
) {
    val visuals = snackbarData.visuals
    val contentColor = Theme.color.contentColorFor(compositeColor)
    Row(
        modifier = modifier
            .padding(SnackbarMargin)
            .glass(shape, SnackbarElevation, tint = compositeColor)
            .padding(start = SnackbarPadding, top = 4.dp, bottom = 4.dp, end = if (visuals.withDismissAction) 4.dp else 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = visuals.message,
            modifier = Modifier.weight(1f, fill = false).padding(vertical = 12.dp),
            color = contentColor,
            style = Theme.textStyle.body
        )
        visuals.actionLabel?.let { label ->
            TextButton(onClick = snackbarData::performAction) {
                Text(label, color = actionColor, style = Theme.textStyle.emphasis)
            }
        }
        if (visuals.withDismissAction) {
            SketchbookIconButton(
                onClick = snackbarData::dismiss,
                contentDescription = dismissContentDescription,
                compositeColor = compositeColor
            ) {
                Icon(Icons.Default.Close, contentDescription = null)
            }
        }
    }
}

@Composable
fun SketchbookSnackbarHost(
    hostState: SnackbarHostState,
    modifier: Modifier = Modifier,
    snackbar: @Composable (SnackbarData) -> Unit = { SketchbookSnackbar(it) }
) = SnackbarHost(hostState, modifier, snackbar)

@SketchbookComponentPreview
@SketchbookValidation
@Composable
private fun SketchbookSnackbarPreview() = SketchbookPreviewLayout {
    Column {
        SketchbookSnackbar(PreviewSnackbarData("Message sent"))
        SketchbookSnackbar(PreviewSnackbarData("Item deleted", actionLabel = "Undo", withDismissAction = true))
    }
}

private class PreviewSnackbarData(
    message: String,
    actionLabel: String? = null,
    withDismissAction: Boolean = false
) : SnackbarData {
    override val visuals = object : androidx.compose.material3.SnackbarVisuals {
        override val message = message
        override val actionLabel = actionLabel
        override val withDismissAction = withDismissAction
        override val duration = androidx.compose.material3.SnackbarDuration.Indefinite
    }

    override fun performAction() = Unit

    override fun dismiss() = Unit
}
