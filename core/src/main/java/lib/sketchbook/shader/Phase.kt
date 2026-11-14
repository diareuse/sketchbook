package lib.sketchbook.shader

internal class Phase(private val periodNanos: Long) {

    private var origin = -1L

    fun at(frameNanos: Long): Float {
        if (origin < 0) origin = frameNanos
        return ((frameNanos - origin) % periodNanos).toFloat() / periodNanos
    }

    fun reset() {
        origin = -1L
    }
}
