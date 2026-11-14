package lib.sketchbook.modifier

import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import lib.sketchbook.util.toPath

internal class OutlineCache {
    private var size = Size.Unspecified
    private var direction: LayoutDirection? = null
    private var density: Density? = null
    private var cachedOutline: Outline? = null
    private var cachedPath: Path? = null

    fun outline(shape: Shape, size: Size, direction: LayoutDirection, density: Density): Outline {
        val cached = cachedOutline
        if (cached != null && this.size == size && this.direction == direction && sameDensity(density)) {
            return cached
        }
        this.size = size
        this.direction = direction
        this.density = density
        cachedPath = null
        return shape.createOutline(size, direction, density).also { cachedOutline = it }
    }

    fun path(shape: Shape, size: Size, direction: LayoutDirection, density: Density): Path {
        val outline = outline(shape, size, direction, density)
        return cachedPath ?: outline.toPath().also { cachedPath = it }
    }

    fun clear() {
        cachedOutline = null
        cachedPath = null
    }

    private fun sameDensity(other: Density): Boolean {
        val current = density ?: return false
        return current.density == other.density && current.fontScale == other.fontScale
    }
}
