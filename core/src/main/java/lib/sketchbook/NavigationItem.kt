package lib.sketchbook

import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.*
import androidx.compose.foundation.shape.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.*
import androidx.compose.ui.geometry.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.semantics.*
import androidx.compose.ui.unit.*
import androidx.compose.ui.util.*
import lib.sketchbook.theme.Theme
import lib.sketchbook.theme.contentColorFor

private val IndicatorWidth = 32.dp
private val IndicatorHeight = 32.dp
private val ItemMinSize = 56.dp
private const val IndicatorAlpha = 1f
private const val IndicatorMinScale = .0f
private const val UnselectedAlpha = .5f
private const val DisabledAlpha = .38f
private const val AnimationDuration = 500
private val AnimationEasing = FastOutSlowInEasing

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
    modifier: Modifier,
    color: Color = Theme.color.emphasis.primary
) {
    val progress by animateFloatAsState(
        targetValue = if (selected) 1f else 0f,
        animationSpec = tween(AnimationDuration, easing = AnimationEasing),
        label = "indicator"
    )
    val content = Theme.color.contentColorFor(compositeColor)
    val tint = when {
        !enabled -> content.copy(DisabledAlpha)
        selected -> content
        else -> content.copy(UnselectedAlpha)
    }
    val indicatorColor = color.copy(IndicatorAlpha)
    Column(
        modifier = modifier
            .defaultMinSize(minWidth = ItemMinSize)
            .selectable(
                selected = selected,
                onClick = onClick,
                enabled = enabled,
                role = Role.Tab,
                interactionSource = interactionSource,
                indication = null
            )
            .graphicsLayer(compositingStrategy = CompositingStrategy.Offscreen)
            .drawWithContent {
                drawContent()
                if (progress > 0) drawRect(
                    brush = Brush.radialGradient(
                        colors = listOf(tint, content.copy(alpha = UnselectedAlpha)),
                        center = Offset(size.width / 2, 0f),
                        radius = size.height * progress
                    ),
                    blendMode = BlendMode.SrcIn
                ) else drawRect(
                    color = content.copy(alpha = UnselectedAlpha),
                    blendMode = BlendMode.SrcIn
                )
            },
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            Modifier
                .size(24.dp, 4.dp)
                .graphicsLayer {
                    alpha = progress
                    scaleX = lerp(IndicatorMinScale, 1f, progress)
                }
                .background(
                    color = indicatorColor,
                    shape = RoundedCornerShape(bottomStart = 4.dp, bottomEnd = 4.dp)
                )
        )
        Spacer(Modifier.height(4.dp))
        CompositionLocalProvider {
            SketchbookBadgedBox(
                modifier = Modifier
                    .size(IndicatorWidth, IndicatorHeight)
                    .clip(CircleShape)
                    .indication(interactionSource, LocalIndication.current),
                badge = { badge?.invoke() }
            ) {
                icon()
            }
            if (label != null && (selected || alwaysShowLabel)) {
                CompositionLocalProvider(LocalTextStyle provides Theme.textStyle.caption) { label() }
            }
        }
        Spacer(Modifier.height(8.dp))
    }
}
