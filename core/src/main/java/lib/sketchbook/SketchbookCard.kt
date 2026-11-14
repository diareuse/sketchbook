package lib.sketchbook

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import lib.sketchbook.glass.glass
import lib.sketchbook.modifier.specularBorder
import lib.sketchbook.theme.Theme
import lib.sketchbook.theme.contentColorFor

private val CardGlassElevation = 8.dp
private val RimWidth = 1.dp
private const val RimAlpha = .8f
private const val DisabledContentAlpha = .38f

@Composable
private fun Modifier.cardGlass(shape: Shape, useGlow: Boolean, specularRim: Boolean): Modifier {
    val glass = glass(shape, CardGlassElevation, useGlow)
    return if (specularRim) {
        glass.specularBorder(shape, RimWidth, Color.White.copy(RimAlpha))
    } else {
        glass
    }
}

@Composable
fun SketchbookCard(
    modifier: Modifier = Modifier,
    shape: Shape = Theme.container.card,
    compositeColor: Color = Theme.color.emphasis.background,
    useGlow: Boolean = false,
    specularRim: Boolean = false,
    content: @Composable ColumnScope.() -> Unit
) {
    Surface(
        modifier = modifier.cardGlass(shape, useGlow, specularRim),
        shape = shape,
        color = Color.Transparent,
        contentColor = Theme.color.contentColorFor(compositeColor)
    ) {
        Column(content = content)
    }
}

@Composable
fun SketchbookCard(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    shape: Shape = Theme.container.card,
    compositeColor: Color = Theme.color.emphasis.background,
    useGlow: Boolean = false,
    specularRim: Boolean = false,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    content: @Composable ColumnScope.() -> Unit
) {
    val contentColor = Theme.color.contentColorFor(compositeColor)
    Surface(
        onClick = onClick,
        modifier = modifier
            .minimumInteractiveComponentSize()
            .cardGlass(shape, useGlow, specularRim)
            .semantics { role = Role.Button },
        enabled = enabled,
        shape = shape,
        color = Color.Transparent,
        contentColor = if (enabled) contentColor else contentColor.copy(DisabledContentAlpha),
        interactionSource = interactionSource
    ) {
        Column(content = content)
    }
}

@SketchbookComponentPreview
@SketchbookValidation
@Composable
private fun SketchbookCardPreview() = SketchbookPreviewLayout {
    Column(modifier = Modifier.padding(16.dp)) {
        SketchbookCard {
            Text("Static card", modifier = Modifier.padding(16.dp))
        }
        SketchbookCard(onClick = {}, modifier = Modifier.padding(top = 16.dp), useGlow = true) {
            Text("Clickable card", modifier = Modifier.padding(16.dp))
        }
        SketchbookCard(onClick = {}, modifier = Modifier.padding(top = 16.dp), specularRim = true) {
            Text("Specular rim", modifier = Modifier.padding(16.dp))
        }
        SketchbookCard(onClick = {}, modifier = Modifier.padding(top = 16.dp), enabled = false) {
            Text("Disabled card", modifier = Modifier.padding(16.dp))
        }
    }
}
