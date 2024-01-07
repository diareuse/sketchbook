package lib.sketchbook

import android.content.res.Configuration.UI_MODE_NIGHT_NO
import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.*
import androidx.compose.ui.platform.*
import androidx.compose.ui.tooling.preview.*
import androidx.compose.ui.unit.*
import androidx.compose.ui.window.*
import androidx.core.view.WindowCompat
import lib.sketchbook.layout.alignForLargeScreen
import lib.sketchbook.modifier.glow
import lib.sketchbook.modifier.surface
import lib.sketchbook.theme.Theme

@Composable
fun SketchbookDialog(
    visible: Boolean,
    dialog: @Composable DialogScope.() -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    val blur by animateDpAsState(targetValue = if (visible) 32.dp else 0.dp)
    Box(modifier = modifier.blur(blur), propagateMinConstraints = true) {
        content()
    }
    if (visible) dialog(DialogScopeImpl)
}

interface DialogScope

internal object DialogScopeImpl : DialogScope

@Suppress("UnusedReceiverParameter")
@Composable
fun DialogScope.Container(
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) = Dialog(
    onDismissRequest = onDismissRequest,
    properties = DialogProperties(usePlatformDefaultWidth = false, decorFitsSystemWindows = false)
) {
    BackHandler {
        onDismissRequest()
    }
    val view = LocalView.current
    SideEffect {
        (view.parent as? DialogWindowProvider)?.window?.let { window ->
            WindowCompat.setDecorFitsSystemWindows(window, false)
            window.setWindowAnimations(-1)
            window.setDimAmount(.3f)
        }
    }
    Box(
        modifier = modifier
            .padding(24.dp)
            .alignForLargeScreen()
            .surface(1.dp, Theme.container.card)
            .glow(Theme.container.card),
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
        visible = false,
        dialog = { Text("Dialog") }
    ) {
        Box(Modifier)
    }
}