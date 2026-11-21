package lib.sketchbook.glass

import androidx.compose.ui.Modifier

internal fun interface GlassSurface {
    fun Modifier.glass(spec: GlassSpec, source: GlassSource?): Modifier
}
