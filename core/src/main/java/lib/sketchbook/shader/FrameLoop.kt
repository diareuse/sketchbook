package lib.sketchbook.shader

import androidx.compose.runtime.withFrameNanos
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

internal class FrameLoop(private val onFrame: (Long) -> Boolean) {

    private var job: Job? = null

    val running: Boolean get() = job?.isActive == true

    fun start(scope: CoroutineScope) {
        if (running) return
        job = scope.launch {
            var more = true
            while (more && isActive) {
                withFrameNanos { more = onFrame(it) }
            }
        }
    }

    fun stop() {
        job?.cancel()
        job = null
    }
}
