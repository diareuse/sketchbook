package lib.sketchbook

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp
import lib.sketchbook.glass.asGlassShape
import lib.sketchbook.haptic.rememberConfirmedClick
import lib.sketchbook.theme.Theme
import lib.sketchbook.theme.contentColorFor

private const val DisabledContentAlpha = .38f

@Composable
internal fun buttonColors(content: Color): ButtonColors {
    val onBackground = Theme.color.content.background
    return remember(content, onBackground) {
        ButtonColors(
            containerColor = Color.Transparent,
            contentColor = content,
            disabledContainerColor = Color.Transparent,
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
    content: Color,
    tint: Color,
    tintStrength: Float?,
    interactionSource: MutableInteractionSource,
    label: @Composable RowScope.() -> Unit
) {
    val glassShape = shape.asGlassShape()
    val confirmed = rememberConfirmedClick(onClick)
    val control = modifier.glassControl(glassShape, enabled, interactionSource, tint, tintStrength)
    ThemePressHost {
        Button(
            onClick = confirmed,
            modifier = control,
            enabled = enabled,
            shape = glassShape,
            colors = buttonColors(content),
            elevation = null,
            border = null,
            interactionSource = interactionSource,
            content = label
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
    content = Theme.color.overlay.primary,
    tint = Theme.color.emphasis.primary,
    tintStrength = FilledTintStrength,
    interactionSource = interactionSource,
    label = content
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
    content = Theme.color.contentColorFor(compositeColor),
    tint = Theme.color.container.secondary,
    tintStrength = TonalTintStrength,
    interactionSource = interactionSource,
    label = content
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
) = ButtonCore(
    onClick = onClick,
    modifier = modifier,
    enabled = enabled,
    shape = shape,
    content = Theme.color.contentColorFor(compositeColor),
    tint = Theme.color.emphasis.background,
    tintStrength = null,
    interactionSource = interactionSource,
    label = content
)

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
    content = Theme.color.contentColorFor(compositeColor),
    tint = Theme.color.emphasis.background,
    tintStrength = null,
    interactionSource = interactionSource,
    label = content
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
