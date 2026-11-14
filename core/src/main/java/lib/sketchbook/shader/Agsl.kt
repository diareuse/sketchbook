package lib.sketchbook.shader

internal object Agsl {

    const val PressRipple = """
uniform float2 size;
uniform float2 center;
uniform float progress;
uniform float fade;
uniform half4 color;

half4 main(float2 coord) {
    float radius = length(size) * progress + 1.0;
    float dist = length(coord - center);
    float fill = 1.0 - smoothstep(radius * 0.8, radius, dist);
    float ringOffset = (dist - radius * 0.92) / (radius * 0.1 + 1.0);
    float ring = exp(-(ringOffset * ringOffset));
    float a = (fill * 0.45 + ring * 0.35) * fade * color.a;
    return half4(color.rgb * a, a);
}
"""

    const val Shimmer = """
uniform float2 size;
uniform float phase;
uniform half4 color;

half4 main(float2 coord) {
    float t = coord.x / size.x + (coord.y / size.y) * 0.35;
    float d = t - (phase * 1.7 - 0.35);
    float band = exp(-d * d * 28.0);
    float a = band * color.a;
    return half4(color.rgb * a, a);
}
"""

    val MeshGradient: String = auroraShaderSource()
}
