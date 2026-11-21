package lib.sketchbook.shader

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.colorspace.ColorSpaces
import kotlin.math.cbrt
import kotlin.math.exp
import kotlin.math.sin

internal data class AuroraBlob(
    val anchorX: Float,
    val anchorY: Float,
    val amplitudeX: Float,
    val amplitudeY: Float,
    val frequencyX: Int,
    val frequencyY: Int,
    val radius: Float
)

internal object Aurora {
    const val Gain = 1.3f
    const val Strength = .9f
    const val GrainLevels = 3f

    val blobs = listOf(
        AuroraBlob(.20f, .15f, .18f, .12f, 1, 2, .38f),
        AuroraBlob(.85f, .80f, .15f, .15f, 2, 1, .30f),
        AuroraBlob(.75f, .20f, .20f, .18f, 3, 2, .22f),
        AuroraBlob(.20f, .85f, .20f, .15f, 2, 3, .15f)
    )

    fun center(blob: AuroraBlob, time: Float): Offset = Offset(
        blob.anchorX + blob.amplitudeX * sin(blob.frequencyX * time),
        blob.anchorY - blob.amplitudeY * sin(blob.frequencyY * time)
    )

    fun unit(width: Float, height: Float): Float = (width + height) / 2f

    fun alphaAt(weight: Float): Float = (1f - exp(-Gain * weight)) * Strength

    fun weightAt(normalizedDistance: Float, blob: AuroraBlob): Float =
        exp(-(normalizedDistance * normalizedDistance) / (blob.radius * blob.radius))
}

internal data class Oklab(val l: Float, val a: Float, val b: Float)

internal fun Color.toOklab(): Oklab {
    val c = convert(ColorSpaces.LinearSrgb)
    val l = cbrt(.4122214708f * c.red + .5363325363f * c.green + .0514459929f * c.blue)
    val m = cbrt(.2119034982f * c.red + .6806995451f * c.green + .1073969566f * c.blue)
    val s = cbrt(.0883024619f * c.red + .2817188376f * c.green + .6299787005f * c.blue)
    return Oklab(
        .2104542553f * l + .7936177850f * m - .0040720468f * s,
        1.9779984951f * l - 2.4285922050f * m + .4505937099f * s,
        .0259040371f * l + .7827717662f * m - .8086757660f * s
    )
}

internal fun auroraShaderSource(): String = buildString {
    appendLine("uniform float2 size;")
    appendLine("uniform float time;")
    appendLine("uniform float4 base;")
    appendLine("uniform float4 c0;")
    appendLine("uniform float4 c1;")
    appendLine("uniform float4 c2;")
    appendLine("uniform float4 c3;")
    appendLine(
        """
float3 oklabToSrgb(float3 lab) {
    float l_ = lab.x + 0.3963377774 * lab.y + 0.2158037573 * lab.z;
    float m_ = lab.x - 0.1055613458 * lab.y - 0.0638541728 * lab.z;
    float s_ = lab.x - 0.0894841775 * lab.y - 1.2914855480 * lab.z;
    float l = l_ * l_ * l_;
    float m = m_ * m_ * m_;
    float s = s_ * s_ * s_;
    float3 lin = float3(
        4.0767416621 * l - 3.3077115913 * m + 0.2309699292 * s,
        -1.2684380046 * l + 2.6097574011 * m - 0.3413193965 * s,
        -0.0041960863 * l - 0.7034186147 * m + 1.7076147010 * s
    );
    lin = clamp(lin, 0.0, 1.0);
    float3 hi = 1.055 * pow(lin, float3(1.0 / 2.4)) - 0.055;
    float3 lo = 12.92 * lin;
    return float3(
        lin.x > 0.0031308 ? hi.x : lo.x,
        lin.y > 0.0031308 ? hi.y : lo.y,
        lin.z > 0.0031308 ? hi.z : lo.z
    );
}
"""
    )
    appendLine("half4 main(float2 coord) {")
    appendLine("    float unit = (size.x + size.y) * 0.5;")
    appendLine("    float2 uv = coord / size;")
    appendLine("    float2 warped = coord + 0.035 * unit * float2(sin(time + uv.y * 3.0), cos(time * 2.0 + uv.x * 3.0));")
    appendLine("    float total = 0.0;")
    appendLine("    float3 mixed = float3(0.0);")
    Aurora.blobs.forEachIndexed { i, b ->
        appendLine("    float2 p$i = float2(${b.anchorX} + ${b.amplitudeX} * sin(${b.frequencyX}.0 * time), ${b.anchorY} - ${b.amplitudeY} * sin(${b.frequencyY}.0 * time)) * size;")
        appendLine("    float2 d$i = (warped - p$i) / unit;")
        appendLine("    float w$i = exp(-dot(d$i, d$i) / ${b.radius * b.radius}) * c$i.a;")
        appendLine("    total += w$i;")
        appendLine("    mixed += w$i * c$i.rgb;")
    }
    appendLine("    mixed /= max(total, 0.0001);")
    appendLine("    float a = (1.0 - exp(-${Aurora.Gain} * total)) * ${Aurora.Strength};")
    appendLine("    float alpha = base.a * (1.0 - a) + a;")
    appendLine("    float3 lab = (base.rgb * base.a * (1.0 - a) + mixed * a) / max(alpha, 0.0001);")
    appendLine("    float3 rgb = oklabToSrgb(lab);")
    appendLine("    float grain = fract(sin(dot(coord, float2(12.9898, 78.233))) * 43758.5453) - 0.5;")
    appendLine("    rgb = clamp(rgb + grain * ${Aurora.GrainLevels} / 255.0, 0.0, 1.0);")
    appendLine("    return half4(half3(rgb * alpha), half(alpha));")
    appendLine("}")
}
