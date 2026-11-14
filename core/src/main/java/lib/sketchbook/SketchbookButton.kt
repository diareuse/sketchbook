package lib.sketchbook

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp
import lib.sketchbook.theme.Theme
import lib.sketchbook.theme.contentColorFor

private const val DisabledContainerAlpha = .12f
private const val DisabledContentAlpha = .38f
private const val TonalAlpha = .2f
private const val GlassOverlayAlpha = .05f

@Composable
internal fun buttonColors(container: Color, content: Color): ButtonColors {
    val onBackground = Theme.color.content.background
    return remember(container, content, onBackground) {
        ButtonColors(
            containerColor = container,
            contentColor = content,
            disabledContainerColor = onBackground.copy(DisabledContainerAlpha),
            disabledContentColor = onBackground.copy(DisabledContentAlpha)
        )
    }
}

@Composable
internal fun ButtonCore(
    onClick: () -> Unit,
    modifier: Modifier,
    enabled: Boolean,
    shape: Shape,
    colors: ButtonColors,
    border: BorderStroke?,
    glass: Boolean,
    interactionSource: MutableInteractionSource,
    content: @Composable RowScope.() -> Unit
) {
    GlassTouchTarget(glass) {
        Button(
            onClick = onClick,
            modifier = if (glass) modifier.glassControl(shape, enabled) else modifier,
            enabled = enabled,
            shape = shape,
            colors = colors,
            elevation = null,
            border = border,
            interactionSource = interactionSource,
            content = content
        )
    }
}

@Composable
fun SketchbookButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    shape: Shape = Theme.container.button,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    content: @Composable RowScope.() -> Unit
) = ButtonCore(
    onClick = onClick,
    modifier = modifier,
    enabled = enabled,
    shape = shape,
    colors = buttonColors(Theme.color.emphasis.primary, Theme.color.overlay.primary),
    border = null,
    glass = false,
    interactionSource = interactionSource,
    content = content
)

@Composable
fun SketchbookTonalButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    shape: Shape = Theme.container.button,
    compositeColor: Color = Theme.color.emphasis.background,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    content: @Composable RowScope.() -> Unit
) = ButtonCore(
    onClick = onClick,
    modifier = modifier,
    enabled = enabled,
    shape = shape,
    colors = buttonColors(
        Theme.color.emphasis.primary.copy(TonalAlpha),
        Theme.color.contentColorFor(compositeColor)
    ),
    border = null,
    glass = true,
    interactionSource = interactionSource,
    content = content
)

@Composable
fun SketchbookOutlinedButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    shape: Shape = Theme.container.button,
    compositeColor: Color = Theme.color.emphasis.background,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    content: @Composable RowScope.() -> Unit
) {
    val outline = Theme.color.content.outline
    val border = remember(outline, enabled) {
        BorderStroke(1.dp, if (enabled) outline else outline.copy(DisabledContainerAlpha))
    }
    ButtonCore(
        onClick = onClick,
        modifier = modifier,
        enabled = enabled,
        shape = shape,
        colors = buttonColors(Color.Transparent, Theme.color.contentColorFor(compositeColor)),
        border = border,
        glass = false,
        interactionSource = interactionSource,
        content = content
    )
}

@Composable
fun SketchbookGlassButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    shape: Shape = Theme.container.button,
    compositeColor: Color = Theme.color.emphasis.background,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    content: @Composable RowScope.() -> Unit
) = ButtonCore(
    onClick = onClick,
    modifier = modifier,
    enabled = enabled,
    shape = shape,
    colors = buttonColors(
        Theme.color.overlay.background.copy(GlassOverlayAlpha),
        Theme.color.contentColorFor(compositeColor)
    ),
    border = null,
    glass = true,
    interactionSource = interactionSource,
    content = content
)

@SketchbookComponentPreview
@SketchbookValidation
@Composable
private fun SketchbookButtonPreview() = SketchbookPreviewLayout {
    Column(modifier = Modifier.padding(16.dp)) {
        SketchbookButton({}) { Text("Filled") }
        SketchbookTonalButton({}) { Text("Tonal") }
        SketchbookOutlinedButton({}) { Text("Outlined") }
        SketchbookGlassButton({}) { Text("Glass") }
        SketchbookButton({}, enabled = false) { Text("Disabled") }
    }
}
