package lib.sketchbook

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Row
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import lib.sketchbook.theme.Theme
import lib.sketchbook.theme.contentColorFor

private const val DisabledContainerAlpha = .12f
private const val DisabledContentAlpha = .38f
private const val TonalAlpha = .2f
private const val GlassOverlayAlpha = .05f

@Composable
private fun iconButtonColors(container: Color, content: Color): IconButtonColors {
    val onBackground = Theme.color.content.background
    return remember(container, content, onBackground) {
        IconButtonColors(
            containerColor = container,
            contentColor = content,
            disabledContainerColor = if (container == Color.Transparent) Color.Transparent
            else onBackground.copy(DisabledContainerAlpha),
            disabledContentColor = onBackground.copy(DisabledContentAlpha)
        )
    }
}

@Composable
private fun IconButtonCore(
    onClick: () -> Unit,
    contentDescription: String?,
    modifier: Modifier,
    enabled: Boolean,
    shape: Shape,
    colors: IconButtonColors,
    glass: Boolean,
    interactionSource: MutableInteractionSource,
    content: @Composable () -> Unit
) {
    val described = if (contentDescription == null) modifier
    else modifier.semantics { this.contentDescription = contentDescription }
    val control = if (glass) described.glassControl(shape, enabled, interactionSource)
    else described.pressControl(shape, interactionSource)
    ThemePressHost {
        FilledIconButton(
            onClick = onClick,
            modifier = control,
            enabled = enabled,
            shape = shape,
            colors = colors,
            interactionSource = interactionSource,
            content = content
        )
    }
}

@Composable
fun SketchbookIconButton(
    onClick: () -> Unit,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    shape: Shape = Theme.container.button,
    compositeColor: Color = Theme.color.emphasis.background,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    content: @Composable () -> Unit
) = IconButtonCore(
    onClick = onClick,
    contentDescription = contentDescription,
    modifier = modifier,
    enabled = enabled,
    shape = shape,
    colors = iconButtonColors(Color.Transparent, Theme.color.contentColorFor(compositeColor)),
    glass = false,
    interactionSource = interactionSource,
    content = content
)

@Composable
fun SketchbookFilledIconButton(
    onClick: () -> Unit,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    shape: Shape = Theme.container.button,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    content: @Composable () -> Unit
) = IconButtonCore(
    onClick = onClick,
    contentDescription = contentDescription,
    modifier = modifier,
    enabled = enabled,
    shape = shape,
    colors = iconButtonColors(Theme.color.emphasis.primary, Theme.color.overlay.primary),
    glass = false,
    interactionSource = interactionSource,
    content = content
)

@Composable
fun SketchbookTonalIconButton(
    onClick: () -> Unit,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    shape: Shape = Theme.container.button,
    compositeColor: Color = Theme.color.emphasis.background,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    content: @Composable () -> Unit
) = IconButtonCore(
    onClick = onClick,
    contentDescription = contentDescription,
    modifier = modifier,
    enabled = enabled,
    shape = shape,
    colors = iconButtonColors(
        Theme.color.emphasis.primary.copy(TonalAlpha),
        Theme.color.contentColorFor(compositeColor)
    ),
    glass = true,
    interactionSource = interactionSource,
    content = content
)

@Composable
fun SketchbookGlassIconButton(
    onClick: () -> Unit,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    shape: Shape = Theme.container.button,
    compositeColor: Color = Theme.color.emphasis.background,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    content: @Composable () -> Unit
) = IconButtonCore(
    onClick = onClick,
    contentDescription = contentDescription,
    modifier = modifier,
    enabled = enabled,
    shape = shape,
    colors = iconButtonColors(
        Theme.color.overlay.background.copy(GlassOverlayAlpha),
        Theme.color.contentColorFor(compositeColor)
    ),
    glass = true,
    interactionSource = interactionSource,
    content = content
)

@SketchbookComponentPreview
@SketchbookValidation
@Composable
private fun SketchbookIconButtonPreview() = SketchbookPreviewLayout {
    Row {
        SketchbookIconButton({}, "Plain") { Icon(Icons.Default.Favorite, null) }
        SketchbookFilledIconButton({}, "Filled") { Icon(Icons.Default.Favorite, null) }
        SketchbookTonalIconButton({}, "Tonal") { Icon(Icons.Default.Favorite, null) }
        SketchbookGlassIconButton({}, "Glass") { Icon(Icons.Default.Favorite, null) }
        SketchbookGlassIconButton({}, "Disabled", enabled = false) { Icon(Icons.Default.Favorite, null) }
    }
}
