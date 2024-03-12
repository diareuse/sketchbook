package lib.sketchbook.modifier

import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.VectorConverter
import androidx.compose.animation.core.animate
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.offset
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import lib.sketchbook.util.rememberSaveable

@Composable
fun Modifier.animateItemAppearance(
    offset: DpOffset = DpOffset(x = 0.dp, 48.dp),
    scale: Float = .9f,
    offsetAnimationSpec: AnimationSpec<DpOffset> = spring(
        Spring.DampingRatioMediumBouncy,
        Spring.StiffnessLow
    ),
    scaleAnimationSpec: AnimationSpec<Float> = tween()
): Modifier {
    val isEditMode = LocalView.current.isInEditMode
    var offset by rememberSaveable(if (isEditMode) DpOffset.Zero else offset)
    var scale by rememberSaveable { mutableFloatStateOf(if (isEditMode) 1f else scale) }
    LaunchedEffect(Unit) {
        launch {
            animate(
                typeConverter = DpOffset.VectorConverter,
                initialValue = offset,
                targetValue = DpOffset.Zero,
                animationSpec = offsetAnimationSpec,
                block = { value, _ -> offset = value }
            )
        }
        launch {
            animate(
                initialValue = scale,
                targetValue = 1f,
                animationSpec = scaleAnimationSpec,
                block = { value, _ -> scale = value }
            )
        }
    }
    return this
        .offset { IntOffset(offset.x.roundToPx(), offset.y.roundToPx()) }
        .scale(scale)
}