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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import lib.sketchbook.theme.Theme

private val FabElevation = 6.dp

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
): Unit = ThemePressHost {
    FloatingActionButton(
        onClick = onClick,
        modifier = (if (contentDescription == null) modifier
        else modifier.semantics { this.contentDescription = contentDescription })
            .shadow(FabElevation, shape)
            .pressIndicated(shape, interactionSource),
        shape = shape,
        elevation = FloatingActionButtonDefaults.elevation(0.dp, 0.dp, 0.dp, 0.dp),
        containerColor = containerColor,
        contentColor = contentColor,
        interactionSource = interactionSource,
        content = content
    )
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
): Unit = ThemePressHost {
    ExtendedFloatingActionButton(
        text = text,
        icon = icon,
        onClick = onClick,
        modifier = modifier.shadow(FabElevation, shape).pressIndicated(shape, interactionSource),
        expanded = expanded,
        shape = shape,
        elevation = FloatingActionButtonDefaults.elevation(0.dp, 0.dp, 0.dp, 0.dp),
        containerColor = containerColor,
        contentColor = contentColor,
        interactionSource = interactionSource
    )
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
