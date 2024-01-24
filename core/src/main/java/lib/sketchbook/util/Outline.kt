package lib.sketchbook.util

import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path

fun Outline.toPath() = when (this) {
    is Outline.Generic -> path
    is Outline.Rectangle -> Path().apply {
        addRect(bounds)
    }

    is Outline.Rounded -> Path().apply {
        addRoundRect(roundRect)
    }
}