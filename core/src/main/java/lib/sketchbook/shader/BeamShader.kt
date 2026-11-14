/*
* Copyright (c) 2026 ismnoiet
*
* Permission is hereby granted, free of charge, to any person obtaining a copy
* of this software and associated documentation files (the "Software"), to deal
* in the Software without restriction, including without limitation the rights
* to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
* copies of the Software, and to permit persons to whom the Software is
* furnished to do so, subject to the following conditions:
*
* The above copyright notice and this permission notice shall be included in all
* copies or substantial portions of the Software.
*
* THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
* IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
* FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
* AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
* LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
* OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
*/

package lib.sketchbook.shader

import android.graphics.RuntimeShader
import androidx.annotation.RequiresApi
import androidx.compose.ui.graphics.ShaderBrush
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.clipPath
import lib.sketchbook.modifier.BeamStyle
import lib.sketchbook.util.Api

internal const val BeamAgsl = """
uniform float2 size;
uniform float density;
uniform float phase;
uniform float direction;
uniform float mode;
uniform float arc;
uniform float glow;
uniform float stroke;
uniform float intensity;
uniform float count;
uniform float4 colors[8];
uniform float2 pts[64];
uniform float perim;

const float PI = 3.14159265;

float4 samplePalette(float t) {
    int n = int(count);
    float4 result = colors[0];
    if (n <= 1) {
        return result;
    }
    float x = clamp(t, 0.0, 1.0) * float(n - 1);
    for (int i = 0; i < 7; i++) {
        if (i + 1 < n) {
            result = mix(result, colors[i + 1], clamp(x - float(i), 0.0, 1.0));
        }
    }
    return result;
}

float2 scanSegment(float2 p, float2 a, float2 b) {
    float2 ab = b - a;
    float t = clamp(dot(p - a, ab) / max(dot(ab, ab), 0.000001), 0.0, 1.0);
    float2 d = p - (a + ab * t);
    return float2(dot(d, d), t);
}

float2 nearestOnBorder(float2 p) {
    float stepLen = perim / 64.0;
    float best = 1000000000.0;
    float along = 0.0;
    for (int i = 0; i < 63; i++) {
        float2 hit = scanSegment(p, pts[i], pts[i + 1]);
        if (hit.x < best) {
            best = hit.x;
            along = (float(i) + hit.y) * stepLen;
        }
    }
    float2 last = scanSegment(p, pts[63], pts[0]);
    if (last.x < best) {
        best = last.x;
        along = (63.0 + last.y) * stepLen;
    }
    return float2(sqrt(best), along);
}

float2 boundaryPoint(float s) {
    float stepLen = perim / 64.0;
    float wrapped = s - perim * floor(s / max(perim, 0.001));
    float f = wrapped / max(stepLen, 0.0001);
    float idx = min(floor(f), 63.0);
    float t = f - idx;
    float2 a = pts[0];
    float2 b = pts[0];
    for (int i = 0; i < 63; i++) {
        if (float(i) == idx) {
            a = pts[i];
            b = pts[i + 1];
        }
    }
    if (idx >= 63.0) {
        a = pts[63];
        b = pts[0];
    }
    return mix(a, b, t);
}

float4 orbitGlowBlobs(float2 p, float dist, float sigmaBase) {
    float delta = arc * perim / 24.0;
    float sigma = max(sigmaBase, 0.8 * delta);
    if (dist > 3.0 * sigma) { return float4(0.0); }
    float inv2s2 = 0.5 / (sigma * sigma);
    float norm = delta / (sigma * 2.50663);

    float start = 1.0 - arc;
    float4 acc = float4(0.0);
    for (int k = 0; k < 24; k++) {
        float t = (float(k) + 0.5) / 24.0;
        float sp = fract(start + t * arc + direction * phase);
        float2 pt = boundaryPoint(sp * perim);
        float2 dv = p - pt;
        float g = exp(-min(dot(dv, dv) * inv2s2, 16.0));
        float bell = pow(max(sin(PI * t), 0.0), 1.6);
        float4 c4 = samplePalette(t);
        float v = g * norm * bell * clamp(c4.a, 0.0, 1.0);
        acc += float4(c4.rgb * v, v);
    }
    return acc;
}

half4 main(float2 fragCoord) {
    float2 position = fragCoord / density;
    float4 acc = float4(0.0);

    if (mode < 0.5) {
        float2 border = nearestOnBorder(position);
        float dist = border.x;
        float a = border.y / max(perim, 0.001);
        float tt = (fract(a - direction * phase) - (1.0 - arc)) / arc;
        if (tt >= 0.0) {
            float profile = pow(max(sin(PI * clamp(tt, 0.0, 1.0)), 0.0), 1.2);
            float crisp = 1.0 - smoothstep(stroke - 0.75, stroke, dist);
            float halo = 0.6 * exp(-min(dist / max(glow * 0.1, 0.6), 16.0));
            float4 c4 = samplePalette(tt);
            float w = (crisp + halo) * profile * clamp(c4.a, 0.0, 1.0);
            acc += float4(c4.rgb * w, w);
        }
        acc += 0.55 * orbitGlowBlobs(position, dist, max(glow, 0.001) * 0.7);
    } else {
        float u = position.x / size.x;
        float edgeDist = size.y - position.y;

        float len = 0.28;
        float tri = phase < 0.5 ? phase * 2.0 : (1.0 - phase) * 2.0;
        float eased = tri * tri * (3.0 - 2.0 * tri);
        float c = len * 0.5 + (1.0 - len) * eased;
        float k = (u - (c - len * 0.5)) / len;
        if (k < 0.0 || k > 1.0) { return half4(0.0); }
        float tp = 1.0 - abs(2.0 * k - 1.0);
        float profile = tp * tp * (3.0 - 2.0 * tp);
        float4 c4 = samplePalette(k);

        float crisp = 1.0 - smoothstep(stroke - 0.75, stroke, edgeDist);
        float halo = 0.8 * exp(-min(edgeDist / max(glow * 0.2, 0.5), 16.0));
        float wide = 0.6 * exp(-min(pow(max(edgeDist, 0.0) / max(glow, 0.001), 1.4), 16.0));
        float w = ((crisp + halo) * profile + wide * profile) * clamp(c4.a, 0.0, 1.0);
        acc += float4(c4.rgb * w, w);
    }

    float total = acc.a;
    if (total <= 0.0001) { return half4(0.0); }
    float3 rgb = acc.rgb / total;
    float a = clamp(total * intensity, 0.0, 1.0);
    return half4(half3(rgb) * half(a), half(a));
}
"""

@RequiresApi(Api.Agsl)
internal class AgslBeamPainter : BeamPainter {
    private val shader = RuntimeShader(BeamAgsl)
    private val brush = ShaderBrush(shader)
    private var uploadedGeometry: BeamGeometry? = null
    private var uploadedLook: BeamLook? = null

    override fun DrawScope.paint(geometry: BeamGeometry, look: BeamLook, phase: Float, fade: Float) {
        if (uploadedGeometry !== geometry) {
            shader.setFloatUniform("pts", geometry.samples.points)
            shader.setFloatUniform("perim", geometry.samples.perimeter)
            uploadedGeometry = geometry
        }
        if (uploadedLook != look) {
            val palette = look.palette
            shader.setFloatUniform("colors", packPalette(palette))
            shader.setFloatUniform("count", palette.size.toFloat())
            shader.setFloatUniform("direction", look.directionSign)
            shader.setFloatUniform("mode", if (look.style == BeamStyle.Line) 1f else 0f)
            shader.setFloatUniform("arc", look.arc)
            shader.setFloatUniform("glow", look.glowReach)
            shader.setFloatUniform("stroke", look.strokeWidth.value)
            uploadedLook = look
        }
        shader.setFloatUniform("size", size.width / density, size.height / density)
        shader.setFloatUniform("density", density)
        shader.setFloatUniform("phase", phase)
        shader.setFloatUniform("intensity", look.intensity * fade)
        clipPath(geometry.path) { drawRect(brush = brush) }
    }
}
