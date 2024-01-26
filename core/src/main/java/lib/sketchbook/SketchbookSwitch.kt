package lib.sketchbook

import android.content.res.Configuration.UI_MODE_NIGHT_NO
import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchColors
import androidx.compose.material3.SwitchDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.tooling.preview.Preview
import lib.sketchbook.theme.Theme
import lib.sketchbook.theme.contentColorFor

@Composable
fun SketchbookSwitch(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    compositeColor: Color = Theme.color.emphasis.background,
    colors: SwitchColors = SwitchDefaults.colors(
        checkedBorderColor = Color.Transparent,
        disabledCheckedBorderColor = Color.Transparent,
        disabledUncheckedBorderColor = Color.Transparent,
        uncheckedBorderColor = Color.Transparent,
        checkedTrackColor = Theme.color.emphasis.primary.copy(.15f)
            .compositeOver(compositeColor),
        disabledCheckedTrackColor = Theme.color.overlay.background.copy(.05f)
            .compositeOver(compositeColor),
        disabledUncheckedTrackColor = Theme.color.overlay.background.copy(.05f)
            .compositeOver(compositeColor),
        uncheckedTrackColor = Theme.color.overlay.background.copy(.1f)
            .compositeOver(compositeColor),
        checkedThumbColor = Theme.color.emphasis.primary,
        uncheckedThumbColor = Theme.color.emphasis.background,
        disabledCheckedThumbColor = Theme.color.contentColorFor(compositeColor).copy(.15f),
        disabledUncheckedThumbColor = Theme.color.contentColorFor(compositeColor).copy(.15f)
    ),
    thumbContent: (@Composable () -> Unit)? = {},
    enabled: Boolean = true,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() }
) {
    Switch(
        modifier = modifier,
        checked = checked,
        onCheckedChange = onCheckedChange,
        colors = colors,
        thumbContent = thumbContent,
        enabled = enabled,
        interactionSource = interactionSource
    )
}

@Preview(showBackground = true, uiMode = UI_MODE_NIGHT_YES)
@Preview(showBackground = true, uiMode = UI_MODE_NIGHT_NO)
@Composable
private fun SketchbookSwitchPreview() = SketchbookPreviewLayout {
    Column {
        SketchbookSwitch(checked = true, onCheckedChange = {})
        SketchbookSwitch(checked = false, onCheckedChange = {})
        SketchbookSwitch(checked = true, onCheckedChange = {}, enabled = false)
        SketchbookSwitch(checked = false, onCheckedChange = {}, enabled = false)
    }
}