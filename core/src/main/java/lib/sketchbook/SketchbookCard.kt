package lib.sketchbook

import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import lib.sketchbook.glass.glass
import lib.sketchbook.modifier.BeamPalette
import lib.sketchbook.modifier.borderBeam
import lib.sketchbook.glass.asGlassShape
import lib.sketchbook.haptic.deniedWhenDisabled
import lib.sketchbook.haptic.rememberConfirmedClick
import lib.sketchbook.theme.Theme
import lib.sketchbook.theme.contentColorFor

private val CardGlassElevation = 8.dp
private val RimWidth = 1.dp
private const val RimAlpha = .8f
private const val DisabledContentAlpha = .38f

@Composable
private fun Modifier.cardGlass(shape: Shape, enabled: Boolean, useGlow: Boolean, specularRim: Boolean): Modifier {
    val glass = glass(shape, CardGlassElevation, useGlow && enabled, rimIntensity = if (enabled) null else 0f)
    return if (specularRim && enabled) {
        glass.borderBeam(shape, BeamPalette.glass(Color.White.copy(RimAlpha)), strokeWidth = RimWidth)
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
    val glassShape = shape.asGlassShape()
    Surface(
        modifier = modifier.cardGlass(glassShape, enabled = true, useGlow = useGlow, specularRim = specularRim),
        shape = glassShape,
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
    val glassShape = shape.asGlassShape()
    val confirmed = rememberConfirmedClick(onClick)
    Surface(
        modifier = modifier
            .minimumInteractiveComponentSize()
            .deniedWhenDisabled(enabled)
            .cardGlass(glassShape, enabled, useGlow, specularRim)
            .clip(glassShape)
            .clickable(
                interactionSource = interactionSource,
                indication = LocalIndication.current,
                enabled = enabled,
                role = Role.Button,
                onClick = confirmed
            ),
        shape = glassShape,
        color = Color.Transparent,
        contentColor = if (enabled) contentColor else contentColor.copy(DisabledContentAlpha)
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
