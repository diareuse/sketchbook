@file:OptIn(ExperimentalTransitionApi::class, ExperimentalAnimationApi::class)

package lib.sketchbook

import android.content.res.Configuration.UI_MODE_NIGHT_NO
import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.ExperimentalTransitionApi
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDp
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.window.DialogWindowProvider
import androidx.core.view.WindowCompat
import lib.sketchbook.animation.AnticipateOvershootEasing
import lib.sketchbook.layout.alignForLargeScreen
import lib.sketchbook.modifier.glow
import lib.sketchbook.modifier.optional
import lib.sketchbook.modifier.surface
import lib.sketchbook.theme.Theme

@Composable
fun SketchbookDialog(
    visible: Boolean,
    onVisibleChange: (Boolean) -> Unit,
    dialog: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    contentAlignment: Alignment = Alignment.BottomCenter,
    content: @Composable () -> Unit,
) {
    val transition = updateTransition(visible)
    val blur by transition.animateDp { if (it) 32.dp else 0.dp }
    val dim by transition.animateFloat { if (it) .3f else 0f }
    Box(
        modifier = modifier.blur(blur),
        propagateMinConstraints = true
    ) {
        content()
    }
    if (visible || transition.isRunning) Dialog(
        onDismissRequest = {
            onVisibleChange(false)
        },
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            decorFitsSystemWindows = false
        )
    ) {
        BackHandler {
            onVisibleChange(false)
        }
        val view = LocalView.current
        SideEffect {
            (view.parent as? DialogWindowProvider)?.window?.let { window ->
                WindowCompat.setDecorFitsSystemWindows(window, false)
                window.setWindowAnimations(-1)
                window.setDimAmount(dim)
            }
        }
        var contentVisible by remember { mutableStateOf(view.isInEditMode) }
        SideEffect {
            contentVisible = visible
        }
        Box(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(Unit) {
                    awaitEachGesture {
                        awaitFirstDown()
                        onVisibleChange(false)
                    }
                },
            contentAlignment = contentAlignment
        ) {
            transition.AnimatedVisibility(
                visible = { it || contentVisible },
                enter = slideInVertically(
                    animationSpec = spring(
                        Spring.DampingRatioMediumBouncy,
                        Spring.StiffnessLow
                    ),
                    initialOffsetY = { it }
                ),
                exit = slideOutVertically(
                    animationSpec = tween(700, easing = AnticipateOvershootEasing),
                    targetOffsetY = { it }
                )
            ) {
                dialog()
            }
        }
    }
}

@Composable
fun DialogSurface(
    modifier: Modifier = Modifier,
    padding: PaddingValues = PaddingValues(24.dp),
    shape: Shape = Theme.container.card,
    useGlow: Boolean = false,
    tonalElevation: Dp = 1.dp,
    content: @Composable () -> Unit,
) {
    Box(
        modifier = modifier
            .padding(padding)
            .alignForLargeScreen()
            .surface(tonalElevation, shape)
            .optional(useGlow) { glow(shape) },
        propagateMinConstraints = true
    ) {
        content()
    }
}


@Preview(showBackground = true, uiMode = UI_MODE_NIGHT_YES)
@Preview(showBackground = true, uiMode = UI_MODE_NIGHT_NO)
@Composable
private fun SketchbookDialogPreview() = SketchbookPreviewLayout {
    SketchbookDialog(
        visible = true,
        onVisibleChange = {},
        contentAlignment = Alignment.BottomCenter,
        dialog = {
            DialogSurface {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text("Dialog")
                }
            }
        }
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Gray)
        )
    }
}