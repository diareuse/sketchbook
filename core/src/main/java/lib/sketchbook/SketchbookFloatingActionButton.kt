package lib.sketchbook

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import lib.sketchbook.glass.asGlassShape
import lib.sketchbook.haptic.rememberConfirmedClick
import lib.sketchbook.theme.Theme

@Composable
private fun noElevation() = FloatingActionButtonDefaults.elevation(0.dp, 0.dp, 0.dp, 0.dp)

@Composable
fun SketchbookFloatingActionButton(
    onClick: () -> Unit,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    shape: Shape = Theme.container.card,
    containerColor: Color = Theme.color.emphasis.primary,
    contentColor: Color = Theme.color.overlay.primary,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    content: @Composable () -> Unit
) {
    val glassShape = shape.asGlassShape()
    val confirmed = rememberConfirmedClick(onClick)
    val described = if (contentDescription == null) modifier
    else modifier.semantics { this.contentDescription = contentDescription }
    val control = described.glassControl(glassShape, true, interactionSource, containerColor, ProminentTintStrength)
    ThemePressHost {
        FloatingActionButton(
            onClick = confirmed,
            modifier = control,
            shape = glassShape,
            elevation = noElevation(),
            containerColor = Color.Transparent,
            contentColor = contentColor,
            interactionSource = interactionSource,
            content = content
        )
    }
}

@Composable
fun SketchbookExtendedFloatingActionButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    expanded: Boolean = true,
    icon: @Composable () -> Unit,
    shape: Shape = Theme.container.card,
    containerColor: Color = Theme.color.emphasis.primary,
    contentColor: Color = Theme.color.overlay.primary,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    text: @Composable () -> Unit
) {
    val glassShape = shape.asGlassShape()
    val confirmed = rememberConfirmedClick(onClick)
    val control = modifier.glassControl(glassShape, true, interactionSource, containerColor, ProminentTintStrength)
    ThemePressHost {
        ExtendedFloatingActionButton(
            text = text,
            icon = icon,
            onClick = confirmed,
            modifier = control,
            expanded = expanded,
            shape = glassShape,
            elevation = noElevation(),
            containerColor = Color.Transparent,
            contentColor = contentColor,
            interactionSource = interactionSource
        )
    }
}

@SketchbookComponentPreview
@SketchbookValidation
@Composable
private fun SketchbookFloatingActionButtonPreview() = SketchbookPreviewLayout {
    Row(modifier = Modifier.padding(16.dp)) {
        SketchbookFloatingActionButton({}, "Add") { Icon(Icons.Default.Add, null) }
        SketchbookExtendedFloatingActionButton(
            onClick = {},
            modifier = Modifier.padding(start = 16.dp),
            icon = { Icon(Icons.Default.Add, null) }
        ) { Text("Create") }
    }
}
