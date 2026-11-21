package lib.sketchbook.glass

import androidx.compose.foundation.shape.CornerBasedShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.Shape

internal fun glassShape(shape: Shape): Shape = when (shape) {
    is RoundedCornerShape -> shape
    is CornerBasedShape -> RoundedCornerShape(shape.topStart, shape.topEnd, shape.bottomEnd, shape.bottomStart)
    else -> shape
}

internal fun Shape.asGlassShape(): Shape = glassShape(this)
