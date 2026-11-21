package lib.sketchbook.glass

import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.setValue
import dev.chrisbanes.haze.HazeState

@Stable
internal class GlassSource(val state: HazeState) {
    private var registered by mutableIntStateOf(0)

    val active: Boolean get() = registered > 0

    fun attach() {
        registered++
    }

    fun detach() {
        registered--
    }
}
