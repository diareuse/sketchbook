package lib.sketchbook

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemColors
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import lib.sketchbook.theme.Theme
import lib.sketchbook.theme.contentColorFor

private const val SelectedAlpha = .18f
private const val DisabledContentAlpha = .38f
private const val SupportingAlpha = .86f

@Composable
private fun listItemColors(compositeColor: Color, enabled: Boolean): ListItemColors {
    val content = Theme.color.contentColorFor(compositeColor)
    val primary = if (enabled) content else content.copy(DisabledContentAlpha)
    val secondary = primary.copy(alpha = primary.alpha * SupportingAlpha)
    return ListItemDefaults.colors(
        containerColor = Color.Transparent,
        headlineColor = primary,
        leadingIconColor = primary,
        overlineColor = secondary,
        supportingColor = secondary,
        trailingIconColor = primary
    )
}

@Composable
fun SketchbookListItem(
    headline: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    overline: (@Composable () -> Unit)? = null,
    supporting: (@Composable () -> Unit)? = null,
    leading: (@Composable () -> Unit)? = null,
    trailing: (@Composable () -> Unit)? = null,
    onClick: (() -> Unit)? = null,
    selected: Boolean = false,
    enabled: Boolean = true,
    shape: Shape = Theme.container.buttonSmall,
    compositeColor: Color = Theme.color.container.background,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() }
) {
    val highlight by animateFloatAsState(if (selected) 1f else 0f, label = "selected")
    val selectedColor = Theme.color.emphasis.primary
    ListItem(
        headlineContent = headline,
        modifier = modifier
            .clip(shape)
            .drawBehind { drawRect(selectedColor, alpha = highlight * SelectedAlpha) }
            .then(
                if (onClick == null) Modifier else Modifier.semantics { this.selected = selected }.clickable(
                    enabled = enabled,
                    role = Role.Button,
                    interactionSource = interactionSource,
                    indication = LocalIndication.current,
                    onClick = onClick
                )
            ),
        overlineContent = overline,
        supportingContent = supporting,
        leadingContent = leading,
        trailingContent = trailing,
        colors = listItemColors(compositeColor, enabled)
    )
}

@SketchbookComponentPreview
@SketchbookValidation
@Composable
private fun SketchbookListItemPreview() = SketchbookPreviewLayout {
    Column(modifier = Modifier.padding(16.dp)) {
        SketchbookListItem(
            headline = { Text("Headline") },
            supporting = { Text("Supporting text") },
            leading = { Icon(Icons.Default.Star, null) }
        )
        SketchbookListItem(
            headline = { Text("Selected") },
            overline = { Text("Overline") },
            supporting = { Text("Supporting text") },
            trailing = { Text("Trailing") },
            onClick = {},
            selected = true
        )
        SketchbookListItem(headline = { Text("Disabled") }, onClick = {}, enabled = false)
    }
}
