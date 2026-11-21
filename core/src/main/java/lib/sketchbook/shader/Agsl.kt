package lib.sketchbook.shader

internal object Agsl {
    const val RippleFillStart = .8f
    const val RippleRingCenter = .92f
    const val RippleRingWidth = .1f
    const val RippleFillAlpha = .45f
    const val RippleRingAlpha = .35f
    const val ShimmerSlant = .35f
    const val ShimmerTravel = 1.7f
    const val ShimmerSharpness = 28f

    const val PressRipple = """
uniform float2 size;
uniform float2 center;
uniform float progress;
uniform float fade;
uniform half4 color;

half4 main(float2 coord) {
    float radius = length(size) * progress + 1.0;
    float dist = length(coord - center);
    float fill = 1.0 - smoothstep(radius * $RippleFillStart, radius, dist);
    float ringOffset = (dist - radius * $RippleRingCenter) / (radius * $RippleRingWidth + 1.0);
    float ring = exp(-(ringOffset * ringOffset));
    float a = (fill * $RippleFillAlpha + ring * $RippleRingAlpha) * fade * color.a;
    return half4(color.rgb * a, a);
}
"""

    const val Shimmer = """
uniform float2 size;
uniform float phase;
uniform half4 color;

half4 main(float2 coord) {
    float t = coord.x / size.x + (coord.y / size.y) * $ShimmerSlant;
    float d = t - (phase * $ShimmerTravel - $ShimmerSlant);
    float band = exp(-d * d * $ShimmerSharpness);
    float a = band * color.a;
    return half4(color.rgb * a, a);
}
"""

    val MeshGradient: String = auroraShaderSource()
}
