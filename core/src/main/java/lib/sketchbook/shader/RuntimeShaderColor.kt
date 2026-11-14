package lib.sketchbook.shader

import android.graphics.RuntimeShader
import androidx.annotation.RequiresApi
import androidx.compose.ui.graphics.Color
import lib.sketchbook.util.Api

@RequiresApi(Api.Agsl)
internal fun RuntimeShader.setColor(name: String, color: Color) {
    setFloatUniform(name, color.red, color.green, color.blue, color.alpha)
}
