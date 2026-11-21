package lib.sketchbook.glass

import lib.sketchbook.util.Api

internal enum class GlassTier { Refractive, Blur, Tint }

internal fun selectGlass(sdkInt: Int, hasSource: Boolean): GlassTier = when {
    !hasSource -> GlassTier.Tint
    sdkInt >= Api.Agsl -> GlassTier.Refractive
    sdkInt >= Api.Blur -> GlassTier.Blur
    else -> GlassTier.Tint
}
