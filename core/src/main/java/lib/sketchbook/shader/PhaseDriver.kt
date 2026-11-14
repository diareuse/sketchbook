package lib.sketchbook.shader

import kotlinx.coroutines.CoroutineScope

internal class PhaseDriver(
    durationMillis: Int,
    private val onPhase: (Float) -> Unit
) {

    private var duration = durationMillis
    private var clock = Phase(periodNanos(durationMillis))
    private val loop = FrameLoop {
        onPhase(clock.at(it))
        true
    }

    fun duration(durationMillis: Int) {
        if (duration == durationMillis) return
        duration = durationMillis
        clock = Phase(periodNanos(durationMillis))
    }

    fun run(active: Boolean, scope: CoroutineScope) {
        if (!active) return stop()
        if (!loop.running) clock.reset()
        loop.start(scope)
    }

    fun stop() {
        loop.stop()
        clock.reset()
    }
}
