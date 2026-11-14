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
    float ring = exp(-pow((dist - radius * 0.92) / (radius * 0.1 + 1.0), 2.0));
    float a = (fill * 0.45 + ring * 0.35) * fade * color.a;
    return half4(color.rgb * a, a);
}
"""

    const val SpecularRimSweep = """
uniform float2 size;
uniform float angle;
uniform half4 color;

const float TAU = 6.2831853;

half4 main(float2 coord) {
    float2 p = coord - size * 0.5;
    float a = atan(p.y, p.x);
    float d = abs(fract((a - angle) / TAU + 0.5) - 0.5);
    float glint = exp(-d * d * 90.0);
    float k = clamp(0.2 + glint, 0.0, 1.0) * color.a;
    return half4(color.rgb * k, k);
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

    const val MeshGradient = """
uniform float2 size;
uniform float time;
uniform half4 c0;
uniform half4 c1;
uniform half4 c2;
uniform half4 c3;

half4 main(float2 coord) {
    float2 uv = coord / size;
    float2 w = uv + 0.1 * float2(sin(time + uv.y * 3.0), cos(time * 0.8 + uv.x * 3.0));
    float sx = smoothstep(0.0, 1.0, w.x);
    float sy = smoothstep(0.0, 1.0, w.y);
    half4 top = mix(c0, c1, half(sx));
    half4 bottom = mix(c2, c3, half(sx));
    half4 m = mix(top, bottom, half(sy));
    return half4(m.rgb * m.a, m.a);
}
"""
}
