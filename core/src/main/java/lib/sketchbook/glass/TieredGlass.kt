package lib.sketchbook.glass

import androidx.compose.ui.Modifier

internal class TieredGlass(
    private val sdkInt: Int,
    private val refractive: GlassSurface,
    private val blur: GlassSurface,
    private val tint: GlassSurface
) : GlassSurface {
    override fun Modifier.glass(spec: GlassSpec, source: GlassSource?): Modifier {
        val tier = selectGlass(sdkInt, source != null)
        val surface = when (tier) {
            GlassTier.Refractive -> refractive
            GlassTier.Blur -> blur
            GlassTier.Tint -> tint
        }
        return with(surface) { glass(spec, source) }
    }
}
