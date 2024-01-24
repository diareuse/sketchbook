package lib.sketchbook.util

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.graphics.Color
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionContext
import lib.sketchbook.theme.Theme

fun Context.findActivity(): Activity {
    return findActivityOrNull() ?: error("Unknown Context $this")
}

fun Context.findActivityOrNull(): Activity? {
    when (this) {
        is Activity -> return this
        is ContextWrapper -> return baseContext.findActivityOrNull()
    }
    return null
}

fun ComponentActivity.setContent(
    statusBarStyle: SystemBarStyle = SystemBarStyle.auto(Color.TRANSPARENT, Color.TRANSPARENT),
    navigationBarStyle: SystemBarStyle = SystemBarStyle.auto(Color.TRANSPARENT, Color.TRANSPARENT),
    parent: CompositionContext? = null,
    content: @Composable () -> Unit
) {
    enableEdgeToEdge(statusBarStyle, navigationBarStyle)
    setContent(parent) {
        Theme(content = content)
    }
}