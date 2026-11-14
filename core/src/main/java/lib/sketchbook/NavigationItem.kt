package lib.sketchbook

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.selection.selectable
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalTextStyle
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.lerp
import lib.sketchbook.theme.Theme
import lib.sketchbook.theme.contentColorFor

private val IndicatorWidth = 56.dp
private val IndicatorHeight = 32.dp
private val ItemMinSize = 56.dp
private const val IndicatorAlpha = .28f
private const val IndicatorMinScale = .4f
private const val UnselectedAlpha = .8f
private const val DisabledAlpha = .38f

@Composable
internal fun NavigationItemCore(
    selected: Boolean,
    onClick: () -> Unit,
    icon: @Composable () -> Unit,
    label: (@Composable () -> Unit)?,
    badge: (@Composable () -> Unit)?,
    enabled: Boolean,
    alwaysShowLabel: Boolean,
    compositeColor: Color,
    interactionSource: MutableInteractionSource,
    modifier: Modifier
) {
    val progress by animateFloatAsState(if (selected) 1f else 0f, label = "indicator")
    val content = Theme.color.contentColorFor(compositeColor)
    val tint = when {
        !enabled -> content.copy(DisabledAlpha)
        selected -> content
        else -> content.copy(UnselectedAlpha)
    }
    val indicatorColor = Theme.color.emphasis.primary.copy(IndicatorAlpha)
    val shape = Theme.container.buttonSmall
    Column(
        modifier = modifier
            .defaultMinSize(minWidth = ItemMinSize, minHeight = ItemMinSize)
            .clip(shape)
            .selectable(
                selected = selected,
                onClick = onClick,
                enabled = enabled,
                role = Role.Tab,
                interactionSource = interactionSource,
                indication = LocalIndication.current
            )
            .padding(vertical = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        CompositionLocalProvider(LocalContentColor provides tint) {
            Box(Modifier.size(IndicatorWidth, IndicatorHeight), contentAlignment = Alignment.Center) {
                Box(
                    Modifier
                        .matchParentSize()
                        .graphicsLayer {
                            alpha = progress
                            scaleX = lerp(IndicatorMinScale, 1f, progress)
                        }
                        .background(indicatorColor, shape)
                )
                icon()
                if (badge != null) {
                    Box(Modifier.align(Alignment.TopEnd)) { badge() }
                }
            }
            if (label != null && (selected || alwaysShowLabel)) {
                CompositionLocalProvider(LocalTextStyle provides Theme.textStyle.caption) { label() }
            }
        }
    }
}
