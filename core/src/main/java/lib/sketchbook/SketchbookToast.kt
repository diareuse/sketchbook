package lib.sketchbook

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.delay
import lib.sketchbook.glass.glass
import lib.sketchbook.theme.Theme
import lib.sketchbook.theme.contentColorFor

private val ToastElevation = 20.dp
private val ToastMargin = 16.dp
private const val ShortMillis = 2_000L
private const val LongMillis = 3_500L

@JvmInline
value class ToastDuration(val millis: Long) {
    init {
        require(millis > 0) { "millis must be positive" }
    }

    companion object {
        val Short = ToastDuration(ShortMillis)
        val Long = ToastDuration(LongMillis)
    }
}

@Stable
class SketchbookToastState {

    private val queue = Mutex()

    internal var message: String? by mutableStateOf(null)
        private set

    suspend fun show(message: String, duration: ToastDuration = ToastDuration.Short) {
        queue.withLock {
            this.message = message
            try {
                delay(duration.millis)
            } finally {
                this.message = null
            }
        }
    }
}

@Composable
fun SketchbookToast(
    message: String,
    modifier: Modifier = Modifier,
    shape: Shape = Theme.container.card,
    compositeColor: Color = Theme.color.emphasis.background
) {
    Text(
        text = message,
        modifier = modifier
            .glass(shape, ToastElevation, tint = compositeColor)
            .padding(horizontal = 20.dp, vertical = 12.dp),
        color = Theme.color.contentColorFor(compositeColor),
        style = Theme.textStyle.body
    )
}

@Composable
fun SketchbookToastHost(
    state: SketchbookToastState,
    modifier: Modifier = Modifier,
    toast: @Composable (String) -> Unit = { SketchbookToast(it) }
) {
    AnimatedContent(
        targetState = state.message,
        modifier = modifier,
        transitionSpec = { fadeIn() togetherWith fadeOut() },
        label = "toast"
    ) { message ->
        if (message != null) {
            Box(Modifier.padding(ToastMargin).semantics { liveRegion = LiveRegionMode.Polite }) {
                toast(message)
            }
        }
    }
}

@SketchbookComponentPreview
@SketchbookValidation
@Composable
private fun SketchbookToastPreview() = SketchbookPreviewLayout {
    Box(Modifier.padding(16.dp)) {
        SketchbookToast("Copied to clipboard")
    }
}
