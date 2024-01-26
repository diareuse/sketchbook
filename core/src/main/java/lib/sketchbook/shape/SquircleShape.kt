/*
* Copyright (c) 2023 Stoyan Vuchev
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

package lib.sketchbook.shape

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CornerBasedShape
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.geometry.toRect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp

object CornerSmoothing {

    const val None = 0.55f
    const val Small = 0.67f
    const val Medium = 0.72f
    const val High = 0.8f
    const val Full = 1f

}

abstract class SquircleBasedShape(
    topStart: CornerSize,
    topEnd: CornerSize,
    bottomStart: CornerSize,
    bottomEnd: CornerSize,
    val cornerSmoothing: Float
) : CornerBasedShape(topStart, topEnd, bottomEnd, bottomStart) {

    final override fun createOutline(
        size: Size,
        topStart: Float,
        topEnd: Float,
        bottomEnd: Float,
        bottomStart: Float,
        layoutDirection: LayoutDirection
    ) = createOutline(
        size = size,
        topStart = topStart,
        topEnd = topEnd,
        bottomEnd = bottomEnd,
        bottomStart = bottomStart,
        cornerSmoothing = cornerSmoothing,
        layoutDirection = layoutDirection
    )

    abstract fun createOutline(
        size: Size,
        topStart: Float,
        topEnd: Float,
        bottomEnd: Float,
        bottomStart: Float,
        cornerSmoothing: Float,
        layoutDirection: LayoutDirection
    ): Outline

    abstract fun copy(
        topStart: CornerSize = this.topStart,
        topEnd: CornerSize = this.topEnd,
        bottomEnd: CornerSize = this.bottomEnd,
        bottomStart: CornerSize = this.bottomStart,
        cornerSmoothing: Float = this.cornerSmoothing
    ): SquircleBasedShape

    fun copy(
        all: CornerSize,
        cornerSmoothing: Float = this.cornerSmoothing
    ): SquircleBasedShape = copy(
        topStart = all,
        topEnd = all,
        bottomEnd = all,
        bottomStart = all,
        cornerSmoothing = cornerSmoothing
    )

}

@Immutable
class SquircleShape(
    topStartCorner: CornerSize,
    topEndCorner: CornerSize,
    bottomStartCorner: CornerSize,
    bottomEndCorner: CornerSize,
    cornerSmoothing: Float
) : SquircleBasedShape(
    topStartCorner,
    topEndCorner,
    bottomStartCorner,
    bottomEndCorner,
    cornerSmoothing
) {

    constructor(
        percent: Int = 100,
        cornerSmoothing: Float = CornerSmoothing.Medium
    ) : this(
        topStartCorner = CornerSize(percent),
        topEndCorner = CornerSize(percent),
        bottomStartCorner = CornerSize(percent),
        bottomEndCorner = CornerSize(percent),
        cornerSmoothing = cornerSmoothing
    )

    constructor(
        radius: Dp,
        cornerSmoothing: Float = CornerSmoothing.Medium
    ) : this(
        topStartCorner = CornerSize(radius),
        topEndCorner = CornerSize(radius),
        bottomStartCorner = CornerSize(radius),
        bottomEndCorner = CornerSize(radius),
        cornerSmoothing = cornerSmoothing
    )

    constructor(
        topStart: Int = 0,
        topEnd: Int = 0,
        bottomStart: Int = 0,
        bottomEnd: Int = 0,
        cornerSmoothing: Float = CornerSmoothing.Medium
    ) : this(
        topStartCorner = CornerSize(topStart),
        topEndCorner = CornerSize(topEnd),
        bottomStartCorner = CornerSize(bottomStart),
        bottomEndCorner = CornerSize(bottomEnd),
        cornerSmoothing = cornerSmoothing
    )

    constructor(
        topStart: Dp = 0.dp,
        topEnd: Dp = 0.dp,
        bottomStart: Dp = 0.dp,
        bottomEnd: Dp = 0.dp,
        cornerSmoothing: Float = CornerSmoothing.Medium
    ) : this(
        topStartCorner = CornerSize(topStart),
        topEndCorner = CornerSize(topEnd),
        bottomStartCorner = CornerSize(bottomStart),
        bottomEndCorner = CornerSize(bottomEnd),
        cornerSmoothing = cornerSmoothing
    )

    override fun createOutline(
        size: Size,
        topStart: Float,
        topEnd: Float,
        bottomEnd: Float,
        bottomStart: Float,
        cornerSmoothing: Float,
        layoutDirection: LayoutDirection
    ): Outline = if (topStart + topEnd + bottomEnd + bottomStart == 0.0f) {
        Outline.Rectangle(size.toRect())
    } else {
        val isLtr = layoutDirection == LayoutDirection.Ltr
        Outline.Generic(
            path = squircleShapePath(
                size = size,
                topLeftCorner = if (isLtr) topStart else topEnd,
                topRightCorner = if (isLtr) topEnd else topStart,
                bottomLeftCorner = if (isLtr) bottomStart else bottomEnd,
                bottomRightCorner = if (isLtr) bottomEnd else bottomStart,
                cornerSmoothing = cornerSmoothing
            )
        )
    }

    override fun copy(
        topStart: CornerSize,
        topEnd: CornerSize,
        bottomEnd: CornerSize,
        bottomStart: CornerSize
    ) = copy(
        topStart = topStart,
        topEnd = topEnd,
        bottomEnd = bottomEnd,
        bottomStart = bottomStart,
        cornerSmoothing = cornerSmoothing
    )

    override fun copy(
        topStart: CornerSize,
        topEnd: CornerSize,
        bottomEnd: CornerSize,
        bottomStart: CornerSize,
        cornerSmoothing: Float
    ) = SquircleShape(
        topStartCorner = topStart,
        topEndCorner = topEnd,
        bottomStartCorner = bottomStart,
        bottomEndCorner = bottomEnd,
        cornerSmoothing = cornerSmoothing
    )

    override fun toString(): String {
        return "SquircleShape(topStart = $topStart, topEnd = $topEnd, bottomStart = " +
                "$bottomStart, bottomEnd = $bottomEnd, cornerSmoothing = $cornerSmoothing)"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is SquircleShape) return false
        if (topStart != other.topStart) return false
        if (topEnd != other.topEnd) return false
        if (bottomStart != other.bottomStart) return false
        if (bottomEnd != other.bottomEnd) return false
        if (cornerSmoothing != other.cornerSmoothing) return false
        return true
    }

    override fun hashCode(): Int {
        var result = topStart.hashCode()
        result = 31 * result + topEnd.hashCode()
        result = 31 * result + bottomStart.hashCode()
        result = 31 * result + bottomEnd.hashCode()
        result = 31 * result + cornerSmoothing.hashCode()
        return result
    }

}

@Suppress("NAME_SHADOWING")
fun squircleShapePath(
    size: Size,
    topLeftCorner: Float,
    topRightCorner: Float,
    bottomLeftCorner: Float,
    bottomRightCorner: Float,
    cornerSmoothing: Float = CornerSmoothing.Medium
): Path {
    fun clampedCornerRadius(cornerSize: Float, size: Size) =
        cornerSize.coerceIn(0.0f, size.minDimension / 2)

    fun clampedCornerSmoothing(cornerSmoothing: Float) =
        cornerSmoothing.coerceIn(0.55f, 1f)

    val topLeftCorner = clampedCornerRadius(topLeftCorner, size)
    val topRightCorner = clampedCornerRadius(topRightCorner, size)
    val bottomLeftCorner = clampedCornerRadius(bottomLeftCorner, size)
    val bottomRightCorner = clampedCornerRadius(bottomRightCorner, size)
    val cornerSmoothing = clampedCornerSmoothing(cornerSmoothing)

    return Path().apply {
        val width = size.width
        val height = size.height

        moveTo(
            x = topLeftCorner,
            y = 0f
        )

        lineTo(
            x = width - topRightCorner,
            y = 0f
        )

        cubicTo(
            x1 = width - topRightCorner * (1 - cornerSmoothing),
            y1 = 0f,
            x2 = width,
            y2 = topRightCorner * (1 - cornerSmoothing),
            x3 = width,
            y3 = topRightCorner
        )

        lineTo(
            x = width,
            y = height - bottomRightCorner
        )

        cubicTo(
            x1 = width,
            y1 = height - bottomRightCorner * (1 - cornerSmoothing),
            x2 = width - bottomRightCorner * (1 - cornerSmoothing),
            y2 = height,
            x3 = width - bottomRightCorner,
            y3 = height
        )

        lineTo(
            x = bottomLeftCorner,
            y = height
        )

        cubicTo(
            x1 = bottomLeftCorner * (1 - cornerSmoothing),
            y1 = height,
            x2 = 0f,
            y2 = height - bottomLeftCorner * (1 - cornerSmoothing),
            x3 = 0f,
            y3 = height - bottomLeftCorner
        )

        lineTo(
            x = 0f,
            y = topLeftCorner
        )

        cubicTo(
            x1 = 0f,
            y1 = topLeftCorner * (1 - cornerSmoothing),
            x2 = topLeftCorner * (1 - cornerSmoothing),
            y2 = 0f,
            x3 = topLeftCorner,
            y3 = 0f
        )

        close()

    }

}

@Preview
@Composable
private fun Preview() {
    Column(
        modifier = Modifier.padding(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        val modifier = Modifier.size(100.dp)
        fun shape(smoothing: Float) = SquircleShape(25, smoothing)
        Box(modifier = modifier.background(Color.White, shape(CornerSmoothing.None)))
        Box(modifier = modifier.background(Color.White, shape(CornerSmoothing.Small)))
        Box(modifier = modifier.background(Color.White, shape(CornerSmoothing.Medium)))
        Box(modifier = modifier.background(Color.White, shape(CornerSmoothing.High)))
        Box(modifier = modifier.background(Color.White, shape(CornerSmoothing.Full)))
    }
}