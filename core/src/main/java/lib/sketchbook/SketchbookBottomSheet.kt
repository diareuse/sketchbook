@file:OptIn(ExperimentalMaterial3Api::class)

package lib.sketchbook

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.ZeroCornerSize
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp
import lib.sketchbook.glass.GlassDepthScope
import lib.sketchbook.glass.glass
import lib.sketchbook.theme.Theme
import lib.sketchbook.theme.contentColorFor

private val SheetGlassElevation = 16.dp

@Composable
private fun topRoundedCard(): Shape =
    Theme.container.card.copy(bottomStart = ZeroCornerSize, bottomEnd = ZeroCornerSize)

@Composable
fun SketchbookBottomSheet(
    visible: Boolean,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    shape: Shape = topRoundedCard(),
    compositeColor: Color = Theme.color.emphasis.background,
    useGlow: Boolean = false,
    dragHandle: (@Composable () -> Unit)? = { BottomSheetDefaults.DragHandle() },
    content: @Composable ColumnScope.() -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    LaunchedEffect(visible, sheetState) {
        if (!visible && sheetState.isVisible) sheetState.hide()
    }
    if (!visible && !sheetState.isVisible) return
    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        modifier = modifier,
        sheetState = sheetState,
        shape = shape,
        containerColor = Color.Transparent,
        contentColor = Theme.color.contentColorFor(compositeColor),
        tonalElevation = 0.dp,
        dragHandle = null,
        contentWindowInsets = { WindowInsets(0, 0, 0, 0) }
    ) {
        GlassDepthScope {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .glass(shape, SheetGlassElevation, useGlow)
                    .windowInsetsPadding(WindowInsets.navigationBars)
            ) {
                if (dragHandle != null) {
                    Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) { dragHandle() }
                }
                content()
            }
        }
    }
}

@SketchbookComponentPreview
@SketchbookValidation
@Composable
private fun SketchbookBottomSheetPreview() = SketchbookPreviewLayout {
    SketchbookBottomSheet(visible = true, onDismissRequest = {}) {
        Text("Sheet title", Modifier.padding(16.dp))
        Text("Sheet content", Modifier.padding(16.dp))
    }
}
