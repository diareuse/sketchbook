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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import lib.sketchbook.glass.asGlassShape
import lib.sketchbook.haptic.rememberConfirmedClick
import lib.sketchbook.theme.Theme
import lib.sketchbook.theme.contentColorFor

private const val DisabledContentAlpha = .38f

@Composable
private fun iconButtonColors(content: Color): IconButtonColors {
    val onBackground = Theme.color.content.background
    return remember(content, onBackground) {
        IconButtonColors(
            containerColor = Color.Transparent,
            contentColor = content,
            disabledContainerColor = Color.Transparent,
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
    content: Color,
    tint: Color,
    tintStrength: Float?,
    interactionSource: MutableInteractionSource,
    icon: @Composable () -> Unit
) {
    val glassShape = shape.asGlassShape()
    val confirmed = rememberConfirmedClick(onClick)
    val described = if (contentDescription == null) modifier
    else modifier.semantics { this.contentDescription = contentDescription }
    val control = described.glassControl(glassShape, enabled, interactionSource, tint, tintStrength)
    ThemePressHost {
        FilledIconButton(
            onClick = confirmed,
            modifier = control,
            enabled = enabled,
            shape = glassShape,
            colors = iconButtonColors(content),
            interactionSource = interactionSource,
            content = icon
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
    content = Theme.color.contentColorFor(compositeColor),
    tint = Theme.color.emphasis.background,
    tintStrength = null,
    interactionSource = interactionSource,
    icon = content
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
    content = Theme.color.overlay.primary,
    tint = Theme.color.emphasis.primary,
    tintStrength = FilledTintStrength,
    interactionSource = interactionSource,
    icon = content
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
    content = Theme.color.contentColorFor(compositeColor),
    tint = Theme.color.container.secondary,
    tintStrength = TonalTintStrength,
    interactionSource = interactionSource,
    icon = content
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
    content = Theme.color.contentColorFor(compositeColor),
    tint = Theme.color.emphasis.background,
    tintStrength = null,
    interactionSource = interactionSource,
    icon = content
)

@SketchbookComponentPreview
@SketchbookValidation
@Composable
private fun SketchbookIconButtonPreview() = SketchbookPreviewLayout {
    Row(verticalAlignment = Alignment.CenterVertically) {
        SketchbookIconButton({}, "Plain") { Icon(Icons.Default.Favorite, null) }
        SketchbookFilledIconButton({}, "Filled") { Icon(Icons.Default.Favorite, null) }
        SketchbookTonalIconButton({}, "Tonal") { Icon(Icons.Default.Favorite, null) }
        SketchbookGlassIconButton({}, "Glass") { Icon(Icons.Default.Favorite, null) }
        SketchbookGlassIconButton({}, "Disabled", enabled = false) { Icon(Icons.Default.Favorite, null) }
    }
}
