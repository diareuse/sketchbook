package lib.sketchbook.util

import android.os.Build
import androidx.annotation.ChecksSdkIntAtLeast

internal object Api {
    const val Blur = Build.VERSION_CODES.S
    const val Agsl = Build.VERSION_CODES.TIRAMISU
}

@ChecksSdkIntAtLeast(parameter = 0)
internal fun sdkAtLeast(api: Int): Boolean = Build.VERSION.SDK_INT >= api
